package net.qacidp.goldrush.block.wavetable;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.BlockGetter;
import net.qacidp.goldrush.block.ModBlocks;
import net.qacidp.goldrush.block.entity.WavetableBlockEntity;
import org.jetbrains.annotations.Nullable;

public class WavetableRightBlock extends BaseEntityBlock {

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    private static final VoxelShape SHAPE = Shapes.block();

    public WavetableRightBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide) {
            Direction facing = state.getValue(FACING);
            BlockPos leftPos = pos.relative(facing);
            BlockState leftState = level.getBlockState(leftPos);

            if (leftState.getBlock() instanceof WavetableBlock) {
                level.removeBlock(leftPos, false);
            }
        }
        return super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new WavetableBlockEntity(pos, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public MapCodec<? extends BaseEntityBlock> codec() {
        return simpleCodec(WavetableRightBlock::new);
    }

    @Override
    public ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                           Player player, net.minecraft.world.InteractionHand hand,
                                           net.minecraft.world.phys.BlockHitResult hitResult) {
        if (!level.isClientSide) {
            Direction facing = state.getValue(FACING);
            BlockPos leftPos = pos.relative(facing);
            BlockState leftState = level.getBlockState(leftPos);

            if (leftState.getBlock() instanceof WavetableBlock) {
                // Direkt ans BlockEntity delegieren
                BlockEntity entity = level.getBlockEntity(leftPos);
                if (entity instanceof WavetableBlockEntity wavetableEntity) {
                    return ((WavetableBlock) leftState.getBlock()).handleInteraction(
                            stack, leftState, level, leftPos, player, hand, hitResult, wavetableEntity);
                }
            }
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

}