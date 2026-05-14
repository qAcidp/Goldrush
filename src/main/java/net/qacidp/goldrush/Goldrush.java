package net.qacidp.goldrush;

import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.qacidp.goldrush.item.ModCreativeTabs;
import net.qacidp.goldrush.item.ModItems;
import org.slf4j.Logger;
import net.qacidp.goldrush.block.ModBlocks;
import net.qacidp.goldrush.block.entity.ModBlockEntities;
import net.qacidp.goldrush.component.ModDataComponents;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.qacidp.goldrush.client.ClientSetup;
import net.qacidp.goldrush.data.ModAttachments;
import net.neoforged.neoforge.common.NeoForge;
import net.qacidp.goldrush.client.WashplantNametagHandler;

@Mod(Goldrush.MODID)
public class Goldrush {

    public static final String MODID = "goldrush";
    private static final Logger LOGGER = LogUtils.getLogger();

    public Goldrush(IEventBus modEventBus) {
        LOGGER.info("Goldrush Mod geladen!");
        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModDataComponents.register(modEventBus);
        ModAttachments.register(modEventBus);
        ModCreativeTabs.register(modEventBus);


        modEventBus.addListener(ClientSetup::registerRenderers);

        NeoForge.EVENT_BUS.register(WashplantNametagHandler.class);
    }
}