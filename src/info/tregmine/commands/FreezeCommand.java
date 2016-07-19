package info.tregmine.commands;

import java.util.List;

import org.bukkit.ChatColor;

import info.tregmine.Tregmine;
import info.tregmine.api.TregminePlayer;

public class FreezeCommand extends AbstractCommand {
	private Tregmine plugin;

	public FreezeCommand(Tregmine tregmine) {
		super(tregmine, "freeze");
		plugin = tregmine;
	}

	@Override
	public boolean handlePlayer(TregminePlayer player, String[] args) {
		if (player.isInVanillaWorld()) {
			player.sendStringMessage(ChatColor.RED + "You cannot use that command in this world!");
			return true;
		}
		if (!player.getIsAdmin()) {
			player.sendStringMessage(ChatColor.RED + "You don't have permission to freeze players!");
			return true;
		}
		if (args.length != 1) {
			player.sendStringMessage(ChatColor.RED + "Invalid arguments! Use /freeze <player>");
			return true;
		}
		String raw = args[0];
		List<TregminePlayer> victims = plugin.matchPlayer(raw);
		if (victims.size() != 1) {
			player.sendStringMessage(ChatColor.RED + "That player is not online!");
			return true;
		}
		TregminePlayer victim = victims.get(0);
		if (player.isInVanillaWorld()) {
			player.sendStringMessage(
					ChatColor.RED + "You cannot freeze that player because they are in the vanilla world!");
			return true;
		}
		boolean newValue = !victim.getFrozen();
		victim.setFrozen(newValue);
		String getState = (newValue ? "frozen" : "unfrozen");
		ChatColor color = (newValue ? ChatColor.RED : ChatColor.GREEN);
		player.sendStringMessage(victim.getName() + ChatColor.BLUE + " has been " + getState + ".");
		victim.sendStringMessage(color + "You have been " + getState + ".");
		return true;
	}
}
