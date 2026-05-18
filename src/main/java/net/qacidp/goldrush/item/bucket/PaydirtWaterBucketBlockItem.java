package net.qacidp.goldrush.item.bucket;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.block.Block;

import java.util.List;

public class PaydirtWaterBucketBlockItem extends BlockItem {

    public PaydirtWaterBucketBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context,
                                List<Component> tooltipComponents, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltipComponents, flag);
        int points = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)
                .copyTag()
                .getInt("goldPoints");
        tooltipComponents.add(Component.literal("§7Gold Points: " + points + "/1200"));

        float grams = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)
                .copyTag()
                .getFloat("goldGrams");
        tooltipComponents.add(Component.literal("§7Gold Points: " + points + "/1200"));
        if (grams > 0) {
            tooltipComponents.add(Component.literal("§6Gold: " + String.format("%.4f", grams) + "g"));
        }
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        return 1;
    }
}