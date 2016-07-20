package info.tregminehub.commands;

import org.bukkit.ChatColor;

import info.tregminehub.Tregmine;
import info.tregminehub.api.Lag;
import info.tregminehub.api.TregminePlayer;

public class TpsCommand extends AbstractCommand {
	public TpsCommand(Tregmine tregmine) {
		super(tregmine, "ttps");
	}

	@Override
	public boolean handlePlayer(TregminePlayer player, String[] args) {
		double tps = Lag.getTPS();
		double lagPercentage = Math.round((1.0D - tps / 20.0D) * 100.0D);
		if (isTpsGood(tps)) {
			player.sendStringMessage(ChatColor.GREEN + "Server TPS: " + tps);
		}
		if (!isTpsGood(tps)) {
			player.sendStringMessage(ChatColor.RED + "Server TPS: " + tps);
		}
		player.sendStringMessage(ChatColor.BLUE + "Lag Percentage: " + lagPercentage);
		return true;
	}

	private boolean isTpsGood(double giveMeYourTPS) {
		if (giveMeYourTPS >= 17.0D) {
			return true;
		} else {
			return false;
		}
	}
}
