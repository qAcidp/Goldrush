package net.qacidp.goldrush.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;

public class PlayerGoldData implements INBTSerializable<CompoundTag> {
    private float totalGold = 0f;

    public void addGold(float amount) {
        this.totalGold += amount;
    }

    public float getTotalGold() {
        return totalGold;
    }

    public void resetGold() {
        this.totalGold = 0f;
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        tag.putFloat("totalGold", totalGold);
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag tag) {
        totalGold = tag.getFloat("totalGold");
    }
}