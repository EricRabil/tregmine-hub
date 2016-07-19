package info.tregmine.api;

import org.bukkit.ChatColor;

public class Tools {
	public Tools() {

	}

	public String reverseColorCodes(String a) {
		String b = a.replace("�", "#");
		return b;
	}

	public ChatColor toColor(String raw) {
		String text = raw;
		if (text.contains("#r") || text.contains("#R")) {
			return ChatColor.RESET;
		}
		if (text.contains("#0")) {
			return ChatColor.BLACK;
		}
		if (text.contains("#1")) {
			return ChatColor.DARK_BLUE;
		}
		if (text.contains("#2")) {
			return ChatColor.DARK_GREEN;
		}
		if (text.contains("#3")) {
			return ChatColor.DARK_AQUA;
		}
		if (text.contains("#4")) {
			return ChatColor.DARK_RED;
		}
		if (text.contains("#5")) {
			return ChatColor.DARK_PURPLE;
		}
		if (text.contains("#6")) {
			return ChatColor.GOLD;
		}
		if (text.contains("#7")) {
			return ChatColor.GRAY;
		}
		if (text.contains("#8")) {
			return ChatColor.DARK_GRAY;
		}
		if (text.contains("#9")) {
			return ChatColor.BLUE;
		}
		if (text.contains("#a") || text.contains("#A")) {
			return ChatColor.GREEN;
		}
		if (text.contains("#b") || text.contains("#B")) {
			return ChatColor.AQUA;
		}
		if (text.contains("#c") || text.contains("#C")) {
			return ChatColor.RED;
		}
		if (text.contains("#d") || text.contains("#D")) {
			return ChatColor.LIGHT_PURPLE;
		}
		if (text.contains("#e") || text.contains("#E")) {
			return ChatColor.YELLOW;
		}
		if (text.contains("#f") || text.contains("#F")) {
			return ChatColor.WHITE;
		}
		if (text.contains("#k") || text.contains("#K")) {
			return ChatColor.MAGIC;
		}
		if (text.contains("#l") || text.contains("#L")) {
			return ChatColor.BOLD;
		}
		if (text.contains("#m") || text.contains("#M")) {
			return ChatColor.STRIKETHROUGH;
		}
		if (text.contains("#n") || text.contains("#N")) {
			return ChatColor.UNDERLINE;
		}
		if (text.contains("#o") || text.contains("#O")) {
			return ChatColor.ITALIC;
		}
		return ChatColor.WHITE;
	}
}
