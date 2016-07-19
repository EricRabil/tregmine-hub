package info.tregmine.commands;

import static org.bukkit.ChatColor.RED;
import static org.bukkit.ChatColor.YELLOW;

import info.tregmine.Tregmine;
import info.tregmine.api.TregminePlayer;
import info.tregmine.database.DAOException;
import info.tregmine.database.IContext;
import info.tregmine.database.IPlayerDAO;

public class KeywordCommand extends AbstractCommand {
	public KeywordCommand(Tregmine tregmine) {
		super(tregmine, "keyword");
	}

	@Override
	public boolean handlePlayer(TregminePlayer player, String[] args) {
		if (args.length != 1) {
			return false;
		}

		String keyword = args[0];

		if (keyword.length() < 1) {
			player.sendStringMessage(RED + "Your keyword must be at least " + "1 characters long.");
			return true;
		}

		player.setKeyword(keyword.toLowerCase());
		player.sendStringMessage(
				YELLOW + "From now on you can only log in by using ip " + keyword.toLowerCase() + ".mc.tregmine.com");

		try (IContext ctx = tregmine.createContext()) {
			IPlayerDAO playerDAO = ctx.getPlayerDAO();
			playerDAO.updatePlayerKeyword(player);
		} catch (DAOException e) {
			throw new RuntimeException(e);
		}

		return true;
	}
}
