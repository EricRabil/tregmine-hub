package info.tregmine.listeners;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import info.tregmine.Tregmine;
import info.tregmine.api.Rank;
import info.tregmine.api.TregminePlayer;
import info.tregmine.commands.MentorCommand;
import info.tregmine.database.DAOException;
import info.tregmine.database.IContext;
import info.tregmine.database.IPlayerDAO;
import net.md_5.bungee.api.chat.TextComponent;

public class SetupListener implements Listener {
	private Tregmine plugin;

	public SetupListener(Tregmine instance) {
		this.plugin = instance;
	}

	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent event) {
		TregminePlayer player = plugin.getPlayer(event.getPlayer());
		if (player.getChatState() != TregminePlayer.ChatState.SETUP) {
			return;
		}

		event.setCancelled(true);

		String text = event.getMessage();
		player.sendStringMessage(text);

		Tregmine.LOGGER.info("[SETUP] <" + player.getChatNameNoHover() + "> " + text);

		try (IContext ctx = plugin.createContext()) {
			if ("yes".equalsIgnoreCase(text)) {
				player.sendStringMessage(ChatColor.GREEN + "You have now joined Tregmine "
						+ "and can talk with other players! Say Hi! :)");
				player.setChatState(TregminePlayer.ChatState.CHAT);
				player.setRank(Rank.TOURIST);

				IPlayerDAO playerDAO = ctx.getPlayerDAO();
				playerDAO.updatePlayer(player);

				Tregmine.LOGGER.info("[SETUP] " + player.getChatName() + " joined the server.");

				// server.broadcastMessage(ChatColor.GREEN + "Welcome to
				// Tregmine, " +
				// player.getChatName() + ChatColor.GREEN + "!");
				plugin.broadcast(new TextComponent(ChatColor.GREEN + "Welcome to Tregmine, "), player.getChatName(),
						new TextComponent(ChatColor.GREEN + "!"));

				MentorCommand.findMentor(plugin, player);
			} else if ("no".equalsIgnoreCase(text)) {
				player.sendStringMessage(ChatColor.YELLOW + "Unfortunately Tregmine has an "
						+ "age limit of 13 years and older. Your account has been flagged as a child.");

				player.setChatState(TregminePlayer.ChatState.CHAT);
				player.setFlag(TregminePlayer.Flags.CHILD);
				plugin.broadcast(player.getChatName(),
						new TextComponent(ChatColor.YELLOW + " is a child; Please be aware when sending messages."));
				player.setRank(Rank.TOURIST);

				IPlayerDAO playerDAO = ctx.getPlayerDAO();
				playerDAO.updatePlayer(player);

				Tregmine.LOGGER.info("[SETUP] " + player.getChatNameNoHover() + " has been marked as a child.");
			} else {
				player.sendStringMessage(ChatColor.RED + "Please say \"yes\" or \"no\". "
						+ "You will not be able to talk to other players until you do.");
			}
		} catch (DAOException e) {
			throw new RuntimeException(e);
		}
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		TregminePlayer player = plugin.getPlayer(event.getPlayer());
		if (player == null) {
			event.getPlayer().kickPlayer("Something went wrong");
			Tregmine.LOGGER.info(event.getPlayer().getName() + " was not found " + "in players map.");
			return;
		}

		if (player.getChatState() != TregminePlayer.ChatState.SETUP) {
			return;
		}

		Tregmine.LOGGER.info("[SETUP] " + player.getChatName() + " is a new player!");

		player.sendStringMessage(ChatColor.YELLOW + "Welcome to Tregmine!");
		player.sendStringMessage(ChatColor.YELLOW + "This is an age restricted server. "
				+ "Please confirm that you are 13 years or older by typing \"yes\". "
				+ "If you are younger than 13, please leave this server, or " + "type \"no\" to quit.");
		player.sendStringMessage(ChatColor.YELLOW + "You will not be able to talk "
				+ "to other players until you've verified your age.");
		TextComponent msg = new TextComponent(ChatColor.RED + "" + ChatColor.UNDERLINE + "Are you 13 years or older?");
		msg.setHoverEvent(this.plugin.buildHover(ChatColor.AQUA + "Because we are a " + ChatColor.GOLD + "COPPA"
				+ ChatColor.AQUA + " compliant server, we must enforce the age requirement."));
		player.sendSpigotMessage(msg);
	}
}
