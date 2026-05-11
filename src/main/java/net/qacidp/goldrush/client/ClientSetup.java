package net.qacidp.goldrush.client;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.qacidp.goldrush.block.entity.ModBlockEntities;
import net.qacidp.goldrush.client.render.PaydirtNuggetRenderer;
import net.qacidp.goldrush.client.render.WashplantHeadRenderer;
import net.qacidp.goldrush.block.entity.ModBlockEntities;
import net.qacidp.goldrush.client.render.WashplantBaseRenderer;
import net.qacidp.goldrush.client.render.WashplantExtensionRenderer;

import static net.qacidp.goldrush.Goldrush.MODID;

public class ClientSetup {

    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntities.PAYDIRT_BLOCK_ENTITY.get(), PaydirtNuggetRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.WASHPLANT_HEAD_BLOCK_ENTITY.get(), WashplantHeadRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.WASHPLANT_BASE_BLOCK_ENTITY.get(), WashplantBaseRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.WASHPLANT_EXTENSION_BLOCK_ENTITY.get(), WashplantExtensionRenderer::new);

    }
}