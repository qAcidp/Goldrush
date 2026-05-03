package net.qacidp.goldrush.block.entity;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.qacidp.goldrush.block.ModBlocks;

import java.util.function.Supplier;

import static net.qacidp.goldrush.Goldrush.MODID;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, MODID);

    public static final Supplier<BlockEntityType<PaydirtBlockEntity>> PAYDIRT_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("paydirt_block_entity",
                    () -> BlockEntityType.Builder.of(PaydirtBlockEntity::new, ModBlocks.PAY_DIRT_LOW.get()).build(null));

    public static void register(IEventBus modEventBus) {
        BLOCK_ENTITIES.register(modEventBus);
    }
}