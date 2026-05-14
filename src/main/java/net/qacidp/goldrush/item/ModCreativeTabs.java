package net.qacidp.goldrush.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.qacidp.goldrush.block.ModBlocks;
import net.qacidp.goldrush.component.GoldDistributionComponent;
import net.qacidp.goldrush.component.ModDataComponents;
import net.qacidp.goldrush.util.PaydirtConfig;

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
                        ItemStack paydirtStack = new ItemStack(ModBlocks.PAY_DIRT_LOW.get());
                        float totalGold = PaydirtConfig.generateGoldAmount("pay_dirt_low");
                        float[] goldDistribution = PaydirtConfig.getGoldDistribution("pay_dirt_low", totalGold);
                        paydirtStack.set(ModDataComponents.GOLD_DISTRIBUTION.get(), new GoldDistributionComponent(goldDistribution));
                        output.accept(paydirtStack);

                        ItemStack paydirtNStack = new ItemStack(ModBlocks.PAY_DIRT_LOW_NUGGET.get());
                        float totalGoldN = PaydirtConfig.generateGoldAmount("pay_dirt_low");
                        float[] goldDistributionN = PaydirtConfig.getGoldDistribution("pay_dirt_low", totalGoldN);
                        paydirtNStack.set(ModDataComponents.GOLD_DISTRIBUTION.get(), new GoldDistributionComponent(goldDistributionN));
                        output.accept(paydirtNStack);

                        ItemStack commonStack = new ItemStack(ModBlocks.PAY_DIRT_COMMON.get());
                        float totalGoldCommon = PaydirtConfig.generateGoldAmount("pay_dirt_common");
                        float[] goldDistCommon = PaydirtConfig.getGoldDistribution("pay_dirt_common", totalGoldCommon);
                        commonStack.set(ModDataComponents.GOLD_DISTRIBUTION.get(), new GoldDistributionComponent(goldDistCommon));
                        output.accept(commonStack);

                        ItemStack commonNStack = new ItemStack(ModBlocks.PAY_DIRT_COMMON_NUGGET.get());
                        float totalGoldCommonN = PaydirtConfig.generateGoldAmount("pay_dirt_common");
                        float[] goldDistCommonN = PaydirtConfig.getGoldDistribution("pay_dirt_common", totalGoldCommonN);
                        commonNStack.set(ModDataComponents.GOLD_DISTRIBUTION.get(), new GoldDistributionComponent(goldDistCommonN));
                        output.accept(commonNStack);


                        output.accept(ModItems.PAYDIRT_SHOVEL.get());
                        output.accept(ModItems.WASHPLANT_MAT);
                        output.accept(ModItems.WASHPLANT_MAT_LIGHT);
                        output.accept(ModItems.WASHPLANT_MAT_MEDIUM);
                        output.accept(ModItems.WASHPLANT_MAT_HEAVY);
                        output.accept(ModBlocks.PAYDIRT_BUCKET_BLOCK_EMPTY.get());
                        output.accept(ModBlocks.PAYDIRT_BUCKET_BLOCK_25.get());
                        output.accept(ModBlocks.PAYDIRT_BUCKET_BLOCK_50.get());
                        output.accept(ModBlocks.PAYDIRT_BUCKET_BLOCK_75.get());
                        output.accept(ModBlocks.PAYDIRT_BUCKET_BLOCK_100.get());
                        output.accept(ModBlocks.WASHPLANT_BASE.get());
                        output.accept(ModBlocks.WASHPLANT_EXTENSION.get());
                        output.accept(ModBlocks.WASHPLANT_HEAD.get());
                        output.accept(ModBlocks.PAYDIRT_WATER_BUCKET_BLOCK);
                        output.accept(ModBlocks.PAYDIRT_WATER_BUCKET_BLOCK_HALF);
                        output.accept(ModBlocks.PAYDIRT_WATER_BUCKET_BLOCK_FULL);




                    })
                    .build());

    public static void register(IEventBus modEventBus) {
        CREATIVE_MODE_TABS.register(modEventBus);
    }
}
