package fr.tt54.killTheBrioche.inventories.config;

import com.github.twitch4j.helix.domain.CustomReward;
import fr.tt54.killTheBrioche.inventories.CorePersonalInventory;
import fr.tt54.killTheBrioche.rewards.MCReward;
import fr.tt54.killTheBrioche.rewards.RewardsConfig;
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
        Inventory inv = createBaseInventory(5);

        this.setRewardSlot(inv, RewardsConfig.spawnZombie, 9 + 3);
        this.setRewardSlot(inv, RewardsConfig.spawnSkeleton, 9 + 4);
        this.setRewardSlot(inv, RewardsConfig.spawnCreeper, 9 + 5);

        return inv;
    }

    private void setRewardSlot(Inventory inv, MCReward reward, int slot){
        CustomReward twitchReward = RewardsConfig.getTwitchReward(reward);
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
