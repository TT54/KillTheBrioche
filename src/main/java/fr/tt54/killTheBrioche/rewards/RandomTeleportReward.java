package fr.tt54.killTheBrioche.rewards;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class RandomTeleportReward extends MCReward{

    private final int radius;

    public RandomTeleportReward(int radius, Material display) {
        super("rtp_" + radius, "Téléportation dans un rayon de " + radius + " blocs", "§eVous avez été téléporté dans un rayon de §c§l" + radius + " blocs§e autour du spawn", display);
        this.radius = radius;
    }

    @Override
    public void execute(Player target) {
        int dx = random.nextInt(2 * radius + 1) - radius;
        int dz = random.nextInt(2 * radius + 1) - radius;

        Location location = Bukkit.getWorlds().getFirst().getSpawnLocation().clone().add(dx, 0, dz).toHighestLocation();
        target.teleport(location);
    }
}
