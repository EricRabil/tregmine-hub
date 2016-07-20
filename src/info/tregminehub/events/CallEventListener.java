package info.tregminehub.events;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.server.ServerListPingEvent;

import info.tregminehub.Tregmine;
import info.tregminehub.api.TregminePlayer;

public class CallEventListener implements Listener {
	private Tregmine plugin;

	public CallEventListener(Tregmine instance) {
		this.plugin = instance;
	}

	@EventHandler
	public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
		if (event.getMessage().equalsIgnoreCase("/afk")) {
			return;
		} else {
			TregminePlayer sender = plugin.getPlayer(event.getPlayer().getPlayer());
			if (sender.isAfk()) {
				sender.setAfk(false);
			}
		}
	}

	// Triggers when a player pings the server
	@EventHandler
	public void onServerListPing(ServerListPingEvent event) {
		if (plugin.getLockdown()) {
			event.setMaxPlayers(0);
			event.setMotd(ChatColor.GOLD + plugin.getConfig().getString("general.servername") + ChatColor.RED
					+ " is on lockdown.\n" + ChatColor.RED + "Only staff can join.");
		} else {
			event.setMaxPlayers(plugin.plConfig().getInt("general.motd.maxplayers"));
			event.setMotd(
					ChatColor.translateAlternateColorCodes('#', plugin.getConfig().getString("general.motd.lineone"))
							+ "\n" + ChatColor.RESET + "" + ChatColor.translateAlternateColorCodes('#',
									plugin.getConfig().getString("general.motd.linetwo")));
		}
	}

	
	// Triggers when a player moves position, not head.
	@EventHandler
	public void PlayerMoveBlockEventListener(PlayerMoveEvent event) {
		TregminePlayer player = plugin.getPlayer(event.getPlayer());

		if (player == null) {
			event.getPlayer().kickPlayer("Something went wrong!");
			Tregmine.LOGGER.info(event.getPlayer().getName() + " was not found in players map (PlayerMoveEvent).");
			return;
		}

		if (event.getFrom().getX() == event.getTo().getX() && event.getFrom().getY() == event.getTo().getY()
				&& event.getFrom().getZ() == event.getTo().getZ()) {
			return;
		}

		PlayerMoveBlockEvent customEvent = new PlayerMoveBlockEvent(event.getFrom(), event.getTo(), player);
		plugin.getServer().getPluginManager().callEvent(customEvent);
	}
	
	// Triggers on a server chat event
	@EventHandler
	public void TregmineChatEventListener(AsyncPlayerChatEvent event) {
		TregminePlayer player = plugin.getPlayer(event.getPlayer());
		if (player.getChatState() != TregminePlayer.ChatState.CHAT) {
			return;
		}

		TregmineChatEvent customEvent = new TregmineChatEvent(player, event.getMessage(), player.getChatChannel(),
				false);
		plugin.getServer().getPluginManager().callEvent(customEvent);

		event.setCancelled(true);
	}
}
