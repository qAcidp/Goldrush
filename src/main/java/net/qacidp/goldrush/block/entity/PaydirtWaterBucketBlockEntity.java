package net.qacidp.goldrush.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;


public class PaydirtWaterBucketBlockEntity extends BlockEntity {

    private int goldPoints = 0; // 0-1200

    public PaydirtWaterBucketBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.PAYDIRT_WATER_BUCKET_BLOCK_ENTITY.get(), pos, state);
    }

    public int getGoldPoints() {
        return goldPoints;
    }

    public void addGoldPoints(int points) {
        this.goldPoints = Math.min(1200, this.goldPoints + points);
        setChanged();
    }



    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        tag.putInt("goldPoints", goldPoints);
    }



    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        goldPoints = tag.getInt("goldPoints");
    }
}