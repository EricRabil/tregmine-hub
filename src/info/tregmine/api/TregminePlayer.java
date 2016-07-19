package info.tregmine.api;

import static org.bukkit.ChatColor.BLUE;
import static org.bukkit.ChatColor.GRAY;
import static org.bukkit.ChatColor.GREEN;
import static org.bukkit.ChatColor.ITALIC;
import static org.bukkit.ChatColor.RESET;

import java.util.ArrayList;
import java.util.Date;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

import info.tregmine.Tregmine;
import info.tregmine.api.encryption.BCrypt;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class TregminePlayer extends PlayerDelegate {
	public enum ChatState {
		SETUP, CHAT, TRADE, SELL, FISHY_SETUP, FISHY_WITHDRAW, FISHY_BUY, BANK;
	};

	// Flags are stored as integers - order must _NOT_ be changed
	public enum Flags {
		CHILD, TPSHIELD, SOFTWARNED, HARDWARNED, INVISIBLE, HIDDEN_LOCATION, FLY_ENABLED, FORCESHIELD, CHEST_LOG, HIDDEN_ANNOUNCEMENT, CHANNEL_VIEW, WATCHING_CHUNKS, AFK_KICK;
	};

	public enum GuardianState {
		ACTIVE, INACTIVE, QUEUED;
	};

	public enum Property {
		NICKNAME
	}

	// Persistent values
	private int id = 0;
	private UUID storedUuid = null;
	private String name = null;
	private String realName = null;
	private String password = null;
	private String keyword = null;
	private Rank rank = Rank.UNVERIFIED;
	private boolean Staff = false;
	private String quitMessage = null;
	private int guardianRank = 0;
	private int playTime = 0;
	private Set<Flags> flags;
	private Set<CommandStatus> commandstatus;
	private Set<Property> properties;
	private Map<Badge, Integer> badges;
	private Location lastpos = null;

	// Discord values
	private boolean alertedAfk;

	private QuitCause causeofquit = null;

	// One-time state
	private String chatChannel = "GLOBAL";
	private GuardianState guardianState = GuardianState.QUEUED;
	private int blessTarget = 0;
	private ChatState chatState = ChatState.CHAT;
	private Date loginTime = null;
	private boolean valid = true;
	private String ip;
	private String host;
	private String city;
	private String country;
	private TregminePlayer mentor;
	private TregminePlayer student;
	private String currentInventory;
	private int combatLog;
	private long lastOnlineActivity;
	private String lastMessenger;
	private boolean AfkKick = true;
	private boolean CurseWarned;
	private PermissionAttachment attachment;
	private PlayerMute mute = null;
	private boolean muted = false;

	// Reports
	private List<String[]> reports = new ArrayList<String[]>();
	private int kicks = 0;
	private int softwarns = 0;
	private int hardwarns = 0;
	private int bans = 0;

	// Player state for block fill
	private Block fillBlock1 = null;
	private Block fillBlock2 = null;
	private int fillBlockCounter = 0;

	// Player state for zone creation
	private Block zoneBlock1 = null;
	private Block zoneBlock2 = null;
	private int zoneBlockCounter = 0;
	private int targetZoneId = 0;

	// Player state for activity
	private boolean afk = false;
	private boolean isFrozen = false;

	// Fishy Block state
	private FishyBlock newFishyBlock;
	private FishyBlock currentFishyBlock;
	private int fishyBuyCount;

	// Chunk Watcher
	private boolean newChunk = false;

	// Ranks
	private boolean isTemporaryRank = false;
	private Rank temporaryRank = null;

	// Death states
	private String deathcause = "";

	// Nickname stats
	private boolean hasNick = false;
	private Nickname nickname;

	private Tregmine plugin;

	public TregminePlayer(Player player, Tregmine instance) {
		super(player);

		this.name = player.getName();
		this.realName = player.getName();
		this.loginTime = new Date();

		this.flags = EnumSet.noneOf(Flags.class);
		this.commandstatus = EnumSet.noneOf(CommandStatus.class);
		this.properties = EnumSet.noneOf(Property.class);
		this.badges = new EnumMap<Badge, Integer>(Badge.class);
		this.plugin = instance;
	}

	public TregminePlayer(UUID uuid, Tregmine instance) {
		super(null);
		OfflinePlayer player = Bukkit.getServer().getOfflinePlayer(uuid);
		this.name = player.getName();
		this.realName = player.getName();
		this.loginTime = new Date();

		this.flags = EnumSet.noneOf(Flags.class);
		this.commandstatus = EnumSet.noneOf(CommandStatus.class);
		this.properties = EnumSet.noneOf(Property.class);
		this.badges = new EnumMap<Badge, Integer>(Badge.class);
		this.plugin = instance;
	}

	public void addReport(String[] report) {
		this.reports.add(report);
	}

	public boolean alertedAfk() {
		return this.alertedAfk;
	}

	public void awardBadgeLevel(Badge badge, String message) {
		int badgeLevel = getBadgeLevel(badge) + 1;
		badges.put(badge, badgeLevel);

		if (badgeLevel == 1) {
			sendStringMessage(ChatColor.GOLD + "Congratulations! You've been awarded " + "the " + badge.getName()
					+ " badge of honor: " + message);
		} else {
			sendStringMessage(ChatColor.GOLD + "Congratulations! You've been awarded " + "the level " + ChatColor.GREEN
					+ badgeLevel + " " + ChatColor.GOLD + badge.getName() + "badge of honor: " + message);
		}
	}

	public boolean canMentor() {
		if (hasFlag(TregminePlayer.Flags.SOFTWARNED) || hasFlag(TregminePlayer.Flags.HARDWARNED)) {

			return false;
		}

		return getRank().canMentor();
	}

	public void setCommandStatus(CommandStatus status) {
		this.commandstatus.add(status);
	}

	public boolean hasCommandStatus(CommandStatus status) {
		return this.commandstatus.contains(status);
	}

	public void removeCommandStatus(CommandStatus status) {
		this.commandstatus.remove(status);
	}

	public boolean canVS() {
		return this.getRank().canViewPlayerStats();
	}

	public String causeOfDeath() {
		return this.deathcause;
	}

	public void checkActivity() {
		long autoafkkick = plugin.getConfig().getInt("general.afk.timeout");
		if (autoafkkick > 0 && lastOnlineActivity > 0
				&& (lastOnlineActivity + (autoafkkick * 1000)) < System.currentTimeMillis() && this.AfkKick == true
				&& !this.getRank().bypassAFKKick()) {
			String reason = ChatColor.RED + "You were kicked from " + ChatColor.GOLD
					+ plugin.getConfig().getString("general.servername") + ChatColor.RED + " for idling longer than "
					+ autoafkkick + " seconds.";
			this.causeofquit = QuitCause.AFK;
			this.lastOnlineActivity = 0;
			this.setSilentAfk(false);
			this.kickPlayer(this.plugin, reason);
			plugin.broadcast(new TextComponent(ChatColor.GRAY + ""), this.getChatName(), new TextComponent(
					ChatColor.GRAY + " was kicked for idling longer than " + autoafkkick / 60 + " minutes."));
		}
		long autoafk = plugin.getConfig().getLong("general.afk.autoafk");
		if (!isAfk() && autoafk > 0 && lastOnlineActivity + autoafk * 1000 < System.currentTimeMillis()) {
			setAfk(true);
		}

	}

	public TextComponent decideVS(TregminePlayer canthey) {
		if (canthey.canVS()) {
			return this.getChatNameStaff();
		} else {
			return this.getChatName();
		}
	}

	public QuitCause getQuitCause() {
		return this.causeofquit;
	}

	public void setQuitCause(QuitCause q) {
		this.causeofquit = q;
	}

	// java.lang.Object overrides
	@Override
	public boolean equals(Object obj) {
		return ((TregminePlayer) obj).getId() == getId();
	}

	public boolean getAfkKick() {
		return AfkKick;
	}

	public PermissionAttachment getAttachment() {
		return this.attachment;
	}

	public int getBadgeLevel(Badge badge) {
		if (!hasBadge(badge)) {
			return 0;
		} else {
			return badges.get(badge);
		}
	}

	public Map<Badge, Integer> getBadges() {
		return badges;
	}

	public int getBlessTarget() {
		return blessTarget;
	}

	public String getChatChannel() {
		return chatChannel;
	}

	public TextComponent getChatName() {
		TextComponent returns = new TextComponent(this.name);
		if (this.hasFlag(Flags.CHILD)) {
			returns.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
					new ComponentBuilder(this.getRank().getName(plugin) + "\n" + ChatColor.AQUA + "CHILD").create()));
		} else {
			returns.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
					new ComponentBuilder(this.getRank().getName(plugin)).create()));
		}
		return returns;
	}

	public String getChatNameNoColor() {
		return ChatColor.stripColor(this.getChatNameNoHover());
	}

	public String getChatNameNoHover() {
		return name;
	}

	public TextComponent getChatNameStaff() {
		TextComponent returns = new TextComponent(this.name);
		String addon = "";
		if (this.hasNick) {
			addon += "\n" + ChatColor.AQUA + "Real name: " + this.name;
		}
		if (this.getTotalBans() != 0) {
			addon += "\n" + ChatColor.DARK_GRAY + "Bans: " + this.getTotalBans();
		}
		if (this.getTotalKicks() != 0) {
			addon += "\n" + ChatColor.GRAY + "Kicks: " + this.getTotalKicks();
		}
		if (this.getTotalHards() != 0) {
			addon += "\n" + ChatColor.DARK_GRAY + "Hard-Warns: " + this.getTotalHards();
		}
		if (this.getTotalSofts() != 0) {
			addon += "\n" + ChatColor.GRAY + "Soft-Warns " + this.getTotalSofts();
		}
		if (this.hasFlag(Flags.CHILD)) {
			returns.setHoverEvent(
					new HoverEvent(HoverEvent.Action.SHOW_TEXT,
							new ComponentBuilder(
									this.getRank().getName(plugin) + "\n" + ChatColor.AQUA + "CHILD" + addon)
											.create()));
		} else {
			returns.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
					new ComponentBuilder(this.getRank().getName(plugin) + addon).create()));
		}
		return returns;
	}

	public ChatState getChatState() {
		return chatState;
	}

	public String getCity() {
		return city;
	}

	public int getCombatLog() {
		return combatLog;
	}

	public String getCountry() {
		return country;
	}

	public FishyBlock getCurrentFishyBlock() {
		return currentFishyBlock;
	}

	public String getCurrentInventory() {
		return currentInventory;
	}

	public Block getFillBlock1() {
		return fillBlock1;
	}

	public Block getFillBlock2() {
		return fillBlock2;
	}

	public int getFillBlockCounter() {
		return fillBlockCounter;
	}

	public int getFishyBuyCount() {
		return fishyBuyCount;
	}

	public boolean getFrozen() {
		return isFrozen;
	}

	public int getGuardianRank() {
		return guardianRank;
	}

	public GuardianState getGuardianState() {
		return guardianState;
	}

	public String getHost() {
		return host;
	}

	public int getId() {
		return id;
	}

	public String getIp() {
		return ip;
	}

	public boolean getIsAdmin() {
		boolean isStaff = false;
		if (rank == Rank.JUNIOR_ADMIN || rank == Rank.SENIOR_ADMIN || isOp()) {
			isStaff = true;
		}
		return isStaff;
	}

	public boolean getIsStaff() {
		return Staff;
	}

	public String getKeyword() {
		return keyword;
	}

	public String getLastMessenger() {
		return lastMessenger;
	}

	public long getLastOnlineActivity() {
		return lastOnlineActivity;
	}

	public Location getLastPos() {
		return this.lastpos;
	}

	public TregminePlayer getMentor() {
		return mentor;
	}

	public ChatColor getNameColor() {
		if (hasFlag(Flags.SOFTWARNED)) {
			return ChatColor.GRAY;
		} else if (hasFlag(Flags.HARDWARNED)) {
			return ChatColor.GRAY;
		} else if (hasFlag(Flags.CHILD)) {
			return ChatColor.AQUA;
		}

		if (rank == null) {
			return ChatColor.WHITE;
		} else if (rank == Rank.GUARDIAN) {
			switch (guardianState) {
			case ACTIVE:
				return plugin.getRankColor(Rank.GUARDIAN);
			case INACTIVE:
			case QUEUED:
				return ChatColor.GOLD;
			default:
			}
		}

		return plugin.getRankColor(rank);
	}

	public boolean getNewChunk() {
		return newChunk;
	}

	public FishyBlock getNewFishyBlock() {
		return newFishyBlock;
	}

	public Nickname getNickname() {
		return this.nickname;
	}

	public String getPasswordHash() {
		return password;
	}

	public int getPlayTime() {
		return playTime;
	}

	public Tregmine getPlugin() {
		return plugin;
	}

	public String getQuitMessage() {
		return quitMessage;
	}

	public Rank getRank() {
		if (isTemporaryRank) {
			return temporaryRank;
		} else {
			return rank;
		}
	}

	public String getRealName() {
		return realName;
	}

	public List<String[]> getReports() {
		return this.reports;
	}

	public int getReportTotal() {
		return this.reports.size();
	}

	public UUID getStoredUuid() {
		return storedUuid;
	}

	public TregminePlayer getStudent() {
		return student;
	}

	public int getTargetZoneId() {
		return targetZoneId;
	}

	public int getTimeOnline() {
		return (int) ((new Date().getTime() - loginTime.getTime()) / 1000L);
	}

	public int getTotalBans() {
		return this.bans;
	}

	public int getTotalHards() {
		return this.hardwarns;
	}

	public int getTotalKicks() {
		return this.kicks;
	}

	public int getTotalSofts() {
		return this.softwarns;
	}

	public Rank getTrueRank() {
		return rank;
	}

	public Block getZoneBlock1() {
		return zoneBlock1;
	}

	public Block getZoneBlock2() {
		return zoneBlock2;
	}

	public int getZoneBlockCounter() {
		return zoneBlockCounter;
	}

	public void gotoWorld(Player player, Location loc, String success, String failure) {
		World world = loc.getWorld();
		Chunk chunk = world.getChunkAt(loc);
		world.loadChunk(chunk);
		if (world.isChunkLoaded(chunk)) {
			plugin.getPlayer(player).teleportWithHorse(loc);
			player.sendMessage(success);
		} else {
			player.sendMessage(failure);
		}
	}

	public boolean hasBadge(Badge badge) {
		return badges.containsKey(badge);
	}


	public boolean hasFlag(Flags flag) {
		return flags.contains(flag);
	}

	// non-persistent state methods

	@Override
	public int hashCode() {
		return getId();
	}

	public boolean hasNick() {
		return this.hasNick;
	}

	public boolean hasProperty(Property prop) {
		return properties.contains(prop);
	}

	// convenience methods
	public void hidePlayer(TregminePlayer player) {
		hidePlayer(player.getDelegate());
	}

	public boolean isAfk() {
		return this.afk;
	}

	public boolean isCombatLogged() {
		if (combatLog > 0) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isCurseWarned() {
		return this.CurseWarned;
	}

	public boolean isHidden() {
		return this.hasFlag(Flags.INVISIBLE);
	}

	public boolean isInVanillaWorld() {
		if (this.getWorld() == this.plugin.getVanillaWorld() || this.getWorld() == this.plugin.getVanillaNether()
				|| this.getWorld() == this.plugin.getVanillaEnd()) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean isValid() {
		return valid;
	}

	public void removeFlag(Flags flag) {
		flags.remove(flag);
	}

	public void removeProperty(Property prop) {
		properties.remove(prop);
	}

	public void resetTimeOnline() {
		loginTime = new Date();
	}

	public void sendNotification(Notification notif) {
		playSound(getLocation(), notif.getSound(), 2F, 1F);
	}

	/**
	 * Sends the player a notification along with an associated message.
	 * <p>
	 *
	 * If the message is <b>null</b> or equal to "", the message won't send,
	 * however the notification will still play.
	 *
	 * If the notification is <b>null</b>, and the message is not if will send
	 * the player the message.
	 * 
	 * @param notif
	 *            - The notification to send to the player
	 * @param message
	 *            - The message to send the player with the notification
	 * @throws IllegalArgumentException
	 *             if both notif and message are null
	 */
	public void sendNotification(Notification notif, BaseComponent... message) {
		if (notif != null && notif != Notification.NONE) {
			playSound(getLocation(), notif.getSound(), 2F, 1F);
			sendSpigotMessage(message);
		} else {
			sendSpigotMessage(message);
		}
	}

	public void sendNotification(Notification notif, BaseComponent message) {
		if (notif != null && notif != Notification.NONE) {
			playSound(getLocation(), notif.getSound(), 2F, 1F);
			sendSpigotMessage(message);
		} else {
			sendSpigotMessage(message);
		}
	}

	public void setAfk(boolean value) {
		if (value == true) {
			this.afk = true;
			if (!this.isHidden()) {
				this.plugin.broadcast(new TextComponent(ITALIC + ""), getChatName(),
						new TextComponent(RESET + "" + BLUE + " is now afk."));
			}
			String oldname = getChatNameNoHover();
			setTemporaryChatName(GRAY + "[AFK] " + RESET + oldname);
		} else if (value == false) {
			final long currentTime = System.currentTimeMillis();
			this.setLastOnlineActivity(currentTime);
			this.afk = false;
			setTemporaryChatName(getNameColor() + getRealName());
			if (!this.isHidden()) {
				TextComponent message = new TextComponent(
						ITALIC + "" + getChatName() + RESET + GREEN + " is no longer afk.");
				this.plugin.broadcast(new TextComponent(ITALIC + ""), getChatName(),
						new TextComponent(RESET + "" + GREEN + " is no longer afk."));
			}
		}
	}

	public void setAfkKick(boolean a) {
		this.AfkKick = a;
	}

	public void setMute(PlayerMute p0) {
		this.mute = p0;
	}

	public void setMuted(boolean p0) {
		this.muted = p0;
	}

	public boolean isMuted() {
		return this.muted;
	}

	public PlayerMute getMute() {
		return this.mute;
	}

	public void setAlerted(boolean a) {
		this.alertedAfk = a;
	}

	public void setAttachment(PermissionAttachment ment) {
		this.attachment = ment;
	}

	public void setBadges(Map<Badge, Integer> v) {
		this.badges = v;
	}

	public void setBlessTarget(int v) {
		this.blessTarget = v;
	}

	public void setChatChannel(String v) {
		this.chatChannel = v;
	}

	public void setChatState(ChatState v) {
		this.chatState = v;
	}

	public void setCity(String v) {
		this.city = v;
	}

	public void setCombatLog(int value) {
		this.combatLog = value;
	}

	public void setCountry(String v) {
		this.country = v;
	}

	public void setCurrentFishyBlock(FishyBlock v) {
		this.currentFishyBlock = v;
	}

	public void setCurrentInventory(String inv) {
		this.currentInventory = inv;
	}

	public void setCurrentTexture(String url) {
		/*
		 * if (url == null) { this.texture =
		 * "https://dl.dropbox.com/u/5405236/mc/df.zip"; }
		 * 
		 * if (!url.equals(this.texture)) { this.texture = url;
		 * setTexturePack(url); }
		 */
	}

	public void setCurseWarned(boolean a) {
		this.CurseWarned = a;
	}

	public void setDeathCause(String a) {
		this.deathcause = a;
	}

	// block fill state
	public void setFillBlock1(Block v) {
		this.fillBlock1 = v;
	}

	public void setFillBlock2(Block v) {
		this.fillBlock2 = v;
	}

	public void setFillBlockCounter(int v) {
		this.fillBlockCounter = v;
	}

	public void setFishyBuyCount(int v) {
		this.fishyBuyCount = v;
	}

	public void setFlag(Flags flag) {
		flags.add(flag);
	}

	public void setFrozen(boolean v) {
		isFrozen = v;
	}

	public void setGuardianRank(int v) {
		this.guardianRank = v;
	}

	public void setGuardianState(GuardianState v) {
		this.guardianState = v;

		setTemporaryChatName(getNameColor() + getRealName());
	}

	public void setHost(String v) {
		this.host = v;
	}

	public void setId(int v) {
		this.id = v;
	}

	public void setIp(String v) {
		this.ip = v;
	}

	public void setKeyword(String v) {
		this.keyword = v;
	}

	public void setLastMessenger(String messenger) {
		this.lastMessenger = messenger;
	}

	public void setLastOnlineActivity(long a) {
		lastOnlineActivity = a;
	}

	public void setLastPos(Location pos) {
		this.lastpos = pos;
	}

	public void setMentor(TregminePlayer v) {
		this.mentor = v;
	}

	public void setNewChunk(boolean value) {
		this.newChunk = value;
	}

	// Fishy block state
	public void setNewFishyBlock(FishyBlock v) {
		this.newFishyBlock = v;
	}

	public void setNick(Nickname n) {
		this.nickname = n;
		this.hasNick = true;
		this.name = n.getNickname();
	}

	public void setPassword(String newPassword) {
		password = BCrypt.hashpw(newPassword, BCrypt.gensalt());
	}

	public void setPasswordHash(String v) {
		password = v;
	}

	public void setPlayTime(int v) {
		this.playTime = v;
	}

	public void setProperty(Property prop) {
		properties.add(prop);
	}

	public void setQuitMessage(String v) {
		this.quitMessage = v;
	}

	public void setRank(Rank v) {
		this.rank = v;
		if (v == Rank.GUARDIAN || v == Rank.JUNIOR_ADMIN || v == Rank.SENIOR_ADMIN || v == Rank.CODER) {
			this.Staff = true;
		}
		setTemporaryChatName(getNameColor() + getRealName());
	}

	public void setSilentAfk(boolean value) {
		if (this.isHidden()) {
			return;
		}
		if (value == true) {
			this.afk = true;
			String oldname = getChatNameNoHover();
			setTemporaryChatName(GRAY + "[AFK] " + RESET + oldname);
		} else if (value == false) {
			final long currentTime = System.currentTimeMillis();
			this.setLastOnlineActivity(currentTime);
			this.afk = false;
			setTemporaryChatName(getNameColor() + getRealName());
		} else {
			return;
		}
	}

	public void setStaff(boolean v) {
		this.Staff = v;
	}

	public void setStoredUuid(UUID v) {
		this.storedUuid = v;
	}

	public void setStudent(TregminePlayer v) {
		this.student = v;
	}

	public void setTargetZoneId(int v) {
		this.targetZoneId = v;
	}

	public void setTemporaryChatName(String name) {
		this.name = name;

		if (getDelegate() != null) {
			if (getChatNameNoHover().length() > 16) {
				setPlayerListName(name.substring(0, 15));
			} else {
				setPlayerListName(name);
			}
		}
	}

	public void setTemporaryRank(Rank v) {
		this.temporaryRank = v;
		this.isTemporaryRank = true;
		if (v == Rank.GUARDIAN || v == Rank.JUNIOR_ADMIN || v == Rank.SENIOR_ADMIN || v == Rank.CODER) {
			this.Staff = true;
		}
		setTemporaryChatName(getNameColor() + getRealName());
	}

	public void setTotalBans(int total) {
		this.bans = total;
	}

	public void setTotalHards(int total) {
		this.hardwarns = total;
	}

	// -----------------------------//
	// Tregmine Inventory Handling //
	// -----------------------------//

	public void setTotalKicks(int total) {
		this.kicks = total;
	}

	public void setTotalSofts(int total) {
		this.softwarns = total;
	}

	public void setValid(boolean v) {
		this.valid = v;
	}

	// zones state
	public void setZoneBlock1(Block v) {
		this.zoneBlock1 = v;
	}

	public void setZoneBlock2(Block v) {
		this.zoneBlock2 = v;
	}

	public void setZoneBlockCounter(int v) {
		this.zoneBlockCounter = v;
	}

	public void showPlayer(TregminePlayer player) {
		showPlayer(player.getDelegate());
	}
	
	public void teleportWithHorse(Location loc) {
		World cWorld = loc.getWorld();
		String[] worldNamePortions = cWorld.getName().split("_");

		Entity v = getVehicle();
		if (v != null && v instanceof Horse) {
			if (!worldNamePortions[0].equalsIgnoreCase("world")) {
				this.sendStringMessage(ChatColor.RED + "Can not teleport with horse! Sorry!");
				return;
			}

			Horse horse = (Horse) v;
			horse.eject();
			horse.teleport(loc);
			teleport(loc);
			horse.setPassenger(getDelegate());
		} else {
			teleport(loc);
		}
	}

	public boolean verifyPassword(String attempt) {
		return BCrypt.checkpw(attempt, password);
	}

}
