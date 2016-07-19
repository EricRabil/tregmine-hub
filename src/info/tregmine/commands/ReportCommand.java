package info.tregmine.commands;

import static org.bukkit.ChatColor.DARK_AQUA;
import static org.bukkit.ChatColor.YELLOW;

import java.util.List;

import info.tregmine.Tregmine;
import info.tregmine.api.PlayerReport;
import info.tregmine.api.TregminePlayer;
import info.tregmine.database.DAOException;
import info.tregmine.database.IContext;
import info.tregmine.database.IPlayerReportDAO;

public class ReportCommand extends AbstractCommand {
	public ReportCommand(Tregmine tregmine) {
		super(tregmine, "report");
	}

	private String argsToMessage(String[] args) {
		StringBuffer buf = new StringBuffer();
		for (int i = 1; i < args.length; ++i) {
			buf.append(" ");
			buf.append(args[i]);
		}

		return buf.toString();
	}

	@Override
	public boolean handlePlayer(TregminePlayer player, String[] args) {
		if (!player.getRank().canReport()) {
			return true;
		}

		if (args.length < 2) {
			player.sendStringMessage(DARK_AQUA + "/report <player> <message>");
			return true;
		}

		String pattern = args[0];
		String message = argsToMessage(args);

		List<TregminePlayer> candidates = tregmine.matchPlayer(pattern);
		if (candidates.size() != 1) {
			// TODO: error message
			return true;
		}

		TregminePlayer victim = candidates.get(0);

		try (IContext ctx = tregmine.createContext()) {
			PlayerReport report = new PlayerReport();
			report.setSubjectId(victim.getId());
			report.setIssuerId(player.getId());
			report.setAction(PlayerReport.Action.COMMENT);
			report.setMessage(message);

			IPlayerReportDAO reportDAO = ctx.getPlayerReportDAO();
			reportDAO.insertReport(report);
		} catch (DAOException e) {
			throw new RuntimeException(e);
		}

		player.sendStringMessage(YELLOW + "Report filed.");

		return true;
	}
}
