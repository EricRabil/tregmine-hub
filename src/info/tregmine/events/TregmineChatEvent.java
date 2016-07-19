package info.tregmine.events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import info.tregmine.api.TregminePlayer;

public class TregmineChatEvent extends Event implements Cancellable {
	private static final HandlerList handlers = new HandlerList();

	public static HandlerList getHandlerList() {
		return handlers;
	}

	private boolean cancelled;
	private TregminePlayer player;
	private String message;
	private String channel;

	private boolean web;

	public TregmineChatEvent(TregminePlayer player, String message, String channel, boolean web) {
		this.player = player;
		this.message = message;
		this.channel = channel;
		this.web = web;
	}

	public String getChannel() {
		return channel;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public String getMessage() {
		return message;
	}

	public TregminePlayer getPlayer() {
		return player;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	public boolean isWebChat() {
		return web;
	}

	@Override
	public void setCancelled(boolean value) {
		this.cancelled = value;
	}
}
