package info.tregmine.commands;

import static org.bukkit.ChatColor.RED;
import static org.bukkit.ChatColor.YELLOW;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

import info.tregmine.Tregmine;
import info.tregmine.api.TregminePlayer;

public class CreateMobCommand extends AbstractCommand {
	public CreateMobCommand(Tregmine tregmine) {
		super(tregmine, "createmob");
	}

	@Override
	public boolean handlePlayer(TregminePlayer player, String[] args) {
		if (player.getWorld().getName().equalsIgnoreCase("vanilla") || player.isInVanillaWorld()) {
			player.sendStringMessage(ChatColor.RED + "You cannot use that command in this world!");
			return true;
		}
		if (!player.getRank().canSpawnMobs()) {
			return true;
		}

		EntityType mobType;
		try {
			String mobName = args[0];
			mobType = EntityType.fromName(mobName);
		} catch (Exception e) {
			player.sendStringMessage(RED + "Sorry, that mob doesn't exist.");
			return true;
		}

		int amount = 1;
		try {
			amount = Integer.parseInt(args[1]);
		} catch (NumberFormatException e) {
			amount = 1;
		} catch (ArrayIndexOutOfBoundsException e) {
			amount = 1;
		}

		if (mobType == null) {
			StringBuilder buf = new StringBuilder();

			String delim = "";
			for (EntityType mob : EntityType.values()) {
				if (mob.isSpawnable() && mob.isAlive()) {
					buf.append(delim);
					buf.append(mob.getName());
					delim = ", ";
				}
			}

			player.sendStringMessage("Valid names are: ");
			player.sendStringMessage(buf.toString());

			return true;
		}

		World world = player.getWorld();
		Location loc = player.getLocation();
		for (int i = 0; i < amount; i++) {

			if (!mobType.isSpawnable()) {
				continue;
			}
			if (!mobType.isAlive()) {
				continue;
			}

			LivingEntity ent = (LivingEntity) world.spawnEntity(loc, mobType);
			if (args.length == 3) {
				ent.setCustomName(args[2]);
				ent.setCustomNameVisible(true);
			}
		}

		player.sendStringMessage(YELLOW + "You created " + amount + " " + mobType.getName() + ".");
		LOGGER.info(player.getName() + " created " + amount + " " + mobType.getName());

		return true;
	}
}
