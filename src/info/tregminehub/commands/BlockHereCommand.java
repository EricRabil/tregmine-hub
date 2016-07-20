package info.tregminehub.commands;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;

import info.tregminehub.Tregmine;
import info.tregminehub.api.TregminePlayer;

public class BlockHereCommand extends AbstractCommand {
	public BlockHereCommand(Tregmine tregmine) {
		super(tregmine, "blockhere");
	}

	@Override
	public boolean handlePlayer(TregminePlayer player, String[] args) {
		if (!player.getRank().canFill()) {
			return true;
		}

		Block block = player.getWorld().getBlockAt(player.getLocation());
		block.setType(Material.DIRT);

		return true;
	}
}
