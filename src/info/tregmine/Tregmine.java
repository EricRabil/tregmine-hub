package info.tregmine;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.TreeMap;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import com.maxmind.geoip.LookupService;
import com.scarsz.discordsrv.DiscordSRV;

import info.tregmine.api.BlockStats;
import info.tregmine.api.FishyBlock;
import info.tregmine.api.Lag;
import info.tregmine.api.PlayerBannedException;
import info.tregmine.api.PlayerReport;
import info.tregmine.api.Rank;
import info.tregmine.api.Timer;
import info.tregmine.api.Tools;
import info.tregmine.api.TregminePlayer;
import info.tregmine.api.UUIDFetcher;
import info.tregmine.commands.ActionCommand;
import info.tregmine.commands.AfkCommand;
import info.tregmine.commands.AlertCommand;
import info.tregmine.commands.BackCommand;
import info.tregmine.commands.BadgeCommand;
import info.tregmine.commands.BanCommand;
import info.tregmine.commands.BlessCommand;
import info.tregmine.commands.BlockHereCommand;
import info.tregmine.commands.BrushCommand;
import info.tregmine.commands.ChangeNameCommand;
import info.tregmine.commands.ChannelCommand;
import info.tregmine.commands.ChannelViewCommand;
import info.tregmine.commands.CheckBlocksCommand;
import info.tregmine.commands.ChunkCountCommand;
import info.tregmine.commands.CleanInventoryCommand;
import info.tregmine.commands.CreateMobCommand;
import info.tregmine.commands.CreateWarpCommand;
import info.tregmine.commands.FillCommand;
import info.tregmine.commands.FlyCommand;
import info.tregmine.commands.ForceCommand;
import info.tregmine.commands.ForceShieldCommand;
import info.tregmine.commands.FreezeCommand;
import info.tregmine.commands.GameModeCommand;
import info.tregmine.commands.GameModeLegacyCommand;
import info.tregmine.commands.GiveCommand;
import info.tregmine.commands.HeadCommand;
import info.tregmine.commands.HideCommand;
import info.tregmine.commands.HomeCommand;
import info.tregmine.commands.IgnoreCommand;
import info.tregmine.commands.InventoryCommand;
import info.tregmine.commands.InventoryLogCommand;
import info.tregmine.commands.ItemCommand;
import info.tregmine.commands.KeywordCommand;
import info.tregmine.commands.KickCommand;
import info.tregmine.commands.KillCommand;
import info.tregmine.commands.LockdownCommand;
import info.tregmine.commands.LotCommand;
import info.tregmine.commands.LotteryCommand;
import info.tregmine.commands.MailCommand;
import info.tregmine.commands.MentorCommand;
import info.tregmine.commands.MsgCommand;
import info.tregmine.commands.NewSpawnCommand;
import info.tregmine.commands.NormalCommand;
import info.tregmine.commands.NotifyCommand;
import info.tregmine.commands.NukeCommand;
import info.tregmine.commands.OWCommand;
import info.tregmine.commands.PasswordCommand;
import info.tregmine.commands.PositionCommand;
import info.tregmine.commands.PromoteCommand;
import info.tregmine.commands.PropertyCommand;
import info.tregmine.commands.QuitMessageCommand;
import info.tregmine.commands.RegenerateChunkCommand;
import info.tregmine.commands.RemItemsCommand;
import info.tregmine.commands.ReplyCommand;
import info.tregmine.commands.ReportCommand;
import info.tregmine.commands.ResetLoreCommand;
import info.tregmine.commands.ResetNameCommand;
import info.tregmine.commands.SayCommand;
import info.tregmine.commands.SeenCommand;
import info.tregmine.commands.SellCommand;
import info.tregmine.commands.SendBackCommand;
import info.tregmine.commands.SendToCommand;
import info.tregmine.commands.SetBiomeCommand;
import info.tregmine.commands.SetSpawnerCommand;
import info.tregmine.commands.SkipMentorCommand;
import info.tregmine.commands.SpawnCommand;
import info.tregmine.commands.StaffHandbookCommand;
import info.tregmine.commands.StaffNewsCommand;
import info.tregmine.commands.SuicideCommand;
import info.tregmine.commands.SummonCommand;
import info.tregmine.commands.SupportCommand;
import info.tregmine.commands.TeleportCommand;
import info.tregmine.commands.TeleportShieldCommand;
import info.tregmine.commands.TeleportToCommand;
import info.tregmine.commands.TimeCommand;
import info.tregmine.commands.TpsCommand;
import info.tregmine.commands.TradeCommand;
import info.tregmine.commands.UpdateCommand;
import info.tregmine.commands.VanillaCommand;
import info.tregmine.commands.VanishCommand;
import info.tregmine.commands.WalletCommand;
import info.tregmine.commands.WarnCommand;
import info.tregmine.commands.WarpCommand;
import info.tregmine.commands.WatchChunksCommand;
import info.tregmine.commands.WeatherCommand;
import info.tregmine.commands.WebKickCommand;
import info.tregmine.commands.WhoCommand;
import info.tregmine.commands.ZoneCommand;
import info.tregmine.database.DAOException;
import info.tregmine.database.IBlessedBlockDAO;
import info.tregmine.database.IBlockDAO;
import info.tregmine.database.IContext;
import info.tregmine.database.IContextFactory;
import info.tregmine.database.IFishyBlockDAO;
import info.tregmine.database.ILogDAO;
import info.tregmine.database.IMiscDAO;
import info.tregmine.database.IPlayerDAO;
import info.tregmine.database.IPlayerReportDAO;
import info.tregmine.database.IZonesDAO;
import info.tregmine.database.db.DBContextFactory;
import info.tregmine.events.CallEventListener;
import info.tregmine.events.TregmineChatEvent;
import info.tregmine.listeners.AchievementListener;
import info.tregmine.listeners.AfkListener;
import info.tregmine.listeners.BankListener;
import info.tregmine.listeners.BlessedBlockListener;
import info.tregmine.listeners.BoxFillBlockListener;
import info.tregmine.listeners.ChatListener;
import info.tregmine.listeners.ChunkListener;
import info.tregmine.listeners.CompassListener;
import info.tregmine.listeners.CraftListener;
import info.tregmine.listeners.DamageListener;
import info.tregmine.listeners.DonationSigns;
import info.tregmine.listeners.EggListener;
import info.tregmine.listeners.ExpListener;
import info.tregmine.listeners.FishyBlockListener;
import info.tregmine.listeners.InventoryListener;
import info.tregmine.listeners.ItemFrameListener;
import info.tregmine.listeners.MiscListener;
import info.tregmine.listeners.PistonListener;
import info.tregmine.listeners.PlayerLookupListener;
import info.tregmine.listeners.RareDropListener;
import info.tregmine.listeners.SetupListener;
import info.tregmine.listeners.SignColorListener;
import info.tregmine.listeners.TabListener;
import info.tregmine.listeners.TauntListener;
import info.tregmine.listeners.TregmineBlockListener;
import info.tregmine.listeners.TregminePlayerListener;
import info.tregmine.listeners.ZoneBlockListener;
import info.tregmine.listeners.ZoneEntityListener;
import info.tregmine.listeners.ZonePlayerListener;
import info.tregmine.quadtree.IntersectionException;
import info.tregmine.tools.BuyToolCommand;
import info.tregmine.tools.LumberListener;
import info.tregmine.tools.PortalListener;
import info.tregmine.tools.ToolCraft;
import info.tregmine.tools.ToolCraftRegistry;
import info.tregmine.tools.ToolRepairCommand;
import info.tregmine.tools.ToolSpawnCommand;
import info.tregmine.tools.VeinListener;
import info.tregmine.zones.Lot;
import info.tregmine.zones.Zone;
import info.tregmine.zones.ZoneWorld;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

