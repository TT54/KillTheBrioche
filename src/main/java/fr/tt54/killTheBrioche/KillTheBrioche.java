package fr.tt54.killTheBrioche;

import fr.tt54.killTheBrioche.listeners.PlayerListener;
import fr.tt54.killTheBrioche.twitch.OAuthCallbackServer;
import fr.tt54.killTheBrioche.twitch.TwitchBridge;
import org.bukkit.plugin.java.JavaPlugin;

public final class KillTheBrioche extends JavaPlugin {

    private static KillTheBrioche instance;

    @Override
    public void onEnable() {
        instance = this;

        this.saveDefaultConfig();

        TwitchBridge.instance = new TwitchBridge(this.getConfig().getString("client_id", "client_id"), this.getConfig().getString("client_secret", "client_secret"));
        OAuthCallbackServer.launchServer();

        this.getServer().getPluginManager().registerEvents(new PlayerListener(), this);
    }

    @Override
    public void onDisable() {
        OAuthCallbackServer.stopServer();
    }

    public static KillTheBrioche getInstance() {
        return instance;
    }
}
