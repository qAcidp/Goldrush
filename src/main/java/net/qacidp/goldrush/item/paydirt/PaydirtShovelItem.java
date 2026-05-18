package net.qacidp.goldrush.item.paydirt;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.qacidp.goldrush.block.ModBlocks;
import net.qacidp.goldrush.block.bucket.PaydirtBucketBlock;
import net.qacidp.goldrush.block.entity.BucketBlockEntity;
import net.qacidp.goldrush.block.entity.PaydirtBlockEntity;
import net.qacidp.goldrush.block.paydirt.PaydirtBlock;
import net.qacidp.goldrush.block.paydirt.nugget.PaydirtNuggetBlock;
import net.qacidp.goldrush.component.GoldDistributionComponent;
import net.qacidp.goldrush.component.ModDataComponents;
import net.qacidp.goldrush.data.ModAttachments;
import net.qacidp.goldrush.data.PlayerGoldData;
import net.neoforged.neoforge.network.PacketDistributor;
import net.qacidp.goldrush.network.SyncPlayerGoldPacket;

public class PaydirtShovelItem extends ShovelItem {

    public PaydirtShovelItem() {
        super(Tiers.IRON, new Properties()
                .durability(250));
    }

    private Block getNextBucketBlock(int fillLevel) {
        return switch (fillLevel) {
            case 25 -> ModBlocks.PAYDIRT_BUCKET_BLOCK_25.get();
            case 50 -> ModBlocks.PAYDIRT_BUCKET_BLOCK_50.get();
            case 75 -> ModBlocks.PAYDIRT_BUCKET_BLOCK_75.get();
            case 100 -> ModBlocks.PAYDIRT_BUCKET_BLOCK_100.get();
            default -> ModBlocks.PAYDIRT_BUCKET_BLOCK_EMPTY.get();
        };
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

                // Gefüllte Schaufel auf Eimer klicken
                if (state.getBlock() instanceof PaydirtBucketBlock) {
                    BlockEntity entity = level.getBlockEntity(pos);
                    if (entity instanceof BucketBlockEntity bucketEntity) {
                        if (bucketEntity.getFillLevel() < 100) {
                            GoldDistributionComponent component = context.getItemInHand()
                                    .get(ModDataComponents.GOLD_DISTRIBUTION.get());
                            bucketEntity.addGold(component.getGoldDistribution()[0]);
                            System.out.println("[BUCKET] Gold im Eimer: " + bucketEntity.getTotalGold() + "g (+" + component.getGoldDistribution()[0] + "g)");

                            Block nextBlock = getNextBucketBlock(bucketEntity.getFillLevel());
                            level.setBlock(pos, nextBlock.defaultBlockState(), 3);

                            BlockEntity newEntity = level.getBlockEntity(pos);
                            if (newEntity instanceof BucketBlockEntity newBucket) {
                                newBucket.setTotalGold(bucketEntity.getTotalGold());
                                newBucket.setFillLevel(bucketEntity.getFillLevel());
                                newBucket.setChanged();
                            }

                            context.getItemInHand().remove(ModDataComponents.GOLD_DISTRIBUTION.get());
                            return InteractionResult.SUCCESS;
                        }
                    }
                    return InteractionResult.FAIL;
                }

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


                // Auf PaydirtBlock oder PaydirtNuggetBlock klicken → Layer erhöhen
                if (state.getBlock() instanceof PaydirtBlock || state.getBlock() instanceof PaydirtNuggetBlock) {
                    BlockEntity entity = level.getBlockEntity(pos);
                    if (entity instanceof PaydirtBlockEntity paydirtEntity) {
                        int currentLayers = paydirtEntity.getCurrentLayers();
                        if (currentLayers < 8) {
                            GoldDistributionComponent component = context.getItemInHand().get(ModDataComponents.GOLD_DISTRIBUTION.get());
                            float[] dist = paydirtEntity.getGoldDistribution();
                            dist[currentLayers] = component.getGoldDistribution()[0];
                            paydirtEntity.setGoldDistribution(dist);
                            paydirtEntity.addLayer();
                            if (state.getBlock() instanceof PaydirtBlock) {
                                level.setBlock(pos, state.setValue(PaydirtBlock.LAYERS, paydirtEntity.getCurrentLayers()), 3);
                            } else if (state.getBlock() instanceof PaydirtNuggetBlock) {
                                level.setBlock(pos, state.setValue(PaydirtNuggetBlock.LAYERS, paydirtEntity.getCurrentLayers()), 3);
                            }
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

                float[] singleLayer = new float[]{0f};
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

        // Leere Schaufel → Layer von PaydirtBlock oder PaydirtNuggetBlock abbauen
        if (!(state.getBlock() instanceof PaydirtBlock || state.getBlock() instanceof PaydirtNuggetBlock)) {
            return InteractionResult.PASS;
        }

        if (!level.isClientSide) {
            BlockEntity entity = level.getBlockEntity(pos);
            if (entity instanceof PaydirtBlockEntity paydirtEntity) {
                float goldForLayer = paydirtEntity.removeTopLayer();
                System.out.println("[SHOVEL] Gold aufgenommen: " + goldForLayer + "g");
                int remainingLayers = paydirtEntity.getCurrentLayers();

                float[] singleLayer = new float[]{goldForLayer};
                context.getItemInHand().set(
                        ModDataComponents.GOLD_DISTRIBUTION.get(),
                        new GoldDistributionComponent(singleLayer)
                );

                // Check für Nugget bei NuggetBlock
                if (state.getBlock() instanceof PaydirtNuggetBlock) {
                    int nuggetLayer = paydirtEntity.getNuggetLayer();

                    // Wenn letzter Layer abgebaut wird UND Nugget-Layer erreicht
                    if (remainingLayers == 0 && nuggetLayer >= 0) {
                        float nuggetGold = paydirtEntity.getNuggetGold();

                        if (player != null) {
                            player.displayClientMessage(
                                    Component.literal("§6§l✦ Gold Nugget Found! §r§6" + String.format("%.2f", nuggetGold) + "g"),
                                    true
                            );

                            // Gold zu PlayerGoldData hinzufügen
                            PlayerGoldData goldData = player.getData(ModAttachments.PLAYER_GOLD);
                            goldData.addGold(nuggetGold);
                            PacketDistributor.sendToPlayer((net.minecraft.server.level.ServerPlayer) player,
                                    new SyncPlayerGoldPacket(goldData.getTotalGold()));
                        }
                    }
                }

                if (paydirtEntity.isEmpty()) {
                    level.removeBlock(pos, false);
                } else {
                    if (state.getBlock() instanceof PaydirtBlock) {
                        level.setBlock(pos, state.setValue(PaydirtBlock.LAYERS, remainingLayers), 3);
                    } else if (state.getBlock() instanceof PaydirtNuggetBlock) {
                        level.setBlock(pos, state.setValue(PaydirtNuggetBlock.LAYERS, remainingLayers), 3);
                    }
                    paydirtEntity.setChanged();
                }

                context.getItemInHand().hurtAndBreak(1, player, player.getEquipmentSlotForItem(context.getItemInHand()));
            }
        }

        return InteractionResult.SUCCESS;
    }
}