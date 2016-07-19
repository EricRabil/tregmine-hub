package info.tregmine.database;

import java.util.List;

import info.tregmine.api.TregminePlayer;

public interface IMailDAO {
	public boolean deleteMail(String username, int mailId) throws DAOException;

	public List<String[]> getAllMail(String username) throws DAOException;

	public int getMailTotal(String username) throws DAOException;

	int getMailTotalEver(String username) throws DAOException;

	public boolean sendMail(TregminePlayer player, String sendTo, String message) throws DAOException;
}
