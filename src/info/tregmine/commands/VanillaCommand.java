package info.tregmine.commands;

import org.bukkit.ChatColor;

import info.tregmine.Tregmine;
import info.tregmine.api.TregminePlayer;

public class VanillaCommand extends AbstractCommand {
	Tregmine plugin;

	public VanillaCommand(Tregmine t) {
		super(t, "vanilla");
		plugin = t;
	}

	@Override
	public boolean handlePlayer(TregminePlayer sender, String[] args) {
		if (plugin.getVanillaWorld() == null) {
			sender.sendStringMessage(ChatColor.RED + "The server does not have the vanilla world enabled.");
			return true;
		}
		if (!sender.getRank().canGoToVanilla()) {
			sender.sendStringMessage(ChatColor.RED + "You have to be a donator to go to the vanilla world!");
			return true;
		}
		if (sender.getWorld() == plugin.getServer().getWorld("world")) {
			sender.gotoWorld(sender.getPlayer(), plugin.getServer().getWorld("vanilla").getSpawnLocation(),
					ChatColor.YELLOW + "Thanks for riding the Starlight Express!",
					ChatColor.RED + "The Starlight Express is having some issues, try again later.");
		} else if (sender.getWorld() == plugin.getVanillaWorld()) {
			sender.gotoWorld(sender.getPlayer(), plugin.getServer().getWorld("world").getSpawnLocation(),
					ChatColor.YELLOW + "Thanks for riding the Starlight Express!",
					ChatColor.RED + "The Starlight Express is having some issues, try again later.");
		} else {
			sender.sendStringMessage(
					ChatColor.RED + "You cannot switch between worlds if you are not in WORLD or VANILLA");
		}
		return true;
	}
}
