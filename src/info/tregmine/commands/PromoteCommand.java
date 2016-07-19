package info.tregmine.commands;

import static org.bukkit.ChatColor.BLUE;
import static org.bukkit.ChatColor.GREEN;
import static org.bukkit.ChatColor.ITALIC;
import static org.bukkit.ChatColor.RED;
import static org.bukkit.ChatColor.RESET;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Server;

import info.tregmine.Tregmine;
import info.tregmine.api.Rank;
import info.tregmine.api.TregminePlayer;
import info.tregmine.api.TregminePlayer.Flags;
import info.tregmine.database.DAOException;
import info.tregmine.database.IContext;
import info.tregmine.database.IPlayerDAO;
import net.md_5.bungee.api.chat.TextComponent;

public class PromoteCommand extends AbstractCommand {

	public PromoteCommand(Tregmine tregmine) {
		super(tregmine, "promote");
	}

	@Override
	public boolean handlePlayer(TregminePlayer player, String[] args) {
		if (player.getRank() != Rank.SENIOR_ADMIN && !player.isOp()) {
			player.sendStringMessage(ChatColor.RED + "You certainly don't have permission to promote players!");
			return true;
		}
		// This player is a senior admin and is allowed to promote. Continue.
		if (args.length != 2) {
			// Player didn't enter two arguments, terminate.
			player.sendStringMessage(RED + "You have entered an invalid amount of arguments. Please try again.");
			return true;
		}
		// The checks have finished, perform the command
		String possibleuser = args[0];
		String newrank = args[1];
		Rank rank = null;
		String sayrank = "";
		if (newrank.equalsIgnoreCase("settler")) {
			rank = Rank.SETTLER;
			sayrank = "Settler";
		} else if (newrank.equalsIgnoreCase("resident")) {
			rank = Rank.RESIDENT;
			sayrank = "Resident";
		} else if (newrank.equalsIgnoreCase("donator")) {
			rank = Rank.DONATOR;
			sayrank = "Donator";
		} else if (newrank.equalsIgnoreCase("guardian")) {
			rank = Rank.GUARDIAN;
			sayrank = "Guardian";
		} else if (newrank.equalsIgnoreCase("builder")) {
			rank = Rank.BUILDER;
			sayrank = "Builder";
		} else if (newrank.equalsIgnoreCase("coder")) {
			rank = Rank.CODER;
			sayrank = "Coder";
		} else if (newrank.equalsIgnoreCase("junioradmin") || newrank.equalsIgnoreCase("junior_admin")) {
			rank = Rank.JUNIOR_ADMIN;
			sayrank = "Junior Admin";
		} else if (newrank.equalsIgnoreCase("senioradmin") || newrank.equalsIgnoreCase("senior_admin")) {
			rank = Rank.SENIOR_ADMIN;
			sayrank = "Senior Admin";
		} else {
			player.sendStringMessage(RED + "You have specified an invalid rank. Please try again.");
			return true;
		}
		List<TregminePlayer> candidate = tregmine.matchPlayer(possibleuser);
		if (candidate.size() != 1) {
			player.sendStringMessage(RED + "The player specified was not found. Please try again.");
			return true;
		}
		TregminePlayer user = candidate.get(0);
		if (user.hasFlag(Flags.HARDWARNED)) {
			// Players with a hardwarn cannot be promoted using this command.
			// They must be promoted manually.
			player.sendStringMessage(
					RED + "The player specified has been hardwarned and is not eligible for promotion.");
			return true;
		}
		// Any other errors have now been checked and dealt with. Promote the
		// user.
		try (IContext ctx = tregmine.createContext()) {
			user.setRank(rank);
			if (rank != Rank.SENIOR_ADMIN && rank != Rank.GUARDIAN && rank != Rank.JUNIOR_ADMIN) {
				user.setStaff(false);
			}
			user.setMentor(null);

			IPlayerDAO playerDAO = ctx.getPlayerDAO();
			playerDAO.updatePlayer(user);
			playerDAO.updatePlayerInfo(user);
		} catch (DAOException e) {
			throw new RuntimeException(e);
		}
		this.tregmine.broadcast(new TextComponent("" + BLUE + ITALIC), user.getChatName(), new TextComponent(
				RESET + "" + GREEN + " has been promoted to " + RESET + BLUE + ITALIC + sayrank + "!"));
		return true;
	}

	@Override
	public boolean handleOther(Server server, String[] args) {
		if (args.length != 2) {
			server.getLogger().info("[PROMOTE] You must specify two arguments.");
			return true;
		}
		TregminePlayer user = tregmine.getPlayer(args[0]);
		String getrank = args[1];
		Rank rank = user.getRank();
		Rank oldrank = user.getRank();
		for (Rank r : Rank.values()) {
			if (r.name().toLowerCase().equals(getrank.toLowerCase())) {
				rank = r;
				break;
			}
		}
		try (IContext ctx = tregmine.createContext()) {
			user.setRank(rank);
			if (rank != Rank.SENIOR_ADMIN && rank != Rank.GUARDIAN && rank != Rank.JUNIOR_ADMIN) {
				user.setStaff(false);
			}
			user.setMentor(null);

			IPlayerDAO playerDAO = ctx.getPlayerDAO();
			playerDAO.updatePlayer(user);
			playerDAO.updatePlayerInfo(user);
		} catch (DAOException e) {
			throw new RuntimeException(e);
		}
		if (oldrank != rank) {
			this.tregmine.broadcast(new TextComponent("" + BLUE + ITALIC), user.getChatName(), new TextComponent(
					"" + RESET + GREEN + " has been promoted to " + RESET + BLUE + ITALIC + rank + "!"));
		}
		return true;
	}

}
