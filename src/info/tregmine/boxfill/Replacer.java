package info.tregmine.boxfill;

import org.bukkit.block.Block;
import org.bukkit.material.MaterialData;

import info.tregmine.Tregmine;
import info.tregmine.api.TregminePlayer;

public class Replacer extends AbstractFiller {
	private History history;
	private TregminePlayer player;
	private MaterialData match;
	private MaterialData item;
	private SavedBlocks currentJob;

	public Replacer(Tregmine plugin, History history, TregminePlayer player, Block block1, Block block2,
			MaterialData match, MaterialData item, int workSize) {
		super(plugin, block1, block2, workSize);

		this.history = history;
		this.player = player;
		this.match = match;
		this.item = item;
		this.currentJob = new SavedBlocks();
	}

	@Override
	public void changeBlock(Block block) {
		if (block.getType() == match.getItemType()
				&& (match.getItemType() == org.bukkit.Material.AIR || block.getType() == match.getItemType())) {

			currentJob.addBlock(block.getState());

			block.setType(item.getItemType());
			block.setData(item.getData());
		}
	}

	@Override
	public void finished() {
		history.set(player, currentJob);
	}
}
