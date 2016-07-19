package info.tregmine.commands;

import info.tregmine.Tregmine;
import info.tregmine.api.TregminePlayer;
import net.md_5.bungee.api.ChatColor;

public class OWCommand extends AbstractCommand {
	Tregmine tregmine;
	TregminePlayer sender;

	public OWCommand(Tregmine inst) {
		super(inst, "taxi");
		this.tregmine = inst;
	}

	@Override
	public boolean handlePlayer(TregminePlayer player, String[] args) {
		if (!tregmine.hasSecondaryWorld()) {
			player.sendStringMessage(ChatColor.RED + "There's no old world on this server!");
			return true;
		}
		if (tregmine.getConfig().getBoolean("worlds.special.restrictnewworldtostaff")) {
			if (!player.getRank().canGoToNewWorld()) {
				player.sendStringMessage(ChatColor.RED + "The new world isn't ready yet!");
				return true;
			}
		}
		if (player.getWorld() == tregmine.getSWorld()) {
			player.gotoWorld(player.getPlayer(), tregmine.getServer().getWorld("world").getSpawnLocation(),
					ChatColor.YELLOW + "[TAXI-R-US] Welcome back to the main world!", ChatColor.RED
							+ "[TAXI-R-US] Something bad happened; Try again or contact an admin for assistance.");
		} else if (player.getWorld() == tregmine.getServer().getWorld("world")) {
			player.gotoWorld(player.getPlayer(), tregmine.getSWorld().getSpawnLocation(),
					ChatColor.YELLOW + "[TAXI-R-US] Welcome to the new world!", ChatColor.YELLOW
							+ "[TAXI-R-US] Something bad happened; Try again or contact an admin for assistance.");
		} else {
			player.sendStringMessage(
					ChatColor.RED + "You must be in the overworld of either the old world or the main world");
		}
		return true;
	}
}
