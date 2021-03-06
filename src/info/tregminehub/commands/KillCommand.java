package info.tregminehub.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import info.tregminehub.Tregmine;
import info.tregminehub.api.TregminePlayer;

public class KillCommand extends AbstractCommand {
	Tregmine t;

	public KillCommand(Tregmine instance) {
		super(instance, "kill");
		t = instance;
	}

	@Override
	public boolean handlePlayer(TregminePlayer player, String[] args) {
		if (!player.getIsAdmin()) {
			player.sendStringMessage(ChatColor.RED + "You do not have permission to use that command!");
			return true;
		}
		if (args.length != 1) {
			player.sendStringMessage(ChatColor.RED + "Invalid arguments - Use /kill player");
			return true;
		}
		if (!player.isOp()) {
			player.sendStringMessage(ChatColor.RED + "You don't have permission to kill people!");
		}
		List<TregminePlayer> candidates = tregmine.matchPlayer(args[0]);
		if (candidates.size() != 1) {
			player.sendStringMessage(ChatColor.RED + "That player does not exist!");
		}
		TregminePlayer victim = candidates.get(0);
		if (victim.getGameMode() == GameMode.CREATIVE) {
			player.sendStringMessage(ChatColor.RED + "Cannot kill someone in creative!");
			return true;
		}
		player.sendStringMessage(ChatColor.RED + "Killing " + victim.getName() + ChatColor.RED + "...");
		player.addPotionEffect(new PotionEffect(PotionEffectType.HARM, 1, 10000));
		victim.setDeathCause("adminkilled");
		return true;
	}
}
