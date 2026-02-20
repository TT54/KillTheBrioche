package fr.tt54.killTheBrioche.listeners;

import fr.tt54.killTheBrioche.KillTheBrioche;
import fr.tt54.killTheBrioche.managers.RunManager;
import fr.tt54.killTheBrioche.rewards.SubReward;
import fr.tt54.killTheBrioche.scoreboard.ScoreboardManager;
import fr.tt54.killTheBrioche.twitch.TwitchBridge;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        if(player.isOp() && !TwitchBridge.instance.isUserConnected()){
            player.sendMessage(Component.text("Plugin non connecté à Twitch !", NamedTextColor.RED)
                    .append(Component.text(" (Cliquez pour le connecter)", NamedTextColor.GRAY)
                            .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.OPEN_URL, ClickEvent.Payload.string(TwitchBridge.instance.getConnectionUrlString())))
                    )
            );
        }

        RunManager.updateListName(player);
        ScoreboardManager.removeScoreboard(event.getPlayer());
        ScoreboardManager.showScoreboard(player, KillTheBrioche.gameScoreboard);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event){
        RunManager.handleDeath(event.getEntity());
        SubReward.subReward.handleDeath(event);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event){
        ScoreboardManager.removeScoreboard(event.getPlayer());
    }

    @EventHandler
    public void onDragonDeath(EntityDeathEvent event){
        if(event.getEntity().getType() == EntityType.ENDER_DRAGON && RunManager.isStarted()){
            RunManager.stopGame(event.getEntity().getKiller());
        }
    }

}
