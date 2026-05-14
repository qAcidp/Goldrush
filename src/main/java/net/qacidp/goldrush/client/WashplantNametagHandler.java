package net.qacidp.goldrush.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.qacidp.goldrush.block.entity.WashplantBaseBlockEntity;
import net.qacidp.goldrush.block.entity.WashplantExtensionBlockEntity;
import net.qacidp.goldrush.block.entity.WashplantHeadBlockEntity;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import com.mojang.blaze3d.vertex.VertexConsumer;


import static net.qacidp.goldrush.Goldrush.MODID;

public class WashplantNametagHandler {

    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null || mc.screen != null) return;

        Vec3 camPos = event.getCamera().getPosition();
        PoseStack poseStack = event.getPoseStack();
        MultiBufferSource.BufferSource buffer = mc.renderBuffers().bufferSource();

        BlockPos playerPos = mc.player.blockPosition();
        int renderDistance = 6;

        for (int x = -renderDistance; x <= renderDistance; x++) {
            for (int z = -renderDistance; z <= renderDistance; z++) {
                int chunkX = (playerPos.getX() >> 4) + x;
                int chunkZ = (playerPos.getZ() >> 4) + z;
                LevelChunk chunk = mc.level.getChunk(chunkX, chunkZ);

                for (BlockEntity be : chunk.getBlockEntities().values()) {
                    BlockPos pos = be.getBlockPos();
                    int percent = -1;

                    if (be instanceof WashplantHeadBlockEntity head) {
                        percent = (int)(head.getFillLevel() * 100);
                    } else if (be instanceof WashplantBaseBlockEntity base && base.hasMat()) {
                        percent = Math.min(100, base.getMatMaterialPoints() / 6);
                    } else if (be instanceof WashplantExtensionBlockEntity ext && ext.hasMat()) {
                        percent = Math.min(100, ext.getMatMaterialPoints() / 6);
                    }

                    if (percent < 0) continue;

                    double dx = pos.getX() + 0.5 - camPos.x;
                    double dy = pos.getY() + 1.8 - camPos.y;
                    double dz = pos.getZ() + 0.5 - camPos.z;
                    double dist = Math.sqrt(dx*dx + dy*dy + dz*dz);
                    if (dist > 16) continue;

                    poseStack.pushPose();
                    poseStack.translate(dx, dy, dz);
                    poseStack.mulPose(event.getCamera().rotation());
                    float scale = 0.025f;
                    poseStack.scale(-scale, -scale, scale);

                    Matrix4f matrix = poseStack.last().pose();
                    VertexConsumer vc = buffer.getBuffer(net.minecraft.client.renderer.RenderType.textBackgroundSeeThrough());

                    float bgW = 50f;
                    float bgH = 8f;

                    // Hintergrund
                    vc.addVertex(matrix, -bgW/2, -bgH/2, 0).setColor(40, 40, 40, 180).setUv(0, 0).setLight(15728880).setNormal(0, 0, 1);
                    vc.addVertex(matrix,  bgW/2, -bgH/2, 0).setColor(40, 40, 40, 180).setUv(1, 0).setLight(15728880).setNormal(0, 0, 1);
                    vc.addVertex(matrix,  bgW/2,  bgH/2, 0).setColor(40, 40, 40, 180).setUv(1, 1).setLight(15728880).setNormal(0, 0, 1);
                    vc.addVertex(matrix, -bgW/2,  bgH/2, 0).setColor(40, 40, 40, 180).setUv(0, 1).setLight(15728880).setNormal(0, 0, 1);

                    // Füllbalken
                    float fillW = (bgW - 4f) * (percent / 100f);
                    float fillX = -bgW/2 + 2f;

                    int r, g, b;
                    if (percent >= 75)      { r = 255; g = 80;  b = 80;  }
                    else if (percent >= 50) { r = 255; g = 170; b = 0;   }
                    else if (percent >= 25) { r = 255; g = 255; b = 80;  }
                    else                    { r = 80;  g = 255; b = 80;  }

                    vc.addVertex(matrix, fillX,         -bgH/2+2, -0.01f).setColor(r, g, b, 220).setUv(0, 0).setLight(15728880).setNormal(0, 0, 1);
                    vc.addVertex(matrix, fillX + fillW, -bgH/2+2, -0.01f).setColor(r, g, b, 220).setUv(1, 0).setLight(15728880).setNormal(0, 0, 1);
                    vc.addVertex(matrix, fillX + fillW,  bgH/2-2, -0.01f).setColor(r, g, b, 220).setUv(1, 1).setLight(15728880).setNormal(0, 0, 1);
                    vc.addVertex(matrix, fillX,          bgH/2-2, -0.01f).setColor(r, g, b, 220).setUv(0, 1).setLight(15728880).setNormal(0, 0, 1);

                    buffer.endBatch();
                    poseStack.popPose();
                }
            }
        }
    }

    private static int getFillColor(int percent) {
        if (percent >= 75) return 0xFFFF5555;
        else if (percent >= 50) return 0xFFFFAA00;
        else if (percent >= 25) return 0xFFFFFF55;
        else return 0xFFFFFFFF;
    }

    private static int getMatColor(int percent) {
        if (percent >= 75) return 0xFFFF5555;
        else if (percent >= 50) return 0xFFFFAA00;
        else if (percent >= 25) return 0xFFFFFF55;
        else return 0xFF55FF55;
    }
}