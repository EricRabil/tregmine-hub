package info.tregminehub.commands;

import static org.bukkit.ChatColor.YELLOW;

import org.bukkit.ChatColor;
import org.bukkit.WeatherType;

import info.tregminehub.Tregmine;
import info.tregminehub.api.TregminePlayer;

public class WeatherCommand extends AbstractCommand {
	public WeatherCommand(Tregmine tregmine) {
		super(tregmine, "weather");
	}

	@Override
	public boolean handlePlayer(TregminePlayer player, String[] args) {
		if (!player.getRank().canSetWeather()) {
			return true;
		}
		if (args.length != 1) {
			player.sendStringMessage("/weather <downfall or clear>");
			player.resetPlayerWeather();
			return true;
		}

		try {
			WeatherType type = WeatherType.valueOf(args[0].toUpperCase());
			player.setPlayerWeather(type);
			player.sendStringMessage(YELLOW + "Weather set to " + type);
		} catch (IllegalArgumentException e) {
			return false;
		}

		return true;
	}
}
