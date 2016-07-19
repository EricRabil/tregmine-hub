package info.tregmine.commands;

import static org.bukkit.ChatColor.AQUA;
import static org.bukkit.ChatColor.RED;

import java.util.List;

import org.bukkit.ChatColor;

import info.tregmine.Tregmine;
import info.tregmine.api.TregminePlayer;
import net.md_5.bungee.api.chat.TextComponent;

public class SummonCommand extends AbstractCommand {
	public SummonCommand(Tregmine tregmine) {
		super(tregmine, "summon");
	}

	@Override
	public boolean handlePlayer(TregminePlayer player, String[] args) {
		if (player.getWorld().getName() == "vanilla") {
			player.sendStringMessage(ChatColor.RED + "You cannot use that command in this world!");
			return true;
		}
		if (args.length == 0) {
			return false;
		}

		String pattern = args[0];

		List<TregminePlayer> candidates = tregmine.matchPlayer(pattern);
		if (candidates.size() != 1) {
			player.sendStringMessage(RED + "Can't find user.");
		}

		TregminePlayer victim = candidates.get(0);
		if (victim.getWorld().getName() == "vanilla") {
			player.sendStringMessage(ChatColor.RED + "That player is in the vanilla world!");
			return true;
		}
		victim.setLastPos(victim.getLocation());

		// Mentors can summon their students, but nobody else. In those cases,
		// you need the canSummon-permission.
		if (victim != player.getStudent() && !player.getRank().canSummon()) {
			return true;
		}

		victim.setNoDamageTicks(200);

		victim.teleportWithHorse(player.getLocation());

		victim.sendSpigotMessage(player.decideVS(victim), new TextComponent(AQUA + " summoned you."));
		player.sendSpigotMessage(new TextComponent(AQUA + "You summoned "), victim.decideVS(player),
				new TextComponent(AQUA + " to yourself."));

		return true;
	}
}
