package info.tregmine.commands;

import java.util.List;

import org.bukkit.ChatColor;

import info.tregmine.Tregmine;
import info.tregmine.api.TregminePlayer;
import info.tregmine.database.DAOException;
import info.tregmine.database.IContext;
import info.tregmine.database.IHandbookDAO;

public class StaffHandbookCommand extends AbstractCommand {
	Tregmine t;

	public StaffHandbookCommand(Tregmine instance) {
		super(instance, "staffbook");
		this.t = instance;
	}

	@Override
	public boolean handlePlayer(TregminePlayer a, String[] b) {
		if (!a.getIsStaff()) {
			a.sendStringMessage(ChatColor.RED + "You don't need to read this, books are boring!");
			return true;
		}
		try (IContext ctx = tregmine.createContext()) {
			IHandbookDAO c = ctx.getHandbookDAO();
			List<String[]> handbook = c.getHandbook();
			if (handbook.size() == 0) {
				a.sendStringMessage(ChatColor.RED + "There are no rules in the handbook.");
				return true;
			}
			a.sendStringMessage(ChatColor.GOLD + t.getConfig().getString("general.servername") + ChatColor.AQUA
					+ " Staff Handbook");
			for (String[] rule : handbook) {
				a.sendStringMessage(ChatColor.translateAlternateColorCodes('#', rule[0]) + ChatColor.RESET + ". "
						+ ChatColor.translateAlternateColorCodes('#', rule[1]));
			}
		} catch (DAOException e) {
			a.sendStringMessage(ChatColor.RED + "Something bad happened...");
			throw new RuntimeException(e);
		}
		return true;
	}
}
