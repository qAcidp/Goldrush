package net.qacidp.goldrush.block;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.qacidp.goldrush.item.ModItems;
import net.qacidp.goldrush.block.paydirt.PaydirtBlock;
import net.qacidp.goldrush.block.paydirt.PaydirtLayerBlock;
import net.qacidp.goldrush.block.bucket.PaydirtBucketBlock;
import net.qacidp.goldrush.block.paydirt.nugget.PaydirtNuggetBlock;
import net.qacidp.goldrush.block.washplant.WashplantBaseBlock;
import net.qacidp.goldrush.block.washplant.WashplantExtensionBlock;
import net.qacidp.goldrush.block.entity.WashplantHeadBlockEntity;
import net.qacidp.goldrush.block.washplant.WashplantHeadBlock;


import java.util.function.Supplier;

import static net.qacidp.goldrush.Goldrush.MODID;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);


    //paydirts
    public static final DeferredBlock<Block> PAY_DIRT_LOW = registerBlock("pay_dirt_low",
            () -> new PaydirtBlock(BlockBehaviour.Properties.of()
                    .strength(0.5f)
                    .sound(SoundType.GRAVEL), "pay_dirt_low"));

    public static final DeferredBlock<Block> PAY_DIRT_COMMON = registerBlock("pay_dirt_common",
            () -> new PaydirtBlock(BlockBehaviour.Properties.of()
                    .strength(0.5f)
                    .sound(SoundType.GRAVEL), "pay_dirt_common"));

    //Nugget Blöcke

    public static final DeferredBlock<Block> PAY_DIRT_LOW_NUGGET = registerBlock("pay_dirt_low_nugget",
            () -> new PaydirtNuggetBlock(BlockBehaviour.Properties.of()
                    .strength(0.5f)
                    .sound(SoundType.GRAVEL), "pay_dirt_low"));

    public static final DeferredBlock<Block> PAY_DIRT_COMMON_NUGGET = registerBlock("pay_dirt_common_nugget",
            () -> new PaydirtNuggetBlock(BlockBehaviour.Properties.of()
                    .strength(0.5f)
                    .sound(SoundType.GRAVEL), "pay_dirt_common"));

    //Layer
    public static final DeferredBlock<Block> PAYDIRT_LAYER = registerBlock("paydirt_layer",
            () -> new PaydirtLayerBlock(BlockBehaviour.Properties.of()
                    .strength(0.5f)
                    .sound(SoundType.GRAVEL)
                    .noOcclusion()));
//Buckets
    public static final DeferredBlock<Block> PAYDIRT_BUCKET_BLOCK_EMPTY = registerBlock("paydirt_bucket_block_empty",
            () -> new PaydirtBucketBlock(BlockBehaviour.Properties.of()
                    .strength(1.0f)
                    .sound(SoundType.WOOD)
                    .noOcclusion())); //Block -> PaydirtBucketBlock

    public static final DeferredBlock<Block> PAYDIRT_BUCKET_BLOCK_25 = registerBlock("paydirt_bucket_block_25",
            () -> new PaydirtBucketBlock(BlockBehaviour.Properties.of()
                    .strength(1.0f)
                    .sound(SoundType.WOOD)
                    .noOcclusion()));

    public static final DeferredBlock<Block> PAYDIRT_BUCKET_BLOCK_50 = registerBlock("paydirt_bucket_block_50",
            () -> new PaydirtBucketBlock(BlockBehaviour.Properties.of()
                    .strength(1.0f)
                    .sound(SoundType.WOOD)
                    .noOcclusion()));

    public static final DeferredBlock<Block> PAYDIRT_BUCKET_BLOCK_75 = registerBlock("paydirt_bucket_block_75",
            () -> new PaydirtBucketBlock(BlockBehaviour.Properties.of()
                    .strength(1.0f)
                    .sound(SoundType.WOOD)
                    .noOcclusion()));

    public static final DeferredBlock<Block> PAYDIRT_BUCKET_BLOCK_100 = registerBlock("paydirt_bucket_block_100",
            () -> new PaydirtBucketBlock(BlockBehaviour.Properties.of()
                    .strength(1.0f)
                    .sound(SoundType.WOOD)
                    .noOcclusion()));



    //Washplant

    public static final DeferredBlock<Block> WASHPLANT_BASE = registerBlock("washplant_base",
            () -> new WashplantBaseBlock(BlockBehaviour.Properties.of()
                    .strength(2.0f)
                    .sound(SoundType.METAL)
                    .noOcclusion()));


    public static final DeferredBlock<Block> WASHPLANT_EXTENSION = registerBlock("washplant_extension",
            () -> new WashplantExtensionBlock(BlockBehaviour.Properties.of()
                    .strength(2.0f)
                    .sound(SoundType.METAL)
                    .noOcclusion()));

    public static final DeferredBlock<Block> WASHPLANT_HEAD = registerBlock("washplant_head",
            () -> new WashplantHeadBlock(BlockBehaviour.Properties.of()
                    .strength(2.0f)
                    .sound(SoundType.METAL)
                    .noOcclusion()));



    private static <T extends Block> DeferredBlock<T> registerBlock(String name, Supplier<T> block) {
        DeferredBlock<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> void registerBlockItem(String name, DeferredBlock<T> block) {
        ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static void register(IEventBus modEventBus) {
        BLOCKS.register(modEventBus);
    }
}
