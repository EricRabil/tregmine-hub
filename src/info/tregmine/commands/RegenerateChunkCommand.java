package info.tregmine.commands;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Block;

import info.tregmine.Tregmine;
import info.tregmine.api.Rank;
import info.tregmine.api.TregminePlayer;

public class RegenerateChunkCommand extends AbstractCommand {
	public RegenerateChunkCommand(Tregmine tregmine) {
		super(tregmine, "regeneratechunk");
	}

	@Override
	public boolean handlePlayer(TregminePlayer player, String[] args) {
		if (player.getRank() != Rank.SENIOR_ADMIN) {
			return false;
		}
		if (player.getFillBlock1() == null) {
			player.sendStringMessage(ChatColor.RED + "You haven't made a selection! [Wand is the wooden shovel]");
			return true;
		}

		World world = player.getWorld();
		Block b1 = player.getFillBlock1();
		Chunk chunk = b1.getChunk();
		world.regenerateChunk(chunk.getX(), chunk.getZ());

		return true;
	}
}
