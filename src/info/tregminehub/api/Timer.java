//This timer is a butchered version of the Essentials plug-in timer, partial credit to Essentials and its creators
package info.tregminehub.api;

import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;

import info.tregminehub.Tregmine;
import info.tregminehub.api.TregminePlayer.Flags;
import net.md_5.bungee.api.chat.TextComponent;

public class Timer implements Runnable {
	Tregmine t;
	private final long maxTime = 10 * 1000000;
	private transient long lastPoll = System.nanoTime();
	private int skip1 = 0;

	public Timer(Tregmine plugin) {
		t = plugin;
	}

	@Override
	public void run() {
		final long startTime = System.nanoTime();

		long timeSpent = (startTime - lastPoll) / 1000;
		if (timeSpent == 0) {
			timeSpent = 1;
		}
		lastPoll = startTime;
		int count = 0;
		for (TregminePlayer player : t.getOnlinePlayers()) {
			count++;
			if (skip1 > 0) {
				skip1--;
				continue;
			}
			if (count % 10 == 0) {
				if (System.nanoTime() - startTime > maxTime / 2) {
					skip1 = count - 1;
				}
			}
			try {
				player.checkActivity();

			} catch (Exception e) {
				t.getLogger().log(Level.WARNING, "Timer Exception", e);
			}
		}
		// try {
		// Thread.sleep(30 * 1000);
		// } catch (InterruptedException e) {
		// e.printStackTrace();
		// }
	}
}
