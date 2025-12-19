package fr.tt54.killTheBrioche.twitch;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.auth.providers.TwitchIdentityProvider;
import com.github.twitch4j.eventsub.Conduit;
import com.github.twitch4j.eventsub.EventSubSubscription;
import com.github.twitch4j.eventsub.events.ChannelPointsCustomRewardRedemptionEvent;
import com.github.twitch4j.eventsub.socket.IEventSubConduit;
import com.github.twitch4j.eventsub.socket.conduit.TwitchConduitSocketPool;
import com.github.twitch4j.eventsub.subscriptions.SubscriptionTypes;
import com.github.twitch4j.helix.domain.*;
import fr.tt54.killTheBrioche.KillTheBrioche;
import fr.tt54.killTheBrioche.utils.FileManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Level;

public class TwitchBridge {

    public static TwitchBridge instance;

    public TwitchToken token;
    public long lastTokenRefresh = 0;
    private final String clientId;
    private final String clientSecret;

    private TwitchClient twitchClient;
    private User currentUser;

    private IEventSubConduit conduit;
    private Consumer<TwitchBridge> connectionConsumer;
    private Consumer<ChannelPointsCustomRewardRedemptionEvent> redemptionEventConsumer;
    private final HttpClient httpClient = HttpClientBuilder.create().build();

    public TwitchBridge(String clientId, String clientSecret) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    public TwitchBridge onConnection(Consumer<TwitchBridge> connectionConsumer){
        this.connectionConsumer = connectionConsumer;
        return this;
    }

    @SuppressWarnings("UnusedReturnValue")
    public TwitchBridge onRewardRedemption(Consumer<ChannelPointsCustomRewardRedemptionEvent> redemptionEventConsumer){
        this.redemptionEventConsumer = redemptionEventConsumer;
        return this;
    }

    public void retrieveTwitchToken(String code) throws IOException {
        Bukkit.broadcast(Component.text("Connexion à l'API Twitch en cours...", NamedTextColor.GRAY));

        HttpPost request = new HttpPost("https://id.twitch.tv/oauth2/token");
        final List<NameValuePair> params = List.of(
                new BasicNameValuePair("client_id", this.clientId),
                new BasicNameValuePair("client_secret", this.clientSecret),
                new BasicNameValuePair("code", code),
                new BasicNameValuePair("grant_type", "authorization_code"),
                new BasicNameValuePair("redirect_uri", "http://localhost:8080/callback")
        );
        request.setEntity(new UrlEncodedFormEntity(params));

        HttpResponse response = httpClient.execute(request);
        String body = EntityUtils.toString(response.getEntity());
        System.out.println(body);
        token = KillTheBrioche.gson.fromJson(body, TwitchToken.class);
        saveTwitchToken();

        this.lastTokenRefresh = System.currentTimeMillis() / 1000;

        this.removePreviousConduits();
        connect();
    }

    private void removePreviousConduits(){
        TwitchIdentityProvider identityProvider = new TwitchIdentityProvider(this.clientId, this.clientSecret, null);
        OAuth2Credential appToken = identityProvider.getAppAccessToken();

        EventSubSubscriptionList subs = twitchClient.getHelix().getEventSubSubscriptions(appToken.getAccessToken(), null, null, null).execute();
        System.out.println("Subscriptions trouvées : " + subs.getTotal());
        for (EventSubSubscription sub : subs.getSubscriptions()) {
            twitchClient.getHelix().deleteEventSubSubscription(appToken.getAccessToken(), sub.getId()).execute();
        }
        System.out.println("Les subscriptions ont été supprimées.");

        ConduitList conduits = twitchClient.getHelix().getConduits(appToken.getAccessToken()).execute();
        System.out.println("Conduits trouvés : " + conduits.getConduits().size());
        for (Conduit conduit : conduits.getConduits()) {
            twitchClient.getHelix().deleteConduit(appToken.getAccessToken(), conduit.getId()).execute();
        }
        System.out.println("Les conduits ont été supprimés");
    }

