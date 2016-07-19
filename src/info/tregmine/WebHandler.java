package info.tregmine;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import info.tregmine.api.util.Base64;

public class WebHandler extends AbstractHandler implements Listener {
	public interface Action {
		public void generateResponse(PrintWriter writer) throws WebException;

		public void queryGameState(Tregmine tregmine);
	}

	public interface ActionFactory {
		public Action createAction(Request request) throws WebException;

		public String getName();
	}

	public static class WebEvent extends Event {
		private static final HandlerList handlers = new HandlerList();

		public static HandlerList getHandlerList() {
			return handlers;
		}

		private Action action;

		public WebEvent(Action action) {
			this.action = action;
		}

		public Action getAction() {
			return action;
		}

		@Override
		public HandlerList getHandlers() {
			return handlers;
		}
	}

	public static class WebException extends Exception {
		private int responseCode;

		public WebException(String message, int responseCode) {
			super(message);

			this.responseCode = responseCode;
		}

		public WebException(Throwable t) {
			super(t);

			this.responseCode = 500;
		}

		public int getResponseCode() {
			return responseCode;
		}
	}

	private static String hmac(String key, String msg) throws NoSuchAlgorithmException, InvalidKeyException {
		SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), "HmacSHA1");

		Mac mac = Mac.getInstance("HmacSHA1");
		mac.init(keySpec);
		byte[] result = mac.doFinal(msg.getBytes());

		return Base64.encode(result);
	}

	private Tregmine tregmine;

	private PluginManager pluginMgr;

	private Map<String, ActionFactory> actions;

	private String key;

	public WebHandler(Tregmine tregmine, PluginManager pluginMgr, String key) {
		this.tregmine = tregmine;
		this.pluginMgr = pluginMgr;

		this.actions = new HashMap<String, ActionFactory>();

		this.key = key;
	}

	public void addAction(ActionFactory factory) {
		actions.put(factory.getName(), factory);
	}

	@Override
	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		System.out.println("Servlet path: " + baseRequest.getServletPath());
		System.out.println("Path info: " + baseRequest.getPathInfo());

		baseRequest.setHandled(true);
		response.setContentType("application/json;charset=utf-8");

		String signingStr = request.getPathInfo();
		if (request.getQueryString() != null) {
			signingStr += "?" + request.getQueryString();
		}

		try {
			String auth = hmac(key, signingStr);
			String authCmp = request.getHeader("Authorization");
			// TODO: Possible timing sidechannel?
			if (!auth.equals(authCmp)) {
				Tregmine.LOGGER.info("Web: " + signingStr + " FAILED");
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				return;
			} else {
				Tregmine.LOGGER.info("Web: " + signingStr + " OK");
			}
		} catch (NoSuchAlgorithmException e) {
			throw new ServletException(e);
		} catch (InvalidKeyException e) {
			throw new ServletException(e);
		}

		// Look up appropriate action factory for this request
		try {
			ActionFactory factory = actions.get(baseRequest.getPathInfo());
			if (factory == null) {
				response.setStatus(HttpServletResponse.SC_NOT_FOUND);
				return;
			}

			// Create an action
			Action action = factory.createAction(baseRequest);

			// Wait for bukkit to process it
			pluginMgr.callEvent(new WebEvent(action));

			// Prepare response
			response.setStatus(HttpServletResponse.SC_OK);

			// Generate response
			PrintWriter writer = response.getWriter();
			action.generateResponse(writer);
		} catch (WebException e) {
			response.setStatus(e.getResponseCode());
			Tregmine.LOGGER.log(Level.WARNING, "Error in processing request", e);
		} catch (Throwable e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			Tregmine.LOGGER.log(Level.WARNING, "Error in processing request", e);
		}
	}

	@EventHandler
	public void onWebEvent(WebEvent event) {
		Action action = event.getAction();

		// Execute action, safely
		try {
			action.queryGameState(tregmine);
		} catch (Throwable e) {
			Tregmine.LOGGER.log(Level.WARNING, "Querying game state failed", e);
		}
	}
}
