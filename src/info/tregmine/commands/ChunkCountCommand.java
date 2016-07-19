package info.tregmine.commands;

import org.bukkit.Server;
import org.bukkit.World;

import info.tregmine.Tregmine;
import info.tregmine.api.TregminePlayer;

public class ChunkCountCommand extends AbstractCommand {
	public ChunkCountCommand(Tregmine tregmine) {
		super(tregmine, "chunkcount");
	}

	@Override
	public boolean handleOther(Server server, String[] args) {
		for (World world : server.getWorlds()) {
			int chunks = world.getLoadedChunks().length;
			Tregmine.LOGGER.info(world.getName() + ": " + chunks + " chunks loaded, autosave: " + world.isAutoSave());
		}

		return true;
	}

	@Override
	public boolean handlePlayer(TregminePlayer player, String[] args) {
		if (!player.getRank().canGetChunkInfo()) {
			return true;
		}

		Server server = player.getServer();
		for (World world : server.getWorlds()) {
			int chunks = world.getLoadedChunks().length;
			player.sendStringMessage(
					world.getName() + ": " + chunks + " chunks loaded, autosave: " + world.isAutoSave());
		}

		return true;
	}
}
