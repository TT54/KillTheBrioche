package fr.tt54.killTheBrioche.rewards;

import com.github.twitch4j.eventsub.events.ChannelPointsCustomRewardRedemptionEvent;
import com.github.twitch4j.helix.domain.CustomReward;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import fr.tt54.killTheBrioche.KillTheBrioche;
import fr.tt54.killTheBrioche.twitch.TwitchBridge;
import fr.tt54.killTheBrioche.utils.FileManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RewardsConfig {

    private static Map<String, String> rewardsLink = new HashMap<>();
    private static final Map<String, CustomReward> twitchRewardsId = new HashMap<>();
    private static final Map<String, MCReward> mcRewards = new HashMap<>();

    private static final Type rewardsLinkType = new TypeToken<Map<String, String>>() {}.getType();
    private static final Gson gson = new Gson();

    public static final MCReward spawnZombie = registerMCReward(new SpawnMobReward(EntityType.ZOMBIE, 5));
    public static final MCReward spawnSkeleton = registerMCReward(new SpawnMobReward(EntityType.SKELETON, 5));
    public static final MCReward spawnCreeper = registerMCReward(new SpawnMobReward(EntityType.CREEPER, 5));
    public static final MCReward effectHunger = registerMCReward(new EffectReward(PotionEffectType.HUNGER, 1, 10, Material.ROTTEN_FLESH));
    public static final MCReward effectRegen = registerMCReward(new EffectReward(PotionEffectType.REGENERATION, 0, 5, Material.GOLDEN_APPLE));
    public static final MCReward rtp2500 = registerMCReward(new RandomTeleportReward(2500, Material.ENDER_PEARL));
    public static final MCReward tpAncientCity = registerMCReward(new AncientCityTeleportationReward(Material.SCULK_SHRIEKER));
    public static final MCReward locateVillage = registerMCReward(new VillageLocateReward(Material.EMERALD));

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
                executeReward(mcReward);
            } else {
                System.err.println("La reward MC associée à " + id + " n'a pas été trouvée");
            }
        }
    }

    public static void executeReward(MCReward mcReward){
        Bukkit.getOnlinePlayers().forEach(mcReward::execute);
        Bukkit.broadcast(Component.text(mcReward.getMessage()));
    }

    private static MCReward registerMCReward(MCReward reward){
        mcRewards.put(reward.getId(), reward);
        return reward;
    }

    public static CustomReward getTwitchReward(MCReward reward) {
        for(Map.Entry<String, String> entry : rewardsLink.entrySet()){
            if(entry.getValue().equalsIgnoreCase(reward.getId())) return twitchRewardsId.get(entry.getKey());
        }
        return null;
    }

    public static List<CustomReward> getTwitchRewards(){
        return twitchRewardsId.values().stream().toList();
    }

    public static void linkRedeem(CustomReward twitchReward, MCReward mcReward) {
        rewardsLink.put(twitchReward.getId(), mcReward.getId());
    }

    public static Set<String> getMCRewardIds() {
        return mcRewards.keySet();
    }

    public static MCReward getMcReward(String rewardID) {
        return mcRewards.get(rewardID);
    }
}
