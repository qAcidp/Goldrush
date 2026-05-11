package net.qacidp.goldrush.block.washplant;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.network.PacketDistributor;
import net.qacidp.goldrush.block.ModBlocks;
import net.qacidp.goldrush.block.entity.ModBlockEntities;
import net.qacidp.goldrush.block.entity.WashplantHeadBlockEntity;
import net.qacidp.goldrush.network.SyncWashplantFillPacket;
import org.jetbrains.annotations.Nullable;

public class WashplantHeadBlock extends BaseEntityBlock {

    private static final VoxelShape SHAPE = box(0, 0, 0, 16, 12, 16);

    public WashplantHeadBlock(Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        WashplantHeadBlockEntity entity = new WashplantHeadBlockEntity(pos, state);
        entity.setFillLevel(0.0f);
        return entity;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockPos below = pos.below();

        if (!(level.getBlockState(below).getBlock() instanceof WashplantBaseBlock)) {
            return null;
        }

        BlockPos southOfBase = below.south();
        if (level.getBlockState(southOfBase).getBlock() instanceof WashplantBaseBlock) {
            return null;
        }

        return this.defaultBlockState();
    }

    @Override
    public MapCodec<? extends BaseEntityBlock> codec() {
        return simpleCodec(WashplantHeadBlock::new);
    }

    @Override
    public ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                           Player player, net.minecraft.world.InteractionHand hand, BlockHitResult hitResult) {

        if (!level.isClientSide) {
            BlockEntity entity = level.getBlockEntity(pos);
            if (!(entity instanceof WashplantHeadBlockEntity headEntity)) {
                return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            }

            // Wasser-Eimer Check
            if (stack.getItem() == Items.WATER_BUCKET) {
                if (headEntity.getFillLevel() <= 0) {
                    player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§cHead is empty!"));
                    return ItemInteractionResult.FAIL;
                }

                headEntity.startWashing(0.20f);

                if (!player.isCreative()) {
                    stack.shrink(1);
                    ItemStack emptyBucket = new ItemStack(Items.BUCKET);
                    if (!player.getInventory().add(emptyBucket)) {
                        player.drop(emptyBucket, false);
                    }
                }

                player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§bWashing..."));
                return ItemInteractionResult.SUCCESS;
            }

            // Paydirt Bucket Check
            if (stack.getItem() instanceof net.minecraft.world.item.BlockItem blockItem) {

                float bucketFill = 0f;
                Block bucketBlock = blockItem.getBlock();

                if (bucketBlock == ModBlocks.PAYDIRT_BUCKET_BLOCK_25.get()) {
                    bucketFill = 0.05f;
                } else if (bucketBlock == ModBlocks.PAYDIRT_BUCKET_BLOCK_50.get()) {
                    bucketFill = 0.10f;
                } else if (bucketBlock == ModBlocks.PAYDIRT_BUCKET_BLOCK_75.get()) {
                    bucketFill = 0.15f;
                } else if (bucketBlock == ModBlocks.PAYDIRT_BUCKET_BLOCK_100.get()) {
                    bucketFill = 0.20f;
                } else {
                    return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
                }

                float currentFill = headEntity.getFillLevel();
                float spaceLeft = 1.0f - currentFill;

                if (spaceLeft <= 0) {
                    player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§cHead is full!"));
                    return ItemInteractionResult.FAIL;
                }

                float amountToAdd = Math.min(bucketFill, spaceLeft);
                float bucketRemaining = bucketFill - amountToAdd;

                headEntity.addFillLevel(amountToAdd);

                PacketDistributor.sendToPlayersTrackingChunk((ServerLevel) level,
                        new net.minecraft.world.level.ChunkPos(pos),
                        new SyncWashplantFillPacket(pos, headEntity.getFillLevel(), headEntity.isWashing()));  // <- ) hinzugefügt

                ItemStack resultBucket = getBucketForFillLevel(bucketRemaining);

                if (!player.isCreative()) {
                    stack.shrink(1);
                    if (!player.getInventory().add(resultBucket)) {
                        player.drop(resultBucket, false);
                    }
                }

                player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                        "§6Head: " + String.format("%.0f", headEntity.getFillLevel() * 100) + "%"));

                return ItemInteractionResult.SUCCESS;
            }
        }

        // Wasser-Eimer Check (AUSSERHALB des if (!level.isClientSide))
        if (stack.getItem() == Items.WATER_BUCKET) {
            if (!level.isClientSide) {
                BlockEntity entity = level.getBlockEntity(pos);
                if (entity instanceof WashplantHeadBlockEntity headEntity) {
                    if (headEntity.getFillLevel() <= 0) {
                        player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§cHead is empty!"));
                        return ItemInteractionResult.FAIL;
                    }

                    headEntity.startWashing(0.20f);

                    if (!player.isCreative()) {
                        stack.shrink(1);
                        ItemStack emptyBucket = new ItemStack(Items.BUCKET);
                        if (!player.getInventory().add(emptyBucket)) {
                            player.drop(emptyBucket, false);
                        }
                    }

                    player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§bWashing..."));
                }
            }
            return ItemInteractionResult.SUCCESS; // Verhindert Wasser-Platzierung
        }

        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    private ItemStack getBucketForFillLevel(float fillLevel) {
        if (fillLevel <= 0) {
            return new ItemStack(ModBlocks.PAYDIRT_BUCKET_BLOCK_EMPTY.get());
        } else if (fillLevel <= 0.05f) {
            return new ItemStack(ModBlocks.PAYDIRT_BUCKET_BLOCK_25.get());
        } else if (fillLevel <= 0.10f) {
            return new ItemStack(ModBlocks.PAYDIRT_BUCKET_BLOCK_50.get());
        } else if (fillLevel <= 0.15f) {
            return new ItemStack(ModBlocks.PAYDIRT_BUCKET_BLOCK_75.get());
        } else {
            return new ItemStack(ModBlocks.PAYDIRT_BUCKET_BLOCK_100.get());
        }
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide ? null : createTickerHelper(type, ModBlockEntities.WASHPLANT_HEAD_BLOCK_ENTITY.get(), WashplantHeadBlockEntity::tick);
    }
}