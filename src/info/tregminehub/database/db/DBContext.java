package info.tregminehub.database.db;

import java.sql.Connection;
import java.sql.SQLException;

import info.tregminehub.Tregmine;
import info.tregminehub.database.IContext;
import info.tregminehub.database.IHandbookDAO;
import info.tregminehub.database.ILogDAO;
import info.tregminehub.database.IMailDAO;
import info.tregminehub.database.IMiscDAO;
import info.tregminehub.database.IMotdDAO;
import info.tregminehub.database.IPlayerDAO;
import info.tregminehub.database.IPlayerReportDAO;
import info.tregminehub.database.IStaffNewsDAO;
import info.tregminehub.database.ITradeDAO;
import info.tregminehub.database.IWalletDAO;

public class DBContext implements IContext {
	private Connection conn;
	private Tregmine plugin;

	public DBContext(Connection conn, Tregmine instance) {
		this.conn = conn;
		this.plugin = instance;
	}

	@Override
	public void close() {
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
			}
		}
	}
	
	
	@Override
	public IHandbookDAO getHandbookDAO() {
		return new DBHandbookDAO(conn);
	}

	@Override
	public IMailDAO getMailDAO() {
		return new DBMailDAO(conn, this.plugin);
	}
	
	@Override
	public ILogDAO getLogDAO(){
		return new DBLogDAO(conn);
	}

	@Override
	public IMiscDAO getMiscDAO() {
		return new DBMiscDAO(conn);
	}

	@Override
	public IMotdDAO getMotdDAO() {
		return new DBMotdDAO(conn);
	}

	@Override
	public IStaffNewsDAO getNewsByUploader() {
		return new DBNewsDAO(conn);
	}

	@Override
	public IPlayerDAO getPlayerDAO() {
		return new DBPlayerDAO(conn, plugin);
	}

	@Override
	public IPlayerReportDAO getPlayerReportDAO() {
		return new DBPlayerReportDAO(conn);
	}

	@Override
	public ITradeDAO getTradeDAO() {
		return new DBTradeDAO(conn);
	}

	@Override
	public IWalletDAO getWalletDAO() {
		return new DBWalletDAO(conn);
	}

}
