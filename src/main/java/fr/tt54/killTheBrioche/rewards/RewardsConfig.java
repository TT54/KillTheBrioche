package fr.tt54.killTheBrioche.rewards;

import com.github.twitch4j.eventsub.events.ChannelPointsCustomRewardRedemptionEvent;
import com.github.twitch4j.helix.domain.CustomReward;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import fr.tt54.killTheBrioche.KillTheBrioche;
import fr.tt54.killTheBrioche.twitch.TwitchBridge;
import fr.tt54.killTheBrioche.utils.FileManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;

import java.io.File;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class RewardsConfig {

    public static final MCReward spawnZombie = registerMCReward(new SpawnMobReward(EntityType.ZOMBIE, 5));
    public static final MCReward spawnSkeleton = registerMCReward(new SpawnMobReward(EntityType.SKELETON, 5));
    public static final MCReward spawnCreeper = registerMCReward(new SpawnMobReward(EntityType.CREEPER, 5));

    private static Map<String, String> rewardsLink = new HashMap<>();
    private static final Map<String, CustomReward> twitchRewardsId = new HashMap<>();
    private static final Map<String, MCReward> mcRewards = new HashMap<>();

    private static final Type rewardsLinkType = new TypeToken<Map<String, String>>() {}.getType();
    private static final Gson gson = new Gson();

    public static void load() {
        rewardsLink.clear();

        File rewardsFile = FileManager.getFileWithoutCreating("rewards.json", KillTheBrioche.getInstance());

        if (!rewardsFile.exists()) {
            KillTheBrioche.getInstance().saveResource("rewards.json", false);
        }

        rewardsLink = gson.fromJson(FileManager.read(rewardsFile), rewardsLinkType);
    }

    public static void save(){
        File rewardsFile = FileManager.getFile("rewards.json", KillTheBrioche.getInstance());
        FileManager.write(gson.toJson(rewardsLink), rewardsFile);
    }

    public static void loadTwitchRewards(TwitchBridge bridge){
        twitchRewardsId.clear();
        for(CustomReward reward : bridge.getCustomRewards(false)){
            twitchRewardsId.put(reward.getId(), reward);
        }
    }

    public static void onTwitchRewardRedeemed(ChannelPointsCustomRewardRedemptionEvent event){
        String id = event.getReward().getId();
        if(rewardsLink.containsKey(id)){
            MCReward mcReward = mcRewards.get(rewardsLink.get(id));
            if(mcReward != null){
                Bukkit.getOnlinePlayers().forEach(mcReward::execute);
                Bukkit.broadcastMessage(mcReward.getMessage());
            } else {
                System.err.println("La reward MC associée à " + id + " n'a pas été trouvée");
            }
        }
    }

    private static MCReward registerMCReward(MCReward reward){
        mcRewards.put(reward.getId(), reward);
        return reward;
    }

}
