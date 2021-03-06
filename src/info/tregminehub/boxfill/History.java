package info.tregminehub.boxfill;

import java.util.HashMap;
import java.util.Map;

import info.tregminehub.api.TregminePlayer;

public class History {
	private Map<TregminePlayer, SavedBlocks> currentState;

	public History() {
		currentState = new HashMap<TregminePlayer, SavedBlocks>();
	}

	public SavedBlocks get(TregminePlayer player) {
		return currentState.get(player);
	}

	public void set(TregminePlayer player, SavedBlocks blocks) {
		currentState.put(player, blocks);
	}
}
