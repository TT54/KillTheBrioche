package fr.tt54.killTheBrioche.cmd;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import fr.tt54.killTheBrioche.inventories.config.MCRewardsInventory;
import fr.tt54.killTheBrioche.rewards.MCReward;
import fr.tt54.killTheBrioche.rewards.RewardsConfig;
import fr.tt54.killTheBrioche.twitch.TwitchBridge;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CmdKillTheBrioche {

    public static final LiteralCommandNode<CommandSourceStack> ktbCommand = Commands.literal("ktb")
            .requires(commandSourceStack -> commandSourceStack.getSender().hasPermission("ktb"))
            .then(Commands.literal("config")
                    .requires(commandSourceStack -> commandSourceStack.getSender() instanceof Player)
                    .executes(ctx -> {
                        final CommandSender sender = ctx.getSource().getSender();
                        if(sender instanceof Player player){
                            MCRewardsInventory inv = new MCRewardsInventory(player);
                            inv.openInventory();
                        }
                        return Command.SINGLE_SUCCESS;
                    })
            ).then(Commands.literal("simulate")
                    .then(Commands.argument("reward", StringArgumentType.word())
                            .suggests((commandContext, suggestionsBuilder) -> {
                                for(String id : RewardsConfig.getMCRewardIds()){
                                    suggestionsBuilder.suggest(id);
                                }
                                return suggestionsBuilder.buildFuture();
                            })
                            .executes(ctx -> {
                                final CommandSender sender = ctx.getSource().getSender();
                                final String rewardID = ctx.getArgument("reward", String.class);
                                final MCReward reward = RewardsConfig.getMcReward(rewardID);
                                if(reward != null){
                                    RewardsConfig.executeReward(reward);
                                } else {
                                    sender.sendMessage("§cRécompense " + rewardID + " non trouvée");
                                }
                                return Command.SINGLE_SUCCESS;
                            })
                    )
            ).then(Commands.literal("refresh")
                    .executes(ctx -> {
                        final CommandSender sender = ctx.getSource().getSender();
                        if(TwitchBridge.instance.isUserConnected()){
                            RewardsConfig.loadTwitchRewards(TwitchBridge.instance);
                        } else {
                            sender.sendMessage("§cAucune chaine twitch connectée");
                        }
                        return Command.SINGLE_SUCCESS;
                    })
            ).then(Commands.literal("connect")
                    .executes(ctx -> {
                        final CommandSender sender = ctx.getSource().getSender();
                        sender.sendMessage(Component.text(" Cliquez pour vous connecter à Twitch", NamedTextColor.GREEN)
                                .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.OPEN_URL, ClickEvent.Payload.string(TwitchBridge.instance.getConnectionUrlString()))));
                        return Command.SINGLE_SUCCESS;
                    })
            )
            .build();


}
