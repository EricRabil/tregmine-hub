package info.tregmine.database.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;

import info.tregmine.database.DAOException;
import info.tregmine.database.IMiscDAO;

public class DBMiscDAO implements IMiscDAO {
	private Connection conn;

	public DBMiscDAO(Connection conn) {
		this.conn = conn;
	}

	@Override
	public boolean blocksWereChanged(Location start, int radius) throws DAOException {
		String sql = "SELECT * FROM stats_blocks " + "WHERE (x BETWEEN ? AND ?) " + "AND (y BETWEEN ? AND ?) "
				+ "AND (z BETWEEN ? AND ?) " + "AND world = ?";
		try (PreparedStatement stm = conn.prepareStatement(sql)) {
			int x = start.getBlockX();
			int y = start.getBlockY();
			int z = start.getBlockZ();
			String world = start.getWorld().getName();

			stm.setInt(1, x - radius);
			stm.setInt(2, x + radius);
			stm.setInt(3, y - radius);
			stm.setInt(4, y + radius);
			stm.setInt(5, z - radius);
			stm.setInt(6, z + radius);
			stm.setString(7, world);
			stm.execute();

			try (ResultSet rs = stm.getResultSet()) {
				return rs.next();
			}
		} catch (SQLException e) {
			throw new DAOException(sql, e);
		}
	}

	@Override
	public List<String> loadBannedWords() throws DAOException {
		String sql = "SELECT * FROM banned_words";
		List<String> words = new ArrayList<String>();

		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.execute();

			try (ResultSet rs = stmt.getResultSet()) {
				while (rs.next()) {
					words.add(rs.getString("word"));
				}
			}
		} catch (SQLException e) {
			throw new DAOException(sql, e);
		}

		return words;
	}

	/*
	 * Table Structure Table: banned_words Column 1: word VARCHAR(64) NOT NULL
	 * NO AI
	 * 
	 */

	@Override
	public List<String> loadInsults() throws DAOException {
		// Structure: [message_id, message_type (enum: 'INSULT','QUIT'),
		// message_value]
		String sql = "SELECT * FROM misc_message WHERE message_type = 'INSULT'";
		List<String> insults = new ArrayList<String>();

		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.execute();

			try (ResultSet rs = stmt.getResultSet()) {
				while (rs.next()) {
					insults.add(ChatColor.translateAlternateColorCodes('#', rs.getString("message_value")));
				}
			}
		} catch (SQLException e) {
			throw new DAOException(sql, e);
		}

		return insults;
	}

	@Override
	public List<String> loadQuitMessages() throws DAOException {
		// Structure: [message_id, message_type (enum: 'INSULT','QUIT'),
		// message_value]
		String sql = "SELECT * FROM misc_message WHERE message_type = 'QUIT'";
		List<String> messages = new ArrayList<String>();

		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.execute();

			try (ResultSet rs = stmt.getResultSet()) {
				while (rs.next()) {
					messages.add(rs.getString("message_value"));
				}
			}
		} catch (SQLException e) {
			throw new DAOException(sql, e);
		}

		return messages;
	}

}
