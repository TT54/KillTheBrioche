package fr.tt54.killTheBrioche.twitch;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.eventsub.events.ChannelPointsCustomRewardRedemptionEvent;
import com.github.twitch4j.eventsub.socket.IEventSubConduit;
import com.github.twitch4j.eventsub.socket.conduit.TwitchConduitSocketPool;
import com.github.twitch4j.eventsub.socket.conduit.exceptions.*;
import com.github.twitch4j.eventsub.subscriptions.SubscriptionTypes;
import com.github.twitch4j.helix.domain.CustomReward;
import com.github.twitch4j.helix.domain.CustomRewardList;
import com.github.twitch4j.helix.domain.User;
import com.github.twitch4j.helix.domain.UserList;
import com.google.gson.Gson;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class TwitchBridge {

    public static TwitchBridge instance;

    public TwitchToken token;
    private final String clientId;
    private final String clientSecret;

    private TwitchClient twitchClient;
    private User currentUser;

    public TwitchBridge(String clientId, String clientSecret) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    public void retrieveTwitchToken(String code) throws IOException {
        final HttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost request = new HttpPost("https://id.twitch.tv/oauth2/token");
        final List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("client_id", this.clientId));
        params.add(new BasicNameValuePair("client_secret", this.clientSecret));
        params.add(new BasicNameValuePair("code", code));
        params.add(new BasicNameValuePair("grant_type", "authorization_code"));
        params.add(new BasicNameValuePair("redirect_uri", "http://localhost:8080/callback"));
        request.setEntity(new UrlEncodedFormEntity(params));

        HttpResponse response = httpClient.execute(request);
        String body = EntityUtils.toString(response.getEntity());
        System.out.println(body);
        token = new Gson().fromJson(body, TwitchToken.class);

        connect();
    }

    public void connect(){
        OAuth2Credential credential = new OAuth2Credential("twitch", token.access_token());
        this.twitchClient = TwitchClientBuilder.builder()
                .withEnableHelix(true)
                .withDefaultAuthToken(credential)
                .build();
        this.currentUser = getCurrentUser(twitchClient);

        try {
            IEventSubConduit conduit = TwitchConduitSocketPool.create(spec -> {
                spec.clientId(this.clientId);
                spec.clientSecret(this.clientSecret);
                spec.poolShards(4); // customizable pool size
            });
            conduit.register(SubscriptionTypes.CHANNEL_POINTS_CUSTOM_REWARD_REDEMPTION_ADD, b -> b.broadcasterUserId(currentUser.getId()).build());
            conduit.getEventManager().onEvent(ChannelPointsCustomRewardRedemptionEvent.class, event -> {
                System.out.println("Récompense utilisée !");
                System.out.println("Utilisateur : " + event.getUserName());
                System.out.println("Récompense : " + event.getReward().getTitle());
                System.out.println("Id de la récompense : " + event.getReward().getId());
                System.out.println("Coût : " + event.getReward().getCost());
            });
        } catch (CreateConduitException | ConduitNotFoundException | ConduitResizeException | ShardTimeoutException |
                 ShardRegistrationException e) {
            e.printStackTrace();
        }
    }

    public void askConnection() throws IOException, URISyntaxException {
        URI uri = new URI("https://id.twitch.tv/oauth2/authorize" +
                "?response_type=code" +
                "&client_id=" + this.clientId +
                "&redirect_uri=http://localhost:8080/callback" +
                "&scope=channel:read:redemptions" +
                "&state=RANDOM_STRING");
        Desktop.getDesktop().browse(uri);
    }

    public User getCurrentUser(TwitchClient twitchClient){
        UserList resultList = twitchClient.getHelix().getUsers(token.access_token(), null, null).execute();
        resultList.getUsers().forEach(System.out::println);
        return resultList.getUsers().getFirst();
    }

    public List<CustomReward> getCustomRewards(TwitchClient twitchClient){
        try {
            CustomRewardList rewards = twitchClient.getHelix().getCustomRewards(this.token.access_token(), this.currentUser.getId(), null, false).execute();
            return rewards.getRewards();
        } catch (Exception e){
            e.printStackTrace();
        }
        return new ArrayList<>();
    }
}
