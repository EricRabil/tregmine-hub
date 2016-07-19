package info.tregmine.commands;

import static org.bukkit.ChatColor.YELLOW;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;

import info.tregmine.Tregmine;
import info.tregmine.api.TregminePlayer;

public class GameModeCommand extends AbstractCommand {
	private GameMode mode;

	public GameModeCommand(Tregmine tregmine, String name, GameMode mode) {
		super(tregmine, name);

		this.mode = mode;
	}

	@Override
	public boolean handlePlayer(TregminePlayer player, String[] args) {
		if (player.getWorld().getName().equalsIgnoreCase("vanilla") || player.isInVanillaWorld()) {
			player.setFireTicks(30);
			player.sendStringMessage(ChatColor.RED + "You cannot use that command in this world!");
			return true;
		}
		if (!player.getRank().canUseCreative()) {
			this.insufficientPerms(player);
			return true;
		}
		player.setGameMode(mode);
		player.sendStringMessage(YELLOW + "You are now in " + mode.toString().toLowerCase() + " mode. ");

		if (player.getRank().canFly()) {
			player.setAllowFlight(true);
		}

		return true;
	}
}
