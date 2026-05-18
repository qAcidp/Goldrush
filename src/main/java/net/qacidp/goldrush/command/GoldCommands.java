package net.qacidp.goldrush.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
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
                        .then(Commands.literal("givemat")
                                .executes(context -> {
                                    if (context.getSource().getEntity() instanceof ServerPlayer player) {
                                        net.minecraft.world.item.ItemStack mat = new net.minecraft.world.item.ItemStack(
                                                net.qacidp.goldrush.item.ModItems.WASHPLANT_MAT_HEAVY.get());
                                        net.qacidp.goldrush.item.washplant.WashplantMatItem.setMaterialPoints(mat, 400);
                                        net.qacidp.goldrush.item.washplant.WashplantMatItem.setGoldGrams(mat, 5.0f);

                                        if (!player.getInventory().add(mat)) {
                                            player.drop(mat, false);
                                        }

                                        player.sendSystemMessage(Component.literal("§aGiven Heavy Mat with 400 points and 5.0g gold"));
                                        return 1;
                                    }
                                    return 0;
                                })
                        )

                        .then(Commands.literal("givebucket")
                                .executes(context -> {
                                    if (context.getSource().getEntity() instanceof ServerPlayer player) {
                                        ItemStack bucket = new ItemStack(net.qacidp.goldrush.block.ModBlocks.PAYDIRT_WATER_BUCKET_BLOCK_FULL.get());
                                        net.minecraft.world.item.component.CustomData.update(
                                                net.minecraft.core.component.DataComponents.CUSTOM_DATA,
                                                bucket,
                                                tag -> {
                                                    tag.putInt("goldPoints", 1200);
                                                    tag.putFloat("goldGrams", 0.24f);
                                                }
                                        );
                                        if (!player.getInventory().add(bucket)) {
                                            player.drop(bucket, false);
                                        }
                                        player.sendSystemMessage(Component.literal("§6Given Full Bucket with 0.24g gold"));
                                        return 1;
                                    }
                                    return 0;
                                })
                        )

        );
    }
}