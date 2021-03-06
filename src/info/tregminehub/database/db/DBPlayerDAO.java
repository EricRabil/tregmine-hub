package info.tregminehub.database.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Player;

import info.tregminehub.Tregmine;
import info.tregminehub.api.Rank;
import info.tregminehub.api.TregminePlayer;
import info.tregminehub.api.TregminePlayer.Property;
import info.tregminehub.database.DAOException;
import info.tregminehub.database.IPlayerDAO;

public class DBPlayerDAO implements IPlayerDAO {
	private Connection conn;
	private Tregmine plugin;

	public DBPlayerDAO(Connection conn) {
		this.conn = conn;
	}

	public DBPlayerDAO(Connection conn, Tregmine instance) {
		this.conn = conn;
		this.plugin = instance;
	}

	@Override
	public TregminePlayer createPlayer(Player wrap) throws DAOException {
		String sql = "INSERT INTO player (player_uuid, player_name, player_rank, player_keywords) VALUE (?, ?, ?, ?)";

		TregminePlayer player = new TregminePlayer(wrap, plugin);
		player.setStoredUuid(player.getUniqueId());

		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, player.getUniqueId().toString());
			stmt.setString(2, player.getName());
			stmt.setString(3, player.getTrueRank().toString());
			stmt.setString(4, player.getRealName());
			stmt.execute();

			stmt.executeQuery("SELECT LAST_INSERT_ID()");

