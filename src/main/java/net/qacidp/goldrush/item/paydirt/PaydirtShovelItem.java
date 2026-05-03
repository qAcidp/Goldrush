package net.qacidp.goldrush.item.paydirt;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.qacidp.goldrush.block.ModBlocks;
import net.qacidp.goldrush.block.entity.PaydirtBlockEntity;
import net.qacidp.goldrush.block.paydirt.PaydirtBlock;
import net.qacidp.goldrush.component.GoldDistributionComponent;
import net.qacidp.goldrush.component.ModDataComponents;

public class PaydirtShovelItem extends ShovelItem {

    public PaydirtShovelItem() {
        super(Tiers.IRON, new Properties()
                .durability(250));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();
        BlockState state = level.getBlockState(pos);
        boolean isFilled = context.getItemInHand().get(ModDataComponents.GOLD_DISTRIBUTION.get()) != null;

        if (isFilled) {
            if (!level.isClientSide) {
                // Auf einen bestehenden Layer klicken → stapeln
                if (state.getBlock() == ModBlocks.PAYDIRT_LAYER.get()) {
                    int currentLayers = state.getValue(net.minecraft.world.level.block.SnowLayerBlock.LAYERS);
                    if (currentLayers < 8) {
                        level.setBlock(pos, state.setValue(net.minecraft.world.level.block.SnowLayerBlock.LAYERS, currentLayers + 1), 3);
                        context.getItemInHand().remove(ModDataComponents.GOLD_DISTRIBUTION.get());
                        return InteractionResult.SUCCESS;
                    }
                    return InteractionResult.FAIL;
                }

                // Auf PAY_DIRT_LOW klicken → Layer auf Original Block erhöhen
                if (state.getBlock() == ModBlocks.PAY_DIRT_LOW.get()) {
                    BlockEntity entity = level.getBlockEntity(pos);
                    if (entity instanceof PaydirtBlockEntity paydirtEntity) {
                        int currentLayers = paydirtEntity.getCurrentLayers();
                        if (currentLayers < 8) {
                            GoldDistributionComponent component = context.getItemInHand().get(ModDataComponents.GOLD_DISTRIBUTION.get());
                            float[] dist = paydirtEntity.getGoldDistribution();
                            dist[currentLayers] = component.getGoldDistribution()[0];
                            paydirtEntity.setGoldDistribution(dist);
                            paydirtEntity.addLayer();
                            level.setBlock(pos, state.setValue(PaydirtBlock.LAYERS, paydirtEntity.getCurrentLayers()), 3);
                            paydirtEntity.setChanged();
                            context.getItemInHand().remove(ModDataComponents.GOLD_DISTRIBUTION.get());
                            return InteractionResult.SUCCESS;
                        } else {
                            // Block voll → neuen Layer darüber platzieren
                            BlockPos placePos = pos.above();
                            if (level.getBlockState(placePos).isAir()) {
                                level.setBlock(placePos, ModBlocks.PAYDIRT_LAYER.get().defaultBlockState(), 3);
                                context.getItemInHand().remove(ModDataComponents.GOLD_DISTRIBUTION.get());
                                return InteractionResult.SUCCESS;
                            }
                        }
                    }
                    return InteractionResult.FAIL;
                }

                // Auf Luft klicken → neuen Layer platzieren
                BlockPos placePos = pos.relative(context.getClickedFace());
                if (level.getBlockState(placePos).isAir()) {
                    level.setBlock(placePos, ModBlocks.PAYDIRT_LAYER.get().defaultBlockState(), 3);
                    context.getItemInHand().remove(ModDataComponents.GOLD_DISTRIBUTION.get());
                    return InteractionResult.SUCCESS;
                }
            }
            return InteractionResult.FAIL;
        }

// Leere Schaufel → Layer von PaydirtLayer abbauen
        if (state.getBlock() == ModBlocks.PAYDIRT_LAYER.get()) {
            if (!level.isClientSide) {
                int currentLayers = state.getValue(net.minecraft.world.level.block.SnowLayerBlock.LAYERS);

                float[] singleLayer = new float[]{0f}; // Gold-Wert später aus Data Component
                context.getItemInHand().set(
                        ModDataComponents.GOLD_DISTRIBUTION.get(),
                        new GoldDistributionComponent(singleLayer)
                );

                if (currentLayers <= 1) {
                    level.removeBlock(pos, false);
                } else {
                    level.setBlock(pos, state.setValue(net.minecraft.world.level.block.SnowLayerBlock.LAYERS, currentLayers - 1), 3);
                }

                context.getItemInHand().hurtAndBreak(1, player, player.getEquipmentSlotForItem(context.getItemInHand()));
            }
            return InteractionResult.SUCCESS;
        }
// Leere Schaufel → Layer von Paydirt abbauen
        if (state.getBlock() != ModBlocks.PAY_DIRT_LOW.get()) {
            return InteractionResult.PASS;
        }

        if (!level.isClientSide) {
            BlockEntity entity = level.getBlockEntity(pos);
            if (entity instanceof PaydirtBlockEntity paydirtEntity) {
                float goldForLayer = paydirtEntity.removeTopLayer();
                int remainingLayers = paydirtEntity.getCurrentLayers();

                float[] singleLayer = new float[]{goldForLayer};
                context.getItemInHand().set(
                        ModDataComponents.GOLD_DISTRIBUTION.get(),
                        new GoldDistributionComponent(singleLayer)
                );

                if (paydirtEntity.isEmpty()) {
                    level.removeBlock(pos, false);
                } else {
                    level.setBlock(pos, state.setValue(PaydirtBlock.LAYERS, remainingLayers), 3);
                    paydirtEntity.setChanged();
                }

                context.getItemInHand().hurtAndBreak(1, player, player.getEquipmentSlotForItem(context.getItemInHand()));
            }
        }

        return InteractionResult.SUCCESS;
    }

}