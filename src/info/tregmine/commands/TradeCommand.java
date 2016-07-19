package info.tregmine.commands;

import static org.bukkit.ChatColor.RED;
import static org.bukkit.ChatColor.WHITE;
import static org.bukkit.ChatColor.YELLOW;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginManager;

import info.tregmine.Tregmine;
import info.tregmine.api.Badge;
import info.tregmine.api.TregminePlayer;
import info.tregmine.api.math.MathUtil;
import info.tregmine.database.DAOException;
import info.tregmine.database.IContext;
import info.tregmine.database.IEnchantmentDAO;
import info.tregmine.database.ITradeDAO;
import info.tregmine.database.IWalletDAO;
import info.tregmine.events.TregminePortalEvent;
import info.tregmine.listeners.ExpListener;

public class TradeCommand extends AbstractCommand implements Listener {
	private static class TradeContext {
		Inventory inventory;
		TregminePlayer firstPlayer;
		TregminePlayer secondPlayer;
		TradeState state;
		int bid;
	}

	private enum TradeState {
		ITEM_SELECT, BID, CONSIDER_BID;
	}

	String tradePre = YELLOW + "[Trade] ";

	String type = "";;

	boolean isIllegal = false;

	private Map<TregminePlayer, TradeContext> activeTrades;

	public TradeCommand(Tregmine tregmine) {
		super(tregmine, "trade");

		activeTrades = new HashMap<TregminePlayer, TradeContext>();

		PluginManager pluginMgm = tregmine.getServer().getPluginManager();
		pluginMgm.registerEvents(this, tregmine);
	}

