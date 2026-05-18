package net.qacidp.goldrush.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.PacketDistributor;

public class WashplantExtensionBlockEntity extends BlockEntity {

    private boolean isWashing = false;
    private boolean hasMat = false;
    private int matWashCycles = 0;
    private int matMaterialPoints = 0; // 0-600+ Punkte

    public WashplantExtensionBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.WASHPLANT_EXTENSION_BLOCK_ENTITY.get(), pos, state);
    }

    public boolean isWashing() {
        return isWashing;
    }

    public void setWashing(boolean washing) {
        this.isWashing = washing;
        setChanged();
    }

    public boolean hasMat() {
        return hasMat;
    }

    public void setHasMat(boolean has) {
        this.hasMat = has;
        setChanged();
        // Für beide Seiten
        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    public int getMatWashCycles() {
        return matWashCycles;
    }

    public void incrementMatWashCycles() {
        if (!hasMat) return;
        this.matWashCycles++;
        setChanged();
    }

    public void setMatWashCycles(int cycles) {
        this.matWashCycles = cycles;
        setChanged();

        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }


    public int getMatMaterialPoints() {
        return matMaterialPoints;
    }

    public void addMatMaterialPoints(int points) {
        if (!hasMat) return;
        this.matMaterialPoints += points;
        setChanged();

        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    public void setMatMaterialPoints(int points) {
        this.matMaterialPoints = points;
        setChanged();
    }


    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        tag.putBoolean("isWashing", isWashing);
        tag.putBoolean("hasMat", hasMat);
        tag.putInt("matWashCycles", matWashCycles);
        tag.putInt("matMaterialPoints", matMaterialPoints); // NEU
        tag.putFloat("matGoldGrams", matGoldGrams);
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        isWashing = tag.getBoolean("isWashing");
        hasMat = tag.getBoolean("hasMat");
        matWashCycles = tag.getInt("matWashCycles");
        matMaterialPoints = tag.getInt("matMaterialPoints"); // NEU
        matGoldGrams = tag.getFloat("matGoldGrams");
    }

    public void removeMat() {
        this.hasMat = false;
        this.matWashCycles = 0;
        this.matMaterialPoints = 0; // RESET
        this.matGoldGrams = 0f;
        setChanged();
    }

    private float matGoldGrams = 0f;

    public float getMatGoldGrams() { return matGoldGrams; }

    public void setMatGoldGrams(float grams) {
        this.matGoldGrams = grams;
        setChanged();
    }

    public void addMatGoldGrams(float grams) {
        this.matGoldGrams += grams;
        setChanged();
    }



}