package info.tregminehub.commands;

import static org.bukkit.ChatColor.YELLOW;

import info.tregminehub.Tregmine;
import info.tregminehub.api.TregminePlayer;

public class TimeCommand extends AbstractCommand {
	public TimeCommand(Tregmine tregmine) {
		super(tregmine, "time");
	}

	@Override
	public boolean handlePlayer(TregminePlayer player, String[] args) {
		if (!player.getRank().canSetTime()) {
			return true;
		}

		if (args.length != 1) {
			player.sendStringMessage(YELLOW + "Say /time day|night|normal");
			return false;
		}

		String time = args[0];

		if ("day".equalsIgnoreCase(time)) {
			player.setPlayerTime(6000, false);
			player.sendStringMessage(YELLOW + "Time set to day");
		} else if ("night".equalsIgnoreCase(time)) {
			player.setPlayerTime(18000, false);
			player.sendStringMessage(YELLOW + "Time set to night");
		} else if ("normal".equalsIgnoreCase(time)) {
			player.resetPlayerTime();
			player.sendStringMessage(YELLOW + "Time set to normal");
		}

		return true;
	}
}
