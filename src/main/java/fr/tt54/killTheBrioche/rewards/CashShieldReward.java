package fr.tt54.killTheBrioche.rewards;

import fr.tt54.killTheBrioche.managers.RunManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class CashShieldReward extends MCReward{

    public CashShieldReward() {
        super("cash_shield", "Bouclier anti-diviseur de CashPrice", "§eGain d'un bouclier de cashprice", Material.SHIELD);
    }

    @Override
    public void execute(Player target) {
        RunManager.addCashShield(target.getUniqueId(), 1);
        target.sendMessage(Component.text("§eVous avez gagné §61 §eBouclier !"));
    }
}
