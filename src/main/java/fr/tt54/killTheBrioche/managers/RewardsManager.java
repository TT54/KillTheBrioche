package fr.tt54.killTheBrioche.managers;

import com.github.twitch4j.eventsub.events.ChannelPointsCustomRewardRedemptionEvent;
import com.github.twitch4j.helix.domain.CustomReward;
import com.google.common.reflect.TypeToken;
import fr.tt54.killTheBrioche.KillTheBrioche;
import fr.tt54.killTheBrioche.rewards.*;
import fr.tt54.killTheBrioche.twitch.TwitchBridge;
import fr.tt54.killTheBrioche.utils.FileManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.lang.reflect.Type;
import java.util.*;

public class RewardsManager {

    private static Map<String, String> rewardsLink = new HashMap<>();
    private static final Map<String, CustomReward> twitchRewardsId = new HashMap<>();
    private static final Map<String, MCReward> mcRewards = new HashMap<>();

    private static final Type rewardsLinkType = new TypeToken<@NotNull Map<String, String>>() {}.getType();

    public static final MCReward spawnZombie = registerMCReward(new SpawnMobReward(EntityType.ZOMBIE, 5));
    public static final MCReward spawnSkeleton = registerMCReward(new SpawnMobReward(EntityType.SKELETON, 5));
    public static final MCReward spawnCreeper = registerMCReward(new SpawnMobReward(EntityType.CREEPER, 5));
    public static final MCReward effectHunger = registerMCReward(new EffectReward(PotionEffectType.HUNGER, 1, 10, Material.ROTTEN_FLESH));
    public static final MCReward effectRegen = registerMCReward(new EffectReward(PotionEffectType.REGENERATION, 0, 5, Material.GOLDEN_APPLE));
    public static final MCReward rtp2500 = registerMCReward(new RandomTeleportReward(2500, Material.ENDER_PEARL));
    public static final MCReward tpAncientCity = registerMCReward(new AncientCityTeleportationReward(Material.SCULK_SHRIEKER));
    public static final MCReward breakBlocks = registerMCReward(new BreakBlocksReward(10, 5, Material.DIAMOND_PICKAXE));
    public static final MCReward locateVillage = registerMCReward(new VillageLocateReward(Material.EMERALD));
    public static final MCReward randomFood = registerMCReward(new FoodReward(Material.COOKED_BEEF));
    public static final MCReward randomStuff = registerMCReward(new StuffReward(Material.DIAMOND_CHESTPLATE));

    public static void load() {
        rewardsLink.clear();

        File rewardsFile = FileManager.getFileWithoutCreating("rewards.json", KillTheBrioche.getInstance());

        if (!rewardsFile.exists()) {
            KillTheBrioche.getInstance().saveResource("rewards.json", false);
        }

        rewardsLink = KillTheBrioche.gson.fromJson(FileManager.read(rewardsFile), rewardsLinkType);
    }

    public static void save(){
        File rewardsFile = FileManager.getFile("rewards.json", KillTheBrioche.getInstance());
        FileManager.write(KillTheBrioche.gson.toJson(rewardsLink), rewardsFile);
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
        Bukkit.getScheduler().runTask(KillTheBrioche.getInstance(), () -> {
            for(UUID uuid : RunManager.getRunners()){
                Player player = Bukkit.getPlayer(uuid);
                if(player != null) mcReward.execute(player);
            }
            for(Player player : Bukkit.getOnlinePlayers()){
                if(!RunManager.isRunner(player.getUniqueId())){
                    player.sendMessage(Component.text(mcReward.getMessage()));
                }
            }
            KillTheBrioche.logger.info("Récompense exécutée : " + mcReward.getId());
        });
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
