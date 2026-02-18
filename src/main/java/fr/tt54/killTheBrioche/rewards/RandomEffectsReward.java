package fr.tt54.killTheBrioche.rewards;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.List;
import java.util.Random;

public class RandomEffectsReward extends MCReward{

    public static final List<PotionEffectType> types = List.of(PotionEffectType.HASTE, PotionEffectType.SPEED, PotionEffectType.JUMP_BOOST,
            PotionEffectType.STRENGTH, PotionEffectType.REGENERATION, PotionEffectType.RESISTANCE, PotionEffectType.FIRE_RESISTANCE,
            PotionEffectType.WATER_BREATHING, PotionEffectType.NIGHT_VISION, PotionEffectType.INSTANT_HEALTH, PotionEffectType.LUCK,
            PotionEffectType.ABSORPTION, PotionEffectType.HEALTH_BOOST, PotionEffectType.HERO_OF_THE_VILLAGE, PotionEffectType.INVISIBILITY,
            PotionEffectType.SATURATION, PotionEffectType.SLOW_FALLING
    );

    private static final Random random = new Random();

    public RandomEffectsReward() {
        super("random_effect", "Effets positifs aléatoires", "§aEffet de potion aléatoire !", Material.POTION);
    }

    @Override
    public void execute(Player target) {
        target.addPotionEffect(types.get(random.nextInt(types.size())).createEffect(20 * 30, 1));
    }
}
