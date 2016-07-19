package info.tregmine.listeners;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;

import info.tregmine.Tregmine;
import info.tregmine.api.TregminePlayer;

public class ChunkListener implements Listener {
	private Tregmine plugin;

	public ChunkListener(Tregmine instance) {
		this.plugin = instance;
	}

	@EventHandler
	public void onLoadChunk(ChunkLoadEvent event) {
		Chunk loadedChunk = event.getChunk();
		List<TregminePlayer> playersInChunk = new ArrayList<TregminePlayer>();

		for (Entity entity : loadedChunk.getEntities()) {
			if (entity instanceof Player) {
				TregminePlayer playerInChunk = plugin.getPlayer((Player) entity);
				playersInChunk.add(playerInChunk);
			}
		}

		for (TregminePlayer player : playersInChunk) {
			if (!player.hasFlag(TregminePlayer.Flags.WATCHING_CHUNKS)) {
				continue;
			}

			// If previous chunk was new, and this one is new - Don't spam them.
			if (player.getNewChunk() && event.isNewChunk()) {
				continue;
			}

			// If previous chunk was old, and this one is old - Don't spam them.
			if (!player.getNewChunk() && !event.isNewChunk()) {
				continue;
			}

			// If previous chunk was new, and this one is old
			if (player.getNewChunk() && !event.isNewChunk()) {
				player.sendStringMessage(ChatColor.RED + "You just loaded an already existing chunk!");
				player.setNewChunk(false);
				continue;
			}

			// If previou chunk was old, and this one is new
			if (!player.getNewChunk() && event.isNewChunk()) {
				player.sendStringMessage(ChatColor.GREEN + "You just loaded a new chunk!");
				player.setNewChunk(true);
				continue;
			}
		}
	}
}
