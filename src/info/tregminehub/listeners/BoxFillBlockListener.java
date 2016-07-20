package info.tregminehub.listeners;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import info.tregminehub.Tregmine;
import info.tregminehub.api.TregminePlayer;

public class BoxFillBlockListener implements Listener {
	private Tregmine plugin;

	public BoxFillBlockListener(Tregmine tregmine) {
		this.plugin = tregmine;
	}

	@EventHandler
	public void onBlockDamage(BlockDamageEvent event) {

		TregminePlayer player = plugin.getPlayer(event.getPlayer());
		if (player.getGameMode() == GameMode.CREATIVE) {
			return;
		}
		if (!player.getRank().canFill()) {
			return;
		}

		if (player.getItemInHand().getType() != Material.WOOD_SPADE) {
			return;
		} else {
			event.setCancelled(true);
		}
		Block block = event.getBlock();

		player.setFillBlock1(block);
		event.getPlayer().sendMessage("First block set");
		player.setFillBlockCounter(1);
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		TregminePlayer player = plugin.getPlayer(event.getPlayer());
		if (!player.getRank().canFill()) {
			return;
		}

		if (player.getItemInHand().getType() != Material.WOOD_SPADE) {
			return;
		} else {
			event.setCancelled(true);
		}
		Block block = event.getBlock();
		player.setFillBlock1(block);
		event.getPlayer().sendMessage("First block set");
		player.setFillBlockCounter(1);
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
			return;
		}

		TregminePlayer player = plugin.getPlayer(event.getPlayer());
		if (!player.getRank().canFill()) {
			return;
		}

		if (player.getItemInHand().getType() != Material.WOOD_SPADE) {
			return;
		}

		Block block = event.getClickedBlock();
		player.setFillBlock2(block);
		event.getPlayer().sendMessage("Second block set");
		player.setFillBlockCounter(0);
	}
}
