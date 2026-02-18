package fr.tt54.killTheBrioche.rewards;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

public class StuffReward extends MCReward{

    private static final List<Material> NOOB_STUFF = List.of(Material.STONE_SWORD, Material.CHAINMAIL_HELMET, Material.CHAINMAIL_CHESTPLATE, Material.CHAINMAIL_LEGGINGS,
            Material.CHAINMAIL_BOOTS, Material.COPPER_CHESTPLATE, Material.COPPER_HELMET, Material.COPPER_LEGGINGS, Material.COPPER_BOOTS, Material.COPPER_SWORD);
    private static final List<Material> LESS_NOOB_STUFF = List.of(Material.IRON_SWORD, Material.IRON_HELMET, Material.IRON_CHESTPLATE, Material.IRON_LEGGINGS, Material.IRON_BOOTS,
            Material.GOLDEN_SWORD, Material.GOLDEN_HELMET, Material.GOLDEN_CHESTPLATE, Material.GOLDEN_LEGGINGS, Material.GOLDEN_BOOTS);
    private static final List<Material> DIAMOND_STUFF = List.of(Material.DIAMOND_SWORD, Material.DIAMOND_HELMET, Material.DIAMOND_CHESTPLATE,
            Material.DIAMOND_LEGGINGS, Material.DIAMOND_BOOTS);
    private static final List<Material> NETHERITE_STUFF = List.of(Material.NETHERITE_SWORD, Material.NETHERITE_HELMET, Material.NETHERITE_CHESTPLATE,
            Material.NETHERITE_LEGGINGS, Material.NETHERITE_BOOTS);

    public StuffReward(Material display) {
        super("random_stuff", "Stuff aléatoire", "§eVous avez reçu du stuff", display);
    }

    @Override
    public void execute(Player target) {
        double proba = random.nextDouble();
        if(proba < .35){
            giveItem(target, NOOB_STUFF);
        } else if(proba < .65){
            giveItem(target, LESS_NOOB_STUFF);
        } else if(proba < .9){
            giveItem(target, DIAMOND_STUFF);
        } else {
            giveItem(target, NETHERITE_STUFF);
        }
    }

    private void giveItem(Player target, List<Material> pool){
        Map<Integer, ItemStack> notAdded = target.getInventory().addItem(new ItemStack(pool.get(random.nextInt(pool.size()))));
        for(ItemStack is : notAdded.values()){
            target.getWorld().dropItemNaturally(target.getLocation(), is);
        }
    }
}
