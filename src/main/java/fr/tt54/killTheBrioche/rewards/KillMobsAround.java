package fr.tt54.killTheBrioche.rewards;

import org.bukkit.Material;
import org.bukkit.entity.Enemy;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;

public class KillMobsAround extends MCReward{

    private final int radius;

    public KillMobsAround(int radius) {
        super("kill_mobs_around", "Tuer les mobs dans un rayon de " + radius + " blocs", "§eLes mobs ont été tué dans un rayon de " + radius + " blocs", Material.DIAMOND_SWORD);
        this.radius = radius;
    }

    @Override
    public void execute(Player target) {
        for(Entity entity : target.getNearbyEntities(2 * radius, 2 * radius,  2 * radius)){
            if(entity instanceof Mob mob && mob instanceof Enemy) {
                mob.clearLootTable();
                mob.remove();
            }
        }
    }
}
