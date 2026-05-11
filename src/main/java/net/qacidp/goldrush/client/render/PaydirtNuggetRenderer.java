package net.qacidp.goldrush.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
import net.qacidp.goldrush.block.entity.PaydirtBlockEntity;
import net.qacidp.goldrush.block.paydirt.nugget.PaydirtNuggetBlock;

public class PaydirtNuggetRenderer implements BlockEntityRenderer<PaydirtBlockEntity> {

    public PaydirtNuggetRenderer(BlockEntityRendererProvider.Context ctx) {
    }

    @Override
    public void render(PaydirtBlockEntity be, float partialTicks, PoseStack poseStack,
                       MultiBufferSource buffer, int light, int overlay) {

        BlockState state = be.getBlockState();

        if (!(state.getBlock() instanceof PaydirtNuggetBlock)) return;

        int currentLayers = state.getValue(PaydirtNuggetBlock.LAYERS);
        int nuggetLayer = be.getNuggetLayer();

        // Nur rendern wenn currentLayers <= nuggetLayer (Nugget ist freigelegt)
        if (currentLayers > nuggetLayer || nuggetLayer == -1) return;

        poseStack.pushPose();

        float yOffset = (currentLayers * 2f) / 16f;
        poseStack.translate(0.5, yOffset + 0.1, 0.5);

        poseStack.scale(0.4f, 0.4f, 0.4f);

        ItemStack nugget = new ItemStack(Items.GOLD_NUGGET);

        Minecraft.getInstance().getItemRenderer().renderStatic(
                nugget,
                ItemDisplayContext.FIXED,
                light,
                overlay,
                poseStack,
                buffer,
                be.getLevel(),
                0
        );

        poseStack.popPose();
    }
}