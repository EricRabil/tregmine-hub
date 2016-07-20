package info.tregminehub.database;

import java.util.List;

public interface IHandbookDAO {
	public List<String[]> getHandbook() throws DAOException;
}
