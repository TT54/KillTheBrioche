package fr.tt54.killTheBrioche.rewards;

import fr.tt54.killTheBrioche.utils.NMS;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class VillageLocateReward extends MCReward{

    public VillageLocateReward(Material display) {
        super("locate_village", "Recherche d'un village", "", display);
    }

    @Override
    public void execute(Player target) {
        Location location = NMS.locateStructure(target, "minecraft:village_plains");
        if(location != null){
            Bukkit.broadcast(Component.text("§eUn village a été trouvé en §c§l" + location.getBlockX() + " / " + location.getBlockZ()));
        }
    }
}
