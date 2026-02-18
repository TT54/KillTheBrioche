package fr.tt54.killTheBrioche.inventories.config;

import com.github.twitch4j.helix.domain.CustomReward;
import fr.tt54.killTheBrioche.inventories.CorePersonalInventory;
import fr.tt54.killTheBrioche.rewards.MCReward;
import fr.tt54.killTheBrioche.managers.RewardsManager;
import fr.tt54.killTheBrioche.utils.ItemBuilder;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class MCRewardsInventory extends CorePersonalInventory {

    private final Map<Integer, MCReward> slots = new HashMap<>();

    public MCRewardsInventory(Player player) {
        super("Configuration", player);
    }

    @Override
    public @NotNull Inventory getInventory() {
        Inventory inv = createBaseInventory(6);

        this.setRewardSlot(inv, RewardsManager.spawnZombie, 9 + 3);
        this.setRewardSlot(inv, RewardsManager.spawnSkeleton, 9 + 4);
        this.setRewardSlot(inv, RewardsManager.spawnCreeper, 9 + 5);

        this.setRewardSlot(inv, RewardsManager.effectHunger, 9 * 2 + 4);

        this.setRewardSlot(inv, RewardsManager.rtp2500, 9 * 3 + 3);
        this.setRewardSlot(inv, RewardsManager.tpAncientCity, 9 * 3 + 4);
        this.setRewardSlot(inv, RewardsManager.breakBlocks, 9 * 3 + 5);

        this.setRewardSlot(inv, RewardsManager.effectRegen, 9 * 4 + 2);
        this.setRewardSlot(inv, RewardsManager.locateVillage, 9 * 4 + 3);
        this.setRewardSlot(inv, RewardsManager.randomFood, 9 * 4 + 5);
        this.setRewardSlot(inv, RewardsManager.randomStuff, 9 * 4 + 6);

        return inv;
    }

    private void setRewardSlot(Inventory inv, MCReward reward, int slot){
        CustomReward twitchReward = RewardsManager.getTwitchReward(reward);
        inv.setItem(slot, new ItemBuilder(reward.getDisplay(), "§e" + reward.getDisplayName())
                .setLore(twitchReward == null ? "§cPas de récompense twitch associée" : "§d§lTwitch : §f" + twitchReward.getTitle())
                .build());
        this.slots.put(slot, reward);
    }

    @Override
    public void onInventoryClick(InventoryClickEvent event) {
        if(event.getClickedInventory() == event.getInventory()){
            MCReward reward = this.slots.get(event.getSlot());
            if(reward != null){
                TwitchRewardsListInventory inv = new TwitchRewardsListInventory(player, reward, this);
                inv.openInventory();
            }
        }
    }

    @Override
    public void onInventoryOpen() {

    }

    @Override
    public void onInventoryClose(InventoryCloseEvent event) {

    }

    @Override
    public void onInventoryDrag(InventoryDragEvent event) {

    }
}
