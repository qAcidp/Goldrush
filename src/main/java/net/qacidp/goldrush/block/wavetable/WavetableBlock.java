package net.qacidp.goldrush.block.wavetable;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
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
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.qacidp.goldrush.block.ModBlocks;
import net.qacidp.goldrush.block.entity.ModBlockEntities;
import net.qacidp.goldrush.block.entity.WavetableBlockEntity;
import org.jetbrains.annotations.Nullable;

public class WavetableBlock extends BaseEntityBlock {

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    private static final VoxelShape SHAPE = Shapes.block();

    public WavetableBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction facing = context.getHorizontalDirection().getOpposite();
        BlockPos pos = context.getClickedPos();
        BlockPos rightPos = pos.relative(facing.getOpposite());

        if (context.getLevel().getBlockState(rightPos).canBeReplaced(context)) {
            return this.defaultBlockState().setValue(FACING, facing);
        }
        return null;
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide) {
            Direction facing = state.getValue(FACING);
            BlockPos rightPos = pos.relative(facing.getOpposite());
            BlockState rightState = level.getBlockState(rightPos);

            if (rightState.getBlock() instanceof WavetableRightBlock) {
                level.removeBlock(rightPos, false);
            }
        }
        return super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        if (!level.isClientSide) {
            Direction facing = state.getValue(FACING);
            BlockPos rightPos = pos.relative(facing.getOpposite());

            level.setBlock(rightPos, ModBlocks.WAVETABLE_RIGHT.get().defaultBlockState()
                    .setValue(WavetableRightBlock.FACING, facing), 3);
        }
    }

    @Override
    public ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                           Player player, net.minecraft.world.InteractionHand hand,
                                           net.minecraft.world.phys.BlockHitResult hitResult) {
        if (!level.isClientSide) {
            BlockEntity entity = level.getBlockEntity(pos);
            if (entity instanceof WavetableBlockEntity wavetableEntity) {
                return handleInteraction(stack, state, level, pos, player, hand, hitResult, wavetableEntity);
            }
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    public ItemInteractionResult handleInteraction(ItemStack stack, BlockState state, Level level,
                                                   BlockPos pos, Player player,
                                                   net.minecraft.world.InteractionHand hand,
                                                   net.minecraft.world.phys.BlockHitResult hitResult,
                                                   WavetableBlockEntity wavetableEntity) {
        if (wavetableEntity.isProcessing()) {
            player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§cAlready processing!"));
            return ItemInteractionResult.FAIL;
        }

        if (stack.getItem().equals(ModBlocks.PAYDIRT_WATER_BUCKET_BLOCK_HALF.get().asItem()) ||
                stack.getItem().equals(ModBlocks.PAYDIRT_WATER_BUCKET_BLOCK_FULL.get().asItem())) {

            float goldGrams = stack.getOrDefault(
                            net.minecraft.core.component.DataComponents.CUSTOM_DATA,
                            net.minecraft.world.item.component.CustomData.EMPTY)
                    .copyTag()
                    .getFloat("goldGrams");

            if (goldGrams <= 0) {
                player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§cNo gold in this bucket!"));
                return ItemInteractionResult.FAIL;
            }

            wavetableEntity.startProcessing(goldGrams, player.getUUID());

            if (!player.isCreative()) {
                stack.shrink(1);
                ItemStack emptyBucket = new ItemStack(ModBlocks.PAYDIRT_BUCKET_BLOCK_EMPTY.get());
                if (!player.getInventory().add(emptyBucket)) {
                    player.drop(emptyBucket, false);
                }
            }

            player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                    "§6Processing " + String.format("%.4f", goldGrams) + "g..."));
            return ItemInteractionResult.SUCCESS;
        }

        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
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
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
            Level level, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, ModBlockEntities.WAVETABLE_BLOCK_ENTITY.get(),
                WavetableBlockEntity::tick);
    }

    @Override
    public MapCodec<? extends BaseEntityBlock> codec() {
        return simpleCodec(WavetableBlock::new);
    }
}