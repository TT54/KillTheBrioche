package fr.tt54.killTheBrioche.managers;

import com.github.twitch4j.eventsub.events.ChannelPointsCustomRewardRedemptionEvent;
import com.github.twitch4j.eventsub.events.ChannelSubscriptionMessageEvent;
import com.github.twitch4j.helix.domain.CustomReward;
import com.github.twitch4j.helix.domain.CustomRewardList;
import com.google.common.reflect.TypeToken;
import fr.tt54.killTheBrioche.KillTheBrioche;
import fr.tt54.killTheBrioche.rewards.*;
import fr.tt54.killTheBrioche.twitch.TwitchBridge;
import fr.tt54.killTheBrioche.utils.FileManager;
import net.kyori.adventure.text.Component;
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
    private static final Map<String, TwitchRewardAttributes> rewardAttributes = new HashMap<>();

    private static final Type rewardsLinkType = new TypeToken<@NotNull Map<String, String>>() {}.getType();

    public static final MCReward spawnZombie = registerMCReward(new SpawnMobReward(EntityType.ZOMBIE, 5, 1), "", 40, 60);
    public static final MCReward spawnSkeleton = registerMCReward(new SpawnMobReward(EntityType.SKELETON, 5, 1), "", 70, 60);
    public static final MCReward spawnCreeper = registerMCReward(new SpawnMobReward(EntityType.CREEPER, 5, 1), "", 180, 60);
    public static final MCReward spawnWitch = registerMCReward(new SpawnMobReward(EntityType.WITCH, 5, 1), "", 250, 60);
    public static final MCReward spawnVex = registerMCReward(new SpawnMobReward(EntityType.VEX, 5, 1), "", 750, 300);
    public static final MCReward spawn5Zombies = registerMCReward(new SpawnMobReward(EntityType.ZOMBIE, 10, 5), "", 280, 300);
    public static final MCReward spawn5Skeletons = registerMCReward(new SpawnMobReward(EntityType.SKELETON, 10, 5), "", 540, 300);
    public static final MCReward breakBlocks = registerMCReward(new BreakBlocksReward(10, 5, Material.DIAMOND_PICKAXE), "", 2500, 300);
    public static final MCReward rtp2500 = registerMCReward(new RandomTeleportReward(2500, Material.ENDER_PEARL), "", 5000, 15 * 60);
    public static final MCReward tpAncientCity = registerMCReward(new AncientCityTeleportationReward(Material.SCULK_SHRIEKER), "", 10000, 30 * 60);

    public static final MCReward randomFood = registerMCReward(new FoodReward(Material.COOKED_BEEF), "", 80, 0);
    public static final MCReward effectInstantHeal1 = registerMCReward(new EffectReward(PotionEffectType.INSTANT_HEALTH, 0, 1, Material.GOLDEN_APPLE), "", 160, 0);
    public static final MCReward killMobsAround = registerMCReward(new KillMobsAround(25), "", 250, 60);
    public static final MCReward spawnIronGolem = registerMCReward(new SpawnMobReward(EntityType.IRON_GOLEM, 1, 1), "", 480, 120);
    public static final MCReward randomEffet = registerMCReward(new RandomEffectsReward(), "", 550, 300);
    public static final MCReward randomStuff = registerMCReward(new StuffReward(Material.DIAMOND_CHESTPLATE), "35% : Pierre / Copper \n30% : Fer / Or \n25% : Diamant \n10% : Netherite", 1600, 600);
    public static final MCReward deathTPReward = registerMCReward(new DeathTPReward(), "", 2500, 15 * 60);
    public static final MCReward cashShieldReward = registerMCReward(new CashShieldReward(), "", 2000, 15 * 60);


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

        for(Map.Entry<String, MCReward> mcRewardEntry : mcRewards.entrySet()){
            String mcRewardId = mcRewardEntry.getKey();
            MCReward mcReward = mcRewardEntry.getValue();
            TwitchRewardAttributes attributes = rewardAttributes.get(mcRewardId);
            if(!rewardsLink.containsValue(mcRewardId)){
                KillTheBrioche.logger.info("La reward MC " + mcRewardId + " n'est pas liée à une reward Twitch, création d'une reward Twitch associée...");
                CustomRewardList result = bridge.createCustomReward(mcReward.getDisplayName(), attributes.description(), attributes.cost(), attributes.cooldown());
                if(!result.getRewards().isEmpty()) {
                    CustomReward twitchReward = result.getRewards().getFirst();
                    twitchRewardsId.put(twitchReward.getId(), twitchReward);
                    rewardsLink.put(twitchReward.getId(), mcRewardId);
                    KillTheBrioche.logger.info("Reward Twitch créée et liée à " + mcRewardId);
                } else {
                    KillTheBrioche.logger.severe("Erreur lors de la création de la reward Twitch pour " + mcRewardId);
                }
            }
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

    private static MCReward registerMCReward(MCReward reward, String description, int cost, int cooldown){
        mcRewards.put(reward.getId(), reward);
        rewardAttributes.put(reward.getId(), new TwitchRewardAttributes(description, cost, cooldown));
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

    public static void onTwitchSub(ChannelSubscriptionMessageEvent event) {
        // TODO : Faire une reward spéciale pour les subs
    }

    private record TwitchRewardAttributes(String description, int cost, int cooldown){

    }
}
