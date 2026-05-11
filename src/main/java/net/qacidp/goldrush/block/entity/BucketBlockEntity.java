package net.qacidp.goldrush.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class BucketBlockEntity extends BlockEntity {

    private float totalGold = 0f;
    private int fillLevel = 0; // 0, 25, 50, 75, 100

    public BucketBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.BUCKET_BLOCK_ENTITY.get(), pos, state);
    }

    public float getTotalGold() { return totalGold; }
    public int getFillLevel() { return fillLevel; }

    public void addGold(float gold) {
        this.totalGold += gold;
        this.fillLevel += 25;
        setChanged();
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        tag.putFloat("totalGold", totalGold);
        tag.putInt("fillLevel", fillLevel);
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        totalGold = tag.getFloat("totalGold");
        fillLevel = tag.getInt("fillLevel");
    }

    public void setTotalGold(float gold) { this.totalGold = gold; }
    public void setFillLevel(int level) { this.fillLevel = level; }
}
