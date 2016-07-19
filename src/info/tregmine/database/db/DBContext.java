package info.tregmine.database.db;

import java.sql.Connection;
import java.sql.SQLException;

import info.tregmine.Tregmine;
import info.tregmine.database.IContext;
import info.tregmine.database.IHandbookDAO;
import info.tregmine.database.ILogDAO;
import info.tregmine.database.IMailDAO;
import info.tregmine.database.IMiscDAO;
import info.tregmine.database.IMotdDAO;
import info.tregmine.database.IPlayerDAO;
import info.tregmine.database.IPlayerReportDAO;
import info.tregmine.database.IStaffNewsDAO;
import info.tregmine.database.ITradeDAO;
import info.tregmine.database.IWalletDAO;

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
