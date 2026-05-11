package net.qacidp.goldrush.item;

import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredItem;
import net.qacidp.goldrush.item.paydirt.PaydirtShovelItem;
import net.minecraft.world.item.BlockItem;
import net.qacidp.goldrush.block.ModBlocks;


import static net.qacidp.goldrush.Goldrush.MODID;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);

    public static final DeferredItem<Item> TEST_ITEM = ITEMS.register("test_item",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<PaydirtShovelItem> PAYDIRT_SHOVEL =
            ITEMS.register("paydirt_shovel", PaydirtShovelItem::new);


    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
    }
}
