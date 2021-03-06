package info.tregminehub.api.util;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.ScoreboardManager;

import info.tregminehub.api.TregminePlayer;

public class ScoreboardClearTask implements Runnable {
	public static void start(Plugin plugin, TregminePlayer player) {
		Runnable runnable = new ScoreboardClearTask(player);

		Server server = Bukkit.getServer();
		BukkitScheduler scheduler = server.getScheduler();
		scheduler.scheduleSyncDelayedTask(plugin, runnable, 400);
	}

	private TregminePlayer player;

	private ScoreboardClearTask(TregminePlayer player) {
		this.player = player;
	}

	@Override
	public void run() {
		if (!player.isOnline()) {
			return;
		}

		try {
			ScoreboardManager manager = Bukkit.getScoreboardManager();
			player.setScoreboard(manager.getNewScoreboard());
		} catch (IllegalStateException e) {
			// We don't really care
		}
	}
}
