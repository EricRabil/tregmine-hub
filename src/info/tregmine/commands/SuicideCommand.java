package info.tregmine.commands;

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import info.tregmine.Tregmine;
import info.tregmine.api.Tools;
import info.tregmine.api.TregminePlayer;

public class SuicideCommand extends AbstractCommand {
	Tregmine t;

	public SuicideCommand(Tregmine inst) {
		super(inst, "suicide");
		t = inst;
	}

	@Override
	public boolean handlePlayer(TregminePlayer player, String[] args) {
		player.addPotionEffect(new PotionEffect(PotionEffectType.HARM, 1, 10000));
		player.setDeathCause("suicide");
		Tools tools = new Tools();
		return true;
	}
}
