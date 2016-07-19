package info.tregmine.commands;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;

import info.tregmine.Tregmine;
import info.tregmine.api.TregminePlayer;

public class BlockHereCommand extends AbstractCommand {
	public BlockHereCommand(Tregmine tregmine) {
		super(tregmine, "blockhere");
	}

	@Override
	public boolean handlePlayer(TregminePlayer player, String[] args) {
		if (player.getWorld() == player.getPlugin().getVanillaWorld()
				|| player.getWorld() == player.getPlugin().getVanillaNether()
				|| player.getWorld() == player.getPlugin().getVanillaEnd()) {
			player.sendStringMessage(ChatColor.RED + "You cannot use that command in this world!");
			return true;
		}
		if (!player.getRank().canFill()) {
			return true;
		}

		Block block = player.getWorld().getBlockAt(player.getLocation());
		block.setType(Material.DIRT);

		return true;
	}
}
