package net.qacidp.goldrush.component;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

import static net.qacidp.goldrush.Goldrush.MODID;

public class ModDataComponents {
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS =
            DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, MODID);

    public static final Supplier<DataComponentType<GoldDistributionComponent>> GOLD_DISTRIBUTION =
            DATA_COMPONENTS.register("gold_distribution",
                    () -> DataComponentType.<GoldDistributionComponent>builder()
                            .persistent(GoldDistributionComponent.CODEC)
                            .build());

    public static void register(IEventBus modEventBus) {
        DATA_COMPONENTS.register(modEventBus);
    }
}