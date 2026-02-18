package fr.tt54.killTheBrioche.rewards;

import fr.tt54.killTheBrioche.utils.NMS;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class AncientCityTeleportationReward extends MCReward{

    public AncientCityTeleportationReward(Material display) {
        super("teleport_ancient_city", "Téléportation vers une Ancient City", "§eVous avez été téléporté vers l'§c§lAncient City§e la plus proche", display);
    }

    @Override
    public void execute(Player target) {
        Location centerLocation = target.getWorld().getEnvironment() == World.Environment.NORMAL ? target.getLocation() : Bukkit.getWorlds().getFirst().getSpawnLocation();
        Location location = NMS.locateStructure(target, "minecraft:ancient_city", centerLocation);
        if(location != null){
            for(int i = location.getWorld().getMinHeight(); i < location.getWorld().getMaxHeight(); i++){
                Location loc = location.clone();
                loc.setY(i);
                if(loc.getBlock().isSolid() && !loc.clone().add(0, 1, 0).getBlock().isSolid() && !loc.clone().add(0, 2, 0).getBlock().isSolid()){
                    target.teleport(loc.add(0, 1, 0));
                    return;
                }
            }
        }
    }
}
