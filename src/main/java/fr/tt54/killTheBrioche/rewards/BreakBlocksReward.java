package fr.tt54.killTheBrioche.rewards;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class BreakBlocksReward extends MCReward {

    public static final int MAX_TRIES = 200;

    private final int amount;
    private final int radius;

    public BreakBlocksReward(int amount, int radius, Material material) {
        super("break_" + amount + "_blocks_" + radius, "Casse " + amount + " blocs dans un rayon de " + radius + " blocs", "§c§l" + amount + " blocs§e ont été cassés dans un rayon de §6" + radius + " blocs", material);
        this.amount = amount;
        this.radius = radius;
    }

    @Override
    public void execute(Player target) {
        for(int k = 0; k < this.amount; k++) {
            for (int i = 0; i < MAX_TRIES; i++) {
                int dx = random.nextInt(2 * radius + 1) - radius;
                int dy = random.nextInt(2 * radius + 1) - radius;
                int dz = random.nextInt(2 * radius + 1) - radius;

                Location location = target.getLocation().clone().add(dx, dy, dz);
                if(!location.getBlock().getType().isAir()){
                    location.getBlock().setType(Material.AIR);
                    break;
                }
            }
        }
    }
}