	@Override
	public boolean handlePlayer(TregminePlayer player, String[] args) {
		if (args.length != 1) {
			return false;
		}
		if (player.getChatState() != TregminePlayer.ChatState.CHAT) {
			player.sendStringMessage(RED + "A trade is already in progress!");
			return true;
		}

		Server server = tregmine.getServer();
		String pattern = args[0];

		List<TregminePlayer> candidates = tregmine.matchPlayer(pattern);
		if (candidates.size() != 1) {
			// TODO: error message
			return true;
		}

		TregminePlayer target = candidates.get(0);
		if (target.getChatState() != TregminePlayer.ChatState.CHAT) {
			player.sendStringMessage(RED + target.getName() + "is in a trade!");
			return true;
		}

		if (target.getId() == player.getId()) {
			player.sendStringMessage(RED + "You cannot trade with yourself!");
			return true;
		}

		double distance = MathUtil.calcDistance2d(player.getLocation(), target.getLocation());

		if (!target.hasFlag(TregminePlayer.Flags.INVISIBLE) && (distance > player.getRank().getTradeDistance(player))) {
			player.sendStringMessage(RED + "You can only trade with people less than "
					+ player.getRank().getTradeDistance(player) + " blocks away.");
			return true;
		}

		Inventory inv = server.createInventory(null, InventoryType.CHEST);
		player.openInventory(inv);

		TradeContext ctx = new TradeContext();
		ctx.inventory = inv;
		ctx.firstPlayer = player;
		ctx.secondPlayer = target;
		ctx.state = TradeState.ITEM_SELECT;

		activeTrades.put(player, ctx);
		activeTrades.put(target, ctx);

		player.setChatState(TregminePlayer.ChatState.TRADE);
		target.setChatState(TregminePlayer.ChatState.TRADE);

		player.sendStringMessage(YELLOW + "[Trade] You are now trading with " + target.getName() + YELLOW
				+ ". What do you want " + "to offer?");
		String extra = "";
		if (isIllegal) {
			extra = ChatColor.RED + "[Trade] Warning! " + player.getName() + " has sent an item that has the " + type
					+ " flag! This means you cannot sell it, use it to buy tools, and using it to craft will result in the product being flagged as well.";
		}
		target.sendStringMessage(YELLOW + "[Trade] You are now in a trade with " + player.getName() + YELLOW
				+ ". To exit, type \"quit\".");
		if (isIllegal) {
			target.sendStringMessage(extra);
		}

		return true;
	}

	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event) {
		TregminePlayer player = tregmine.getPlayer((Player) event.getPlayer());
		TradeContext ctx = activeTrades.get(player);
		if (ctx == null) {
			return;
		}

		TregminePlayer target = ctx.secondPlayer;

		target.sendStringMessage(tradePre + player.getName() + " " + YELLOW + " is offering: ");
		player.sendStringMessage("[Trade] You are offering: ");

		ItemStack[] contents = ctx.inventory.getContents();
		for (ItemStack i : contents) {
			if (i == null) {
				// ABORT!
			}
			if (!isIllegal && i.getType() != Material.AIR) {
				ItemMeta im = i.getItemMeta();
				List<String> lore = im.getLore();
				if (lore.get(0).contains("CREATIVE") || lore.get(0).contains("SURVIVAL")) {
					isIllegal = true;
					type = lore.get(0);
				}
			}
		}
		for (ItemStack stack : contents) {
			if (stack == null) {
				continue;
			}
			Material material = stack.getType();
			int amount = stack.getAmount();

			ItemMeta materialMeta = stack.getItemMeta();
			int xpValue;
			try {
				String[] materialLore = materialMeta.getLore().toString().split(" ");
				xpValue = Integer.parseInt(materialLore[2]);
			} catch (NullPointerException e) {
				xpValue = 0;
			} catch (NumberFormatException e) {
				xpValue = 0;
			}

			Map<Enchantment, Integer> enchantments = stack.getEnchantments();
			if (material == Material.EXP_BOTTLE && xpValue > 0
					&& ExpListener.ITEM_NAME.equals(materialMeta.getDisplayName())) {

				target.sendStringMessage(tradePre + amount + " XP Bottle holding " + xpValue + " levels");
				player.sendStringMessage(tradePre + amount + " XP Bottle holding " + xpValue + " levels");
			} else if (enchantments.size() > 0) {
				target.sendStringMessage(tradePre + " Enchanted " + material.toString() + " with: ");
				player.sendStringMessage(tradePre + " Enchanted " + material.toString() + " with: ");
				try (IContext dbCtx = tregmine.createContext()) {
					IEnchantmentDAO enchantDAO = dbCtx.getEnchantmentDAO();
					for (Enchantment i : enchantments.keySet()) {
						String enchantName = enchantDAO.localize(i.getName());
						target.sendStringMessage("- " + enchantName + " Level: " + enchantments.get(i).toString());
						player.sendStringMessage("- " + enchantName + " Level: " + enchantments.get(i).toString());
					}
				} catch (DAOException e) {
					throw new RuntimeException(e);
				}
			} else {
				target.sendStringMessage(tradePre + amount + " " + material.toString());
				player.sendStringMessage(tradePre + amount + " " + material.toString());
			}
		}

		target.sendStringMessage(YELLOW + "[Trade] To make a bid, type \"bid <tregs>\". " + "For example: bid 1000");
		player.sendStringMessage(YELLOW + "[Trade] Waiting for " + target.getName() + YELLOW
				+ " to make a bid. Type \"change\" to modify " + "your offer.");

		ctx.state = TradeState.BID;
	}

	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent event) {
		TregminePlayer player = tregmine.getPlayer(event.getPlayer());
		if (player.getChatState() != TregminePlayer.ChatState.TRADE) {
			return;
		}

		event.setCancelled(true);

		TradeContext ctx = activeTrades.get(player);
		if (ctx == null) {
			return;
		}

		TregminePlayer first = ctx.firstPlayer;
		TregminePlayer second = ctx.secondPlayer;

		String text = event.getMessage();
		String[] args = text.split(" ");

		Tregmine.LOGGER
				.info("[TRADE " + first.getName() + ":" + second.getName() + "] <" + player.getName() + "> " + text);
		// Tregmine.LOGGER.info("state: " + ctx.state);

		if ("quit".equals(args[0]) && args.length == 1) {
			first.setChatState(TregminePlayer.ChatState.CHAT);
			second.setChatState(TregminePlayer.ChatState.CHAT);
			activeTrades.remove(first);
			activeTrades.remove(second);
			if (ctx.state == TradeState.ITEM_SELECT) {
				first.closeInventory();
			}

			// Restore contents to player inventory
			ItemStack[] contents = ctx.inventory.getContents();
			Inventory firstInv = first.getInventory();
			for (ItemStack stack : contents) {
				if (stack == null) {
					continue;
				}
				firstInv.addItem(stack);
			}

			first.sendStringMessage(YELLOW + "[Trade] Trade ended.");
			second.sendStringMessage(YELLOW + "[Trade] Trade ended.");
		} else if ("bid".equalsIgnoreCase(args[0]) && args.length == 2) {
			if (second != player) {
				player.sendStringMessage(RED + "[Trade] You can't make a bid!");
				return;
			}
			if (ctx.state != TradeState.BID) {
				player.sendStringMessage(RED + "[Trade] You can't make a bid right now.");
				return;
			}

			int amount = 0;
			try {
				amount = Integer.parseInt(args[1]);
				if (amount < 0) {
					player.sendStringMessage(RED + "[Trade] You have to bid an integer " + "number of tregs.");
					return;
				}
			} catch (NumberFormatException e) {
				player.sendStringMessage(RED + "[Trade] You have to bid an integer " + "number of tregs.");
				return;
			}

			try (IContext dbCtx = tregmine.createContext()) {
				IWalletDAO walletDAO = dbCtx.getWalletDAO();

				long balance = walletDAO.balance(second);
				if (amount > balance) {
					player.sendStringMessage(RED + "[Trade] You only have " + balance + " tregs!");
					return;
				}
			} catch (DAOException e) {
				throw new RuntimeException(e);
			}

			first.sendStringMessage(tradePre + second.getName() + YELLOW + " bid " + amount
					+ " tregs. Type \"accept\" to " + "proceed with the trade. Type \"change\" to modify "
					+ "your offer. Type \"quit\" to stop trading.");
			second.sendStringMessage(YELLOW + "[Trade] You bid " + amount + " tregs.");

			ctx.bid = amount;
			ctx.state = TradeState.CONSIDER_BID;
		} else if ("change".equalsIgnoreCase(args[0]) && args.length == 1) {
			if (first != player) {
				player.sendStringMessage(RED + "[Trade] You can't change the offer!");
				return;
			}

			player.openInventory(ctx.inventory);
			ctx.state = TradeState.ITEM_SELECT;
		} else if ("accept".equals(args[0]) && args.length == 1) {
			if (first != player) {
				player.sendStringMessage(RED + "[Trade] You can't accept an offer!");
				return;
			}

			ItemStack[] contents = ctx.inventory.getContents();

			int t = 0;
			for (ItemStack tis : contents) {
				if (tis == null) {
					continue;
				}
				t++;
			}

			int p = 0;
			Inventory secondInv = second.getInventory();
			for (ItemStack pis : secondInv.getContents()) {
				if (pis != null) {
					continue;
				}
				p++;
			}

			if (p < t) {
				int diff = t - p;
				first.sendStringMessage(tradePre + second.getName() + YELLOW
						+ " doesn't have enough inventory space, please wait a " + "minute and try again :)");
				second.sendStringMessage(tradePre + "You need to remove " + diff
						+ " item stack(s) from your inventory to be able to recieve " + "the items!");
				return;
			}

			// Withdraw ctx.bid tregs from second players wallet
			// Add ctx.bid tregs from first players wallet
			try (IContext dbCtx = tregmine.createContext()) {
				IWalletDAO walletDAO = dbCtx.getWalletDAO();
				ITradeDAO tradeDAO = dbCtx.getTradeDAO();

				if (walletDAO.take(second, ctx.bid)) {
					walletDAO.add(first, ctx.bid);
					walletDAO.insertTransaction(second.getId(), first.getId(), ctx.bid);

					int tradeId = tradeDAO.insertTrade(first.getId(), second.getId(), ctx.bid);
					tradeDAO.insertStacks(tradeId, contents);

					first.sendStringMessage(tradePre + ctx.bid + " tregs was " + "added to your wallet!");
					second.sendStringMessage(tradePre + ctx.bid + " tregs was " + "withdrawn to your wallet!");

					if ((tradeDAO.getAmountofTrades(first.getId()) > 100)
							&& !(first.getBadgeLevel(Badge.MERCHANT) == 0)) {
						first.awardBadgeLevel(Badge.MERCHANT, "For completing 100 transactions!");
					}
				} else {
					first.sendStringMessage(RED + "[Trade] Failed to withdraw " + ctx.bid + " from the wallet of "
							+ second.getName() + "!");
					return;
				}
			} catch (DAOException e) {
				throw new RuntimeException(e);
			}

			// Move items to second players inventory
			for (ItemStack stack : contents) {
				if (stack == null) {
					continue;
				}
				secondInv.addItem(stack);
			}

			// Finalize
			first.setChatState(TregminePlayer.ChatState.CHAT);
			second.setChatState(TregminePlayer.ChatState.CHAT);
			activeTrades.remove(first);
			activeTrades.remove(second);

			first.giveExp(5);
			second.giveExp(5);
		} else {
			first.sendStringMessage(tradePre + WHITE + "<" + player.getName() + WHITE + "> " + text);

			second.sendStringMessage(tradePre + WHITE + "<" + player.getName() + WHITE + "> " + text);
		}
	}

	@EventHandler
	public void onPortalUsage(TregminePortalEvent event) {
		TradeContext ctx = activeTrades.get(event.getPlayer());
		if (ctx == null) {
			return;
		}

		TregminePlayer first = ctx.firstPlayer;
		TregminePlayer second = ctx.secondPlayer;

		first.setChatState(TregminePlayer.ChatState.CHAT);
		second.setChatState(TregminePlayer.ChatState.CHAT);

		activeTrades.remove(first);
		activeTrades.remove(second);

		if (ctx.state == TradeState.ITEM_SELECT) {
			first.closeInventory();
		}

		first.sendStringMessage(YELLOW + "[Trade] Trade ended due to portal use! Now that was silly...");
		second.sendStringMessage(YELLOW + "[Trade] Trade ended due to portal use! Now that was silly...");
	}
}
