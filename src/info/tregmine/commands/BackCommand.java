package info.tregmine.commands;

import org.bukkit.ChatColor;

import info.tregmine.Tregmine;
import info.tregmine.api.TregminePlayer;

public class BackCommand extends AbstractCommand {
	Tregmine plugin;

	public BackCommand(Tregmine tregmine) {
		super(tregmine, "back");
		plugin = tregmine;
	}

	@Override
	public boolean handlePlayer(TregminePlayer player, String[] args) {
		if (player.getWorld().getName().equalsIgnoreCase("vanilla") || player.isInVanillaWorld()) {
			player.sendStringMessage(ChatColor.RED + "You cannot use that command in this world!");
			return true;
		}
		if (!player.getIsStaff()) {
			player.sendStringMessage(ChatColor.RED + "You don't have the permissions to do that command.");
			player.sendStringMessage(ChatColor.RED + "There's no going back!");
			return true;
		}
		if (player.getLastPos() == null) {
			player.sendStringMessage(ChatColor.RED + "You don't have a last location!");
			return true;
		}
		boolean success = player.teleport(player.getLastPos());
		if (!success) {
			player.sendStringMessage(ChatColor.RED + "Failed to teleport back. Sorry!");
			if (!player.getLastPos().toString().isEmpty()) {
				player.sendStringMessage(
						ChatColor.RED + "But... I can give you your coordinates. X" + player.getLastPos().getBlockX()
								+ " Y" + player.getLastPos().getBlockY() + " Z" + player.getLastPos().getBlockZ());
			}
		}
		return true;
	}
}
