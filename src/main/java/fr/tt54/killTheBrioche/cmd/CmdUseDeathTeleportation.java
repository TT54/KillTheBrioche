package fr.tt54.killTheBrioche.cmd;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;
import fr.tt54.killTheBrioche.managers.RunManager;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CmdUseDeathTeleportation {

    public static final LiteralCommandNode<CommandSourceStack> deathTpCommand = Commands.literal("deathtp")
            .requires(commandSourceStack -> commandSourceStack.getSender() instanceof Player)
            .executes(ctx -> {
                final CommandSender sender = ctx.getSource().getSender();
                if(sender instanceof Player player && RunManager.isRunner(player.getUniqueId())){
                    RunManager.tryToTeleportToLastDeath(player);
                }
                return Command.SINGLE_SUCCESS;
            })
            .build();

}
