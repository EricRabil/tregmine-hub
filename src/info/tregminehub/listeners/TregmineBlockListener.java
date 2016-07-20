package info.tregminehub.listeners;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;

import info.tregminehub.Tregmine;
import info.tregminehub.api.TregminePlayer;

public class TregmineBlockListener implements Listener {

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
		if(!player.hasBlockPermission()){
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onBlockBurn(BlockBurnEvent event) {
		event.setCancelled(true);
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		TregminePlayer player = plugin.getPlayer(event.getPlayer());
		if(!player.hasBlockPermission()){
			event.setCancelled(true);
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
