package info.tregminehub.database;

import java.util.List;

import info.tregminehub.api.StaffNews;

public interface IStaffNewsDAO {
	public List<StaffNews> getStaffNews() throws DAOException;

	public void insertNews(StaffNews news) throws DAOException;
}
