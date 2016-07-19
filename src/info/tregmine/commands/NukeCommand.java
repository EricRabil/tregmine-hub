package info.tregmine.commands;

import static org.bukkit.ChatColor.YELLOW;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Animals;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;

import info.tregmine.Tregmine;
import info.tregmine.api.TregminePlayer;
import info.tregmine.api.math.MathUtil;

public class NukeCommand extends AbstractCommand {
	public NukeCommand(Tregmine tregmine) {
		super(tregmine, "nuke");
	}

	@Override
	public boolean handlePlayer(TregminePlayer player, String[] args) {
		if (player.getWorld().getName().equalsIgnoreCase("vanilla") || player.isInVanillaWorld()) {
			player.sendStringMessage(ChatColor.RED + "You cannot use that command in this world!");
			return true;
		}
		if (!player.getRank().canNuke()) {
			return true;
		}

		int distance;
		try {
			distance = Integer.parseInt(args[0]);
		} catch (NumberFormatException e) {
			distance = 160;
		} catch (ArrayIndexOutOfBoundsException e) {
			distance = 160;
		}

		player.sendStringMessage(YELLOW + "You nuked all mobs within " + distance + " meters");
		player.sendStringMessage(YELLOW + "say /nuke <number> to select a larger or smaller distance");

		Location loc = player.getLocation();
		for (Entity ent : player.getWorld().getLivingEntities()) {
			if (MathUtil.calcDistance2d(loc, ent.getLocation()) > distance) {
				continue;
			}

			if (ent instanceof Monster) {
				Monster mob = (Monster) ent;
				mob.remove();
			} else if (ent instanceof Animals) {
				Animals animal = (Animals) ent;
				animal.remove();
			} else if (ent instanceof Slime) {
				Slime slime = (Slime) ent;
				slime.remove();
			} else if (ent instanceof EnderDragon) {
				EnderDragon dragon = (EnderDragon) ent;
				dragon.remove();
			} else if (ent instanceof LivingEntity && !(ent instanceof Player)) {
				ent.remove();
			}
		}

		return true;
	}
}
