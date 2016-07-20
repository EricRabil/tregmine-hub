package info.tregminehub.database;

public interface IContextFactory {
	public IContext createContext() throws DAOException;
}
