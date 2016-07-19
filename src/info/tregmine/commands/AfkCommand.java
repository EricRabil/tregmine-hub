package info.tregmine.commands;

import info.tregmine.Tregmine;
import info.tregmine.api.TregminePlayer;

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