    public void loadTwitchToken(){
        File tokenFile = FileManager.getFileWithoutCreating("token.json", KillTheBrioche.getInstance());
        if (tokenFile.exists()) {
            this.token = KillTheBrioche.gson.fromJson(FileManager.read(tokenFile), TwitchToken.class);
            try {
                this.refreshToken();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void saveTwitchToken() throws IOException {
        if(TwitchBridge.instance.token != null) {
            System.out.println("Saving Twitch token");
            File tokenFile = FileManager.getFile("token.json", KillTheBrioche.getInstance());
            if(!tokenFile.exists()) {
                System.out.println("Should create a file");
                System.out.println("Creation succeed: " + tokenFile.createNewFile());
            }
            FileManager.write(KillTheBrioche.gson.toJson(this.token), tokenFile);
            System.out.println("Twitch token saved");
        }
    }

    public void refreshToken() throws IOException {
        HttpPost request = new HttpPost("https://id.twitch.tv/oauth2/token");
        final List<NameValuePair> params = List.of(
                new BasicNameValuePair("client_id", this.clientId),
                new BasicNameValuePair("client_secret", this.clientSecret),
                new BasicNameValuePair("grant_type", "refresh_token"),
                new BasicNameValuePair("refresh_token", this.token.refresh_token())
        );
        request.setEntity(new UrlEncodedFormEntity(params));

        HttpResponse response = httpClient.execute(request);
        String body = EntityUtils.toString(response.getEntity());
        System.out.println(body);
        token = KillTheBrioche.gson.fromJson(body, TwitchToken.class);
        this.saveTwitchToken();

        this.lastTokenRefresh = System.currentTimeMillis() / 1000;

        connect();
    }

    private void connect(){
        OAuthCallbackServer.stopServer();

        OAuth2Credential credential = new OAuth2Credential("twitch", token.access_token());
        this.twitchClient = TwitchClientBuilder.builder()
                .withEnableHelix(true)
                .withDefaultAuthToken(credential)
                .build();
        this.currentUser = getCurrentUser(twitchClient);

        try {
            if(conduit != null) {
                conduit.close();
            }
            conduit = TwitchConduitSocketPool.create(spec -> {
                spec.clientId(this.clientId);
                spec.clientSecret(this.clientSecret);
                spec.poolShards(4);
            });
            conduit.register(SubscriptionTypes.CHANNEL_POINTS_CUSTOM_REWARD_REDEMPTION_ADD, b -> b.broadcasterUserId(currentUser.getId()).build());
            conduit.getEventManager().onEvent(ChannelPointsCustomRewardRedemptionEvent.class, event -> {
                if(this.redemptionEventConsumer != null) this.redemptionEventConsumer.accept(event);
            });
        } catch (Exception e) {
            KillTheBrioche.logger.log(Level.SEVERE, "Impossible d'écouter l'achat de récompenses via les points de chaîne", e);
            Bukkit.broadcast(Component.text("Impossible d'écouter l'achat de récompense via les points de chaîne", NamedTextColor.RED));
        }

        if(this.connectionConsumer != null) this.connectionConsumer.accept(this);
        Bukkit.broadcast(Component.text("Connecté à l'API Twitch !", NamedTextColor.GREEN));
    }

    public String getConnectionUrlString(){
        return "https://id.twitch.tv/oauth2/authorize" +
                "?response_type=code" +
                "&client_id=" + this.clientId +
                "&redirect_uri=http://localhost:8080/callback" +
                "&scope=channel:read:redemptions" +
                "&state=RANDOM_STRING";
    }

    public User getCurrentUser(TwitchClient twitchClient){
        UserList resultList = twitchClient.getHelix().getUsers(token.access_token(), null, null).execute();
        resultList.getUsers().forEach(System.out::println);
        return resultList.getUsers().getFirst();
    }

    public List<CustomReward> getCustomRewards(boolean onlyManageable){
        try {
            CustomRewardList rewards = this.twitchClient.getHelix().getCustomRewards(this.token.access_token(), this.currentUser.getId(), null, onlyManageable).execute();
            return rewards.getRewards();
        } catch (Exception e){
            KillTheBrioche.logger.log(Level.SEVERE, "Impossible de récupérer la liste des récompenses", e);
        }
        return new ArrayList<>();
    }

    public boolean isUserConnected() {
        return this.token != null;
    }
}
