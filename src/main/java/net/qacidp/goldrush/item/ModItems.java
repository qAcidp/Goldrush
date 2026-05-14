package net.qacidp.goldrush.item;

import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredItem;
import net.qacidp.goldrush.item.bucket.PaydirtWaterBucketItem;
import net.qacidp.goldrush.item.paydirt.PaydirtShovelItem;
import net.minecraft.world.item.BlockItem;
import net.qacidp.goldrush.block.ModBlocks;
import net.qacidp.goldrush.item.washplant.WashplantMatItem;


import static net.minecraft.world.item.Items.registerItem;
import static net.qacidp.goldrush.Goldrush.MODID;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);

    public static final DeferredItem<Item> TEST_ITEM = ITEMS.register("test_item",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<PaydirtShovelItem> PAYDIRT_SHOVEL =
            ITEMS.register("paydirt_shovel", PaydirtShovelItem::new);

    public static final DeferredItem<Item> WASHPLANT_MAT = ITEMS.register("washplant_mat",
            () -> new WashplantMatItem(new Item.Properties(), 0));

    public static final DeferredItem<Item> WASHPLANT_MAT_LIGHT = ITEMS.register("washplant_mat_light",
            () -> new WashplantMatItem(new Item.Properties(), 2));

    public static final DeferredItem<Item> WASHPLANT_MAT_MEDIUM = ITEMS.register("washplant_mat_medium",
            () -> new WashplantMatItem(new Item.Properties(), 4));

    public static final DeferredItem<Item> WASHPLANT_MAT_HEAVY = ITEMS.register("washplant_mat_heavy",
            () -> new WashplantMatItem(new Item.Properties(), 6));

    public static final DeferredItem<Item> PAYDIRT_WATER_BUCKET = ITEMS.register("paydirt_water_bucket",
            () -> new PaydirtWaterBucketItem(new Item.Properties()));





    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
    }
}
