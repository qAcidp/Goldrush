package net.qacidp.goldrush.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.PacketDistributor;
import net.qacidp.goldrush.network.SyncWashplantFillPacket;
import net.qacidp.goldrush.block.entity.WashplantBaseBlockEntity;
import net.qacidp.goldrush.block.entity.WashplantExtensionBlockEntity;
import net.minecraft.server.level.ServerLevel;
import net.qacidp.goldrush.network.SyncWashplantBasePacket;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.world.level.block.Blocks;
import net.qacidp.goldrush.network.SyncWashplantMatPacket;

public class WashplantHeadBlockEntity extends BlockEntity {

    private float fillLevel = 0f;
    private boolean isWashing = false;
    private float targetFillLevel = 0f;
    private float startFillLevel = 0f;
    private int washingTicks = 0;
    private static final int TOTAL_WASHING_TICKS = 200;
    private float totalMaterialWashed = 0f;
    // Statt totalGoldInHead — eine Queue pro Eimer
    private java.util.ArrayDeque<Float> goldQueue = new java.util.ArrayDeque<>();

    public WashplantHeadBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.WASHPLANT_HEAD_BLOCK_ENTITY.get(), pos, state);
    }

    public float getFillLevel() {
        return fillLevel;
    }

    public boolean isWashing() {
        return isWashing;
    }

    public void setFillLevel(float level) {
        this.fillLevel = Math.max(0f, Math.min(1f, level));
        setChanged();

        if (this.level != null && !this.level.isClientSide) {
            this.level.sendBlockUpdated(this.worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    public void setFillLevelClient(float level, boolean washing) {
        this.fillLevel = level;
        this.isWashing = washing;
    }

    public void addFillLevel(float amount) {
        setFillLevel(fillLevel + amount);
    }

    public void startWashing(float amountToReduce) {
        if (fillLevel <= 0) return;

        this.isWashing = true;
        this.startFillLevel = this.fillLevel;
        this.targetFillLevel = Math.max(0, fillLevel - amountToReduce);
        this.washingTicks = 0;
        setChanged();

        if (level != null && !level.isClientSide) {
            notifyWashplantBlocks(true);
        }
    }

    private void notifyWashplantBlocks(boolean washing) {
        if (level == null || level.isClientSide) return;

        ServerLevel serverLevel = (ServerLevel) level;
        BlockPos below = worldPosition.below();

        for (int i = 0; i < 3; i++) {
            BlockPos basePos = below.north(i);
            BlockEntity entity = level.getBlockEntity(basePos);

            if (entity instanceof WashplantBaseBlockEntity baseEntity) {
                baseEntity.setWashing(washing);

                PacketDistributor.sendToPlayersTrackingChunk(serverLevel,
                        new net.minecraft.world.level.ChunkPos(basePos),
                        new SyncWashplantBasePacket(basePos, washing, false));
            }
        }

        for (int i = 3; i < 6; i++) {
            BlockPos extPos = below.north(i);
            BlockEntity entity = level.getBlockEntity(extPos);

            if (entity instanceof WashplantExtensionBlockEntity extEntity) {
                extEntity.setWashing(washing);

                PacketDistributor.sendToPlayersTrackingChunk(serverLevel,
                        new net.minecraft.world.level.ChunkPos(extPos),
                        new SyncWashplantBasePacket(extPos, washing, true));
            }
        }
    }

    public static void tick(Level level, BlockPos pos, BlockState state, WashplantHeadBlockEntity entity) {
        if (!entity.isWashing) return;

        entity.washingTicks++;

        float progress = Math.min(1.0f, (float) entity.washingTicks / TOTAL_WASHING_TICKS);
        float newLevel = entity.startFillLevel - (entity.startFillLevel - entity.targetFillLevel) * progress;
        entity.fillLevel = newLevel;

        if (!level.isClientSide && entity.washingTicks % 5 == 0) {
            PacketDistributor.sendToPlayersTrackingChunk((ServerLevel) level,
                    new net.minecraft.world.level.ChunkPos(pos),
                    new SyncWashplantFillPacket(pos, entity.fillLevel, entity.isWashing));

            spawnWaterParticles((ServerLevel) level, pos);
        }

        if (!level.isClientSide && entity.washingTicks % 10 == 0 &&
                entity.washingTicks < TOTAL_WASHING_TICKS - 40) {
            spawnGoldGlitterInRiffles((ServerLevel) level, pos);
        }

        if (entity.washingTicks >= TOTAL_WASHING_TICKS) {
            float actualReduced = entity.startFillLevel - entity.targetFillLevel;
            entity.totalMaterialWashed += actualReduced;

            System.out.println("Reduced: " + actualReduced + ", Total washed: " + entity.totalMaterialWashed);

            entity.fillLevel = entity.targetFillLevel;
            entity.isWashing = false;
            entity.washingTicks = 0;

            if (entity.totalMaterialWashed >= 0.18f) {
                int materialPoints = Math.round(entity.totalMaterialWashed * 100);
                System.out.println("Adding " + materialPoints + " points, resetting totalMaterialWashed");

                addMaterialPointsToMats(level, pos, materialPoints);
                entity.totalMaterialWashed = 0f;
            }

            entity.notifyWashplantBlocks(false);
            if (!level.isClientSide) {
                PacketDistributor.sendToPlayersTrackingChunk((ServerLevel) level,
                        new net.minecraft.world.level.ChunkPos(pos),
                        new SyncWashplantFillPacket(pos, entity.fillLevel, entity.isWashing));
            }
        }

        entity.setChanged();
    }

    private static void addMaterialPointsToMats(Level level, BlockPos headPos, int points) {
        if (level.isClientSide || points <= 0) return;

        ServerLevel serverLevel = (ServerLevel) level;
        BlockPos below = headPos.below();

        WashplantHeadBlockEntity headEntity = (WashplantHeadBlockEntity) level.getBlockEntity(headPos);
        float totalGold = 0f;
        if (headEntity != null && !headEntity.goldQueue.isEmpty()) {
            totalGold = headEntity.goldQueue.poll(); // erstes Element nehmen und entfernen
            headEntity.setChanged();
        }

        // Sammle alle Matten
        java.util.List<Object> mats = new java.util.ArrayList<>();
        for (int i = 0; i < 3; i++) {
            BlockPos basePos = below.north(i);
            BlockEntity entity = level.getBlockEntity(basePos);
            if (entity instanceof WashplantBaseBlockEntity baseEntity && baseEntity.hasMat()) {
                mats.add(baseEntity);
            }
        }
        for (int i = 3; i < 6; i++) {
            BlockPos extPos = below.north(i);
            BlockEntity entity = level.getBlockEntity(extPos);
            if (entity instanceof WashplantExtensionBlockEntity extEntity && extEntity.hasMat()) {
                mats.add(extEntity);
            }
        }

        int matCount = mats.size();
        if (matCount == 0) return;

        // Zufällige Gewichte ~20% ± 10% pro Matte
        java.util.Random random = new java.util.Random();
        float[] weights = new float[matCount];
        float weightSum = 0f;
        for (int i = 0; i < matCount; i++) {
            weights[i] = 0.1f + random.nextFloat() * 0.2f; // 0.1 bis 0.3
            weightSum += weights[i];
        }

        // Normalisieren damit Summe = 1.0
        for (int i = 0; i < matCount; i++) {
            weights[i] /= weightSum;
        }

        // Punkte und Gold verteilen
        for (int i = 0; i < matCount; i++) {
            float goldShare = totalGold * weights[i];
            Object mat = mats.get(i);

            if (mat instanceof WashplantBaseBlockEntity baseEntity) {
                baseEntity.addMatMaterialPoints(points);
                baseEntity.addMatGoldGrams(goldShare);
                System.out.println("[MAT] Gold zu Matte " + i + ": +" + goldShare + "g (Anteil: " + String.format("%.1f", weights[i]*100) + "%)");
                int newPoints = baseEntity.getMatMaterialPoints();
                int newCycles = Math.min(6, newPoints / 100);
                baseEntity.setMatWashCycles(newCycles);

                PacketDistributor.sendToPlayersTrackingChunk(serverLevel,
                        new net.minecraft.world.level.ChunkPos(baseEntity.getBlockPos()),
                        new SyncWashplantMatPacket(baseEntity.getBlockPos(), true, newCycles, newPoints, false));

            } else if (mat instanceof WashplantExtensionBlockEntity extEntity) {
                extEntity.addMatMaterialPoints(points);
                extEntity.addMatGoldGrams(goldShare);
                System.out.println("[MAT] Gold zu Matte " + i + ": +" + goldShare + "g (Anteil: " + String.format("%.1f", weights[i]*100) + "%)");
                int newPoints = extEntity.getMatMaterialPoints();
                int newCycles = Math.min(6, newPoints / 100);
                extEntity.setMatWashCycles(newCycles);

                PacketDistributor.sendToPlayersTrackingChunk(serverLevel,
                        new net.minecraft.world.level.ChunkPos(extEntity.getBlockPos()),
                        new SyncWashplantMatPacket(extEntity.getBlockPos(), true, newCycles, newPoints, true));
            }
        }

    }

    private static void spawnWaterParticles(ServerLevel level, BlockPos headPos) {
        BlockPos below = headPos.below();

        BlockPos lastBlockPos = below.north(2);

        for (int i = 3; i < 6; i++) {
            BlockPos extPos = below.north(i);
            if (level.getBlockEntity(extPos) instanceof WashplantExtensionBlockEntity) {
                lastBlockPos = extPos;
            } else {
                break;
            }
        }

        double xNord = lastBlockPos.getX() + 0.3 + level.random.nextDouble() * 0.4;
        double yNord = lastBlockPos.getY() + 7.0 / 16.0;
        double zNord = lastBlockPos.getZ() - 0.1;

        for (int i = 0; i < 3; i++) {
            level.sendParticles(
                    new BlockParticleOption(ParticleTypes.BLOCK, Blocks.DIRT.defaultBlockState()),
                    xNord + (level.random.nextDouble() - 0.5) * 0.3,
                    yNord,
                    zNord,
                    1,
                    0, -0.1, 0,
                    0.02
            );
        }

        BlockPos firstBlockPos = below;

        double xSued = firstBlockPos.getX() + 0.3 + level.random.nextDouble() * 0.4;
        double ySued = firstBlockPos.getY() + 7.0 / 16.0;
        double zSued = firstBlockPos.getZ() + 1.1;

        for (int i = 0; i < 3; i++) {
            level.sendParticles(
                    new BlockParticleOption(ParticleTypes.BLOCK, Blocks.DIRT.defaultBlockState()),
                    xSued + (level.random.nextDouble() - 0.5) * 0.3,
                    ySued,
                    zSued,
                    1,
                    0, -0.1, 0,
                    0.02
            );
        }
    }

    private static void spawnGoldGlitterInRiffles(ServerLevel level, BlockPos headPos) {
        BlockPos below = headPos.below();

        for (int i = 0; i < 6; i++) {
            BlockPos blockPos = below.north(i);
            BlockEntity entity = level.getBlockEntity(blockPos);

            if (entity instanceof WashplantBaseBlockEntity ||
                    entity instanceof WashplantExtensionBlockEntity) {

                double x = blockPos.getX() + 0.2 + level.random.nextDouble() * 0.6;
                double y = blockPos.getY() + 7.0 / 16.0 + 0.1;
                double z = blockPos.getZ() + level.random.nextDouble();

                level.sendParticles(
                        ParticleTypes.WAX_ON,
                        x, y, z,
                        1,
                        0, 0, 0,
                        0.0
                );
            }
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        tag.putFloat("fillLevel", fillLevel);
        tag.putBoolean("isWashing", isWashing);
        tag.putFloat("targetFillLevel", targetFillLevel);
        tag.putFloat("startFillLevel", startFillLevel);
        tag.putInt("washingTicks", washingTicks);
        tag.putFloat("totalMaterialWashed", totalMaterialWashed);
        net.minecraft.nbt.ListTag goldList = new net.minecraft.nbt.ListTag();
        for (float g : goldQueue) {
            goldList.add(net.minecraft.nbt.FloatTag.valueOf(g));
        }
        tag.put("goldQueue", goldList);

    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        fillLevel = tag.getFloat("fillLevel");
        isWashing = tag.getBoolean("isWashing");
        targetFillLevel = tag.getFloat("targetFillLevel");
        startFillLevel = tag.getFloat("startFillLevel");
        washingTicks = tag.getInt("washingTicks");
        totalMaterialWashed = tag.getFloat("totalMaterialWashed");
        goldQueue.clear();
        net.minecraft.nbt.ListTag goldList = tag.getList("goldQueue", net.minecraft.nbt.Tag.TAG_FLOAT);
        for (int i = 0; i < goldList.size(); i++) {
            goldQueue.add(goldList.getFloat(i));
        }
    }


    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        tag.putFloat("fillLevel", fillLevel);
        tag.putBoolean("isWashing", isWashing);
        return tag;
    }

    public float getTotalGoldInHead() {
        return goldQueue.stream().reduce(0f, Float::sum);
    }

    public void addGoldToHead(float gold) {
        goldQueue.add(gold);
        setChanged();
    }

}