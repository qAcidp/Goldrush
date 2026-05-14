package net.qacidp.goldrush.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.qacidp.goldrush.block.entity.WashplantHeadBlockEntity;
import org.joml.Matrix4f;
import net.minecraft.client.gui.Font;


import static net.qacidp.goldrush.Goldrush.MODID;

public class WashplantHeadRenderer implements BlockEntityRenderer<WashplantHeadBlockEntity> {

    private static final ResourceLocation MATERIAL_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(MODID, "block/washplant/washplant_head_material");

    private static final ResourceLocation WATER_FLOWING =
            ResourceLocation.fromNamespaceAndPath("minecraft", "block/water_flow");

    Font font = Minecraft.getInstance().font;


    public WashplantHeadRenderer(BlockEntityRendererProvider.Context ctx) {
        this.font = ctx.getFont();
    }

    @Override
    public void render(WashplantHeadBlockEntity be, float partialTicks, PoseStack poseStack,
                       MultiBufferSource buffer, int light, int overlay) {

        float fillLevel = be.getFillLevel();
        boolean isWashing = be.isWashing();

        if (fillLevel > 0) {
            renderMaterial(be, poseStack, buffer, light, fillLevel);
        }

        if (isWashing) {
            renderWater(poseStack, buffer, light);
        }


    }





    private void renderMaterial(WashplantHeadBlockEntity be, PoseStack poseStack,
                                MultiBufferSource buffer, int light, float fillLevel) {
        poseStack.pushPose();

        float minY = -4f / 16f;
        float maxY = 7f / 16f;
        float currentY = minY + (maxY - minY) * fillLevel + 0.001f;

        TextureAtlasSprite sprite = Minecraft.getInstance()
                .getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
                .apply(MATERIAL_TEXTURE);

        VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.cutout());
        Matrix4f matrix = poseStack.last().pose();

        float x1 = 2f / 16f;
        float x2 = 14f / 16f;
        float z1 = 1f / 16f;
        float z2 = 14f / 16f;

        vertexConsumer.addVertex(matrix, x1, currentY, z2)
                .setColor(255, 255, 255, 255)
                .setUv(sprite.getU0(), sprite.getV1())
                .setLight(light)
                .setNormal(0, 1, 0);

        vertexConsumer.addVertex(matrix, x2, currentY, z2)
                .setColor(255, 255, 255, 255)
                .setUv(sprite.getU1(), sprite.getV1())
                .setLight(light)
                .setNormal(0, 1, 0);

        vertexConsumer.addVertex(matrix, x2, currentY, z1)
                .setColor(255, 255, 255, 255)
                .setUv(sprite.getU1(), sprite.getV0())
                .setLight(light)
                .setNormal(0, 1, 0);

        vertexConsumer.addVertex(matrix, x1, currentY, z1)
                .setColor(255, 255, 255, 255)
                .setUv(sprite.getU0(), sprite.getV0())
                .setLight(light)
                .setNormal(0, 1, 0);

        poseStack.popPose();
    }

    private void renderWater(PoseStack poseStack, MultiBufferSource buffer, int light) {
        poseStack.pushPose();

        float waterY = 7f / 16f + 0.002f;

        TextureAtlasSprite waterSprite = Minecraft.getInstance()
                .getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
                .apply(WATER_FLOWING);

        VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.translucent());
        Matrix4f matrix = poseStack.last().pose();

        float x1 = 2f / 16f;
        float x2 = 14f / 16f;
        float z1 = 1f / 16f;
        float z2 = 14f / 16f;

        int r = 100, g = 120, b = 80, a = 180;

        vertexConsumer.addVertex(matrix, x1, waterY, z2)
                .setColor(r, g, b, a)
                .setUv(waterSprite.getU0(), waterSprite.getV1())
                .setLight(light)
                .setNormal(0, 1, 0);

        vertexConsumer.addVertex(matrix, x2, waterY, z2)
                .setColor(r, g, b, a)
                .setUv(waterSprite.getU1(), waterSprite.getV1())
                .setLight(light)
                .setNormal(0, 1, 0);

        vertexConsumer.addVertex(matrix, x2, waterY, z1)
                .setColor(r, g, b, a)
                .setUv(waterSprite.getU1(), waterSprite.getV0())
                .setLight(light)
                .setNormal(0, 1, 0);

        vertexConsumer.addVertex(matrix, x1, waterY, z1)
                .setColor(r, g, b, a)
                .setUv(waterSprite.getU0(), waterSprite.getV0())
                .setLight(light)
                .setNormal(0, 1, 0);

        poseStack.popPose();
    }



    private int getColorForFillLevel(int percent) {
        if (percent >= 75) return 0xFFFF5555; // Rot
        else if (percent >= 50) return 0xFFFFAA00; // Orange
        else if (percent >= 25) return 0xFFFFFF55; // Gelb
        else return 0xFFFFFFFF; // Weiß
    }
}