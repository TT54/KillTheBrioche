package fr.tt54.killTheBrioche;

import fr.tt54.killTheBrioche.cmd.CmdKillTheBrioche;
import fr.tt54.killTheBrioche.listeners.PlayerListener;
import fr.tt54.killTheBrioche.rewards.RewardsConfig;
import fr.tt54.killTheBrioche.twitch.OAuthCallbackServer;
import fr.tt54.killTheBrioche.twitch.TwitchBridge;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.logging.Logger;

public final class KillTheBrioche extends JavaPlugin {

    public static Logger logger;

    private static KillTheBrioche instance;

    @Override
    public void onEnable() {
        instance = this;
        logger = this.getLogger();

        this.saveDefaultConfig();

        TwitchBridge.instance = new TwitchBridge(this.getConfig().getString("client_id", "client_id"), this.getConfig().getString("client_secret", "client_secret"));
        TwitchBridge.instance.onConnection(RewardsConfig::loadTwitchRewards).onRewardRedemption(RewardsConfig::onTwitchRewardRedeemed);
        OAuthCallbackServer.launchServer();

        RewardsConfig.load();

        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            commands.registrar().register(CmdKillTheBrioche.ktbCommand, List.of("killthebrioche", "ktb"));
        });

        this.getServer().getPluginManager().registerEvents(new PlayerListener(), this);
    }

    @Override
    public void onDisable() {
        OAuthCallbackServer.stopServer();
        RewardsConfig.save();
    }

    public static KillTheBrioche getInstance() {
        return instance;
    }
}
