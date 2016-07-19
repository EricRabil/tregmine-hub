package info.tregmine.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;

import info.tregmine.Tregmine;
import info.tregmine.api.TregminePlayer;

public class SendBackCommand extends AbstractCommand {
	Tregmine plugin;

	public SendBackCommand(Tregmine tregmine) {
		super(tregmine, "sendback");
		plugin = tregmine;
	}

	@Override
	public boolean handlePlayer(TregminePlayer player, String[] args) {
		if (player.getWorld().getName().equalsIgnoreCase("vanilla") || player.isInVanillaWorld()) {
			player.sendStringMessage(ChatColor.RED + "You cannot use that command in this world!");
			return true;
		}
		if (!player.getIsAdmin()) {
			player.sendStringMessage(ChatColor.RED + "You don't have permission to send a player back!");
			return true;
		}
		if (args.length != 1) {
			player.sendStringMessage(ChatColor.RED + "Arguments error: /sendback <player name>");
			return true;
		}
		List<TregminePlayer> players = plugin.matchPlayer(args[0]);
		if (players.size() != 1) {
			player.sendStringMessage(ChatColor.RED + "Player is not online!");
			return true;
		}
		TregminePlayer target = players.get(0);
		if (target.getWorld().getName().equalsIgnoreCase("vanilla") || player.isInVanillaWorld()) {
			player.sendStringMessage(ChatColor.RED + "The player specified is in the vanilla world!");
			return true;
		}
		if (target.getLastPos() == null) {
			player.sendStringMessage(ChatColor.RED + "Player does not have a last location.");
			return true;
		}
		if (!target.teleport(target.getLastPos())) {
			Location pos = target.getLastPos();
			player.sendStringMessage(ChatColor.RED + "Player could not be teleported. Here are the coordinates:");
			player.sendStringMessage(
					ChatColor.AQUA + "X" + pos.getBlockX() + " Y" + pos.getBlockY() + " Z" + pos.getBlockZ());
			return true;
		}
		return true;
	}
}
