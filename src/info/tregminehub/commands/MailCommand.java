package info.tregminehub.commands;

import java.util.List;

import org.bukkit.ChatColor;

import info.tregminehub.Tregmine;
import info.tregminehub.api.TregminePlayer;
import info.tregminehub.database.DAOException;
import info.tregminehub.database.IContext;
import info.tregminehub.database.IMailDAO;
import net.md_5.bungee.api.chat.TextComponent;

public class MailCommand extends AbstractCommand {
	Tregmine instance;

	public MailCommand(Tregmine plugin) {
		super(plugin, "mail");
		instance = plugin;
	}

	private String argsToMessage(String[] args) {
		StringBuffer buf = new StringBuffer();
		for (int i = 2; i < args.length; ++i) {
			buf.append(" ");
			buf.append(args[i]);
		}

		return buf.toString();
	}

	public int getArgs(String[] args) {
		int total = 0;
		for (String text : args) {
			total = total + 1;
		}
		return total;
	}

	@Override
	public boolean handlePlayer(TregminePlayer player, String[] args) {
		if (args.length == 0) {
			sendHelp(player);
			return true;
		}
		if (args[0].contains("send")) {
			String msg = argsToMessage(args);
			try (IContext ctx = tregmine.createContext()) {
				IMailDAO mail = ctx.getMailDAO();
				boolean success = mail.sendMail(player, args[1], msg);
				if (success) {
					player.sendStringMessage(ChatColor.AQUA + "Message sent successfully! :)");
				} else {
					player.sendStringMessage(ChatColor.RED + "Message failed, check the username and send again.");
				}
			} catch (DAOException e) {
				throw new RuntimeException(e);
			}
			return true;
		} else if (args[0].contains("delete")) {
			if (getArgs(args) < 2) {
				player.sendStringMessage(ChatColor.RED + "You must specify what to delete!");
			} else {
				try (IContext ctx = tregmine.createContext()) {
					IMailDAO mail = ctx.getMailDAO();

					int toInt = Integer.parseInt(args[1]);
					boolean success = mail.deleteMail(player.getName(), toInt);
					if (success) {
						player.sendStringMessage(ChatColor.AQUA + "Message deleted successfully! :)");
					} else {
						player.sendStringMessage(ChatColor.RED + "Delete failed, check the id and try again.");
					}
				} catch (DAOException e) {
					throw new RuntimeException(e);
				} catch (NumberFormatException e) {
					player.sendStringMessage(ChatColor.RED + "You must input a number.");
				}
			}
			return true;
		} else if (args[0].contains("total")) {
			List<String[]> messages;
			try (IContext ctx = tregmine.createContext()) {
				IMailDAO mail = ctx.getMailDAO();
				messages = mail.getAllMail(player.getName());

				if (messages.size() == 0) {
					player.sendStringMessage(ChatColor.AQUA + "You haven't received any messages.");
				} else if (messages.size() == 1) {
					player.sendStringMessage(ChatColor.AQUA + "You have " + messages.size() + " message.");
				} else {
					player.sendStringMessage(ChatColor.AQUA + "You have " + messages.size() + " messages.");
				}
			} catch (DAOException e) {
				throw new RuntimeException(e);
			}
			return true;
		} else if (args[0].contains("tbt")) {
			if (player.getIsStaff()) {
				if (getArgs(args) < 2) {
					player.sendStringMessage(ChatColor.RED + "You must provide a player");
				} else {
					try (IContext ctx = tregmine.createContext()) {
						IMailDAO mail = ctx.getMailDAO();
						int amount = mail.getMailTotalEver(args[1]);
						String suffix = "";
						if (amount == 1) {
							suffix = "letter";
						} else {
							suffix = "letters";
						}
						player.sendStringMessage(ChatColor.AQUA + args[1] + " has received " + amount + " " + suffix
								+ " during their time on Tregmine.");
					} catch (DAOException e) {
						throw new RuntimeException(e);
					}
				}
			} else {
				try (IContext ctx = tregmine.createContext()) {
					IMailDAO mail = ctx.getMailDAO();
					int amount = mail.getMailTotal(player.getName());
					String suffix = "";
					if (amount == 1) {
						suffix = "letter";
					} else {
						suffix = "letters";
					}
					player.sendStringMessage(ChatColor.AQUA + "You have received " + amount + " " + suffix
							+ " during your time on Tregmine.");
				} catch (DAOException e) {
					throw new RuntimeException(e);
				}
			}
			return true;
		} else if (args[0].contains("read")) {
			List<String[]> messages;
			try (IContext ctx = tregmine.createContext()) {
				IMailDAO mail = ctx.getMailDAO();
				messages = mail.getAllMail(player.getName());
				for (String[] message : messages) {
					player.sendStringMessage(
							ChatColor.AQUA + "You have a message from " + message[0] + " [ID " + message[4] + "]");
					player.sendStringMessage(ChatColor.AQUA + "\"" + message[3].trim() + "\"");
				}
				if (messages.size() == 0) {
					player.sendStringMessage(ChatColor.AQUA + "You haven't received any messages.");
				}
			} catch (DAOException e) {
				throw new RuntimeException(e);
			}
			return true;
		} else {
			sendHelp(player);
			return true;
		}
	}

	public void sendHelp(TregminePlayer player) {
		String[] help = new String[6];
		help[0] = ChatColor.AQUA + "****Tregmine Internal****";
		help[1] = ChatColor.AQUA + "To send a message, type " + ChatColor.GRAY + "/mail send <player> <message>";
		help[2] = ChatColor.AQUA + "To delete a message, type " + ChatColor.GRAY + "/mail delete <mail_id>";
		help[3] = ChatColor.AQUA + "To get your total messages, type " + ChatColor.GRAY + "/mail total";
		help[4] = ChatColor.AQUA + "****Nostalgia Center****";
		if (player.getIsStaff()) {
			help[5] = ChatColor.AQUA + "To get the total messages you or another player has received, type "
					+ ChatColor.GRAY + "/mail tbt <player>";
		} else {
			help[5] = ChatColor.AQUA + "To get the total messages you have received, type " + ChatColor.GRAY
					+ "/mail tbt";
		}
		for (String msg : help) {
			player.sendStringMessage(msg);
		}
	}

	public boolean sendMailNotification(boolean isWeb, String receiver, String sender, Tregmine tregmine) {
		if (!isWeb) {
			List<TregminePlayer> candidates1 = tregmine.matchPlayer(sender);
			List<TregminePlayer> candidates = tregmine.matchPlayer(receiver);
			if (candidates.size() != 1 || candidates1.size() != 1) {
				return false;
			}
			TregminePlayer receiverUser = candidates.get(0);
			TregminePlayer senderUser = candidates1.get(0);
			receiverUser.sendSpigotMessage(senderUser.decideVS(receiverUser),
					new TextComponent(ChatColor.AQUA + " sent you a message! Use /mail read to view it. "));
			return true;
		} else {
			// Web, even though its not implemented.
			List<TregminePlayer> candidates = tregmine.matchPlayer(receiver);
			if (candidates.size() != 1) {
				return false;
			}
			TregminePlayer receiverUser = candidates.get(0);
			receiverUser.sendStringMessage(ChatColor.AQUA + sender + " sent you a message! Use /mail read to view it.");
			return false;
		}
	}
}
