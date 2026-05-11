package net.qacidp.goldrush.block.bucket;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.qacidp.goldrush.block.ModBlocks;
import net.qacidp.goldrush.block.entity.BucketBlockEntity;
import net.qacidp.goldrush.component.GoldDistributionComponent;
import net.qacidp.goldrush.component.ModDataComponents;
import org.jetbrains.annotations.Nullable;

public class PaydirtBucketBlock extends BaseEntityBlock {

    private static final VoxelShape SHAPE = Shapes.or(
            box(3, 0, 3, 13, 1, 13),
            box(3, 1, 3, 4, 9, 13),
            box(12, 1, 3, 13, 9, 13),
            box(4, 1, 3, 12, 9, 4),
            box(4, 1, 12, 12, 9, 13),
            box(2, 9, 6, 3, 11, 10),
            box(13, 9, 6, 14, 11, 10)
    );

    public PaydirtBucketBlock(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BucketBlockEntity(pos, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack tool) {
        if (!level.isClientSide && blockEntity instanceof BucketBlockEntity bucketEntity) {
            // Korrekten Füllstand droppen
            ItemStack drop = getDropForFillLevel(bucketEntity.getFillLevel());

            // Gold-Wert in Data Component speichern
            drop.set(ModDataComponents.GOLD_DISTRIBUTION.get(),
                    new GoldDistributionComponent(new float[]{bucketEntity.getTotalGold()}));

            popResource(level, pos, drop);
        }
        super.playerDestroy(level, player, pos, state, blockEntity, tool);
    }

    private ItemStack getDropForFillLevel(int fillLevel) {
        return switch (fillLevel) {
            case 25 -> new ItemStack(ModBlocks.PAYDIRT_BUCKET_BLOCK_25.get());
            case 50 -> new ItemStack(ModBlocks.PAYDIRT_BUCKET_BLOCK_50.get());
            case 75 -> new ItemStack(ModBlocks.PAYDIRT_BUCKET_BLOCK_75.get());
            case 100 -> new ItemStack(ModBlocks.PAYDIRT_BUCKET_BLOCK_100.get());
            default -> new ItemStack(ModBlocks.PAYDIRT_BUCKET_BLOCK_EMPTY.get());
        };
    }

    @Override
    public MapCodec<? extends BaseEntityBlock> codec() {
        return simpleCodec(PaydirtBucketBlock::new);
    }
}