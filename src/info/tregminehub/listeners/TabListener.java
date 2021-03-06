package info.tregminehub.listeners;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatTabCompleteEvent;

import info.tregminehub.Tregmine;
import info.tregminehub.api.TregminePlayer;

public class TabListener implements Listener// , TabCompleter
{
	private Tregmine plugin;

	public TabListener(Tregmine instance) {
		this.plugin = instance;
	}

	@EventHandler
	public void tabcomplete(PlayerChatTabCompleteEvent e) {
		e.getTabCompletions().clear();
		String[] args = e.getChatMessage().split(" ");
		if (args.length == 0) {
			return;
		}

		List<String> nonOps = new ArrayList<String>();
		List<String> result = new ArrayList<String>();

		for (TregminePlayer player : plugin.getOnlinePlayers()) {
			if (!player.hasFlag(TregminePlayer.Flags.INVISIBLE)) {
				nonOps.add(player.getName());
			}
		}
		for (String name : nonOps) {
			if (name.toLowerCase().startsWith(args[args.length - 1].toLowerCase())) {
				result.add(name);
			}
			String tagged = "@" + name;
			if (tagged.toLowerCase().startsWith(args[args.length - 1].toLowerCase())) {
				result.add(tagged);
			}
		}

		e.getTabCompletions().addAll(result);
	}
}
