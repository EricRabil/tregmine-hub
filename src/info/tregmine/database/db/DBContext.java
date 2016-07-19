package info.tregmine.database.db;

import java.sql.Connection;
import java.sql.SQLException;

import info.tregmine.Tregmine;
import info.tregmine.database.IBankDAO;
import info.tregmine.database.IBlessedBlockDAO;
import info.tregmine.database.IBlockDAO;
import info.tregmine.database.IContext;
import info.tregmine.database.IEnchantmentDAO;
import info.tregmine.database.IFishyBlockDAO;
import info.tregmine.database.IHandbookDAO;
import info.tregmine.database.IHomeDAO;
import info.tregmine.database.IInventoryDAO;
import info.tregmine.database.IItemDAO;
import info.tregmine.database.ILogDAO;
import info.tregmine.database.IMailDAO;
import info.tregmine.database.IMentorLogDAO;
import info.tregmine.database.IMiscDAO;
import info.tregmine.database.IMotdDAO;
import info.tregmine.database.IPlayerDAO;
import info.tregmine.database.IPlayerReportDAO;
import info.tregmine.database.IStaffNewsDAO;
import info.tregmine.database.ITradeDAO;
import info.tregmine.database.IWalletDAO;
import info.tregmine.database.IWarpDAO;
import info.tregmine.database.IZonesDAO;

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
	public IBankDAO getBankDAO() {
		return new DBBankDAO(conn);
	}

	@Override
	public IBlessedBlockDAO getBlessedBlockDAO() {
		return new DBBlessedBlockDAO(conn);
	}

	@Override
	public IBlockDAO getBlockDAO() {
		return new DBBlockDAO(conn);
	}

	public Connection getConnection() {
		return conn;
	}

	@Override
	public IEnchantmentDAO getEnchantmentDAO() {
		return new DBEnchantmentDAO(conn);
	}

	@Override
	public IFishyBlockDAO getFishyBlockDAO() {
		return new DBFishyBlockDAO(conn);
	}

	@Override
	public IHandbookDAO getHandbookDAO() {
		return new DBHandbookDAO(conn);
	}

	@Override
	public IHomeDAO getHomeDAO() {
		return new DBHomeDAO(conn);
	}

	@Override
	public IInventoryDAO getInventoryDAO() {
		return new DBInventoryDAO(conn);
	}

	@Override
	public IItemDAO getItemDAO() {
		return new DBItemDAO(conn);
	}

	@Override
	public ILogDAO getLogDAO() {
		return new DBLogDAO(conn);
	}

	@Override
	public IMailDAO getMailDAO() {
		return new DBMailDAO(conn, this.plugin);
	}

	@Override
	public IMentorLogDAO getMentorLogDAO() {
		return new DBMentorLogDAO(conn);
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

	@Override
	public IWarpDAO getWarpDAO() {
		return new DBWarpDAO(conn);
	}

	@Override
	public IZonesDAO getZonesDAO() {
		return new DBZonesDAO(conn);
	}
}
