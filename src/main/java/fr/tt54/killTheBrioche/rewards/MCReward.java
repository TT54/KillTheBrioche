package fr.tt54.killTheBrioche.rewards;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Random;

public abstract class MCReward {

    protected static final Random random = new Random();

    private final String id;
    private final String displayName;
    private final String message;
    private final Material display;

    public MCReward(String id, String displayName, String message, Material display) {
        this.id = id;
        this.displayName = displayName;
        this.message = message;
        this.display = display;
    }

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getMessage() {
        return message;
    }

    public abstract void execute(Player target);
}
