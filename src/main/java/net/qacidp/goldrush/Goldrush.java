package net.qacidp.goldrush;

import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.qacidp.goldrush.item.ModCreativeTabs;
import net.qacidp.goldrush.item.ModItems;
import org.slf4j.Logger;
import net.qacidp.goldrush.block.ModBlocks;

@Mod(Goldrush.MODID)
public class Goldrush {

    public static final String MODID = "goldrush";
    private static final Logger LOGGER = LogUtils.getLogger();

    public Goldrush(IEventBus modEventBus) {
        LOGGER.info("Goldrush Mod geladen!");
        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModCreativeTabs.register(modEventBus);
    }
}