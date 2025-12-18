package fr.tt54.killTheBrioche.inventories.config;

import com.github.twitch4j.helix.domain.CustomReward;
import fr.tt54.killTheBrioche.inventories.CorePersonalInventory;
import fr.tt54.killTheBrioche.inventories.PageableInventory;
import fr.tt54.killTheBrioche.rewards.MCReward;
import fr.tt54.killTheBrioche.rewards.RewardsConfig;
import fr.tt54.killTheBrioche.utils.DefaultItems;
import fr.tt54.killTheBrioche.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class TwitchRewardsListInventory extends PageableInventory<CustomReward> {

    private final MCReward associatedReward;
    private final CorePersonalInventory previousInv;

    public TwitchRewardsListInventory(Player player, MCReward associatedReward, CorePersonalInventory previousInv) {
        super("Configuration", player, 1);
        this.associatedReward = associatedReward;
        this.previousInv = previousInv;
    }

    @Override
    protected ItemStack getItemFromObject(CustomReward twitchReward) {
        return new ItemBuilder(Material.PAPER, "§e" + twitchReward.getTitle())
                .setLore("§fPrix : §7" + twitchReward.getCost())
                .build();
    }

    @Override
    protected List<CustomReward> getObjectsList() {
        return RewardsConfig.getTwitchRewards();
    }

    @Override
    protected void generateOverlayInv(Inventory inv) {
        inv.setItem(9 * 5, DefaultItems.BACK.build());
    }

    @Override
    protected void onObjectClicked(InventoryClickEvent event, CustomReward twitchReward) {
        RewardsConfig.linkRedeem(twitchReward, associatedReward);
        previousInv.openInventory();
    }

    @Override
    protected void onInvClick(InventoryClickEvent event) {
        if(event.getInventory() == event.getClickedInventory() && event.getSlot() == 9 * 5) previousInv.openInventory();
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
