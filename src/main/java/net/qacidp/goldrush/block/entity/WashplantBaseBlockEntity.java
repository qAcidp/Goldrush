package net.qacidp.goldrush.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class WashplantBaseBlockEntity extends BlockEntity {

    private boolean isWashing = false;

    public WashplantBaseBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.WASHPLANT_BASE_BLOCK_ENTITY.get(), pos, state);
    }

    public boolean isWashing() {
        return isWashing;
    }

    public void setWashing(boolean washing) {
        this.isWashing = washing;
        setChanged();
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        tag.putBoolean("isWashing", isWashing);
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        isWashing = tag.getBoolean("isWashing");
    }
}