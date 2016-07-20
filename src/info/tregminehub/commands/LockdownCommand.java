package info.tregminehub.commands;

import org.bukkit.ChatColor;

import info.tregminehub.Tregmine;
import info.tregminehub.api.TregminePlayer;

public class LockdownCommand extends AbstractCommand {
	Tregmine plugin;

	public LockdownCommand(Tregmine tregmine) {
		super(tregmine, "lockdown");
		plugin = tregmine;
	}

	@Override
	public boolean handlePlayer(TregminePlayer player, String[] args) {
		if (!player.getIsAdmin()) {
			player.sendStringMessage(ChatColor.RED + "You don't have permission to call a lockdown!");
			return true;
		}
		if (args.length == 0) {
			player.sendStringMessage(ChatColor.RED + "You must specify <on|off>");
			return true;
		}
		System.out.println(args[0]);
		boolean state;
		if (args[0].equalsIgnoreCase("on")) {
			state = true;
		} else if (args[0].equalsIgnoreCase("off")) {
			state = false;
		} else {
			player.sendStringMessage(ChatColor.RED + "You must specify <on|off>");
			return true;
		}
		plugin.setLockdown(state);
		return true;
	}

}
