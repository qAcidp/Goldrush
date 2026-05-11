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
import net.qacidp.goldrush.block.entity.WashplantBaseBlockEntity;
import org.jetbrains.annotations.Nullable;

public class WashplantBaseBlock extends BaseEntityBlock {

    private static final VoxelShape SHAPE = box(0, 0, 0, 16, 12, 16);

    public WashplantBaseBlock(Properties properties) {
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
        return new WashplantBaseBlockEntity(pos, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();

        BlockPos pos2 = pos.north();
        BlockPos pos3 = pos.north(2);

        if (!level.isClientSide) {
            if (level.getBlockState(pos2).canBeReplaced(context) &&
                    level.getBlockState(pos3).canBeReplaced(context)) {

                // Flag 3 = UPDATE_NEIGHBORS | UPDATE_CLIENTS
                // Flag 11 = UPDATE_ALL (sollte BlockEntity erstellen)
                level.setBlock(pos2, this.defaultBlockState(), 11);
                level.setBlock(pos3, this.defaultBlockState(), 11);

                System.out.println("Placed blocks at " + pos2 + " and " + pos3);
                System.out.println("Entity at pos2: " + level.getBlockEntity(pos2));
                System.out.println("Entity at pos3: " + level.getBlockEntity(pos3));
            }
        }

        return this.defaultBlockState();
    }

    @Override
    public MapCodec<? extends BaseEntityBlock> codec() {
        return simpleCodec(WashplantBaseBlock::new);
    }
}