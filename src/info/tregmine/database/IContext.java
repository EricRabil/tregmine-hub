package info.tregmine.database;

public interface IContext extends AutoCloseable {
	@Override
	public void close();

	public IHandbookDAO getHandbookDAO();

	public IMailDAO getMailDAO();

	public IMiscDAO getMiscDAO();

	public IMotdDAO getMotdDAO();

	public IStaffNewsDAO getNewsByUploader();

	public IPlayerDAO getPlayerDAO();

	public IPlayerReportDAO getPlayerReportDAO();

	public ITradeDAO getTradeDAO();

	public IWalletDAO getWalletDAO();

}
