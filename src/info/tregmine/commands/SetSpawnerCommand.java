package info.tregmine.commands;

import static org.bukkit.ChatColor.RED;

import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;

import info.tregmine.Tregmine;
import info.tregmine.api.TregminePlayer;

public class SetSpawnerCommand extends AbstractCommand {
	public SetSpawnerCommand(Tregmine tregmine) {
		super(tregmine, "setspawner");
	}

	@Override
	public boolean handlePlayer(TregminePlayer player, String[] args) {
		if (player.getWorld().getName().equalsIgnoreCase("vanilla") || player.isInVanillaWorld()) {
			player.sendStringMessage(ChatColor.RED + "You cannot use that command in this world!");
			return true;
		}
		if (!player.getRank().canSetSpawners()) {
			return true;
		}

		if (args.length != 1) {
			player.sendStringMessage(RED + "Type /spawner <mobname> whilst pointing " + "at a spawner");
			return false;
		}

		Block target = player.getDelegate().getTargetBlock((Set<Material>) null, 15);
		if (!target.getType().equals(Material.MOB_SPAWNER)) {
			player.sendStringMessage(RED + "Please point at a spawner.");
			return false;
		}

		CreatureSpawner spawner = (CreatureSpawner) target.getState();
		try {
			spawner.setSpawnedType(EntityType.valueOf(args[0].toUpperCase()));
		} catch (Exception error) {
			player.sendStringMessage(RED + "An error occured. Did you specify a valid " + "mob type?");
		}

		return true;
	}
}
