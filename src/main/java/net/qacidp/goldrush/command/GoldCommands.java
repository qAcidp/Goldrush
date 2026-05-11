package net.qacidp.goldrush.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.qacidp.goldrush.data.ModAttachments;
import net.qacidp.goldrush.data.PlayerGoldData;

public class GoldCommands {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("goldrush")
                        .then(Commands.literal("resetgold")
                                .executes(context -> {
                                    if (context.getSource().getEntity() instanceof ServerPlayer player) {
                                        PlayerGoldData goldData = player.getData(ModAttachments.PLAYER_GOLD);
                                        goldData.resetGold();

                                        player.sendSystemMessage(Component.literal("§6Gold reset to 0g"));
                                        return 1;
                                    }
                                    return 0;
                                })
                        )
                        .then(Commands.literal("checkgold")
                                .executes(context -> {
                                    if (context.getSource().getEntity() instanceof ServerPlayer player) {
                                        PlayerGoldData goldData = player.getData(ModAttachments.PLAYER_GOLD);
                                        float gold = goldData.getTotalGold();

                                        player.sendSystemMessage(Component.literal("§6Total Gold: " + String.format("%.2f", gold) + "g"));
                                        return 1;
                                    }
                                    return 0;
                                })
                        )
        );
    }
}