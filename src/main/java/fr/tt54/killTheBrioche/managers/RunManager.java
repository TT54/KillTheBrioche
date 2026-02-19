package fr.tt54.killTheBrioche.managers;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class RunManager {

    private static final Set<UUID> runners = new HashSet<>();
    private static final Map<UUID, Double> runnerCashPrice = new HashMap<>();
    private static final Map<UUID, Integer> runnerCashShield = new HashMap<>();
    private static final Map<UUID, Integer> runnerDeathTeleportationBonus = new HashMap<>();

    public static final double INITIAL_CASH_PRICE = 100.0;
    public static final int GAME_DURATION_SECONDS = 60 * 60 * 2;

    private static boolean started = false;
    private static int time;

    public static Set<UUID> getRunners() {
        return runners;
    }

    public static void addRunner(Player player){
        runners.add(player.getUniqueId());
        updateListName(player);
    }

    public static void removeRunner(UUID uuid){
        runners.remove(uuid);
        Player player = Bukkit.getPlayer(uuid);
        if(player != null){
            updateListName(player);
        }
    }

    public static boolean isRunner(UUID uuid){
        return runners.contains(uuid);
    }

    public static void updateListName(Player player) {
        if(isRunner(player.getUniqueId())){
            player.playerListName(Component.text("[RUNNER] ", NamedTextColor.RED)
                    .append(Component.text(player.getName(), NamedTextColor.WHITE)));
        } else{
            player.playerListName(Component.text("[SPECTATOR] ", NamedTextColor.DARK_GRAY)
                    .append(Component.text(player.getName(), NamedTextColor.GRAY)));
        }
    }

    public static double getCashPrice(UUID uuid) {
        return runnerCashPrice.getOrDefault(uuid, INITIAL_CASH_PRICE);
    }

    public static void setPlayerCashPrice(UUID uuid, double cashPrice) {
        runnerCashPrice.put(uuid, cashPrice);
    }

    public static int getCashShield(UUID uuid) {
        return runnerCashShield.getOrDefault(uuid, 0);
    }

    public static int getDeathTeleportationBonus(UUID uuid) {
        return runnerDeathTeleportationBonus.getOrDefault(uuid, 0);
    }

    public static void addDeathTeleportationBonus(UUID uuid, int bonus) {
        runnerDeathTeleportationBonus.put(uuid, getDeathTeleportationBonus(uuid) + bonus);
    }

    public static boolean isStarted() {
        return started;
    }

    public static void setStarted(boolean started) {
        RunManager.started = started;
    }

    public static void handleDeath(Player player){
        if(isRunner(player.getUniqueId())){
            int currentCashShield = getCashShield(player.getUniqueId());
            if(currentCashShield > 0){
                runnerCashShield.put(player.getUniqueId(), currentCashShield - 1);
                player.sendMessage(Component.text("Vous avez été protégé par un bouclier de cash ! Il vous en reste " + (currentCashShield - 1), NamedTextColor.GREEN));
            } else{
                runnerCashPrice.put(player.getUniqueId(), getCashPrice(player.getUniqueId()) / 2);
                player.sendMessage(Component.text("Vous avez perdu la moitié de votre cash ! Votre nouveau cash est de " + getCashPrice(player.getUniqueId()), NamedTextColor.RED));
            }
        }
    }

    public static void tryToTeleportToLastDeath(Player player) {
        int bonusAmount = getDeathTeleportationBonus(player.getUniqueId());
        if(bonusAmount > 0){
            if(player.getLastDeathLocation() == null){
                player.sendMessage(Component.text("Aucun point de mort trouvé pour vous téléporter !", NamedTextColor.RED));
                return;
            }
            addDeathTeleportationBonus(player.getUniqueId(), -1);
            player.sendMessage(Component.text("Vous avez utilisé un bonus de téléportation à votre dernier point de mort ! Il vous en reste " + getDeathTeleportationBonus(player.getUniqueId()), NamedTextColor.GREEN));
            player.teleport(player.getLastDeathLocation());
        } else{
            player.sendMessage(Component.text("Vous n'avez aucun bonus de téléportation à votre dernier point de mort !", NamedTextColor.RED));
        }
    }

    public static void addCashShield(@NotNull UUID uuid, int amount) {
        runnerCashShield.put(uuid, getCashShield(uuid) + amount);
    }

    public static void increaseTime() {
        time++;
    }

    public static int getTime() {
        return time;
    }

    public static void startRun() {
        setStarted(true);
        time = 0;
        Bukkit.getWorlds().getFirst().setTime(0);
        for(UUID runnerUUID : runners){
            runnerCashPrice.put(runnerUUID, INITIAL_CASH_PRICE);
            runnerCashShield.put(runnerUUID, 0);
            runnerDeathTeleportationBonus.put(runnerUUID, 0);
            Player player = Bukkit.getPlayer(runnerUUID);
            if(player != null) {
                player.getEnderChest().clear();
                player.getInventory().clear();
                player.teleport(Bukkit.getWorlds().getFirst().getSpawnLocation());
                player.sendMessage(Component.text("La partie a commencé ! Votre cash initial est de " + INITIAL_CASH_PRICE + "€", NamedTextColor.GREEN));
                updateListName(player);
            }
        }
    }

    public static void stopGame(Player winner) {
        setStarted(false);
        Bukkit.broadcast(Component.text("La partie est terminée !", NamedTextColor.GREEN));
        if(winner != null) {
            Bukkit.broadcast(Component.text("Victoire de " + winner.getName() + " avec un cash de " + getCashPrice(winner.getUniqueId()) + "€ !", NamedTextColor.GOLD));
            for(Player player : Bukkit.getOnlinePlayers()){
                Title title = Title.title(Component.text("Victoire de " + winner.getName() + " !", NamedTextColor.GOLD), Component.text("Cash final : " + getCashPrice(winner.getUniqueId()) + "€", NamedTextColor.YELLOW));
                player.showTitle(title);
            }
        } else {
            Bukkit.broadcast(Component.text("Défaite des runners !", NamedTextColor.RED));
            for(Player player : Bukkit.getOnlinePlayers()){
                Title title = Title.title(Component.text("Défaite !", NamedTextColor.RED), Component.text("Les runners ont perdu !", NamedTextColor.GRAY));
                player.showTitle(title);
            }
        }
        for(UUID runnerUUID : runners){
            Player player = Bukkit.getPlayer(runnerUUID);
            if(player != null) {
                player.setGameMode(GameMode.SPECTATOR);
            }
        }
    }
}
