package net.qacidp.goldrush.block.entity;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.qacidp.goldrush.block.ModBlocks;
import net.qacidp.goldrush.block.entity.BucketBlockEntity;
import net.qacidp.goldrush.block.entity.WashplantHeadBlockEntity;
import net.qacidp.goldrush.block.entity.WashplantBaseBlockEntity;
import net.qacidp.goldrush.block.entity.WashplantExtensionBlockEntity;

import java.util.function.Supplier;

import static net.qacidp.goldrush.Goldrush.MODID;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, MODID);

    public static final Supplier<BlockEntityType<PaydirtBlockEntity>> PAYDIRT_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("paydirt_block_entity",
                    () -> BlockEntityType.Builder.of(PaydirtBlockEntity::new,
                            ModBlocks.PAY_DIRT_LOW.get(),
                            ModBlocks.PAY_DIRT_COMMON.get(),
                            ModBlocks.PAY_DIRT_LOW_NUGGET.get(),      // Hinzufügen
                            ModBlocks.PAY_DIRT_COMMON_NUGGET.get() // <-- Hinzufügen
                    ).build(null));
    public static void register(IEventBus modEventBus) {
        BLOCK_ENTITIES.register(modEventBus);
    }


    public static final Supplier<BlockEntityType<WashplantHeadBlockEntity>> WASHPLANT_HEAD_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("washplant_head_block_entity",
                    () -> BlockEntityType.Builder.of(WashplantHeadBlockEntity::new,
                            ModBlocks.WASHPLANT_HEAD.get()
                    ).build(null));

    public static final Supplier<BlockEntityType<WashplantBaseBlockEntity>> WASHPLANT_BASE_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("washplant_base_block_entity",
                    () -> BlockEntityType.Builder.of(WashplantBaseBlockEntity::new,
                            ModBlocks.WASHPLANT_BASE.get()
                    ).build(null));

    public static final Supplier<BlockEntityType<WashplantExtensionBlockEntity>> WASHPLANT_EXTENSION_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("washplant_extension_block_entity",
                    () -> BlockEntityType.Builder.of(WashplantExtensionBlockEntity::new,
                            ModBlocks.WASHPLANT_EXTENSION.get()
                    ).build(null));

    public static final Supplier<BlockEntityType<PaydirtWaterBucketBlockEntity>>
            PAYDIRT_WATER_BUCKET_BLOCK_ENTITY = BLOCK_ENTITIES.register(
            "paydirt_water_bucket_block_entity",
            () -> BlockEntityType.Builder.of(PaydirtWaterBucketBlockEntity::new,
                    ModBlocks.PAYDIRT_WATER_BUCKET_BLOCK.get(),
                    ModBlocks.PAYDIRT_WATER_BUCKET_BLOCK_HALF.get(),
                    ModBlocks.PAYDIRT_WATER_BUCKET_BLOCK_FULL.get()).build(null));


    public static final Supplier<BlockEntityType<BucketBlockEntity>> BUCKET_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("bucket_block_entity",
                    () -> BlockEntityType.Builder.of(BucketBlockEntity::new,
                            ModBlocks.PAYDIRT_BUCKET_BLOCK_EMPTY.get(),
                            ModBlocks.PAYDIRT_BUCKET_BLOCK_25.get(),
                            ModBlocks.PAYDIRT_BUCKET_BLOCK_50.get(),
                            ModBlocks.PAYDIRT_BUCKET_BLOCK_75.get(),
                            ModBlocks.PAYDIRT_BUCKET_BLOCK_100.get()
                    ).build(null));
}