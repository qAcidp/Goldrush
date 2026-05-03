package net.qacidp.goldrush.event;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.qacidp.goldrush.block.ModBlocks;
import net.qacidp.goldrush.component.GoldDistributionComponent;
import net.qacidp.goldrush.component.ModDataComponents;

import static net.qacidp.goldrush.Goldrush.MODID;

@EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
public class TooltipEvents {

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        ItemStack itemStack = event.getItemStack();

        if (itemStack.getItem() == ModBlocks.PAY_DIRT_LOW.get().asItem()) {
            GoldDistributionComponent goldComponent = itemStack.get(ModDataComponents.GOLD_DISTRIBUTION.get());

            if (goldComponent != null) {
                float totalGold = goldComponent.getTotalGold();
                String goldText = String.format("%.2f", totalGold);
                event.getToolTip().add(1, Component.literal("§6Gold: " + goldText + "g"));
            }
        }
    }
}