package net.qacidp.goldrush.item.washplant;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class WashplantMatItem extends Item {

    private final int washCycles; // Wie viele Waschgänge diese Matte hat

    public WashplantMatItem(Properties properties, int washCycles) {
        super(properties);
        this.washCycles = washCycles;
    }

    public int getWashCycles() {
        return washCycles;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);

        if (washCycles == 0) {
            tooltipComponents.add(Component.literal("§7Clean mat"));
        } else if (washCycles < 3) {
            tooltipComponents.add(Component.literal("§7Lightly used - " + washCycles + " washes"));
        } else if (washCycles < 5) {
            tooltipComponents.add(Component.literal("§eMedium used - " + washCycles + " washes"));
        } else {
            tooltipComponents.add(Component.literal("§6Heavily used - " + washCycles + " washes"));
        }
    }
}