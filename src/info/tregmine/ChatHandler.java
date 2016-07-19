package info.tregmine;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.UpgradeRequest;
import org.eclipse.jetty.websocket.api.UpgradeResponse;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import org.eclipse.jetty.websocket.server.WebSocketHandler;
import org.eclipse.jetty.websocket.servlet.WebSocketCreator;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;
import org.json.JSONException;
import org.json.JSONObject;

import info.tregmine.api.TregminePlayer;
import info.tregmine.events.TregmineChatEvent;

public class ChatHandler extends WebSocketHandler implements WebSocketCreator, Listener {
	public static class ChatSocket extends WebSocketAdapter {
		private ChatHandler handler;
		private Session session;
		private TregminePlayer player;

		public ChatSocket(ChatHandler handler) {
			this.handler = handler;
			this.player = null;
		}

		public TregminePlayer getPlayer() {
			return player;
		}

		@Override
		public RemoteEndpoint getRemote() {
			return session.getRemote();
		}

		@Override
		public Session getSession() {
			return session;
		}

		@Override
		public void onWebSocketClose(int statusCode, String reason) {
			handler.removeSession(this);
		}

		@Override
		public void onWebSocketConnect(Session sess) {
			this.session = sess;

			// sess.setIdleTimeout(Long.MAX_VALUE);
			handler.addSession(this);
		}

		@Override
		public void onWebSocketError(Throwable e) {
			Tregmine.LOGGER.log(Level.WARNING, "Socket error", e);
			handler.removeSession(this);
		}

		@Override
		public void onWebSocketText(String message) {
			handler.dispatch(this, message);
		}

		public void sendMessage(JSONObject msg) {
			getRemote().sendStringByFuture(msg.toString());
		}

		public void sendSystemMessage(String message) {
			try {
				JSONObject obj = new JSONObject();
				obj.put("action", "sysmsg");
				obj.put("text", message);

				getRemote().sendStringByFuture(obj.toString());
			} catch (JSONException e) {
				throw new RuntimeException(e);
			}
		}

		public void setPlayer(TregminePlayer v) {
			this.player = v;
		}
	}

	private Tregmine tregmine;
	private PluginManager pluginMgr;

	private Set<ChatSocket> sockets;
	private Map<Integer, Date> kickedPlayers;

	public ChatHandler(Tregmine tregmine, PluginManager pluginMgr) {
		this.tregmine = tregmine;
		this.pluginMgr = pluginMgr;

		sockets = new HashSet<ChatSocket>();
		kickedPlayers = new HashMap<Integer, Date>();
	}

	private void addSession(ChatSocket session) {
		Tregmine.LOGGER.info("Web connected");
		sockets.add(session);
	}

	/**
	 * Do not call directly! Calls are synchronized by
	 * WebServer.executeChatAction.
	 **/
	public void broadcastToWeb(TregminePlayer sender, String channel, String text) {
		try {
			JSONObject obj = new JSONObject();
			obj.put("action", "msg");
			obj.put("sender", ChatColor.stripColor(sender.getChatNameNoHover()));
			obj.put("rank", sender.getRank().toString());
			obj.put("channel", channel);
			obj.put("text", text);

			Iterator<ChatSocket> it = sockets.iterator();
			while (it.hasNext()) {
				ChatSocket socket = it.next();
				Session session = socket.getSession();
				if (!session.isOpen()) {
					it.remove();
					disconnect(socket);
					continue;
				}

				socket.sendMessage(obj);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void configure(WebSocketServletFactory factory) {
		factory.setCreator(this);
	}

	@Override
	public Object createWebSocket(UpgradeRequest req, UpgradeResponse resp) {
		return new ChatSocket(this);
	}

	private void disconnect(ChatSocket socket) {
		try {
			socket.getSession().disconnect();
		} catch (IOException e) {
		}
	}

	private void dispatch(ChatSocket socket, String message) {
		try {
			JSONObject obj = new JSONObject(message);
			if (!obj.has("action")) {
				return;
			}

			String action = obj.getString("action");

			if ("msg".equals(action)) {
				if (socket.getPlayer() == null) {
					Tregmine.LOGGER.info("Player not set.");
					return;
				}

				String channel = obj.getString("channel");
				if (channel == null) {
					return;
				}
				String text = obj.getString("text");
				if (text == null) {
					return;
				}

				TregmineChatEvent event = new TregmineChatEvent(socket.getPlayer(), text, channel, true);
				pluginMgr.callEvent(event);
			} else if ("auth".equals(action)) {
				String authToken = obj.getString("authToken");
				if (authToken == null) {
					return;
				}

				WebServer server = tregmine.getWebServer();
				Map<String, TregminePlayer> authTokens = server.getAuthTokens();
				if (authTokens.get(authToken) == null) {
					Tregmine.LOGGER.info("Auth token " + authToken + " not found.");
					socket.sendSystemMessage("Auth token not found.");
					return;
				}

				TregminePlayer sender = authTokens.get(authToken);

				Date kickTime = kickedPlayers.get(sender.getId());
				if (kickTime != null) {
					long time = (new Date().getTime() - kickTime.getTime()) / 1000L;
					if (time < 600l) {
						socket.sendSystemMessage("You are not allowed to reconnect yet.");
						Tregmine.LOGGER.info(sender.getRealName() + " attempted to "
								+ "reconnect after being kicked before the allowed duration.");

						disconnect(socket);
						removeSession(socket);
						return;
					}
				}

				socket.setPlayer(sender);

				Tregmine.LOGGER.info(sender.getRealName() + " authed successfully.");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public boolean isOnline(TregminePlayer player) {
		for (ChatSocket socket : sockets) {
			TregminePlayer current = socket.getPlayer();
			if (current == null) {
				continue;
			}
			if (current.getId() == player.getId()) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Do not call directly! Calls are synchronized by
	 * WebServer.executeChatAction.
	 **/
	public void kickPlayer(TregminePlayer sender, TregminePlayer victim, String message) {
		for (ChatSocket socket : sockets) {
			TregminePlayer current = socket.getPlayer();
			if (current == null) {
				continue;
			}
			if (current.getId() == victim.getId()) {
				socket.sendSystemMessage("You were kicked by " + sender.getRealName() + ": " + message);

				disconnect(socket);
				removeSession(socket);
				kickedPlayers.put(victim.getId(), new Date());
			}
		}
	}

	public List<TregminePlayer> listPlayers() {
		List<TregminePlayer> players = new ArrayList<TregminePlayer>();
		for (ChatSocket socket : sockets) {
			TregminePlayer current = socket.getPlayer();
			if (current == null) {
				continue;
			}

			players.add(current);
		}

		return players;
	}

	private void removeSession(ChatSocket session) {
		Tregmine.LOGGER.info("Web disconnected");
		sockets.remove(session);
	}
}
