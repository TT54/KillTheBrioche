package fr.tt54.killTheBrioche.rewards;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class EffectReward extends MCReward{

    private final PotionEffectType effectType;
    private final int effectLevel;
    private final int duration;

    public EffectReward(PotionEffectType effectType, int effectLevel, int duration, Material display) {
        super("potion" + effectType.getKey().getKey() + "_" + (effectLevel + 1) + "_" + duration + "s", "Effet " + effectType.getKey().getKey() + " " + (effectLevel + 1) + " pendant " + duration + "s", "§eVous venez de recevoir §c§l" + effectType.getKey().getKey() + " " + (effectLevel + 1) + "§e pendant " + duration + "s", display);
        this.effectType = effectType;
        this.effectLevel = effectLevel;
        this.duration = duration;
    }

    @Override
    public void execute(Player target) {
        target.addPotionEffect(new PotionEffect(this.effectType, duration * 20, effectLevel, false, false));
    }
}
