package info.tregminehub.database;

public interface IMotdDAO {
	public String getMotd() throws DAOException;

	public String getUpdates(String version) throws DAOException;
}