			try (ResultSet rs = stmt.getResultSet()) {
				if (!rs.next()) {
					throw new DAOException("Failed to get player id", sql);
				}

				player.setId(rs.getInt(1));
			}
		} catch (SQLException e) {
			throw new DAOException(sql, e);
		}

		return player;
	}

	@Override
	public boolean doesIgnore(TregminePlayer player, TregminePlayer victim) throws DAOException {
		return false;
		// String sql = "SELECT * FROM player " +
		// "WHERE player_id = ? ";
		//
		// try (PreparedStatement stmt = conn.prepareStatement(sql)) {
		// stmt.setInt(1, player.getId());
		// stmt.execute();
		//
		// try (ResultSet rs = stmt.getResultSet()) {
		// if(!rs.next()) return false;
		//
		// String stringofignored = rs.getString("player_ignore");
		// String[] strings = stringofignored.split(",");
		//
		// List<String> playerignore = new ArrayList<String>();
		// for (String i : strings){
		// if("".equalsIgnoreCase(i)) continue;
		// playerignore.add(i);
		// }
		//
		// if (playerignore.contains(victim.getRealName())) {
		// return true;
		// } else {
		// return false;
		// }
		// }
		// } catch (SQLException e) {
		// throw new DAOException(sql, e);
		// }
	}

	@Override
	public List<String> getIgnored(TregminePlayer to) throws DAOException {
		String sql = "SELECT * FROM player " + "WHERE player_id = ? ";

		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, to.getId());
			stmt.execute();

			try (ResultSet rs = stmt.getResultSet()) {
				if (!rs.next())
					return null;
				String[] strings;
				String stringofignored = rs.getString("player_ignore");
				if (stringofignored == null) {
					strings = new String[0];
				} else {
					strings = stringofignored.split(",");
				}
				List<String> playerignore = new ArrayList<String>();
				for (String i : strings) {
					if ("".equalsIgnoreCase(i))
						continue;
					playerignore.add(i);
				}

				return playerignore;
			}
		} catch (SQLException e) {
			throw new DAOException(sql, e);
		}
	}

	@Override
	public List<String> getKeywords(TregminePlayer to) throws DAOException {
		String sql = "SELECT * FROM player " + "WHERE player_id = ? ";

		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, to.getId());
			stmt.execute();

			try (ResultSet rs = stmt.getResultSet()) {
				if (!rs.next())
					return null;

				String stringofkeywords = rs.getString("player_keywords");
				String[] strings = stringofkeywords.split(",");

				List<String> playerkeywords = new ArrayList<String>();
				for (String i : strings) {
					if ("".equalsIgnoreCase(i))
						continue;
					playerkeywords.add(i);
				}

				return playerkeywords;
			}
		} catch (SQLException e) {
			throw new DAOException(sql, e);
		}
	}

	@Override
	public TregminePlayer getPlayer(int id) throws DAOException {
		String sql = "SELECT * FROM player WHERE player_id = ?";

		TregminePlayer player = null;

		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, id);
			stmt.execute();

			try (ResultSet rs = stmt.getResultSet()) {
				if (!rs.next()) {
					return null;
				}

				player = new TregminePlayer(UUID.fromString(rs.getString("player_uuid")), plugin);
				player.setId(rs.getInt("player_id"));

				String uniqueIdStr = rs.getString("player_uuid");
				if (uniqueIdStr != null) {
					player.setStoredUuid(UUID.fromString(uniqueIdStr));
				}
				player.setPasswordHash(rs.getString("player_password"));
				player.setRank(Rank.fromString(rs.getString("player_rank")));

				if (rs.getString("player_inventory") == null) {
					player.setCurrentInventory("survival");
				} else {
					player.setCurrentInventory(rs.getString("player_inventory"));
				}

				int flags = rs.getInt("player_flags");
				for (TregminePlayer.Flags flag : TregminePlayer.Flags.values()) {
					if ((flags & (1 << flag.ordinal())) != 0) {
						player.setFlag(flag);
					}
				}
			}
		} catch (SQLException e) {
			throw new DAOException(sql, e);
		}

		loadSettings(player);

		return player;
	}

	@Override
	public TregminePlayer getPlayer(UUID id) throws DAOException {
		String sql = "SELECT * FROM player WHERE player_uuid = ?";

		TregminePlayer player = null;

		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, id.toString());
			stmt.execute();

			try (ResultSet rs = stmt.getResultSet()) {
				if (!rs.next()) {
					return null;
				}

				player = new TregminePlayer(UUID.fromString(rs.getString("player_uuid")), plugin);
				player.setId(rs.getInt("player_id"));

				String uniqueIdStr = rs.getString("player_uuid");
				if (uniqueIdStr != null) {
					player.setStoredUuid(UUID.fromString(uniqueIdStr));
				}
				player.setPasswordHash(rs.getString("player_password"));
				player.setRank(Rank.fromString(rs.getString("player_rank")));

				if (rs.getString("player_inventory") == null) {
					player.setCurrentInventory("survival");
				} else {
					player.setCurrentInventory(rs.getString("player_inventory"));
				}

				int flags = rs.getInt("player_flags");
				for (TregminePlayer.Flags flag : TregminePlayer.Flags.values()) {
					if ((flags & (1 << flag.ordinal())) != 0) {
						player.setFlag(flag);
					}
				}
			}
		} catch (SQLException e) {
			throw new DAOException(sql, e);
		}

		loadSettings(player);

		return player;
	}

	@Override
	public TregminePlayer getPlayer(Player wrap) throws DAOException {
		String sql = "SELECT * FROM player WHERE player_uuid = ?";
		String sql1 = "UPDATE `player` SET player_name = ? WHERE player_uuid = ?";
		try (PreparedStatement stmt1 = conn.prepareStatement(sql1)) {
			stmt1.setString(1, wrap.getName());
			stmt1.setString(2, wrap.getUniqueId().toString());
			stmt1.execute();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		TregminePlayer player;
		if (wrap != null) {
			player = new TregminePlayer(wrap, plugin);
		} else {
			return null;
		}

		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, wrap.getUniqueId().toString());
			stmt.execute();

			try (ResultSet rs = stmt.getResultSet()) {
				if (!rs.next()) {
					return null;
				}
				if (rs.getString("player_name") != wrap.getName()) {
					// Name change! Call 911!

				}

				UUID uniqueId = wrap.getUniqueId();

				player.setId(rs.getInt("player_id"));
				player.setStoredUuid(uniqueId);
				player.setPasswordHash(rs.getString("player_password"));
				player.setRank(Rank.fromString(rs.getString("player_rank")));

				if (rs.getString("player_inventory") == null) {
					player.setCurrentInventory("survival");
				} else {
					player.setCurrentInventory(rs.getString("player_inventory"));
				}

				int flags = rs.getInt("player_flags");
				for (TregminePlayer.Flags flag : TregminePlayer.Flags.values()) {
					if ((flags & (1 << flag.ordinal())) != 0) {
						player.setFlag(flag);
					}
				}
			}
		} catch (SQLException e) {
			throw new DAOException(sql, e);
		}

		loadSettings(player);
		loadReports(player);
		return player;
	}

	private void loadReports(TregminePlayer player) throws DAOException {
		String sql = "SELECT * FROM player_report WHERE subject_id = ?";
		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, player.getId());
			stmt.execute();
			try (ResultSet rs = stmt.getResultSet()) {
				while (rs.next()) {
					if ("softwarn".equals(rs.getString("report_action"))) {
						player.setTotalSofts(player.getTotalSofts() + 1);
					}
					if ("hardwarn".equals(rs.getString("report_action"))) {
						player.setTotalHards(player.getTotalHards() + 1);
					}
					if ("kick".equals(rs.getString("report_action"))) {
						player.setTotalKicks(player.getTotalKicks() + 1);
					}
					if ("ban".equals(rs.getString("report_action"))) {
						player.setTotalBans(player.getTotalBans() + 1);
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void loadSettings(TregminePlayer player) throws DAOException {
		String sql = "SELECT * FROM player_property WHERE player_id = ?";

		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, player.getId());
			stmt.execute();

			try (ResultSet rs = stmt.getResultSet()) {
				while (rs.next()) {
					String key = rs.getString("property_key");
					String value = rs.getString("property_value");
					if ("keyword".equals(key)) {
						player.setKeyword(value);
					} else if ("guardian".equals(key)) {
						player.setGuardianRank(Integer.parseInt(value));
					} else if ("quitmessage".equals(key)) {
						player.setQuitMessage(value);
					} else if ("playtime".equals(key)) {
						player.setPlayTime(Integer.parseInt(value));
					} else if ("afkkick".equals(key)) {
						player.setAfkKick(Boolean.valueOf(value));
					} else if ("cursewarned".equals(key)) {
						player.setCurseWarned(Boolean.valueOf(value));
					} else if ("nick".equals(key)) {
						player.setProperty(Property.NICKNAME);
						player.setTemporaryChatName(player.getRank().getName(plugin) + value);
					}
				}
			}
		} catch (SQLException e) {
			throw new DAOException(sql, e);
		}
	}

	@Override
	public void updateIgnore(TregminePlayer player, List<String> update) throws DAOException {
		String sql = "UPDATE player SET player_ignore = ? " + "WHERE player_id = ?";

		StringBuilder buffer = new StringBuilder();
		String delim = "";
		for (String ignored : update) {
			buffer.append(delim);
			buffer.append(ignored);
			delim = ",";
		}
		String updateIgnoreString = buffer.toString();

		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, updateIgnoreString);
			stmt.setInt(2, player.getId());
			stmt.execute();
		} catch (SQLException e) {
			throw new DAOException(sql, e);
		}
	}

	@Override
	public void updateKeywords(TregminePlayer player, List<String> update) throws DAOException {
		String sql = "UPDATE player SET player_keywords = ? " + "WHERE player_id = ?";

		StringBuilder buffer = new StringBuilder();
		String delim = "";
		for (String keyword : update) {
			buffer.append(delim);
			buffer.append(keyword);
			delim = ",";
		}
		String keywordsString = buffer.toString();

		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, keywordsString);
			stmt.setInt(2, player.getId());
			stmt.execute();
		} catch (SQLException e) {
			throw new DAOException(sql, e);
		}
	}

	@Override
	public void updatePlayer(TregminePlayer player) throws DAOException {
		String sql = "UPDATE player SET player_uuid = ?, player_password = ?, "
				+ "player_rank = ?, player_flags = ?, player_inventory = ? ";
		sql += "WHERE player_id = ?";

		int flags = 0;
		for (TregminePlayer.Flags flag : TregminePlayer.Flags.values()) {
			flags |= player.hasFlag(flag) ? 1 << flag.ordinal() : 0;
		}

		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, player.getStoredUuid().toString());
			stmt.setString(2, player.getPasswordHash());
			stmt.setString(3, player.getRank().toString());
			stmt.setInt(4, flags);
			stmt.setString(5, player.getCurrentInventory());
			stmt.setInt(6, player.getId());
			stmt.execute();
		} catch (SQLException e) {
			throw new DAOException(sql, e);
		}
	}

	@Override
	public void updatePlayerInfo(TregminePlayer player) throws DAOException {
		updateProperty(player, "quitmessage", player.getQuitMessage());
	}

	@Override
	public void updatePlayerKeyword(TregminePlayer player) throws DAOException {
		updateProperty(player, "keyword", player.getKeyword());
	}

	@Override
	public void updatePlayTime(TregminePlayer player) throws DAOException {
		int playTime = player.getPlayTime() + player.getTimeOnline();
		updateProperty(player, "playtime", String.valueOf(playTime));
	}

	private void updateProperty(TregminePlayer player, String key, boolean value) throws DAOException {
		updateProperty(player, key, String.valueOf(value));
	}

	private void updateProperty(TregminePlayer player, String key, int value) throws DAOException {
		updateProperty(player, key, String.valueOf(value));
	}

	@Override
	public void updateProperty(TregminePlayer player, String key, String value) throws DAOException {
		String sqlInsert = "REPLACE INTO player_property (player_id, "
				+ "property_key, property_value) VALUE (?, ?, ?)";

		if (value == null) {
			return;
		}

		try (PreparedStatement stmt = conn.prepareStatement(sqlInsert)) {
			stmt.setInt(1, player.getId());
			stmt.setString(2, key);
			stmt.setString(3, value);
			stmt.execute();
		} catch (SQLException e) {
			throw new DAOException(sqlInsert, e);
		}
	}
}
