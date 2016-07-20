package info.tregminehub.commands;

import static org.bukkit.ChatColor.DARK_AQUA;
import static org.bukkit.ChatColor.YELLOW;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import info.tregminehub.Tregmine;
import info.tregminehub.api.TregminePlayer;

public class GiveCommand extends AbstractCommand {
	public GiveCommand(Tregmine tregmine) {
		super(tregmine, "give");
	}

	@Override
	public boolean handlePlayer(TregminePlayer player, String[] args) {
		if (player.isInVanillaWorld()) {
			player.setFireTicks(30);
			player.sendStringMessage(ChatColor.RED + "You cannot use that command in this world!");
			return true;
		}
		if (args.length == 0) {
			return false;
		}
		if (!player.getRank().canSpawnItems()) {
			return true;
		}

		String pattern = args[0];
		String param = args[1].toUpperCase();

		List<TregminePlayer> candidates = tregmine.matchPlayer(pattern);
		if (candidates.size() != 1) {
			// TODO: error message
			return true;
		}

		TregminePlayer target = candidates.get(0);
		if (target.getWorld().getName().equalsIgnoreCase("vanilla") || player.isInVanillaWorld()) {
			player.sendStringMessage(ChatColor.RED + "That player is in the vanilla world!");
			return true;
		}
		int materialId;
		try {
			materialId = Integer.parseInt(param);
		} catch (NumberFormatException e) {
			try {
				Material material = Material.getMaterial(param);
				materialId = material.getId();
			} catch (NullPointerException ne) {
				player.sendStringMessage(DARK_AQUA + "/item <id|name> <amount> <data>.");
				return true;
			}
		}

		int amount;
		try {
			amount = Integer.parseInt(args[2]);
		} catch (ArrayIndexOutOfBoundsException e) {
			amount = 1;
		} catch (NumberFormatException e) {
			amount = 1;
		}

		int data;
		try {
			data = Integer.parseInt(args[3]);
		} catch (ArrayIndexOutOfBoundsException e) {
			data = 0;
		} catch (NumberFormatException e) {
			data = 0;
		}

		ItemStack item = new ItemStack(materialId, amount, (byte) data);
		if (item.getType() == Material.MONSTER_EGG || item.getType() == Material.NAME_TAG) {
			return false;
		}

		PlayerInventory inv = target.getInventory();
		if (inv == null) {
			return true;
		}

		inv.addItem(item);

		Material material = Material.getMaterial(materialId);
		String materialName = material.toString();

		player.sendStringMessage("You gave " + amount + " of " + DARK_AQUA + materialName.toLowerCase() + " to "
				+ target.getName() + ".");
		target.sendStringMessage(YELLOW + "You were gifted by the gods. Look in your " + "inventory!");
		LOGGER.info(player.getName() + " SPAWNED " + amount + ":" + materialName + "=>" + target.getName());

		return true;
	}
}
