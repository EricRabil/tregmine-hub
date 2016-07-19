package info.tregmine.database;

public interface IContext extends AutoCloseable {
	@Override
	public void close();

	public IBankDAO getBankDAO();

	public IBlessedBlockDAO getBlessedBlockDAO();

	public IBlockDAO getBlockDAO();

	public IEnchantmentDAO getEnchantmentDAO();

	public IFishyBlockDAO getFishyBlockDAO();

	public IHandbookDAO getHandbookDAO();

	public IHomeDAO getHomeDAO();

	public IInventoryDAO getInventoryDAO();

	public IItemDAO getItemDAO();

	public ILogDAO getLogDAO();

	public IMailDAO getMailDAO();

	public IMentorLogDAO getMentorLogDAO();

	public IMiscDAO getMiscDAO();

	public IMotdDAO getMotdDAO();

	public IStaffNewsDAO getNewsByUploader();

	public IPlayerDAO getPlayerDAO();

	public IPlayerReportDAO getPlayerReportDAO();

	public ITradeDAO getTradeDAO();

	public IWalletDAO getWalletDAO();

	public IWarpDAO getWarpDAO();

	public IZonesDAO getZonesDAO();
}