/**
 * @author Ein Andersson
 * @author Emil Hernvall
 * @author Eric Rabil
 */
public class Tregmine extends JavaPlugin {
	public final static int VERSION = 0;
	public final static int AMOUNT = 0;

	public final static Logger LOGGER = Logger.getLogger("Minecraft");

	private IContextFactory contextFactory;

	private Server server;
	private WebServer webServer;

	private Map<UUID, TregminePlayer> players;
	private Map<Integer, TregminePlayer> playersById;
	private Map<Location, Integer> blessedBlocks;
	private Map<Location, FishyBlock> fishyBlocks;
	private Map<Material, Integer> minedBlockPrices;

	private Map<String, ZoneWorld> worlds;
	private Map<Integer, Zone> zones;

	private List<String> insults;
	private List<String> quitMessages;
	private List<String> bannedWords;

	private List<TregmineChatEvent> blockedChats;

	private Queue<TregminePlayer> mentors;
	private Queue<TregminePlayer> students;
	private boolean lockdown = false;

	private LookupService cl = null;
	private boolean keywordsEnabled;
	public Tregmine plugin;
	public String releaseType = "re";
	public String serverName;
	private World vanillaWorld;
	private World vanillaNetherWorld;
	private World vanillaEndWorld;
	private ChatColor[] rankcolors = new ChatColor[9];
	FileConfiguration config;

	// Special!
	private World world2;
	private World world2nether;
	private World world2end;
	private boolean secondaryworld;

	// Statistics
	private int onlineGuards = 0;
	private int onlineJuniors = 0;
	private int onlineSeniors = 0;
	private int onlineTeachers = 0;

	private DiscordSRV dsv;

	private Lag lag = new Lag();

	public void addBlockedChat(TregmineChatEvent e) {
		this.blockedChats.add(e);
	}

