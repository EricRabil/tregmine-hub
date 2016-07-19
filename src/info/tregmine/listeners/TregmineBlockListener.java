package info.tregmine.listeners;

import java.util.EnumSet;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;

import info.tregmine.Tregmine;
import info.tregmine.api.TregminePlayer;
import info.tregmine.database.DAOException;
import info.tregmine.database.IBlockDAO;
import info.tregmine.database.IContext;
import info.tregmine.database.ILogDAO;
import info.tregmine.database.IWalletDAO;

public class TregmineBlockListener implements Listener {
	private Set<Material> loggedMaterials = EnumSet.of(Material.DIAMOND_ORE, Material.EMERALD_ORE, Material.GOLD_ORE,
			Material.LAPIS_ORE, Material.QUARTZ_ORE, Material.REDSTONE_ORE, Material.MOB_SPAWNER);

	private Tregmine plugin;

	public TregmineBlockListener(Tregmine instance) {
		this.plugin = instance;
	}

	@EventHandler
	public void onBlockFromTo(BlockFromToEvent event) {
		if (event.getToBlock().getType() == Material.NETHER_WART_BLOCK) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {

		TregminePlayer player = plugin.getPlayer(event.getPlayer());
		Block block = event.getBlock();
		Material material = block.getType();

		if (loggedMaterials.contains(material)) {
			try (IContext ctx = plugin.createContext()) {
				ILogDAO logDAO = ctx.getLogDAO();
				logDAO.insertOreLog(player, block.getLocation(), material.getId());
			} catch (DAOException e) {
				throw new RuntimeException(e);
			}
		}

		try (IContext ctx = plugin.createContext()) {
			IBlockDAO blockDAO = ctx.getBlockDAO();
			int blockvalue = plugin.getMinedPrice(block.getType());
			if (!blockDAO.isPlaced(block) && !event.isCancelled() && blockvalue != 0) {
				if (block.getType() == Material.LEAVES || block.getType() == Material.LEAVES_2) {
					if (player.getItemInHand().getType() != Material.SHEARS) {
						return;
					}
				}
				try (IContext ctxNew = plugin.createContext()) {
					IWalletDAO walletDAO = ctx.getWalletDAO();
					walletDAO.add(player, blockvalue);
				} catch (DAOException e) {
					e.printStackTrace();
				}
			}
		} catch (DAOException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {

		}
	}

	@EventHandler
	public void onBlockBurn(BlockBurnEvent event) {
		event.setCancelled(true);
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		TregminePlayer player = plugin.getPlayer(event.getPlayer());
		if (event.getBlock().getType().equals(Material.LAVA)) {
			if (!player.getRank().canPlaceBannedBlocks()) {
				event.setCancelled(true);
				return;
			}
		}
	}

	@EventHandler
	public void onBlockSpread(BlockSpreadEvent event) {
		Material sourceblock = event.getSource().getType();
		if (sourceblock == Material.FIRE) {
			event.setCancelled(true);
		}
	}
}
