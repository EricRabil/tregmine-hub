package info.tregmine.database;

import java.util.List;

import info.tregmine.api.PlayerReport;
import info.tregmine.api.TregminePlayer;

public interface IPlayerReportDAO {
	public List<PlayerReport> getReportsBySubject(TregminePlayer player) throws DAOException;

	public void insertReport(PlayerReport report) throws DAOException;
}
