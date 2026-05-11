package net.qacidp.goldrush.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.qacidp.goldrush.block.entity.WashplantBaseBlockEntity;
import net.qacidp.goldrush.block.entity.WashplantExtensionBlockEntity;
import org.joml.Matrix4f;

public class WashplantBaseRenderer implements BlockEntityRenderer<WashplantBaseBlockEntity> {

    private static final ResourceLocation WATER_STILL =
            ResourceLocation.fromNamespaceAndPath("minecraft", "block/water_flow");

    public WashplantBaseRenderer(BlockEntityRendererProvider.Context ctx) {
    }

    @Override
    public void render(WashplantBaseBlockEntity be, float partialTicks, PoseStack poseStack,
                       MultiBufferSource buffer, int light, int overlay) {


        if (!be.isWashing()) return;


        poseStack.pushPose();

        // Extension ist 1 Pixel niedriger, also waterY auch
        float waterMinY = 8f / 16f;   // Unten
        float waterMaxY = 9f / 16f;  // Oben (dicker Layer)

        TextureAtlasSprite waterSprite = Minecraft.getInstance()
                .getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
                .apply(WATER_STILL);

        VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.cutout());
        Matrix4f matrix = poseStack.last().pose();

        float x1 = 3f / 16f;
        float x2 = 13f / 16f;
        float z1 = 0.0f;
        float z2 = 1.0f;

        int r = 100, g = 120, b = 80, a = 180;

// Oberseite
        vertexConsumer.addVertex(matrix, x1, waterMaxY, z2)
                .setColor(r, g, b, a).setUv(waterSprite.getU0(), waterSprite.getV0()).setLight(light).setNormal(0, 1, 0);
        vertexConsumer.addVertex(matrix, x2, waterMaxY, z2)
                .setColor(r, g, b, a).setUv(waterSprite.getU1(), waterSprite.getV0()).setLight(light).setNormal(0, 1, 0);
        vertexConsumer.addVertex(matrix, x2, waterMaxY, z1)
                .setColor(r, g, b, a).setUv(waterSprite.getU1(), waterSprite.getV1()).setLight(light).setNormal(0, 1, 0);
        vertexConsumer.addVertex(matrix, x1, waterMaxY, z1)
                .setColor(r, g, b, a).setUv(waterSprite.getU0(), waterSprite.getV1()).setLight(light).setNormal(0, 1, 0);

// Unterseite
        vertexConsumer.addVertex(matrix, x1, waterMinY, z1)
                .setColor(r, g, b, a).setUv(waterSprite.getU0(), waterSprite.getV0()).setLight(light).setNormal(0, -1, 0);
        vertexConsumer.addVertex(matrix, x2, waterMinY, z1)
                .setColor(r, g, b, a).setUv(waterSprite.getU1(), waterSprite.getV0()).setLight(light).setNormal(0, -1, 0);
        vertexConsumer.addVertex(matrix, x2, waterMinY, z2)
                .setColor(r, g, b, a).setUv(waterSprite.getU1(), waterSprite.getV1()).setLight(light).setNormal(0, -1, 0);
        vertexConsumer.addVertex(matrix, x1, waterMinY, z2)
                .setColor(r, g, b, a).setUv(waterSprite.getU0(), waterSprite.getV1()).setLight(light).setNormal(0, -1, 0);

// Nord-Seite (z1)
        vertexConsumer.addVertex(matrix, x1, waterMinY, z1)
                .setColor(r, g, b, a).setUv(waterSprite.getU0(), waterSprite.getV0()).setLight(light).setNormal(0, 0, -1);
        vertexConsumer.addVertex(matrix, x1, waterMaxY, z1)
                .setColor(r, g, b, a).setUv(waterSprite.getU0(), waterSprite.getV1()).setLight(light).setNormal(0, 0, -1);
        vertexConsumer.addVertex(matrix, x2, waterMaxY, z1)
                .setColor(r, g, b, a).setUv(waterSprite.getU1(), waterSprite.getV1()).setLight(light).setNormal(0, 0, -1);
        vertexConsumer.addVertex(matrix, x2, waterMinY, z1)
                .setColor(r, g, b, a).setUv(waterSprite.getU1(), waterSprite.getV0()).setLight(light).setNormal(0, 0, -1);

// Süd-Seite (z2)
        vertexConsumer.addVertex(matrix, x2, waterMinY, z2)
                .setColor(r, g, b, a).setUv(waterSprite.getU1(), waterSprite.getV0()).setLight(light).setNormal(0, 0, 1);
        vertexConsumer.addVertex(matrix, x2, waterMaxY, z2)
                .setColor(r, g, b, a).setUv(waterSprite.getU1(), waterSprite.getV1()).setLight(light).setNormal(0, 0, 1);
        vertexConsumer.addVertex(matrix, x1, waterMaxY, z2)
                .setColor(r, g, b, a).setUv(waterSprite.getU0(), waterSprite.getV1()).setLight(light).setNormal(0, 0, 1);
        vertexConsumer.addVertex(matrix, x1, waterMinY, z2)
                .setColor(r, g, b, a).setUv(waterSprite.getU0(), waterSprite.getV0()).setLight(light).setNormal(0, 0, 1);

// West-Seite (x1)
        vertexConsumer.addVertex(matrix, x1, waterMinY, z2)
                .setColor(r, g, b, a).setUv(waterSprite.getU0(), waterSprite.getV0()).setLight(light).setNormal(-1, 0, 0);
        vertexConsumer.addVertex(matrix, x1, waterMaxY, z2)
                .setColor(r, g, b, a).setUv(waterSprite.getU0(), waterSprite.getV1()).setLight(light).setNormal(-1, 0, 0);
        vertexConsumer.addVertex(matrix, x1, waterMaxY, z1)
                .setColor(r, g, b, a).setUv(waterSprite.getU1(), waterSprite.getV1()).setLight(light).setNormal(-1, 0, 0);
        vertexConsumer.addVertex(matrix, x1, waterMinY, z1)
                .setColor(r, g, b, a).setUv(waterSprite.getU1(), waterSprite.getV0()).setLight(light).setNormal(-1, 0, 0);

// Ost-Seite (x2)
        vertexConsumer.addVertex(matrix, x2, waterMinY, z1)
                .setColor(r, g, b, a).setUv(waterSprite.getU1(), waterSprite.getV0()).setLight(light).setNormal(1, 0, 0);
        vertexConsumer.addVertex(matrix, x2, waterMaxY, z1)
                .setColor(r, g, b, a).setUv(waterSprite.getU1(), waterSprite.getV1()).setLight(light).setNormal(1, 0, 0);
        vertexConsumer.addVertex(matrix, x2, waterMaxY, z2)
                .setColor(r, g, b, a).setUv(waterSprite.getU0(), waterSprite.getV1()).setLight(light).setNormal(1, 0, 0);
        vertexConsumer.addVertex(matrix, x2, waterMinY, z2)
                .setColor(r, g, b, a).setUv(waterSprite.getU0(), waterSprite.getV0()).setLight(light).setNormal(1, 0, 0);

        poseStack.popPose();
    }
}