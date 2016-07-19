package info.tregmine.events;

import org.bukkit.Location;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import info.tregmine.api.TregminePlayer;

public final class PlayerMoveBlockEvent extends Event implements Cancellable {
	private static final HandlerList handlers = new HandlerList();

	public static HandlerList getHandlerList() {
		return handlers;
	}

	private boolean cancelled;
	private Location from;
	private Location to;

	private TregminePlayer player;

	public PlayerMoveBlockEvent(Location fromLoc, Location toLoc, TregminePlayer playerInvolved) {
		this.from = fromLoc;
		this.to = toLoc;
		this.player = playerInvolved;
	}

	public Location getFrom() {
		return from;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public TregminePlayer getPlayer() {
		return player;
	}

	public Location getTo() {
		return to;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancel) {
		cancelled = cancel;
	}

	public void setFrom(Location newFrom) {
		this.from = newFrom;
	}

	public void setTo(Location newTo) {
		this.to = newTo;
	}
}
