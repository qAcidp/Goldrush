package net.qacidp.goldrush.item.washplant;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;

import java.util.List;

public class WashplantMatItem extends Item {

    private final int washCycles;

    public WashplantMatItem(Properties properties, int washCycles) {
        super(properties);
        this.washCycles = washCycles;
    }

    public int getWashCycles() {
        return washCycles;
    }

    public static void setMaterialPoints(ItemStack stack, int points) {
        CustomData existing = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        CompoundTag tag = existing.copyTag();
        tag.putInt("matMaterialPoints", points);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }

    public static int getMaterialPoints(ItemStack stack) {
        CustomData data = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        return data.copyTag().getInt("matMaterialPoints");
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);

        int points = getMaterialPoints(stack);

        if (washCycles == 0) {
            tooltipComponents.add(Component.literal("§7Clean mat"));
        } else if (washCycles < 3) {
            tooltipComponents.add(Component.literal("§7Lightly used - " + washCycles + " washes (" + points + " pts)"));
        } else if (washCycles < 5) {
            tooltipComponents.add(Component.literal("§eMedium used - " + washCycles + " washes (" + points + " pts)"));
        } else {
            tooltipComponents.add(Component.literal("§6Heavily used - " + washCycles + " washes (" + points + " pts)"));
        }
    }
}