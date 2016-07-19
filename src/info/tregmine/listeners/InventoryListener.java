package info.tregmine.listeners;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.block.DoubleChest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import info.tregmine.Tregmine;
import info.tregmine.api.InventoryAccess;
import info.tregmine.api.TregminePlayer;
import info.tregmine.api.lore.Created;
import info.tregmine.database.DAOException;
import info.tregmine.database.IContext;
import info.tregmine.database.IInventoryDAO;
import info.tregmine.database.IInventoryDAO.ChangeType;
import info.tregmine.database.IInventoryDAO.InventoryType;
import net.md_5.bungee.api.chat.TextComponent;

public class InventoryListener implements Listener {
	private Tregmine plugin;
	private Map<Location, ItemStack[]> openInventories;

	public InventoryListener(Tregmine instance) {
		this.plugin = instance;
		this.openInventories = new HashMap<>();
	}

	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event) {
		if (!(event.getPlayer() instanceof Player)) {
			return;
		}

		TregminePlayer player = plugin.getPlayer((Player) event.getPlayer());

		Inventory inv = event.getInventory();
		for (ItemStack item : player.getInventory().getContents()) {
			if (item != null) {
				ItemMeta meta = item.getItemMeta();
				if (meta.hasLore()) {
					List<String> lore = meta.getLore();
					List<String> newlore = new ArrayList<String>();
					for (String a : lore) {
						newlore.add(a.replace("�", ""));
					}
					meta.setLore(newlore);
					item.setItemMeta(meta);
				}
			}
		}
		if (player.getGameMode() == GameMode.CREATIVE) {
			for (ItemStack item : player.getInventory().getContents()) {
				if (item != null) {
					ItemMeta meta = item.getItemMeta();
					List<String> lore = new ArrayList<String>();
					lore.add(Created.CREATIVE.toColorString());
					TregminePlayer p = this.plugin.getPlayer(player.getName());
					lore.add(ChatColor.WHITE + "by: " + p.getChatNameNoHover());
					lore.add(ChatColor.WHITE + "Value: " + ChatColor.MAGIC + "0000" + ChatColor.RESET + ChatColor.WHITE
							+ " Treg");
					meta.setLore(lore);
					item.setItemMeta(meta);
				}
			}
		}
		InventoryHolder holder = inv.getHolder();
		Location loc = null;
		if (holder instanceof BlockState) {

			BlockState block = (BlockState) holder;
			loc = block.getLocation();
		} else if (holder instanceof DoubleChest) {

			DoubleChest block = (DoubleChest) holder;
			loc = block.getLocation();
		} else {
			return;
		}

		if (!openInventories.containsKey(loc)) {
			return;
		}

		try (IContext ctx = plugin.createContext()) {
			IInventoryDAO invDAO = ctx.getInventoryDAO();

			// Find inventory id, or create a new row if none exists
			int id = invDAO.getInventoryId(loc);
			if (id == -1) {
				return;
			}
			ItemStack[] oldContents = openInventories.get(loc);
			ItemStack[] currentContents = inv.getContents();

			ItemStack[] oc = openInventories.get(loc);
			ItemStack[] cc = inv.getContents();
			// Store all changes
			for (int i = 0; i < oldContents.length; i++) {
				ItemStack a = oc[i];
				ItemStack b = cc[i];

				if (a == null && b == null) {
					continue;
				}

				if (a == null || b == null || !a.equals(b)) {
					// Removed
					if (a != null) {
						invDAO.insertChangeLog(player, id, i, a, ChangeType.REMOVE);
					}

					// Added
					if (b != null) {
						invDAO.insertChangeLog(player, id, i, b, ChangeType.ADD);
					}
				}
			}
			// Store contents
			try {
				invDAO.insertStacks(id, currentContents);
			} catch (DAOException e) {
				throw new RuntimeException(e);
			}

		} catch (DAOException e) {
			throw new RuntimeException(e);
		}

		openInventories.remove(loc);
	}

	@EventHandler
	public void onInventoryOpen(InventoryOpenEvent event) {
		if (!(event.getPlayer() instanceof Player)) {
			return;
		}
		TregminePlayer player = plugin.getPlayer((Player) event.getPlayer());
		if (player.getGameMode() == GameMode.CREATIVE
				&& (player.getWorld().getName().equalsIgnoreCase("vanilla") || player.isInVanillaWorld())) {
			event.setCancelled(true);
			player.sendStringMessage(
					ChatColor.RED + "You cannot be in creative in this world! Your gamemode has been set to survival.");
			return;
		}
		Inventory inv = event.getInventory();
		InventoryHolder holder = inv.getHolder();
		Location loc = null;
		if (holder instanceof BlockState) {
			BlockState block = (BlockState) holder;
			loc = block.getLocation();
		} else if (holder instanceof DoubleChest) {
			DoubleChest block = (DoubleChest) holder;
			loc = block.getLocation();
		} else {
			return;
		}

		ItemStack[] contents = inv.getContents();
		ItemStack[] copy = new ItemStack[contents.length];
		for (int i = 0; i < contents.length; i++) {
			if (contents[i] != null) {
				copy[i] = contents[i].clone();
			}
		}

		openInventories.put(loc, contents);

		try (IContext ctx = plugin.createContext()) {
			IInventoryDAO invDAO = ctx.getInventoryDAO();

			// Find inventory id, or create a new row if none exists
			int id = invDAO.getInventoryId(loc);
			if (id == -1) {
				id = invDAO.insertInventory(player, loc, InventoryType.BLOCK);
			} else {
				List<InventoryAccess> accessLog = invDAO.getAccessLog(id, 10);
				int others = 0;
				for (InventoryAccess access : accessLog) {
					if (access.getPlayerId() != player.getId()) {
						others++;
					}
				}

				if (others > 0 && player.hasFlag(TregminePlayer.Flags.CHEST_LOG)) {
					player.sendStringMessage(ChatColor.YELLOW + "Last accessed by:");
					SimpleDateFormat dfm = new SimpleDateFormat("dd/MM/yy hh:mm:ss a");
					int i = 0;
					for (InventoryAccess access : accessLog) {
						if (access.getPlayerId() != player.getId()) {
							if (i > 2) {
								break;
							}
							TregminePlayer p = plugin.getPlayerOffline(access.getPlayerId());
							TextComponent message = new TextComponent(
									ChatColor.YELLOW + " on " + dfm.format(access.getTimestamp()) + ".");
							player.sendSpigotMessage(p.getChatName(), message);
							i++;
						}
					}
				}
			}

			// Insert into access log
			invDAO.insertAccessLog(player, id);
		} catch (DAOException e) {
			throw new RuntimeException(e);
		}
	}
}
