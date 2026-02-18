package fr.tt54.killTheBrioche.managers;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class RunManager {

    private static final Set<UUID> runners = new HashSet<>();

    public static Set<UUID> getRunners() {
        return runners;
    }

    public static void addRunner(Player player){
        runners.add(player.getUniqueId());
        updateListName(player);
    }

    public static void removeRunner(UUID uuid){
        runners.remove(uuid);
        Player player = Bukkit.getPlayer(uuid);
        if(player != null){
            updateListName(player);
        }
    }

    public static boolean isRunner(UUID uuid){
        return runners.contains(uuid);
    }

    public static void updateListName(Player player) {
        if(isRunner(player.getUniqueId())){
            player.playerListName(Component.text("[RUNNER] ", NamedTextColor.RED)
                    .append(Component.text(player.getName(), NamedTextColor.WHITE)));
        } else{
            player.playerListName(Component.text("[SPECTATOR] ", NamedTextColor.DARK_GRAY)
                    .append(Component.text(player.getName(), NamedTextColor.GRAY)));
        }
    }

}
