package fr.tt54.killTheBrioche.scoreboard;

import fr.mrmicky.fastboard.FastBoard;
import fr.tt54.killTheBrioche.managers.RunManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.UUID;

public class GameScoreboard extends ImpyriaScoreboard {

    public static final DecimalFormat format = new DecimalFormat("00");
    public static final DecimalFormat formatDouble = new DecimalFormat("0.00");



    public GameScoreboard() {

    }

    @Override
    protected void generateBoard(FastBoard fastBoard, Player player) {
        drawScoreboard(fastBoard, player);
    }

    @Override
    protected void refreshBoard(FastBoard fastBoard, Player player) {
        drawScoreboard(fastBoard, player);
    }

    private void drawScoreboard(FastBoard fastBoard, Player player){
        int timeLeft = RunManager.GAME_DURATION_SECONDS - RunManager.getTime();
        int minutes = timeLeft / 60;
        int seconds = timeLeft % 60;

        int i = 0;
        fastBoard.updateTitle("§6§lKill The Brioche");
        fastBoard.updateLine(i++, "§7Temps restant : §e" + format.format(minutes) + ":" + format.format(seconds));
        fastBoard.updateLine(i++, "§7");

        for(UUID runnerUUID : RunManager.getRunners()){
            OfflinePlayer runner = Bukkit.getOfflinePlayer(runnerUUID);
            if(runner.getName() != null) {
                fastBoard.updateLine(i++, "§e" + runner.getName() + " : §f" + formatDouble.format(RunManager.getCashPrice(runnerUUID)) + "€");
                String secondLineBuilder = " ".repeat(runner.getName().length()) +
                        "§7" +
                        format.format(RunManager.getCashShield(runnerUUID)) +
                        "\uD83D\uDEE1 | §b" +
                        format.format(RunManager.getCashShield(runnerUUID)) +
                        "TP";
                fastBoard.updateLine(i++, secondLineBuilder);
            }
        }
    }
}
