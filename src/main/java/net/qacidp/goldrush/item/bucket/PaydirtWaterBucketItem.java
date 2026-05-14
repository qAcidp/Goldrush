package net.qacidp.goldrush.item.bucket;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.tags.FluidTags;
import net.minecraft.core.BlockPos;
import net.qacidp.goldrush.block.ModBlocks;

import java.util.List;

public class PaydirtWaterBucketItem extends Item {

    public PaydirtWaterBucketItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();

        if (player == null) return InteractionResult.PASS;

        FluidState fluidState = level.getFluidState(pos);

        if (fluidState.is(FluidTags.WATER) && fluidState.isSource()) {
            if (!level.isClientSide) {
                ItemStack newStack = new ItemStack(ModBlocks.PAYDIRT_WATER_BUCKET_BLOCK.get().asItem());

                if (!player.isCreative()) {
                    context.getItemInHand().shrink(1);
                }

                if (!player.getInventory().add(newStack)) {
                    player.drop(newStack, false);
                }
            }
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context,
                                List<Component> tooltipComponents, TooltipFlag flag) {
        int points = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)
                .copyTag()
                .getInt("goldPoints");
        if (points > 0) {
            tooltipComponents.add(Component.literal("§7Gold Points: " + points + "/1200"));
        }
    }
}