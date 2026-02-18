package fr.tt54.killTheBrioche;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fr.tt54.killTheBrioche.cmd.CmdKillTheBrioche;
import fr.tt54.killTheBrioche.cmd.CmdUseDeathTeleportation;
import fr.tt54.killTheBrioche.listeners.PlayerListener;
import fr.tt54.killTheBrioche.managers.RewardsManager;
import fr.tt54.killTheBrioche.twitch.OAuthCallbackServer;
import fr.tt54.killTheBrioche.twitch.TwitchBridge;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class KillTheBrioche extends JavaPlugin {

    public static Logger logger;
    public static Gson gson = new GsonBuilder().create();

    private static KillTheBrioche instance;

    @Override
    public void onEnable() {
        instance = this;
        logger = this.getLogger();

        this.saveDefaultConfig();

        TwitchBridge.instance = new TwitchBridge(this.getConfig().getString("client_id", "client_id"), this.getConfig().getString("client_secret", "client_secret"));
        TwitchBridge.instance.onConnection(RewardsManager::loadTwitchRewards).onRewardRedemption(RewardsManager::onTwitchRewardRedeemed);
        OAuthCallbackServer.launchServer();
        TwitchBridge.instance.loadTwitchToken();

        RewardsManager.load();

        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            commands.registrar().register(CmdKillTheBrioche.ktbCommand, List.of("killthebrioche", "ktb"));
            commands.registrar().register(CmdUseDeathTeleportation.deathTpCommand, List.of("deathtp", "dtp"));
        });

        this.getServer().getPluginManager().registerEvents(new PlayerListener(), this);

        new BukkitRunnable(){
            @Override
            public void run() {
                if(System.currentTimeMillis() < TwitchBridge.instance.lastTokenRefresh + TwitchBridge.instance.token.expires_in() * 1000L / 4) return;
                Bukkit.broadcast(Component.text("Reconnexion à l'API Twitch", NamedTextColor.GRAY));
                new Thread(() -> {
                    try {
                        TwitchBridge.instance.refreshToken();
                        System.out.println("Token refreshed");
                    } catch (IOException e) {
                        logger.log(Level.SEVERE, "Erreur lors du refresh", e);
                    }
                }).start();
            }
        }.runTaskTimer(this, 20 * 60 * 10, 20 * 60 * 10);
    }

    @Override
    public void onDisable() {
        OAuthCallbackServer.stopServer();
        RewardsManager.save();
    }

    public static KillTheBrioche getInstance() {
        return instance;
    }
}
