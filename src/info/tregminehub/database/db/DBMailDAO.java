package info.tregminehub.database.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import info.tregminehub.Tregmine;
import info.tregminehub.api.TregminePlayer;
import info.tregminehub.database.DAOException;
import info.tregminehub.database.IMailDAO;

public class DBMailDAO implements IMailDAO {
	private Connection conn;
	private Tregmine tregmine;

	public DBMailDAO(Connection v, Tregmine x) {
		conn = v;
		this.tregmine = x;
	}

	@Override
	public boolean deleteMail(String username, int mailId) throws DAOException {
		String rawstmt = "UPDATE player_mail SET deleted='true' WHERE receiver_name = ? AND mail_id = ? AND deleted = 'false'";
		try (PreparedStatement stmt = conn.prepareStatement(rawstmt)) {
			stmt.setString(1, username);
			stmt.setInt(2, mailId);
			int amount = stmt.executeUpdate();
			if (amount != 1) {
				return false;
			}
		} catch (SQLException e) {
			throw new DAOException(rawstmt, e);
		}
		return true;
	}
	// ERIC WOZ HER 2K16 4/16 FUK YALL

	@Override
	public List<String[]> getAllMail(String username) throws DAOException {
		List<String[]> mail = new ArrayList<String[]>();
		String rawstmt = "SELECT * FROM player_mail WHERE receiver_name = ? AND deleted = 'false'";
		try (PreparedStatement stmt = conn.prepareStatement(rawstmt)) {
			stmt.setString(1, username);
			stmt.execute();
			try (ResultSet rs = stmt.getResultSet()) {
				while (rs.next()) {
					String[] tempMail = new String[6];
					tempMail[0] = rs.getString("sender_name");
					tempMail[1] = rs.getString("deleted");
					tempMail[2] = rs.getString("receiver_name");
					tempMail[3] = rs.getString("message");
					int mailid = rs.getInt("mail_id");
					String s_mailid = Integer.toString(mailid);
					tempMail[4] = s_mailid;
					tempMail[5] = rs.getString("timestamp");
					mail.add(tempMail);
				}
			}
		} catch (SQLException e) {
			throw new DAOException(rawstmt, e);
		}
		return mail;
	}

	@Override
	public int getMailTotal(String username) throws DAOException {
		int total = 0;
		String rawstmt = "SELECT * FROM player_mail WHERE receiver_name = ? AND deleted = 'false'";
		try (PreparedStatement stmt = conn.prepareStatement(rawstmt)) {
			stmt.setString(1, username);
			stmt.execute();
			try (ResultSet rs = stmt.getResultSet()) {
				while (rs.next()) {
					total = total + 1;
				}
			}
		} catch (SQLException e) {
			throw new DAOException(rawstmt, e);
		}
		return total;
	}

	@Override
	public int getMailTotalEver(String username) throws DAOException {
		int total = 0;
		String rawstmt = "SELECT * FROM player_mail WHERE receiver_name = ?";
		try (PreparedStatement stmt = conn.prepareStatement(rawstmt)) {
			stmt.setString(1, username);
			stmt.execute();
			try (ResultSet rs = stmt.getResultSet()) {
				while (rs.next()) {
					total = total + 1;
				}
			}
		} catch (SQLException e) {
			throw new DAOException(rawstmt, e);
		}
		return total;
	}

	@Override
	public boolean sendMail(TregminePlayer player, String sendTo, String message) throws DAOException {
		String username = player.getName();
		DBPlayerDAO playerdao = new DBPlayerDAO(conn);
		TregminePlayer receiver = this.tregmine.getPlayer(sendTo);
		if (receiver == null) {
			return false;
		}
		String rawstmt = "INSERT INTO player_mail (sender_name, receiver_name, message, deleted) VALUES (?, ?, ?, ?)";
		try (PreparedStatement stmt = conn.prepareStatement(rawstmt)) {
			stmt.setString(1, username);
			stmt.setString(2, sendTo);
			stmt.setString(3, message);
			stmt.setString(4, "false");
			stmt.execute();
		} catch (SQLException e) {
			throw new DAOException(rawstmt, e);
		}
		return true;
	}
}
