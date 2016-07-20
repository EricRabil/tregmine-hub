package info.tregminehub.commands;

import java.util.List;

import org.bukkit.ChatColor;

import info.tregminehub.Tregmine;
import info.tregminehub.api.TregminePlayer;
import info.tregminehub.database.DAOException;
import info.tregminehub.database.IContext;
import info.tregminehub.database.IPlayerDAO;
import net.md_5.bungee.api.chat.TextComponent;

public class PropertyCommand extends AbstractCommand {
	private Tregmine a;
	// b is the sender and d is the target.
	private TregminePlayer b;
	private List<TregminePlayer> e;
	private TregminePlayer d;
	private String[] c;

	public PropertyCommand(Tregmine instance) {
		super(instance, "property");
		a = instance;
	}

	@Override
	public boolean handlePlayer(TregminePlayer player, String args[]) {
		b = player;
		c = args;
		if (!b.getIsAdmin()) {
			b.sendStringMessage(ChatColor.RED + "You're not allowed to change player properties!");
			return true;
		}
		if (c.length <= 2) {
			b.sendStringMessage(ChatColor.RED + "You gave the wrong amount of arguments.");
			b.sendStringMessage(ChatColor.RED + "/property <target player> <key> <value>");
			return true;
		}
		e = a.matchPlayer(c[0]);
		if (e.size() != 1) {
			b.sendStringMessage(ChatColor.RED + "The target player specified does not exist");
			return true;
		}
		d = e.get(0);
		String j = "";
		for (String i : c) {
			if (i.equals(c[0])) {

			} else if (i.equals(c[1])) {

			} else {
				if (j.length() == 0) {
					j = i;
				} else {
					j += " " + i;
				}
			}
		}
		try (IContext ctx = tregmine.createContext()) {
			IPlayerDAO h = ctx.getPlayerDAO();
			h.updateProperty(d, c[1], j);
			d.sendSpigotMessage(new TextComponent(ChatColor.GOLD + c[1] + ChatColor.GREEN + " has been set to "
					+ ChatColor.GOLD + j + ChatColor.GREEN + " for " + ChatColor.GOLD + d.getName()));
		} catch (DAOException g) {
			g.printStackTrace();
			d.sendStringMessage(ChatColor.RED + "Something went wrong!");
			return true;
		}

		return true;
	}
}
