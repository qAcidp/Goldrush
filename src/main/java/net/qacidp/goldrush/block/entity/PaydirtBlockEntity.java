package net.qacidp.goldrush.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class PaydirtBlockEntity extends BlockEntity {

    private float[] goldDistribution = new float[8];
    private int currentLayers = 8;
    private int nuggetLayer = -1; // -1 = kein Nugget, 0-7 = Layer wo Nugget erscheint
    public int getNuggetLayer() { return nuggetLayer; }
    public void setNuggetLayer(int layer) { this.nuggetLayer = layer; }

    public PaydirtBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.PAYDIRT_BLOCK_ENTITY.get(), pos, state);
    }

    public void setGoldDistribution(float[] distribution) {
        this.goldDistribution = distribution;
    }

    public float[] getGoldDistribution() {
        return this.goldDistribution;
    }

    public float getGoldForLayer(int layer) {
        if (layer >= 0 && layer < 8) {
            return goldDistribution[layer];
        }
        return 0;
    }

    public int getCurrentLayers() {
        return currentLayers;
    }

    public float removeTopLayer() {
        if (currentLayers <= 0) return 0;
        currentLayers--;
        return goldDistribution[currentLayers];
    }

    public boolean isEmpty() {
        return currentLayers <= 0;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        tag.putInt("currentLayers", currentLayers);
        tag.putFloat("nuggetGold", nuggetGold);
        tag.putInt("nuggetLayer", nuggetLayer);
        CompoundTag goldTag = new CompoundTag();
        for (int i = 0; i < 8; i++) {
            goldTag.putFloat("layer_" + i, goldDistribution[i]);
        }
        tag.put("goldDistribution", goldTag);
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        currentLayers = tag.getInt("currentLayers");
        nuggetGold = tag.getFloat("nuggetGold");
        nuggetLayer = tag.getInt("nuggetLayer");
        if (tag.contains("goldDistribution")) {
            CompoundTag goldTag = tag.getCompound("goldDistribution");
            for (int i = 0; i < 8; i++) {
                goldDistribution[i] = goldTag.getFloat("layer_" + i);
            }
        }
    }

    public void addLayer() {
        if (currentLayers < 8) {
            currentLayers++;
        }
    }

    private float nuggetGold = 0f;

    public float getNuggetGold() { return nuggetGold; }
    public void setNuggetGold(float gold) { this.nuggetGold = gold; }

}