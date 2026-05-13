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
import net.qacidp.goldrush.block.entity.WashplantExtensionBlockEntity;
import org.joml.Matrix4f;

import static net.qacidp.goldrush.Goldrush.MODID;

public class WashplantExtensionRenderer implements BlockEntityRenderer<WashplantExtensionBlockEntity> {

    private static final ResourceLocation WATER_FLOWING =
            ResourceLocation.fromNamespaceAndPath("minecraft", "block/water_flow");

    public WashplantExtensionRenderer(BlockEntityRendererProvider.Context ctx) {
    }

    @Override
    public void render(WashplantExtensionBlockEntity be, float partialTicks, PoseStack poseStack,
                       MultiBufferSource buffer, int light, int overlay) {

        // 1. Matte rendern
        if (be.hasMat()) {
            renderMat(be, poseStack, buffer, light);
        }

        // 2. Wasser rendern
        if (be.isWashing()) {
            renderWater(poseStack, buffer, light);
        }
    }

    private void renderMat(WashplantExtensionBlockEntity be, PoseStack poseStack,
                           MultiBufferSource buffer, int light) {
        poseStack.pushPose();

        ResourceLocation texture = getTextureForWashCycles(be.getMatWashCycles());

        TextureAtlasSprite sprite = Minecraft.getInstance()
                .getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
                .apply(texture);

        VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.cutout());
        Matrix4f matrix = poseStack.last().pose();

        float matY = 15f / 16f;
        float x1 = 3f / 16f;
        float x2 = 13f / 16f;
        float z1 = 0.0f;
        float z2 = 1.0f;

        vertexConsumer.addVertex(matrix, x1, matY, z2)
                .setColor(255, 255, 255, 255)
                .setUv(sprite.getU0(), sprite.getV1())
                .setLight(light)
                .setNormal(0, 1, 0);

        vertexConsumer.addVertex(matrix, x2, matY, z2)
                .setColor(255, 255, 255, 255)
                .setUv(sprite.getU1(), sprite.getV1())
                .setLight(light)
                .setNormal(0, 1, 0);

        vertexConsumer.addVertex(matrix, x2, matY, z1)
                .setColor(255, 255, 255, 255)
                .setUv(sprite.getU1(), sprite.getV0())
                .setLight(light)
                .setNormal(0, 1, 0);

        vertexConsumer.addVertex(matrix, x1, matY, z1)
                .setColor(255, 255, 255, 255)
                .setUv(sprite.getU0(), sprite.getV0())
                .setLight(light)
                .setNormal(0, 1, 0);

        poseStack.popPose();
    }

    private void renderWater(PoseStack poseStack, MultiBufferSource buffer, int light) {
        poseStack.pushPose();

        float waterMinY = 6f / 16f;   // Extension 1 Pixel niedriger
        float waterMaxY = 10f / 16f;

        TextureAtlasSprite waterSprite = Minecraft.getInstance()
                .getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
                .apply(WATER_FLOWING);

        VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.cutout());
        Matrix4f matrix = poseStack.last().pose();

        float x1 = 3f / 16f;
        float x2 = 13f / 16f;
        float z1 = 0.0f;
        float z2 = 1.0f;

        int r = 100, g = 120, b = 80, a = 180;

        // Oberseite
        vertexConsumer.addVertex(matrix, x1, waterMaxY, z2)
                .setColor(r, g, b, a).setUv(waterSprite.getU0(), waterSprite.getV1()).setLight(light).setNormal(0, 1, 0);
        vertexConsumer.addVertex(matrix, x2, waterMaxY, z2)
                .setColor(r, g, b, a).setUv(waterSprite.getU1(), waterSprite.getV1()).setLight(light).setNormal(0, 1, 0);
        vertexConsumer.addVertex(matrix, x2, waterMaxY, z1)
                .setColor(r, g, b, a).setUv(waterSprite.getU1(), waterSprite.getV0()).setLight(light).setNormal(0, 1, 0);
        vertexConsumer.addVertex(matrix, x1, waterMaxY, z1)
                .setColor(r, g, b, a).setUv(waterSprite.getU0(), waterSprite.getV0()).setLight(light).setNormal(0, 1, 0);

        // Unterseite
        vertexConsumer.addVertex(matrix, x1, waterMinY, z1)
                .setColor(r, g, b, a).setUv(waterSprite.getU0(), waterSprite.getV0()).setLight(light).setNormal(0, -1, 0);
        vertexConsumer.addVertex(matrix, x2, waterMinY, z1)
                .setColor(r, g, b, a).setUv(waterSprite.getU1(), waterSprite.getV0()).setLight(light).setNormal(0, -1, 0);
        vertexConsumer.addVertex(matrix, x2, waterMinY, z2)
                .setColor(r, g, b, a).setUv(waterSprite.getU1(), waterSprite.getV1()).setLight(light).setNormal(0, -1, 0);
        vertexConsumer.addVertex(matrix, x1, waterMinY, z2)
                .setColor(r, g, b, a).setUv(waterSprite.getU0(), waterSprite.getV1()).setLight(light).setNormal(0, -1, 0);

        // Seiten - kopiere vom Base Renderer

        poseStack.popPose();
    }

    private ResourceLocation getTextureForWashCycles(int cycles) {
        if (cycles == 0) {
            return ResourceLocation.fromNamespaceAndPath(MODID, "block/washplant/washplant_mat_placed");
        } else if (cycles < 3) {
            return ResourceLocation.fromNamespaceAndPath(MODID, "block/washplant/washplant_mat_placed_light");
        } else if (cycles < 5) {
            return ResourceLocation.fromNamespaceAndPath(MODID, "block/washplant/washplant_mat_placed_medium");
        } else {
            return ResourceLocation.fromNamespaceAndPath(MODID, "block/washplant/washplant_mat_placed_heavy");
        }
    }
}