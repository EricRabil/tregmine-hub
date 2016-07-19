package info.tregmine.commands;

import static org.bukkit.ChatColor.AQUA;
import static org.bukkit.ChatColor.GOLD;
import static org.bukkit.ChatColor.RED;
import static org.bukkit.ChatColor.WHITE;

import java.text.NumberFormat;
import java.util.List;

import info.tregmine.Tregmine;
import info.tregmine.api.TregminePlayer;
import info.tregmine.api.math.MathUtil;
import info.tregmine.database.DAOException;
import info.tregmine.database.IContext;
import info.tregmine.database.IWalletDAO;

public class WalletCommand extends AbstractCommand {
	private final static NumberFormat FORMAT = NumberFormat.getNumberInstance();

	public WalletCommand(Tregmine tregmine) {
		super(tregmine, "wallet");
	}

	private boolean balance(TregminePlayer player) {
		try (IContext ctx = tregmine.createContext()) {
			IWalletDAO walletDAO = ctx.getWalletDAO();

			long balance = walletDAO.balance(player);
			if (balance >= 0) {
				player.sendStringMessage("You have " + GOLD + FORMAT.format(balance) + WHITE + " Tregs.");
			} else {
				player.sendStringMessage(RED + "An error occured.");
			}
		} catch (DAOException e) {
			throw new RuntimeException(e);
		}

		return true;
	}

	private boolean donate(TregminePlayer player, TregminePlayer target, int amount) {
		if (MathUtil.calcDistance2d(player.getLocation(), target.getLocation()) > 5) {
			if (player.canSee(target.getDelegate())) {
				player.sendStringMessage(
						RED + target.getName() + " is to far away for a wallet transaction, please move closer");
			}
			return true;
		}

		try (IContext ctx = tregmine.createContext()) {
			IWalletDAO walletDAO = ctx.getWalletDAO();

			if (walletDAO.take(player, amount)) {
				walletDAO.add(target, amount);
				walletDAO.insertTransaction(player.getId(), target.getId(), amount);

				player.sendStringMessage(AQUA + "You donated to " + target.getName() + " " + GOLD
						+ FORMAT.format(amount) + AQUA + " Tregs.");
				target.sendStringMessage(AQUA + "You received " + GOLD + FORMAT.format(amount) + AQUA + " Tregs from a "
						+ "secret admirer.");
				LOGGER.info(amount + ":TREG_DONATED " + player.getName() + "(" + walletDAO.balance(player) + ")"
						+ " => " + target.getName() + "(" + walletDAO.balance(target) + ")");
			} else {
				player.sendStringMessage(RED + "You cant give more then you have!");
			}
		} catch (DAOException e) {
			throw new RuntimeException(e);
		}

		return true;
	}

	private boolean give(TregminePlayer player, TregminePlayer target, int amount) {
		if (MathUtil.calcDistance2d(player.getLocation(), target.getLocation()) > 5) {
			if (player.canSee(target.getDelegate())) {
				player.sendStringMessage(
						RED + target.getName() + " is to far away for a wallet transaction, please move closer");
			}
			return true;
		}

		try (IContext ctx = tregmine.createContext()) {
			IWalletDAO walletDAO = ctx.getWalletDAO();

			if (walletDAO.take(player, amount)) {
				walletDAO.add(target, amount);
				walletDAO.insertTransaction(player.getId(), target.getId(), amount);

				player.sendStringMessage(
						AQUA + "You gave " + target.getName() + " " + GOLD + FORMAT.format(amount) + AQUA + " Tregs.");
				target.sendStringMessage(AQUA + "You received " + GOLD + FORMAT.format(amount) + AQUA + " Tregs from "
						+ player.getName() + ".");
				LOGGER.info(amount + ":TREG " + player.getName() + "(" + walletDAO.balance(player) + ")" + " => "
						+ target.getName() + "(" + walletDAO.balance(target) + ")");
			} else {
				player.sendStringMessage(RED + "You cant give more then you have!");
			}
		} catch (DAOException e) {
			throw new RuntimeException(e);
		}

		return true;
	}

	@Override
	public boolean handlePlayer(TregminePlayer player, String[] args) {
		if (args.length == 0) {
			player.sendStringMessage(RED + "Incorrect usage! Try:");
			player.sendStringMessage(AQUA + "/wallet tell <player>");
			player.sendStringMessage(AQUA + "/wallet balance");
			player.sendStringMessage(AQUA + "/wallet donate <player> <amount>");
			player.sendStringMessage(AQUA + "/wallet give <player> <amount>");
			return true;
		}

		String cmd = args[0];

		// inform people that syntax has changed
		if ("tell".equalsIgnoreCase(cmd) && args.length == 1) {
			player.sendStringMessage(RED + "Usage: /wallet tell <player>");
			return true;
		}
		// new version with player parameter
		else if ("tell".equalsIgnoreCase(cmd) && args.length == 2) {
			return tell(player, args[1]);
		} else if ("balance".equalsIgnoreCase(cmd)) {
			return balance(player);
		} else if ("donate".equalsIgnoreCase(cmd) && args.length == 3) {
			int amount;
			try {
				amount = Integer.parseInt(args[2]);
			} catch (NumberFormatException e) {
				return true;
			}

			List<TregminePlayer> candidates = tregmine.matchPlayer(args[1]);
			if (candidates.size() != 1) {
				player.sendStringMessage(RED + "Unknown Player: " + args[1]);
				return true;
			}

			TregminePlayer target = candidates.get(0);

			// Sneaky ;)
			if (target.hasFlag(TregminePlayer.Flags.INVISIBLE)) {
				player.sendStringMessage(RED + "Unknown Player: " + args[1]);
				return true;
			}
			return donate(player, target, amount);
		} else if ("give".equalsIgnoreCase(cmd) && args.length == 3) {
			int amount;
			try {
				amount = Integer.parseInt(args[2]);
			} catch (NumberFormatException e) {
				return true;
			}

			List<TregminePlayer> candidates = tregmine.matchPlayer(args[1]);
			if (candidates.size() != 1) {
				player.sendStringMessage(RED + "Unknown Player: " + args[1]);
				return true;
			}

			TregminePlayer target = candidates.get(0);

			if (target.hasFlag(TregminePlayer.Flags.INVISIBLE)) {
				player.sendStringMessage(RED + "Unknown Player: " + args[1]);
				return true;
			}

			return give(player, target, amount);
		}

		return false;
	}

	private boolean tell(TregminePlayer player, String name) {
		TregminePlayer target = tregmine.getPlayer(name);
		if (target == null) {
			player.sendStringMessage(RED + "Usage: /wallet tell <player>");
			return true;
		}

		try (IContext ctx = tregmine.createContext()) {
			IWalletDAO walletDAO = ctx.getWalletDAO();

			long balance = walletDAO.balance(player);
			if (balance >= 0) {
				target.sendStringMessage(
						player.getName() + AQUA + " has " + GOLD + FORMAT.format(balance) + AQUA + " Tregs.");
				player.sendStringMessage(" You have " + GOLD + FORMAT.format(balance) + AQUA + " Tregs.");
			} else {
				player.sendStringMessage(RED + "An error occured.");
			}
		} catch (DAOException e) {
			throw new RuntimeException(e);
		}

		return true;
	}
}
