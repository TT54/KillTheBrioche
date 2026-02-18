package fr.tt54.killTheBrioche.rewards;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class SpawnMobReward extends MCReward {

    public static final int MAX_TRIES = 20;

    private final EntityType type;
    private final int radius;
    private final int amount;

    public SpawnMobReward(EntityType type, int radius, int amount) {
        super("spawn_" + radius + "_" + type.name(), "Spawn " + type.name() + " dans un rayon de " + radius + " blocs", "§eUn §c§l" + type.name() + "§e vient d'apparaître dans un rayon de §6" + radius + " blocs", Material.getMaterial(type.name() + "_SPAWN_EGG"));
        this.type = type;
        this.radius = radius;
        this.amount = amount;
    }

    @Override
    public void execute(Player target) {
        for(int k = 0; k < amount; k++) {
            for (int i = 0; i < MAX_TRIES; i++) {
                int dx = random.nextInt(2 * radius + 1) - radius;
                int dz = random.nextInt(2 * radius + 1) - radius;

                Location location = target.getLocation().clone().add(dx, -radius, dz);
                for (int j = -radius; j < radius + 1; j++) {
                    if (location.getBlock().isSolid() && !location.clone().add(0, 1, 0).getBlock().isSolid()) {
                        location.getWorld().spawnEntity(location.add(0, 1, 0), this.type);
                        return;
                    }
                    location.add(0, 1, 0);
                }
            }
            target.getWorld().spawnEntity(target.getLocation(), this.type);
        }
    }
}
