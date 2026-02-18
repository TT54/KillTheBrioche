package fr.tt54.killTheBrioche.listeners;

import fr.tt54.killTheBrioche.managers.RunManager;
import fr.tt54.killTheBrioche.twitch.TwitchBridge;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

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
    }

}
