package info.tregminehub.commands;

import static org.bukkit.ChatColor.BLUE;

import org.bukkit.ChatColor;

import info.tregminehub.Tregmine;
import info.tregminehub.api.StaffNews;
import info.tregminehub.api.TregminePlayer;
import info.tregminehub.database.DAOException;
import info.tregminehub.database.IContext;
import info.tregminehub.database.IStaffNewsDAO;

public class StaffNewsCommand extends AbstractCommand {
	public StaffNewsCommand(Tregmine tregmine) {
		super(tregmine, "staffnews");
	}

	private String argsToMessage(String[] args) {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < args.length; ++i) {
			buf.append(" ");
			buf.append(args[i]);
		}

		return buf.toString();
	}

	@Override
	public boolean handlePlayer(TregminePlayer player, String[] args) {
		if (!player.getRank().canUseStaffNews()) {
			this.insufficientPerms(player);
			return true;
		}
		if (args.length < 3) {
			player.sendStringMessage(ChatColor.RED + "Please provide three or more words.");
			return true;
		}
		String message = argsToMessage(args);
		LOGGER.info(player.getRealName() + " has added the following message to the staff news board: " + message);
		try (IContext ctx = tregmine.createContext()) {
			StaffNews news = new StaffNews();
			news.setText(message);
			news.setUsername(player.getRealName());
			IStaffNewsDAO newsDAO = ctx.getNewsByUploader();
			newsDAO.insertNews(news);
		} catch (DAOException e) {
			throw new RuntimeException(e);
		}
		player.sendStringMessage(BLUE + "Your message has been added to the news board.");
		return true;
	}

}