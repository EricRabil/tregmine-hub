package info.tregmine.commands;

import org.bukkit.GameMode;

import info.tregmine.Tregmine;
import info.tregmine.api.TregminePlayer;
import net.md_5.bungee.api.ChatColor;

public class GameModeLegacyCommand extends AbstractCommand {
	private Tregmine t;

	public GameModeLegacyCommand(Tregmine instance) {
		super(instance, "gamemode");
		this.t = instance;
	}

	@Override
	public boolean handlePlayer(TregminePlayer player, String[] args) {
		GameMode mode = null;
		if (!player.getRank().canUseCreative()) {
			this.insufficientPerms(player);
			return true;
		}
		if (player.isInVanillaWorld()) {
			player.setFireTicks(30);
			player.sendStringMessage(ChatColor.RED + "You cannot use that command in this world!");
			return true;
		}
		if (args.length < 1) {
			this.invalidArguments(player, "/gamemode <mode>");
			return true;
		}
		for (GameMode check : GameMode.values()) {
			int id = check.getValue();
			String name = check.toString().toLowerCase();
			if (args[0].toLowerCase().contains(name)) {
				mode = check;
				break;
			}
			try {
				if (Integer.parseInt(args[0]) == id) {
					mode = check;
					player.sendStringMessage(ChatColor.RED + "" + ChatColor.ITALIC + "Warning: " + ChatColor.RESET + ""
							+ ChatColor.AQUA + "GameMode IDs will be removed in a future release.");
					break;
				}
			} catch (NumberFormatException e) {
			}
		}
		if (mode == null) {
			player.sendStringMessage(ChatColor.RED + "The gamemode specified does not exist.");
			return true;
		}
		player.setGameMode(mode);
		player.sendStringMessage(ChatColor.YELLOW + "You are now in " + mode.toString().toLowerCase() + " mode. ");
		if (player.getRank().canFly()) {
			player.setAllowFlight(true);
		}
		return true;
	}
}
