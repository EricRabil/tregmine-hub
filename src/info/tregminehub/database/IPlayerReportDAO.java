package info.tregminehub.database;

import java.util.List;

import info.tregminehub.api.PlayerReport;
import info.tregminehub.api.TregminePlayer;

public interface IPlayerReportDAO {
	public List<PlayerReport> getReportsBySubject(TregminePlayer player) throws DAOException;

	public void insertReport(PlayerReport report) throws DAOException;
}
