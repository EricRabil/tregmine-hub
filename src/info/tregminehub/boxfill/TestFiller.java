package info.tregminehub.boxfill;

import org.bukkit.block.Block;
import org.bukkit.material.MaterialData;

import info.tregminehub.Tregmine;
import info.tregminehub.api.TregminePlayer;

public class TestFiller extends AbstractFiller {
	private TregminePlayer player;
	private MaterialData item;

	public TestFiller(Tregmine plugin, TregminePlayer player, Block block1, Block block2, MaterialData item,
			int workSize) {
		super(plugin, block1, block2, workSize);
		this.player = player;
		this.item = item;
	}

	@Override
	public void changeBlock(Block block) {
		player.getDelegate().sendBlockChange(block.getLocation(), item.getItemType(), item.getData());
	}
}
