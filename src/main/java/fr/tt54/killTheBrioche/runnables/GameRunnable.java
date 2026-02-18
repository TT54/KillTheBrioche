package fr.tt54.killTheBrioche.runnables;

import fr.tt54.killTheBrioche.KillTheBrioche;
import fr.tt54.killTheBrioche.managers.RunManager;
import org.bukkit.scheduler.BukkitRunnable;

public class GameRunnable extends BukkitRunnable {
    @Override
    public void run() {
        KillTheBrioche.gameScoreboard.updatePlayersScoreboard();
        if(RunManager.isStarted()){
            RunManager.increaseTime();
        }
    }
}
