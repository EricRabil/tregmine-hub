package info.tregmine.commands;

import java.util.Map;

import org.bukkit.ChatColor;

import info.tregmine.Tregmine;
import info.tregmine.api.Badge;
import info.tregmine.api.TregminePlayer;
import info.tregmine.api.UUIDFetcher;
import net.md_5.bungee.api.chat.TextComponent;

public class BadgeCommand extends AbstractCommand {
	public BadgeCommand(Tregmine tregmine) {
		super(tregmine, "badge");
	}

	@Override
	public boolean handlePlayer(TregminePlayer player, String args[]) {
		if (args.length == 1 && "list".equalsIgnoreCase(args[0])) {

			Map<Badge, Integer> badges = player.getBadges();
			if (badges.isEmpty()) {
				player.sendStringMessage(ChatColor.AQUA + "You currently have no badges!");
				return true;
			}

			for (Map.Entry<Badge, Integer> badge : badges.entrySet()) {
				player.sendStringMessage(ChatColor.AQUA + badge.getKey().name() + " - Level " + badge.getValue());
			}

		} else if (args.length == 2 && "list".equalsIgnoreCase(args[0])) {

			if (!player.getRank().canViewPlayersBadge()) {
				return true;
			}

			TregminePlayer target;
			try {
				target = tregmine.getPlayerOffline(UUIDFetcher.getUUIDOf(args[0]));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				target = null;
			}

			if (target == null) {
				player.sendStringMessage(ChatColor.RED + "Could not find player: " + ChatColor.YELLOW + args[0]);
				return true;
			}
			Map<Badge, Integer> badges = target.getBadges();
			if (badges.isEmpty()) {

				player.getSpigot().sendMessage(target.getChatName(),
						new TextComponent(ChatColor.AQUA + " currently has no badges!"));
				return true;
			}

			for (Map.Entry<Badge, Integer> badge : badges.entrySet()) {
				player.sendStringMessage(ChatColor.AQUA + badge.getKey().name() + " - Level " + badge.getValue());
			}

		} else if (args[0].equalsIgnoreCase("give") && args.length == 2) {
			if (!player.getRank().canGiveBadges()) {
				return true;
			}
			TregminePlayer target;
			try {
				target = tregmine.getPlayerOffline(UUIDFetcher.getUUIDOf(args[1]));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				target = null;
			}
			if (target == null) {
				player.sendStringMessage(ChatColor.RED + "Could not find player: " + ChatColor.YELLOW + args[0]);
				return true;
			}

		}
		return true;
	}
}
