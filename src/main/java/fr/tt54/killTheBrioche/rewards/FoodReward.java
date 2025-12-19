package fr.tt54.killTheBrioche.rewards;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

public class FoodReward extends MCReward{

    private static final List<Material> FOOD = List.of(Material.BEEF, Material.COOKED_BEEF, Material.PORKCHOP, Material.COOKED_PORKCHOP, Material.BREAD,
            Material.CAKE, Material.PUMPKIN_PIE, Material.BEETROOT_SOUP, Material.MUSHROOM_STEW, Material.MUTTON, Material.COOKED_MUTTON, Material.SALMON, Material.COOKED_SALMON,
            Material.COOKIE, Material.GOLDEN_APPLE, Material.ENCHANTED_GOLDEN_APPLE, Material.CARROT, Material.GOLDEN_CARROT, Material.POTATO, Material.POISONOUS_POTATO,
            Material.BAKED_POTATO, Material.BEETROOT, Material.COD, Material.COOKED_COD, Material.COOKED_CHICKEN, Material.CHICKEN, Material.RABBIT, Material.RABBIT_STEW,
            Material.COOKED_RABBIT);

    public FoodReward(Material display) {
        super("random_food", "Nourriture aléatoire", "§eVous avez reçu de la nourriture", display);
    }

    @Override
    public void execute(Player target) {
        Map<Integer, ItemStack> notAdded = target.getInventory().addItem(new ItemStack(FOOD.get(random.nextInt(FOOD.size()))));
        for(ItemStack is : notAdded.values()){
            target.getWorld().dropItemNaturally(target.getLocation(), is);
        }
    }
}
