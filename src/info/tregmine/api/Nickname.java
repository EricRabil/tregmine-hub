package info.tregmine.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;

public class Nickname {

	// Base values
	private TregminePlayer owner;

	// Nickname
	private String nickname;
	private String colorString;
	private ChatColor color = ChatColor.WHITE;
	private boolean hasColor = false;

	// Special nickname characteristics
	private List<ChatColor> formatting = new ArrayList<ChatColor>();
	private boolean hasFormatting;
	private boolean showNickFlag;

	// Internal
	private String spacer = "$";
	private String breakcolor = ";;";

	public Nickname(TregminePlayer player, String setnickname) {
		this.owner = player;
		if (this.owner.getRank().canHaveHiddenNickname()) {
			this.showNickFlag = false;
		} else {
			this.showNickFlag = true;
		}
		this.nickname = setnickname;
	}

	public void addFormatting(ChatColor format) {
		if (this.hasFormatting == false) {
			this.hasFormatting = true;
		}
		if (this.formatting.contains(format)) {
			return;
		}
		this.formatting.add(format);
	}

	public ChatColor getChatColor() {
		if (hasColor)
			return this.color;
		else
			return ChatColor.WHITE;
	}

	public List<ChatColor> getChatFormatting() {
		if (hasFormatting)
			return this.formatting;
		else
			return null;
	}

	public String getColorName() {
		if (this.hasColor) {
			return this.colorString;
		} else {
			return "%";
		}
	}

	public List<ChatColor> getFormatting() {
		if (this.hasFormatting) {
			return this.formatting;
		} else {
			return new ArrayList<ChatColor>();
		}
	}

	public String getFormattingCF() {
		String returns = "";
		for (Object color : this.formatting.toArray()) {
			ChatColor clr = (ChatColor) color;
			returns += clr;
		}
		return returns;
	}

	public String getNickname() {
		if (this.showNickFlag) {
			if (hasColor && hasFormatting)
				return "!" + this.color + "" + this.getFormattingCF() + this.nickname;
			else if (hasColor && !hasFormatting)
				return "!" + this.color + "" + this.nickname;
			else
				return "!" + this.nickname;
		} else {
			if (hasColor && hasFormatting)
				return this.color + "" + this.getFormattingCF() + this.nickname;
			else if (hasColor && !hasFormatting)
				return this.color + "" + this.nickname;
			else
				return this.nickname;
		}
	}

	public String getNicknamePlaintext() {
		return this.nickname;
	}

	public TregminePlayer getOwner() {
		return this.owner;
	}

	public boolean hasChatColor() {
		return this.hasColor;
	}

	public void removeColor() {
		this.hasColor = false;
		this.color = ChatColor.WHITE;
	}

	public void removeFormatting() {
		this.hasFormatting = false;
		this.formatting.clear();
	}

	public void setColor(ChatColor setcolor) {
		this.hasColor = true;
		this.color = setcolor;
		this.colorString = setcolor.name();
	}

	public void setFormatting(List<ChatColor> fmt) {
		if (!this.hasFormatting)
			this.hasFormatting = true;
		this.formatting = fmt;
	}

	public void setNickname(String nick) {
		this.nickname = nick;
	}

	public void setNickname(String nick, ChatColor setcolor) {
		this.hasColor = true;
		this.nickname = nick;
		this.color = setcolor;
	}

	public void setOwner(TregminePlayer newowner) {
		this.owner = newowner;
	}

	public String sqlColors(ChatColor color, ChatColor formatting) {
		String colorname = color.getChar() + "";
		String formatname = formatting.name();
		return colorname + this.spacer + formatname + this.spacer + this.breakcolor;
	}

	public Map<ColorType, ChatColor> translateSql(String sql) {
		Map<ColorType, ChatColor> map = new HashMap<>();
		List<String> returnme = Arrays.asList(sql.split(spacer));
		ChatColor returnc;
		ChatColor returnf;
		for (String str : returnme) {
			if (str.contains(this.breakcolor)) {
				returnme.remove(str);
			}
			if (str.contains(this.spacer)) {
				returnme.remove(str);
				returnme.add(str.replace(this.spacer, ""));
			}
		}
		for (String str : returnme) {
			if (ChatColor.getByChar(str).isColor()) {
				returnc = ChatColor.getByChar(str);
				map.put(ColorType.COLOR, returnc);
			} else if (ChatColor.getByChar(str).isFormat()) {
				returnf = ChatColor.getByChar(str);
				map.put(ColorType.FORMAT, returnf);
			} else {
				continue;
			}
		}
		return map;
	}

}
