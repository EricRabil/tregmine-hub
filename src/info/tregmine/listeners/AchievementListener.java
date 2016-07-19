package info.tregmine.listeners;

import org.bukkit.Achievement;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAchievementAwardedEvent;

import info.tregmine.Tregmine;
import info.tregmine.api.TregminePlayer;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;

public class AchievementListener implements Listener {
	private Tregmine t;

	public AchievementListener(Tregmine instance) {
		this.t = instance;
	}

	@EventHandler
	public void PlayerAchievementAwardedEvent(PlayerAchievementAwardedEvent event) {
		event.setCancelled(true);
		TregminePlayer achiever = t.getPlayer(event.getPlayer());
		Achievement achievement = event.getAchievement();
		achiever.awardAchievement(achievement);
		t.broadcast(achiever.getChatName(), new TextComponent(
				ChatColor.YELLOW + " won the achievement " + ChatColor.AQUA + event.getAchievement().name()));
	}
}
