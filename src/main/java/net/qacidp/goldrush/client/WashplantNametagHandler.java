package net.qacidp.goldrush.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.qacidp.goldrush.block.entity.WashplantHeadBlockEntity;
import org.joml.Matrix4f;

import static net.qacidp.goldrush.Goldrush.MODID;

@EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
public class WashplantNametagHandler {

    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return;



        PoseStack poseStack = event.getPoseStack();
        MultiBufferSource.BufferSource buffer = mc.renderBuffers().bufferSource();

        // Iteriere durch geladene Chunks und deren BlockEntities
        int renderDistance = 8; // Chunks
        BlockPos playerPos = mc.player.blockPosition();

        for (int x = -renderDistance; x <= renderDistance; x++) {
            for (int z = -renderDistance; z <= renderDistance; z++) {
                int chunkX = (playerPos.getX() >> 4) + x;
                int chunkZ = (playerPos.getZ() >> 4) + z;

                net.minecraft.world.level.chunk.LevelChunk chunk = mc.level.getChunk(chunkX, chunkZ);

                for (BlockEntity be : chunk.getBlockEntities().values()) {
                    if (be instanceof WashplantHeadBlockEntity headBE) {

                        renderNametag(headBE, poseStack, buffer, event.getCamera().getPosition());
                    }
                }
            }
        }
    }

    private static void renderNametag(WashplantHeadBlockEntity be, PoseStack poseStack,
                                      MultiBufferSource.BufferSource buffer, // GEÄNDERT
                                      net.minecraft.world.phys.Vec3 cameraPos) {



        int fillPercent = (int) (be.getFillLevel() * 100);
        Component component = Component.literal(fillPercent + "%");
        Font font = Minecraft.getInstance().font;

        poseStack.pushPose();

        BlockPos pos = be.getBlockPos();
        poseStack.translate(
                pos.getX() - cameraPos.x + 0.5,
                pos.getY() - cameraPos.y + 1.5,
                pos.getZ() - cameraPos.z + 0.5
        );

        poseStack.mulPose(Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation());

        float scale = 0.025f;
        poseStack.scale(-scale, -scale, scale);

        float textWidth = font.width(component);
        float padding = 3;
        float w = textWidth / 2f + padding;
        float h = font.lineHeight / 2f + 2;

        Matrix4f matrix = poseStack.last().pose();

        VertexConsumer vc = buffer.getBuffer(RenderType.textBackgroundSeeThrough());

        vc.addVertex(matrix, -w, -h, 0)
                .setColor(0, 0, 0, 128)
                .setUv(0, 0).setLight(15728880).setNormal(0, 0, 1);
        vc.addVertex(matrix, w, -h, 0)
                .setColor(0, 0, 0, 128)
                .setUv(1, 0).setLight(15728880).setNormal(0, 0, 1);
        vc.addVertex(matrix, w, h, 0)
                .setColor(0, 0, 0, 128)
                .setUv(1, 1).setLight(15728880).setNormal(0, 0, 1);
        vc.addVertex(matrix, -w, h, 0)
                .setColor(0, 0, 0, 128)
                .setUv(0, 1).setLight(15728880).setNormal(0, 0, 1);

        float x = -textWidth / 2f;
        float y = -font.lineHeight / 2f;
        int color = getColorForFillLevel(fillPercent);

        font.drawInBatch(component, x, y, color, false, matrix, buffer,
                Font.DisplayMode.NORMAL, 0, 15728880);

// WICHTIG: Flush den Buffer!
        buffer.endBatch();

        poseStack.popPose();
    }

    private static int getColorForFillLevel(int percent) {
        if (percent >= 75) return 0xFFFF5555;
        else if (percent >= 50) return 0xFFFFAA00;
        else if (percent >= 25) return 0xFFFFFF55;
        else return 0xFFFFFFFF;
    }
}