	// End interject
	public TregminePlayer addPlayer(Player srcPlayer, InetAddress addr) throws PlayerBannedException {
		// if (players.containsKey(srcPlayer.getName())) {
		// return players.get(srcPlayer.getName());
		// }
		TregminePlayer plr = players.get(srcPlayer.getName());
		if (plr != null) {
			return plr;
		}
		try (IContext ctx = contextFactory.createContext()) {
			IPlayerDAO playerDAO = ctx.getPlayerDAO();

			TregminePlayer player = playerDAO.getPlayer(srcPlayer.getPlayer());

			if (player == null) {
				player = playerDAO.createPlayer(srcPlayer);
			}

			player.removeFlag(TregminePlayer.Flags.SOFTWARNED);
			player.removeFlag(TregminePlayer.Flags.HARDWARNED);

			IPlayerReportDAO reportDAO = ctx.getPlayerReportDAO();
			List<PlayerReport> reports = reportDAO.getReportsBySubject(player);
			for (PlayerReport report : reports) {
				Date validUntil = report.getValidUntil();
				if (validUntil == null) {
					continue;
				}
				if (validUntil.getTime() < System.currentTimeMillis()) {
					continue;
				}

				if (report.getAction() == PlayerReport.Action.SOFTWARN) {
					player.setFlag(TregminePlayer.Flags.SOFTWARNED);
				} else if (report.getAction() == PlayerReport.Action.HARDWARN) {
					player.setFlag(TregminePlayer.Flags.HARDWARNED);
				} else if (report.getAction() == PlayerReport.Action.BAN) {
					throw new PlayerBannedException(report.getMessage());
				}
			}

			player.setIp(addr.getHostAddress());
			player.setHost(addr.getCanonicalHostName());

			if (cl != null) {
				com.maxmind.geoip.Location l1 = cl.getLocation(player.getIp());
				if (l1 != null) {
					Tregmine.LOGGER.info(player.getName() + ": " + l1.countryName + ", " + l1.city + ", "
							+ player.getIp() + ", " + player.getHost());
					player.setCountry(l1.countryName);
					player.setCity(l1.city);
				} else {
					Tregmine.LOGGER.info(player.getName() + ": " + player.getIp() + ", " + player.getHost());
				}
			} else {
				Tregmine.LOGGER.info(player.getName() + ": " + player.getIp() + ", " + player.getHost());
			}

			int onlinePlayerCount = 0;
			Collection<? extends Player> onlinePlayers = getServer().getOnlinePlayers();
			if (onlinePlayers != null) {
				onlinePlayerCount = onlinePlayers.size();
			}

			ILogDAO logDAO = ctx.getLogDAO();
			logDAO.insertLogin(player, false, onlinePlayerCount);

			player.setTemporaryChatName(player.getNameColor() + player.getName());

			players.put(player.getUniqueId(), player);
			playersById.put(player.getId(), player);

			return player;
		} catch (DAOException e) {
			throw new RuntimeException(e);
		}
	}

	public void broadcast(BaseComponent a) {
		for (TregminePlayer player : this.getOnlinePlayers()) {
			player.sendSpigotMessage(a);
		}
	}

	public void broadcast(BaseComponent... a) {
		for (TregminePlayer player : this.getOnlinePlayers()) {
			player.sendSpigotMessage(a);
		}
	}

