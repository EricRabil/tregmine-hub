package info.tregminehub.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldSaveEvent;

import info.tregminehub.Tregmine;

public class MiscListener implements Listener {
	private Tregmine plugin;

	public MiscListener(Tregmine instance) {
		this.plugin = instance;
	}

	@EventHandler
	public void autoSave(WorldSaveEvent event) {
		if ("world".equals(event.getWorld().getName())) {
			String n = plugin.getConfig().getString("general.servername");
			Bukkit.broadcastMessage(
					ChatColor.GOLD + n + ChatColor.DARK_RED + " is saving, You may experience some slowness.");
		}
	}

	/*
	 * @EventHandler public void onUnloadChunk(ChunkUnloadEvent event) { Chunk
	 * chunk = event.getChunk(); Tregmine.LOGGER.info(String.format(
	 * "Unloading chunk %d, %d", chunk.getX(), chunk.getZ())); }
	 */
}
