package info.tregmine.listeners;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import info.tregmine.Tregmine;
import info.tregmine.api.lore.Created;

public class CraftListener implements Listener {
	public CraftListener(Tregmine instance) {

	}

	public void echo(String v) {
		System.out.println(v);
	}

	@EventHandler
	public void onCraft(PrepareItemCraftEvent event) {
		List<ItemStack> items = new ArrayList<ItemStack>();
		for (ItemStack a : event.getInventory().getContents()) {
			if (a.getType() != Material.AIR) {
				items.add(a);
			}
		}
		if (items.size() == 0) {
			return;
		}
		ItemStack[] stack = event.getInventory().getMatrix();

		for (ItemStack onestack : stack) {
			if (onestack.getType() != Material.AIR && onestack.hasItemMeta()) {
				ItemMeta meta = onestack.getItemMeta();
				List<String> lore = meta.getLore();
				if (lore.contains(Created.CREATIVE.toColorString()) || lore.get(0).contains("CREATIVE")) {
					ItemStack result = event.getInventory().getResult();
					if (result != null) {
						ItemMeta resultmeta = result.getItemMeta();
						List<String> resultlore = new ArrayList<String>();
						resultlore.add(Created.CREATIVE.toColorString());
						String line2 = lore.get(1).replace("Â", "");
						String line3 = lore.get(2).replace("Â", "");
						resultlore.add(line2);
						resultlore.add(line3);
						resultmeta.setLore(resultlore);
						result.setItemMeta(resultmeta);
						event.getInventory().setResult(result);
						// Â
					}
				}
				if (lore.contains(Created.SPAWNED.toColorString()) || lore.get(0).contains("SPAWNED")) {
					ItemStack result = event.getInventory().getResult();
					if (result != null) {
						ItemMeta resultmeta = result.getItemMeta();
						List<String> resultlore = new ArrayList<String>();
						resultlore.add(Created.SPAWNED.toColorString());
						String line2 = lore.get(1).replace("Â", "");
						String line3 = lore.get(2).replace("Â", "");
						resultlore.add(line2);
						resultlore.add(line3);
						resultmeta.setLore(resultlore);
						result.setItemMeta(resultmeta);
						event.getInventory().setResult(result);
						// Â
					}
				}
			}
		}
	}
}
