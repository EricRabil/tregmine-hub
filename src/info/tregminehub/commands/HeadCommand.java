package info.tregminehub.commands;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.SkullMeta;

import info.tregminehub.Tregmine;
import info.tregminehub.api.TregminePlayer;

public class HeadCommand extends AbstractCommand {
	public HeadCommand(Tregmine tregmine) {
		super(tregmine, "head");
	}

	@Override
	public boolean handlePlayer(TregminePlayer player, String args[]) {
		if (player.getRank().canGetPlayerHead()) {
			if (args.length == 1) {
				ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (byte) 3);
				SkullMeta itemMeta = (SkullMeta) item.getItemMeta();
				itemMeta.setOwner(args[0]);
				itemMeta.setDisplayName(ChatColor.YELLOW + args[0] + "'s head");
				item.setItemMeta(itemMeta);
				PlayerInventory inventory = player.getInventory();
				inventory.addItem(item);
				player.sendStringMessage(ChatColor.YELLOW + "You received the head of " + args[0]);
			} else {
				player.sendStringMessage(ChatColor.RED + "Type /head <player>");
			}
		} else {
			player.sendStringMessage(ChatColor.RED + "Only admins can use this command!");
		}
		return true;
	}
}
