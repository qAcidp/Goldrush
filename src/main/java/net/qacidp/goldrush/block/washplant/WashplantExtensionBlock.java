package net.qacidp.goldrush.block.washplant;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.qacidp.goldrush.block.entity.WashplantExtensionBlockEntity;
import org.jetbrains.annotations.Nullable;

public class WashplantExtensionBlock extends BaseEntityBlock {

    private static final VoxelShape SHAPE = box(0, 0, 0, 16, 11, 16);

    public WashplantExtensionBlock(Properties properties) {
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
        return new WashplantExtensionBlockEntity(pos, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();

        if (!isNextToBase(level, pos)) {
            return null;
        }

        BlockPos pos2 = pos.north();
        BlockPos pos3 = pos.north(2);

        if (!level.isClientSide) {
            if (level.getBlockState(pos2).canBeReplaced(context) &&
                    level.getBlockState(pos3).canBeReplaced(context)) {

                level.setBlock(pos2, this.defaultBlockState(), 11);
                level.setBlock(pos3, this.defaultBlockState(), 11);

// BlockEntities manuell erstellen
                level.setBlockEntity(new WashplantExtensionBlockEntity(pos2, this.defaultBlockState()));
                level.setBlockEntity(new WashplantExtensionBlockEntity(pos3, this.defaultBlockState()));
            }
        }

        return this.defaultBlockState();
    }

    private boolean isNextToBase(Level level, BlockPos pos) {
        return level.getBlockState(pos.north()).getBlock() instanceof WashplantBaseBlock ||
                level.getBlockState(pos.south()).getBlock() instanceof WashplantBaseBlock ||
                level.getBlockState(pos.east()).getBlock() instanceof WashplantBaseBlock ||
                level.getBlockState(pos.west()).getBlock() instanceof WashplantBaseBlock;
    }

    @Override
    public MapCodec<? extends BaseEntityBlock> codec() {
        return simpleCodec(WashplantExtensionBlock::new);
    }
}