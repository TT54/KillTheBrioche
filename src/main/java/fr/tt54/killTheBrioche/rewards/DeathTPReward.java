package fr.tt54.killTheBrioche.rewards;

import fr.tt54.killTheBrioche.managers.RunManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class DeathTPReward extends MCReward{

    public DeathTPReward() {
        super("death_tp", "Téléportation à la dernière mort", "§eGain de la possibilité de se téléporter à sa dernière mort", Material.ENDER_PEARL);
    }

    @Override
    public void execute(Player target) {
        RunManager.addDeathTeleportationBonus(target.getUniqueId(), 1);
        target.sendMessage(Component.text("§eVous avez gagné §61 §eBonus de téléportation vers votre§6 dernière mort§e !"));
    }
}
