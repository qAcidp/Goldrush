package net.qacidp.goldrush.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.qacidp.goldrush.item.ModItems;
import net.qacidp.goldrush.block.ModBlocks;

import java.util.function.Supplier;

import static net.qacidp.goldrush.Goldrush.MODID;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public static final Supplier<CreativeModeTab> GOLDRUSH_TAB = CREATIVE_MODE_TABS.register("goldrush_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.goldrush"))
                    .icon(() -> new ItemStack(Items.GOLD_NUGGET))
                    .displayItems((parameters, output) -> {

                        output.accept(ModBlocks.PAY_DIRT_LOW.get());
                        output.accept(ModItems.TEST_ITEM.get());


                    })
                    .build());

    public static void register(IEventBus modEventBus) {
        CREATIVE_MODE_TABS.register(modEventBus);
    }
}
