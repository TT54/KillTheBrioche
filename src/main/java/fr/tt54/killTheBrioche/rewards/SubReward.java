package fr.tt54.killTheBrioche.rewards;

import fr.tt54.killTheBrioche.KillTheBrioche;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class SubReward extends MCReward{

    public static final int MAX_WAVE = 4;
    public static final int MAX_DISTANCE = 30;

    public static final SubReward subReward = new SubReward();

    private final Map<UUID, Integer> subCounts = new HashMap<>();
    private final Map<UUID, Integer> subWave = new HashMap<>();
    private final Map<UUID, List<Entity>> spawnedEntities = new HashMap<>();
    private final Map<UUID, Location> spawnLocations = new HashMap<>();

    public SubReward() {
        super("sub_reward", "Nouveau Sub !", "Quelque chose semble se préparer...", Material.WITHER_SKELETON_SKULL);
    }

    @Override
    public void execute(Player target) {
        final UUID uuid = target.getUniqueId();
        subCounts.put(uuid, subCounts.getOrDefault(uuid, 0) + 1);
        int count = subCounts.get(uuid);

        // Il n'y a pas encore de vague en cours, on en lance une
        if(count == 1){
            startReward(uuid, target.getLocation().clone());
        }
    }

    public void startReward(UUID playerUUID, Location startLocation){
        launchWave(playerUUID, 1);
        subWave.put(playerUUID, 1);
        spawnLocations.put(playerUUID, startLocation);
        spawnedEntities.put(playerUUID, new ArrayList<>());

        BukkitRunnable runnable = new BukkitRunnable() {

            int lastWaveCounter = 0;

            @Override
            public void run() {
                // Si la reward est terminée, on l'annule
                if(subWave.getOrDefault(playerUUID, 0) == 0){
                    this.cancel();
                    for(Entity entity : spawnedEntities.get(playerUUID)){
                        entity.remove();
                    }
                    return;
                }

                int currentWave = subWave.getOrDefault(playerUUID, 0);
                final Location spawnLocation = spawnLocations.get(playerUUID);

                // Si on est à la dernière vague, on spawn des silverfish toutes les 10 secondes tant que le joueur n'a pas tué tous les mobs
                if(currentWave == MAX_WAVE) {
                    if (lastWaveCounter == 0 && spawnLocation != null) {
                        for (int i = 0; i < 3; i++) {
                            spawnLocation.getWorld().spawnEntity(spawnLocation, EntityType.SILVERFISH);
                        }
                    }
                    lastWaveCounter = (lastWaveCounter + 1) % 10;
                }

                Player player = Bukkit.getPlayer(playerUUID);
                if(player == null){
                    return;
                }
                checkPlayerDistance(player);

                // S'il reste des mobs à tuer, on s'arrête ici
                for(Entity entity : spawnedEntities.get(playerUUID)){
                    if(!entity.isDead() || entity.isValid()) return;
                }

                // Sinon, on lance la vague suivante
                if(currentWave < MAX_WAVE) {
                    subWave.put(playerUUID, currentWave + 1);
                    spawnedEntities.get(playerUUID).clear();
                    launchWave(playerUUID, currentWave + 1);
                } else{
                    // La reward est terminée
                    subWave.put(playerUUID, 0);
                    spawnedEntities.get(playerUUID).clear();
                    subCounts.put(playerUUID, subCounts.getOrDefault(playerUUID, 1) - 1);

                    player.sendMessage(Component.text("Félicitations, vous avez terminé le raid !", NamedTextColor.GREEN));

                    // Si le joueur a encore des subs à faire, on relance une reward
                    if(subCounts.getOrDefault(playerUUID, 0) > 0) {
                        startReward(playerUUID, player.getLocation().clone());
                    }

                    this.cancel();
                }
            }
        };
        runnable.runTaskTimer(KillTheBrioche.getInstance(), 20, 20);
    }

    public void checkPlayerDistance(Player player){
        final UUID uuid = player.getUniqueId();
        if(!spawnLocations.containsKey(uuid)) return;

        final Location spawnLocation = spawnLocations.get(uuid);
        if(player.getLocation().distance(spawnLocation) > MAX_DISTANCE){
            player.teleport(spawnLocation);
            for(Entity entity : spawnedEntities.get(uuid)){
                if(entity.isValid() && !entity.isDead()){
                    entity.teleport(spawnLocation);
                }
            }
        }
    }

    private void launchWave(UUID targetUUID, int wave){
        final Location location = spawnLocations.get(targetUUID);
        if(location == null) return;

        if(wave == 1) {
            for(int i = 0; i < 5; i++){
                Entity entity = location.getWorld().spawnEntity(location, EntityType.ZOMBIE);
                entity.setPersistent(true);
                entity.setGlowing(true);
                spawnedEntities.computeIfAbsent(targetUUID, k -> new ArrayList<>()).add(entity);
            }
            for(int i = 0; i < 2; i++){
                Entity entity = location.getWorld().spawnEntity(location, EntityType.SKELETON);
                entity.setPersistent(true);
                entity.setGlowing(true);
                spawnedEntities.computeIfAbsent(targetUUID, k -> new ArrayList<>()).add(entity);
            }
        } else if(wave == 2) {
            for(int i = 0; i < 2; i++){
                Parched entity = (Parched) location.getWorld().spawnEntity(location, EntityType.PARCHED);
                entity.setPersistent(true);
                entity.setGlowing(true);
                entity.getEquipment().setArmorContents(new ItemStack[]{new ItemStack(Material.GOLDEN_BOOTS), new ItemStack(Material.GOLDEN_LEGGINGS), new ItemStack(Material.GOLDEN_CHESTPLATE), new ItemStack(Material.GOLDEN_HELMET)});
                spawnedEntities.computeIfAbsent(targetUUID, k -> new ArrayList<>()).add(entity);
            }
            Witch witch = (Witch) location.getWorld().spawnEntity(location, EntityType.WITCH);
            witch.setPersistent(true);
            witch.setGlowing(true);
            spawnedEntities.computeIfAbsent(targetUUID, k -> new ArrayList<>()).add(witch);

            CamelHusk camelHusk = (CamelHusk) location.getWorld().spawnEntity(location, EntityType.CAMEL_HUSK);
            camelHusk.setPersistent(true);
            camelHusk.setGlowing(true);
            spawnedEntities.computeIfAbsent(targetUUID, k -> new ArrayList<>()).add(camelHusk);

            Husk husk = (Husk) location.getWorld().spawnEntity(location, EntityType.HUSK);
            husk.setPersistent(true);
            husk.setGlowing(true);
            husk.getEquipment().setItemInMainHand(new ItemStack(Material.IRON_SPEAR));
            spawnedEntities.computeIfAbsent(targetUUID, k -> new ArrayList<>()).add(husk);

            camelHusk.addPassenger(husk);
        } else if(wave == 3){
            for(int i = 0; i < 3; i++){
                Entity entity = location.getWorld().spawnEntity(location, EntityType.PILLAGER);
                entity.setPersistent(true);
                entity.setGlowing(true);
                spawnedEntities.computeIfAbsent(targetUUID, k -> new ArrayList<>()).add(entity);
            }
            for(int i = 0; i < 2; i++){
                Entity entity = location.getWorld().spawnEntity(location, EntityType.VINDICATOR);
                entity.setPersistent(true);
                entity.setGlowing(true);
                spawnedEntities.computeIfAbsent(targetUUID, k -> new ArrayList<>()).add(entity);
            }
            Entity entity = location.getWorld().spawnEntity(location, EntityType.RAVAGER);
            entity.setPersistent(true);
            entity.setGlowing(true);
            spawnedEntities.computeIfAbsent(targetUUID, k -> new ArrayList<>()).add(entity);
        } else if(wave == 4){
            Illusioner entity = (Illusioner) location.getWorld().spawnEntity(location, EntityType.ILLUSIONER);
            entity.setPersistent(true);
            entity.setGlowing(true);
            entity.addPotionEffect(PotionEffectType.RESISTANCE.createEffect(20 * 60 * 60 * 60, 1));
            spawnedEntities.computeIfAbsent(targetUUID, k -> new ArrayList<>()).add(entity);
        }
    }
}
