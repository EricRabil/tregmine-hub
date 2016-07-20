package info.tregminehub.commands;

import info.tregminehub.Tregmine;
import info.tregminehub.api.TregminePlayer;

public class AfkCommand extends AbstractCommand {

	public AfkCommand(Tregmine tregmine) {
		super(tregmine, "afk");
	}

	@Override
	public boolean handlePlayer(TregminePlayer player, String[] args) {
		if (player.isAfk()) {
			player.setAfk(false);
		} else {
			player.setAfk(true);
		}
		return true;
	}

}
