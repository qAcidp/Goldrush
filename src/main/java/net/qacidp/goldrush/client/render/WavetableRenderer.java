package net.qacidp.goldrush.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.qacidp.goldrush.block.entity.WavetableBlockEntity;
import net.qacidp.goldrush.block.wavetable.WavetableBlock;
import org.joml.Matrix4f;

public class WavetableRenderer implements BlockEntityRenderer<WavetableBlockEntity> {

    private static final ResourceLocation WATER_FLOWING =
            ResourceLocation.fromNamespaceAndPath("minecraft", "block/water_flow");

    public WavetableRenderer(BlockEntityRendererProvider.Context ctx) {}

    @Override
    public void render(WavetableBlockEntity be, float partialTicks, PoseStack poseStack,
                       MultiBufferSource buffer, int light, int overlay) {
        if (!be.isProcessing()) return;
        if (!(be.getBlockState().getBlock() instanceof WavetableBlock)) return;

        BlockState state = be.getBlockState();
        Direction facing = state.getValue(WavetableBlock.FACING);

        renderWater(poseStack, buffer, light, facing);
    }

    private void renderWater(PoseStack poseStack, MultiBufferSource buffer, int light, Direction facing) {
        poseStack.pushPose();

        TextureAtlasSprite sprite = Minecraft.getInstance()
                .getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
                .apply(WATER_FLOWING);

        VertexConsumer vc = buffer.getBuffer(RenderType.cutout());
        Matrix4f matrix = poseStack.last().pose();

        float waterY = 14f / 16f;

        // Linke Hälfte — immer 0-1
        renderQuad(vc, matrix, 0f, waterY, 0f, 1f, 1f, sprite, light);

        // Rechte Hälfte — Offset basierend auf facing.getOpposite()
        // WavetableBlock.onPlace setzt den rechten Block mit facing.getOpposite()
        int dx = facing.getOpposite().getStepX();
        int dz = facing.getOpposite().getStepZ();

        renderQuad(vc, matrix,
                dx,        waterY, dz,
                dx + 1f,   dz + 1f,
                sprite, light);

        poseStack.popPose();
    }

    private void renderQuad(VertexConsumer vc, Matrix4f matrix,
                            float x1, float y, float z1,
                            float x2, float z2,
                            TextureAtlasSprite sprite, int light) {
        // Von oben
        vc.addVertex(matrix, x1, y, z1).setColor(64,128,200,255).setUv(sprite.getU0(), sprite.getV0()).setLight(light).setNormal(0,1,0);
        vc.addVertex(matrix, x2, y, z1).setColor(64,128,200,255).setUv(sprite.getU1(), sprite.getV0()).setLight(light).setNormal(0,1,0);
        vc.addVertex(matrix, x2, y, z2).setColor(64,128,200,255).setUv(sprite.getU1(), sprite.getV1()).setLight(light).setNormal(0,1,0);
        vc.addVertex(matrix, x1, y, z2).setColor(64,128,200,255).setUv(sprite.getU0(), sprite.getV1()).setLight(light).setNormal(0,1,0);

        // Von unten
        vc.addVertex(matrix, x1, y, z2).setColor(64,128,200,255).setUv(sprite.getU0(), sprite.getV1()).setLight(light).setNormal(0,-1,0);
        vc.addVertex(matrix, x2, y, z2).setColor(64,128,200,255).setUv(sprite.getU1(), sprite.getV1()).setLight(light).setNormal(0,-1,0);
        vc.addVertex(matrix, x2, y, z1).setColor(64,128,200,255).setUv(sprite.getU1(), sprite.getV0()).setLight(light).setNormal(0,-1,0);
        vc.addVertex(matrix, x1, y, z1).setColor(64,128,200,255).setUv(sprite.getU0(), sprite.getV0()).setLight(light).setNormal(0,-1,0);
    }
}