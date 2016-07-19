package info.tregmine.tools;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.inventory.meta.ItemMeta;

import info.tregmine.Tregmine;
import info.tregmine.api.TregminePlayer;
import info.tregmine.commands.AbstractCommand;
import info.tregmine.database.DAOException;
import info.tregmine.database.IContext;
import info.tregmine.database.IWalletDAO;

public class ToolRepairCommand extends AbstractCommand {
	public ToolRepairCommand(Tregmine tregmine) {
		super(tregmine, "repair");
	}

	@Override
	public boolean handlePlayer(TregminePlayer player, String args[]) {
		if (player.getWorld().getName().equalsIgnoreCase("vanilla") || player.isInVanillaWorld()) {
			player.sendStringMessage(ChatColor.RED + "You cannot use that command in this world!");
			return true;
		}
		try (IContext dbCtx = tregmine.createContext()) {
			if (player.getItemInHand().getItemMeta().getLore() != null) {
				List<String> lore = player.getItemInHand().getItemMeta().getLore();

				if (!lore.get(1).equalsIgnoreCase("0/1000")) {
					player.sendStringMessage(ChatColor.AQUA + "Must have 0 durability to repair!");
					return true;
				}

				try (IContext dbCtx1 = tregmine.createContext()) {
					IWalletDAO walletDAO = dbCtx1.getWalletDAO();

					if (walletDAO.take(player, 10000)) {
						lore.remove(1);
						lore.add("1000/1000");

						ItemMeta meta = player.getItemInHand().getItemMeta();
						meta.setLore(lore);
						player.getItemInHand().setItemMeta(meta);

						player.sendStringMessage(ChatColor.AQUA + "Tool has been repaired!");
						return true;
					} else {
						player.sendStringMessage(ChatColor.RED + "Can not afford 10,000 tregs to repair this tool!");
						return true;
					}
				} catch (DAOException e) {
					throw new RuntimeException(e);
				}
			} else {
				player.sendStringMessage(ChatColor.RED + "You can't repair that!");
				return true;
			}
		} catch (DAOException e) {
			player.sendStringMessage(ChatColor.RED + "Something bad happened! D:");
			return true;
		}
	}
}
