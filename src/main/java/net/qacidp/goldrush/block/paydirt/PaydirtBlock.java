package net.qacidp.goldrush.block.paydirt;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import net.qacidp.goldrush.block.entity.PaydirtBlockEntity;
import net.qacidp.goldrush.util.PaydirtConfig;

public class PaydirtBlock extends SnowLayerBlock implements EntityBlock {

    public PaydirtBlock(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PaydirtBlockEntity(pos, state);
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, level, pos, oldState, isMoving);

        if (!level.isClientSide) {
            BlockEntity entity = level.getBlockEntity(pos);
            if (entity instanceof PaydirtBlockEntity paydirtEntity) {
                float totalGold = PaydirtConfig.generateGoldAmount("pay_dirt_low");
                float[] goldDistribution = PaydirtConfig.getGoldDistribution("pay_dirt_low", totalGold);
                paydirtEntity.setGoldDistribution(goldDistribution);
                paydirtEntity.setChanged();
            }
        }
    }
}