	public HoverEvent buildHover(String abc) {
		return new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(abc).create());
	}

	public IContext createContext() throws DAOException {
		return contextFactory.createContext();
	}

	public List<String> getBannedWords() {
		return bannedWords;
	}

	public Map<Location, Integer> getBlessedBlocks() {
		return blessedBlocks;
	}

	public List<TregmineChatEvent> getBlockedChats() {
		return this.blockedChats;
	}

	public IContextFactory getContextFactory() {
		return contextFactory;
	}

	public Map<Location, FishyBlock> getFishyBlocks() {
		return fishyBlocks;
	}

	public List<String> getInsults() {
		return insults;
	}

	public boolean getLockdown() {
		return lockdown;
	}

	public Queue<TregminePlayer> getMentorQueue() {
		return mentors;
	}

	public int getMinedPrice(Material q) {
		return this.minedBlockPrices.get(q);
	}

	public int getOnlineGuardians() {
		return this.onlineGuards;
	}

	public int getOnlineJuniors() {
		return this.onlineJuniors;
	}

	public List<TregminePlayer> getOnlinePlayers() {
		List<TregminePlayer> players = new ArrayList<>();
		for (Player player : server.getOnlinePlayers()) {
			players.add(getPlayer(player));
		}

		return players;
	}

	public int getOnlineSeniors() {
		return this.onlineSeniors;
	}

	public int getOnlineTeachers() {
		return this.onlineTeachers;
	}

	public TregminePlayer getPlayer(int id) {
		return playersById.get(id);
	}

	public TregminePlayer getPlayer(Player player) {
		try {
			return players.get(player.getUniqueId());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public TregminePlayer getPlayer(String name) {
		try {
			return players.get(UUIDFetcher.getUUIDOf(name));
		} catch (Exception e) {
			// TODO Auto-generated catch block

			e.printStackTrace();
			return null;
		}
	}

	// ============================================================================
	// Data structure accessors
	// ============================================================================

	public TregminePlayer getPlayerOffline(int id) {
		TregminePlayer plr = playersById.get(id);
		if (plr != null) {
			return plr;
		}

		try (IContext ctx = contextFactory.createContext()) {
			IPlayerDAO playerDAO = ctx.getPlayerDAO();
			return playerDAO.getPlayer(id);
		} catch (DAOException e) {
			throw new RuntimeException(e);
		}
	}

	public TregminePlayer getPlayerOffline(OfflinePlayer player) {
		TregminePlayer plr = players.get(player.getUniqueId());
		if (plr != null) {
			return plr;
		}

		try (IContext ctx = contextFactory.createContext()) {
			IPlayerDAO playerDAO = ctx.getPlayerDAO();
			return playerDAO.getPlayer(player.getUniqueId());
		} catch (DAOException e) {
			throw new RuntimeException(e);
		}
	}

	public TregminePlayer getPlayerOffline(UUID uuid) {
		// if (players.containsKey(name)) {
		// return players.get(name);
		// }
		TregminePlayer plr = players.get(uuid);
		if (plr != null) {
			return plr;
		}

		try (IContext ctx = contextFactory.createContext()) {
			IPlayerDAO playerDAO = ctx.getPlayerDAO();
			return playerDAO.getPlayer(uuid);
		} catch (DAOException e) {
			throw new RuntimeException(e);
		}
	}

	public String getPluginFolder() {
		return this.getDataFolder().getAbsolutePath();
	}

	public List<String> getQuitMessages() {
		return quitMessages;
	}

	public ChatColor getRankColor(Rank rank) {
		if (this.rankcolors[0] == null) {
			Tools tools = new Tools();
			this.rankcolors[0] = tools.toColor(this.getConfig().getString("ranks.colors.tourist"));
			this.rankcolors[1] = tools.toColor(this.getConfig().getString("ranks.colors.settler"));
			this.rankcolors[2] = tools.toColor(this.getConfig().getString("ranks.colors.resident"));
			this.rankcolors[3] = tools.toColor(this.getConfig().getString("ranks.colors.donator"));
			this.rankcolors[4] = tools.toColor(this.getConfig().getString("ranks.colors.guardian"));
			this.rankcolors[5] = tools.toColor(this.getConfig().getString("ranks.colors.coder"));
			this.rankcolors[6] = tools.toColor(this.getConfig().getString("ranks.colors.builder"));
			this.rankcolors[7] = tools.toColor(this.getConfig().getString("ranks.colors.junior"));
			this.rankcolors[8] = tools.toColor(this.getConfig().getString("ranks.colors.senior"));
		}
		if (rank == Rank.TOURIST) {
			return this.rankcolors[0];
		} else if (rank == Rank.SETTLER) {
			return this.rankcolors[1];
		} else if (rank == Rank.RESIDENT) {
			return this.rankcolors[2];
		} else if (rank == Rank.DONATOR) {
			return this.rankcolors[3];
		} else if (rank == Rank.GUARDIAN) {
			return this.rankcolors[4];
		} else if (rank == Rank.CODER) {
			return this.rankcolors[5];
		} else if (rank == Rank.BUILDER) {
			return this.rankcolors[6];
		} else if (rank == Rank.JUNIOR_ADMIN) {
			return this.rankcolors[7];
		} else if (rank == Rank.SENIOR_ADMIN) {
			return this.rankcolors[8];
		} else {
			return ChatColor.WHITE;
		}
	}

	public Queue<TregminePlayer> getStudentQueue() {
		return students;
	}

	public World getSWorld() {
		return this.world2;
	}

	public World getSWorldEnd() {
		return this.world2end;
	}

	public World getSWorldNether() {
		return this.world2nether;
	}

	// ============================================================================
	// Player methods
	// ============================================================================

	public Tregmine getTregmine() {
		return plugin;
	}

	public World getVanillaEnd() {
		return vanillaEndWorld;
	}

	public World getVanillaNether() {
		return vanillaNetherWorld;
	}

	// Interjection point for other stuff

	public World getVanillaWorld() {
		return vanillaWorld;
	}

	public WebServer getWebServer() {
		return webServer;
	}

	public ZoneWorld getWorld(World world) {
		ZoneWorld zoneWorld = worlds.get(world.getName());

		// lazy load zone worlds as required
		if (zoneWorld == null) {
			try (IContext ctx = contextFactory.createContext()) {
				IZonesDAO dao = ctx.getZonesDAO();

				zoneWorld = new ZoneWorld(world);
				List<Zone> zones = dao.getZones(world.getName());
				for (Zone zone : zones) {
					try {
						zoneWorld.addZone(zone);
						this.zones.put(zone.getId(), zone);
					} catch (IntersectionException e) {
						LOGGER.warning("Failed to load zone " + zone.getName() + " with id " + zone.getId() + ".");
					}
				}

				List<Lot> lots = dao.getLots(world.getName());
				for (Lot lot : lots) {
					try {
						zoneWorld.addLot(lot);
					} catch (IntersectionException e) {
						LOGGER.warning("Failed to load lot " + lot.getName() + " with id " + lot.getId() + ".");
					}
				}

				worlds.put(world.getName(), zoneWorld);
			} catch (DAOException e) {
				throw new RuntimeException(e);
			}
		}

		return zoneWorld;
	}

	public Zone getZone(int zoneId) {
		return zones.get(zoneId);
	}

	public boolean hasSecondaryWorld() {
		return this.secondaryworld;
	}

	public boolean isInVanilla(TregminePlayer player) {
		if (player.getWorld() == this.vanillaWorld || player.getWorld() == this.vanillaEndWorld
				|| player.getWorld() == this.vanillaNetherWorld) {
			return true;
		} else {
			return false;
		}
	}

	public boolean keywordsEnabled() {
		return keywordsEnabled;
	}

	public List<TregminePlayer> matchPlayer(String pattern) {
		List<Player> matches = server.matchPlayer(pattern);
		if (matches.size() == 0) {
			return new ArrayList<>();
		}

		List<TregminePlayer> decoratedMatches = new ArrayList<>();
		for (Player match : matches) {
			TregminePlayer decoratedMatch = getPlayer(match);
			if (decoratedMatch == null) {
				continue;
			}
			decoratedMatches.add(decoratedMatch);
		}

		return decoratedMatches;
	}

	// run when plugin is disabled
	@Override
	public void onDisable() {
		server.getScheduler().cancelTasks(this);

		// Add a record of logout to db for all players
		try {
			for (TregminePlayer player : getOnlinePlayers()) {
				player.sendSpigotMessage(new TextComponent(ChatColor.GOLD + this.serverName + ChatColor.DARK_AQUA
						+ " may be shutting down soon! Please prepare to be kicked."));
				player.saveInventory(player.getCurrentInventory());
				removePlayer(player);
			}
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		try {
			webServer.stop();
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Failed to start web server!", e);
		}
	}

	@Override
	public void onEnable() {

		this.server = getServer();
		plugin = this;
		
		Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, this.lag, 100L, 1L);
		Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Timer(this), 100L, 1L);
		List<?> configWorlds = getConfig().getList("worlds.names");
		if (getConfig().getString("worlds.vanillaworld") == "true") {
			WorldCreator addWorld = new WorldCreator("vanilla");
			addWorld.environment(World.Environment.NORMAL);
			addWorld.generateStructures(true);
			addWorld.type(WorldType.NORMAL);
			this.vanillaWorld = addWorld.createWorld();

			// Nether
			WorldCreator addNether = new WorldCreator("vanilla_nether").environment(World.Environment.NETHER)
					.type(WorldType.NORMAL);
			this.vanillaNetherWorld = addNether.createWorld();

			// End
			WorldCreator addEnd = new WorldCreator("vanilla_the_end").environment(World.Environment.THE_END)
					.type(WorldType.NORMAL);
			this.vanillaEndWorld = addEnd.createWorld();
		}
		if (config.getBoolean("worlds.special.newworld")) {
			WorldCreator world2g = new WorldCreator("world_2");
			WorldCreator world2netherg = new WorldCreator("world_2_nether");
			WorldCreator world2endg = new WorldCreator("world_2_the_end");
			world2g.environment(World.Environment.NORMAL);
			world2g.generateStructures(true);
			world2g.type(WorldType.NORMAL);
			this.world2 = world2g.createWorld();
			world2netherg.environment(World.Environment.NETHER);
			world2netherg.generateStructures(true);
			world2netherg.type(WorldType.NORMAL);
			this.world2nether = world2netherg.createWorld();
			world2endg.environment(World.Environment.THE_END);
			world2endg.generateStructures(true);
			world2endg.type(WorldType.NORMAL);
			this.world2end = world2endg.createWorld();
			this.secondaryworld = true;
		}
		this.serverName = getConfig().getString("general.servername");
		this.keywordsEnabled = getConfig().getBoolean("general.keywords");
		if (getConfig().getString("worlds.enabled") == "true") {
			String[] worlds = configWorlds.toArray(new String[configWorlds.size()]);
			for (String worldName : worlds) {
				if (worldName.contains("_the_end") || worldName.contains("_nether")) {
					// Do nothing
				} else {
					WorldCreator addWorld = new WorldCreator(worldName);
					addWorld.environment(World.Environment.NORMAL);
					addWorld.generateStructures(false);
					addWorld.type(WorldType.NORMAL);
					addWorld.createWorld();
				}
			}

			for (String worldName : worlds) {
				if (!worldName.contains("the_end")) {
					// Do nothing
				} else {
					WorldCreator addWorld = new WorldCreator(worldName);
					addWorld.environment(World.Environment.THE_END);
					addWorld.generateStructures(false);
					addWorld.createWorld();
				}
			}
			for (String worldName : worlds) {
				if (!worldName.contains("nether")) {
					// Do nothing
				} else {
					WorldCreator addWorld = new WorldCreator(worldName);
					addWorld.environment(World.Environment.NETHER);
					addWorld.generateStructures(false);
					addWorld.createWorld();
				}
			}
			LOGGER.info("" + configWorlds.size() + " extra worlds attempted to load.");
		}

		try (IContext ctx = contextFactory.createContext()) {
			IBlessedBlockDAO blessedBlockDAO = ctx.getBlessedBlockDAO();
			this.blessedBlocks = blessedBlockDAO.load(getServer());

			LOGGER.info("Loaded " + blessedBlocks.size() + " blessed blocks");

			IFishyBlockDAO fishyBlockDAO = ctx.getFishyBlockDAO();
			this.fishyBlocks = fishyBlockDAO.loadFishyBlocks(getServer());

			LOGGER.info("Loaded " + fishyBlocks.size() + " fishy blocks");

			IBlockDAO blockDAO = ctx.getBlockDAO();
			this.minedBlockPrices = blockDAO.loadBlockMinePrices();

			IMiscDAO miscDAO = ctx.getMiscDAO();
			this.insults = miscDAO.loadInsults();
			this.quitMessages = miscDAO.loadQuitMessages();
			this.bannedWords = miscDAO.loadBannedWords();
			if (insults.size() == 0) {
				insults.add(0, "This is a generic death message, hardcoded into the plugin as a safeguard.");
			}
			if (quitMessages.size() == 0) {
				quitMessages.add(0, "This is a generic quit message, hardcoded into the plugin as a safeguard.");
			}
			LOGGER.info("Loaded " + insults.size() + " insults and " + quitMessages.size() + " quit messages");
		} catch (DAOException e) {
			throw new RuntimeException(e);
		}

		// Set up web server
		webServer = new WebServer(this);
		webServer.start();

		// Register all listeners
		PluginManager pluginMgm = server.getPluginManager();
		pluginMgm.registerEvents(new CraftListener(this), this);
		pluginMgm.registerEvents(new AchievementListener(this), this);
		pluginMgm.registerEvents(new AfkListener(this), this);
		pluginMgm.registerEvents(new BlockStats(this), this);
		pluginMgm.registerEvents(new BlessedBlockListener(this), this);
		pluginMgm.registerEvents(new BoxFillBlockListener(this), this);
		pluginMgm.registerEvents(new ChatListener(this), this);
		pluginMgm.registerEvents(new CompassListener(this), this);
		pluginMgm.registerEvents(new PlayerLookupListener(this), this);
		pluginMgm.registerEvents(new SetupListener(this), this);
		pluginMgm.registerEvents(new SignColorListener(), this);
		pluginMgm.registerEvents(new TabListener(this), this);
		pluginMgm.registerEvents(new TauntListener(this), this);
		pluginMgm.registerEvents(new TregmineBlockListener(this), this);
		pluginMgm.registerEvents(new TregminePlayerListener(this), this);
		pluginMgm.registerEvents(new ZoneBlockListener(this), this);
		pluginMgm.registerEvents(new ZoneEntityListener(this), this);
		pluginMgm.registerEvents(new ZonePlayerListener(this), this);
		pluginMgm.registerEvents(new FishyBlockListener(this), this);
		pluginMgm.registerEvents(new InventoryListener(this), this);
		pluginMgm.registerEvents(new DonationSigns(this), this);
		pluginMgm.registerEvents(new ExpListener(this), this);
		pluginMgm.registerEvents(new ItemFrameListener(this), this);
		pluginMgm.registerEvents(new EggListener(this), this);
		pluginMgm.registerEvents(new PistonListener(this), this);
		pluginMgm.registerEvents(new ToolCraft(this), this);
		pluginMgm.registerEvents(new LumberListener(this), this);
		pluginMgm.registerEvents(new VeinListener(this), this);
		pluginMgm.registerEvents(new CallEventListener(this), this);
		pluginMgm.registerEvents(new PortalListener(this), this);
		pluginMgm.registerEvents(new BankListener(this), this);
		pluginMgm.registerEvents(new RareDropListener(this), this);
		pluginMgm.registerEvents(new DamageListener(this), this);
		pluginMgm.registerEvents(new ChunkListener(this), this);
		pluginMgm.registerEvents(new MiscListener(this), this);

		// Declaration of all commands

		getCommand("admins").setExecutor(new NotifyCommand(this, "admins") {
			@Override
			public ChatColor getColor() {
				return plugin.getRankColor(Rank.JUNIOR_ADMIN);
			}

			@Override
			public boolean isTarget(TregminePlayer player) {
				return player.getRank() == Rank.JUNIOR_ADMIN || player.getRank() == Rank.SENIOR_ADMIN;
			}
		});

		getCommand("guardians").setExecutor(new NotifyCommand(this, "guardians") {
			@Override
			public ChatColor getColor() {
				return plugin.getRankColor(Rank.GUARDIAN);
			}

			@Override
			public boolean isTarget(TregminePlayer player) {
				return player.getRank() == Rank.GUARDIAN || player.getRank() == Rank.JUNIOR_ADMIN
						|| player.getRank() == Rank.SENIOR_ADMIN;
			}
		});

		getCommand("coders").setExecutor(new NotifyCommand(this, "coders") {
			@Override
			public ChatColor getColor() {
				return plugin.getRankColor(Rank.CODER);
			}

			@Override
			public boolean isTarget(TregminePlayer player) {
				return player.getRank() == Rank.CODER || player.getRank() == Rank.JUNIOR_ADMIN
						|| player.getRank() == Rank.SENIOR_ADMIN;
			}
		});
		getCommand("taxi").setExecutor(new OWCommand(this));
		getCommand("property").setExecutor(new PropertyCommand(this));
		getCommand("staffbook").setExecutor(new StaffHandbookCommand(this));
		getCommand("action").setExecutor(new ActionCommand(this));
		getCommand("afk").setExecutor(new AfkCommand(this));
		getCommand("alert").setExecutor(new AlertCommand(this));
		getCommand("allclear").setExecutor(new CheckBlocksCommand(this));
		getCommand("back").setExecutor(new BackCommand(this));
		getCommand("badge").setExecutor(new BadgeCommand(this));
		getCommand("ban").setExecutor(new BanCommand(this));
		getCommand("bless").setExecutor(new BlessCommand(this));
		getCommand("blockhere").setExecutor(new BlockHereCommand(this));
		getCommand("brush").setExecutor(new BrushCommand(this));
		getCommand("buytool").setExecutor(new BuyToolCommand(this));
		getCommand("channel").setExecutor(new ChannelCommand(this));
		getCommand("channelview").setExecutor(new ChannelViewCommand(this));
		getCommand("clean").setExecutor(new CleanInventoryCommand(this));
		getCommand("cname").setExecutor(new ChangeNameCommand(this));
		getCommand("createmob").setExecutor(new CreateMobCommand(this));
		getCommand("createwarp").setExecutor(new CreateWarpCommand(this));
		getCommand("creative").setExecutor(new GameModeCommand(this, "creative", GameMode.CREATIVE));
		getCommand("fill").setExecutor(new FillCommand(this, "fill"));
		getCommand("suicide").setExecutor(new SuicideCommand(this));
		getCommand("fly").setExecutor(new FlyCommand(this));
		getCommand("force").setExecutor(new ForceCommand(this));
		getCommand("forceblock").setExecutor(new ForceShieldCommand(this));
		getCommand("freeze").setExecutor(new FreezeCommand(this));
		getCommand("give").setExecutor(new GiveCommand(this));
		getCommand("head").setExecutor(new HeadCommand(this));
		getCommand("hide").setExecutor(new HideCommand(this));
		getCommand("mail").setExecutor(new MailCommand(this));
		getCommand("home").setExecutor(new HomeCommand(this));
		getCommand("ignore").setExecutor(new IgnoreCommand(this));
		getCommand("inv").setExecutor(new InventoryCommand(this));
		getCommand("invlog").setExecutor(new InventoryLogCommand(this));
		getCommand("item").setExecutor(new ItemCommand(this));
		getCommand("keyword").setExecutor(new KeywordCommand(this));
		getCommand("kick").setExecutor(new KickCommand(this));
		getCommand("kill").setExecutor(new KillCommand(this));
		getCommand("lockdown").setExecutor(new LockdownCommand(this));
		getCommand("lot").setExecutor(new LotCommand(this));
		getCommand("lottery").setExecutor(new LotteryCommand(this));
		getCommand("mentor").setExecutor(new MentorCommand(this));
		getCommand("msg").setExecutor(new MsgCommand(this));
		getCommand("newspawn").setExecutor(new NewSpawnCommand(this));
		getCommand("normal").setExecutor(new NormalCommand(this));
		getCommand("nuke").setExecutor(new NukeCommand(this));
		getCommand("password").setExecutor(new PasswordCommand(this));
		getCommand("pos").setExecutor(new PositionCommand(this));
		getCommand("promote").setExecutor(new PromoteCommand(this));
		getCommand("quitmessage").setExecutor(new QuitMessageCommand(this));
		getCommand("regeneratechunk").setExecutor(new RegenerateChunkCommand(this));
		getCommand("remitems").setExecutor(new RemItemsCommand(this));
		getCommand("repair").setExecutor(new ToolRepairCommand(this));
		getCommand("reply").setExecutor(new ReplyCommand(this));
		getCommand("report").setExecutor(new ReportCommand(this));
		getCommand("rname").setExecutor(new ResetNameCommand(this));
		getCommand("resetlore").setExecutor(new ResetLoreCommand(this));
		getCommand("say").setExecutor(new SayCommand(this));
		getCommand("seen").setExecutor(new SeenCommand(this));
		getCommand("sell").setExecutor(new SellCommand(this));
		getCommand("sendback").setExecutor(new SendBackCommand(this));
		getCommand("sendto").setExecutor(new SendToCommand(this));
		getCommand("setbiome").setExecutor(new SetBiomeCommand(this));
		getCommand("setspawner").setExecutor(new SetSpawnerCommand(this));
		getCommand("skipmentor").setExecutor(new SkipMentorCommand(this));
		getCommand("spawn").setExecutor(new SpawnCommand(this));
		getCommand("summon").setExecutor(new SummonCommand(this));
		getCommand("support").setExecutor(new SupportCommand(this));
		getCommand("survival").setExecutor(new GameModeCommand(this, "survival", GameMode.SURVIVAL));
		getCommand("staffnews").setExecutor(new StaffNewsCommand(this));
		getCommand("testfill").setExecutor(new FillCommand(this, "testfill"));
		getCommand("time").setExecutor(new TimeCommand(this));
		getCommand("tool").setExecutor(new ToolSpawnCommand(this));
		getCommand("town").setExecutor(new ZoneCommand(this, "town"));
		getCommand("tp").setExecutor(new TeleportCommand(this));
		getCommand("ttps").setExecutor(new TpsCommand(this));
		getCommand("tpshield").setExecutor(new TeleportShieldCommand(this));
		getCommand("tpto").setExecutor(new TeleportToCommand(this));
		getCommand("trade").setExecutor(new TradeCommand(this));
		getCommand("update").setExecutor(new UpdateCommand(this));
		getCommand("vanish").setExecutor(new VanishCommand(this));
		getCommand("wallet").setExecutor(new WalletCommand(this));
		getCommand("watchchunks").setExecutor(new WatchChunksCommand(this));
		getCommand("warn").setExecutor(new WarnCommand(this));
		getCommand("warp").setExecutor(new WarpCommand(this));
		getCommand("weather").setExecutor(new WeatherCommand(this));
		getCommand("webkick").setExecutor(new WebKickCommand(this));
		getCommand("who").setExecutor(new WhoCommand(this));
		getCommand("vanilla").setExecutor(new VanillaCommand(this));
		getCommand("zone").setExecutor(new ZoneCommand(this, "zone"));
		getCommand("chunkcount").setExecutor(new ChunkCountCommand(this));
		getCommand("gamemode").setExecutor(new GameModeLegacyCommand(this));
		ToolCraftRegistry.RegisterRecipes(getServer()); // Registers all tool
														// recipes

		try {
			for (TregminePlayer player : getOnlinePlayers()) {
				player.sendStringMessage(ChatColor.AQUA + "Tregmine has been reloaded!");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		BukkitScheduler scheduler = getServer().getScheduler();
		scheduler.scheduleSyncRepeatingTask(this, new Runnable() {
			@Override
			public void run() {
				try {
					for (TregminePlayer player : getOnlinePlayers()) {
						if (player.getCombatLog() > 0) {
							player.setCombatLog(player.getCombatLog() - 1);

							if (player.getCombatLog() == 0) {
								player.sendSpigotMessage(new TextComponent(
										ChatColor.GREEN + "Combat log has warn off... Safe to log off!"));
							}
						}
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}, 20L, 20L);
		PluginManager mgr = Bukkit.getPluginManager();
		Plugin d = mgr.getPlugin("DiscordSRV");
		
		if (d != null) {
			this.dsv = (DiscordSRV) d;
		} else {
			this.dsv = null;
		}
		
	}

	public Lag getLag() {
		return this.lag;
	}

	@Override
	public void onLoad() {
		File folder = getDataFolder();
		Tregmine.LOGGER.info("Data folder is: " + folder);

		reloadConfig();

		config = getConfig();

		contextFactory = new DBContextFactory(config, this);

		// Set up all data structures
		players = new HashMap<>();
		playersById = new HashMap<>();

		mentors = new LinkedList<>();
		students = new LinkedList<>();

		worlds = new TreeMap<>(new Comparator<String>() {
			@Override
			public int compare(String a, String b) {
				return a.compareToIgnoreCase(b);
			}
		});

		zones = new HashMap<>();

		Collection<? extends Player> players = Bukkit.getServer().getOnlinePlayers();
		for (Player player01 : players) {
			try {
				TregminePlayer tp = addPlayer(player01, player01.getAddress().getAddress());
				if (tp.getRank() == Rank.TOURIST) {
					students.offer(tp);
				}
			} catch (PlayerBannedException e) {
				player01.kickPlayer(e.getMessage());
			}
		}

		try {
			cl = new LookupService(new File(folder, "GeoIPCity.dat"), LookupService.GEOIP_MEMORY_CACHE);
		} catch (IOException e) {
			Tregmine.LOGGER.warning("GeoIPCity.dat was not found! ");
		}
	}

	public void reloadDSV() {
		PluginManager mgr = Bukkit.getPluginManager();
		Plugin d = mgr.getPlugin("DiscordSRV");
		if (d != null) {
			this.dsv = (DiscordSRV) d;
		} else {
			this.dsv = null;
		}
	}

	public DiscordSRV getDiscordSRV() {
		return this.dsv;
	}

	public boolean dsvEnabled() {
		return this.dsv != null;
	}

	public FileConfiguration plConfig() {
		return this.config;
	}

	public void reloadPlayer(TregminePlayer player) {
		try {
			addPlayer(player.getDelegate(), player.getAddress().getAddress());
		} catch (PlayerBannedException e) {
			player.kickPlayer(plugin, e.getMessage());
		}
	}

	public void removePlayer(TregminePlayer player) {
		try (IContext ctx = contextFactory.createContext()) {
			int onlinePlayerCount = 0;
			Collection<? extends Player> onlinePlayers = Bukkit.getServer().getOnlinePlayers();
			if (onlinePlayers != null) {
				onlinePlayerCount = onlinePlayers.size();
			}

			ILogDAO logDAO = ctx.getLogDAO();
			logDAO.insertLogin(player, true, onlinePlayerCount);

			IPlayerDAO playerDAO = ctx.getPlayerDAO();
			playerDAO.updatePlayTime(player);
			playerDAO.updateBadges(player);
		} catch (DAOException e) {
			throw new RuntimeException(e);
		}

		player.setValid(false);

		players.remove(player.getName());
		playersById.remove(player.getId());
		mentors.remove(player);
		students.remove(player);
	}

	// ============================================================================
	// Zone methods
	// ============================================================================

	public String serverName() {
		return this.serverName;
	}

	public void setLockdown(boolean v) {
		if (v) {
			Bukkit.broadcastMessage(
					ChatColor.RED + "The server is now on lockdown. Only staff will be able to connect.");
		} else {
			Bukkit.broadcastMessage(ChatColor.GREEN + "The server is no longer on lockdown.");
		}
		this.lockdown = v;
	}

	public void updateStatistics() {
		int g = 0;
		int j = 0;
		int s = 0;
		int t = 0;
		for (TregminePlayer yeezy : this.getOnlinePlayers()) {
			if (yeezy.getRank() == Rank.GUARDIAN)
				g++;
			else if (yeezy.getRank() == Rank.JUNIOR_ADMIN)
				j++;
			else if (yeezy.getRank() == Rank.SENIOR_ADMIN)
				s++;

			if (yeezy.getRank().canMentor())
				t++;
		}
		this.onlineGuards = g;
		this.onlineJuniors = j;
		this.onlineSeniors = s;
		this.onlineTeachers = t;
	}
}
