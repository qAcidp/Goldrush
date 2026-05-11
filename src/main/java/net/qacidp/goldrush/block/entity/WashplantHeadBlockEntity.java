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
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.world.level.block.Blocks;

public class WashplantHeadBlockEntity extends BlockEntity {

    private float fillLevel = 0f;
    private boolean isWashing = false;
    private float targetFillLevel = 0f;
    private int washingTicks = 0;
    private static final int TOTAL_WASHING_TICKS = 200; // 10 Sekunden (20 ticks/sec * 10)

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
        this.targetFillLevel = Math.max(0, fillLevel - amountToReduce);
        this.washingTicks = 0;
        setChanged();

        // Informiere Base/Extension Blöcke
        if (level != null && !level.isClientSide) {
            notifyWashplantBlocks(true);
        }
    }

    private void notifyWashplantBlocks(boolean washing) {
        if (level == null || level.isClientSide) return;

        System.out.println("=== SERVER: Notifying blocks, washing=" + washing + " ===");

        ServerLevel serverLevel = (ServerLevel) level;
        BlockPos below = worldPosition.below();
        System.out.println("Head at: " + worldPosition + ", Below at: " + below);

        for (int i = 0; i < 3; i++) {
            BlockPos basePos = below.north(i);
            BlockEntity entity = level.getBlockEntity(basePos);
            System.out.println("  [Base " + i + "] Position: " + basePos +
                    ", Entity: " + (entity != null ? entity.getClass().getSimpleName() : "NULL"));

            if (entity instanceof WashplantBaseBlockEntity baseEntity) {
                baseEntity.setWashing(washing);
                System.out.println("    -> Set washing to " + washing + ", now is: " + baseEntity.isWashing());

                PacketDistributor.sendToPlayersTrackingChunk(serverLevel,
                        new net.minecraft.world.level.ChunkPos(basePos),
                        new SyncWashplantBasePacket(basePos, washing, false));
                System.out.println("    -> Packet sent!");
            }
        }

        for (int i = 3; i < 6; i++) {
            BlockPos extPos = below.north(i);
            System.out.println("Checking extension at: " + extPos);
            BlockEntity entity = level.getBlockEntity(extPos);
            System.out.println("Extension entity: " + (entity != null ? entity.getClass().getSimpleName() : "NULL"));

            if (entity instanceof WashplantExtensionBlockEntity extEntity) {
                System.out.println("Setting extension washing!");
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
        float startLevel = entity.targetFillLevel + 0.2f;
        float newLevel = startLevel - (startLevel - entity.targetFillLevel) * progress;

        entity.fillLevel = newLevel;

        if (!level.isClientSide && entity.washingTicks % 5 == 0) {
            PacketDistributor.sendToPlayersTrackingChunk((ServerLevel) level,
                    new net.minecraft.world.level.ChunkPos(pos),
                    new SyncWashplantFillPacket(pos, entity.fillLevel, entity.isWashing));

            // Partikel am Ende der letzten Extension oder Base spawnen
            spawnWaterParticles((ServerLevel) level, pos);
        }

        if (entity.washingTicks >= TOTAL_WASHING_TICKS) {
            entity.fillLevel = entity.targetFillLevel;
            entity.isWashing = false;
            entity.washingTicks = 0;
            entity.notifyWashplantBlocks(false);

            if (!level.isClientSide) {
                PacketDistributor.sendToPlayersTrackingChunk((ServerLevel) level,
                        new net.minecraft.world.level.ChunkPos(pos),
                        new SyncWashplantFillPacket(pos, entity.fillLevel, entity.isWashing));
            }
        }

        if (!level.isClientSide && entity.washingTicks % 10 == 0 &&
                entity.washingTicks < TOTAL_WASHING_TICKS - 40) {
            spawnGoldGlitterInRiffles((ServerLevel) level, pos);
        }

        entity.setChanged();
    }

    private static void spawnWaterParticles(ServerLevel level, BlockPos headPos) {
        BlockPos below = headPos.below();

        // === NORD-SEITE (Ende der Rinne) ===
        BlockPos lastBlockPos = below.north(2);

        for (int i = 3; i < 6; i++) {
            BlockPos extPos = below.north(i);
            if (level.getBlockEntity(extPos) instanceof WashplantExtensionBlockEntity) {
                lastBlockPos = extPos;
            } else {
                break;
            }
        }

        // Partikel Nord-Ende
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

        // === SÜD-SEITE (unter dem Head, erster Base-Block) ===
        BlockPos firstBlockPos = below; // Der erste Base-Block direkt unter dem Head

        double xSued = firstBlockPos.getX() + 0.3 + level.random.nextDouble() * 0.4;
        double ySued = firstBlockPos.getY() + 7.0 / 16.0;
        double zSued = firstBlockPos.getZ() + 1.1; // Leicht außerhalb Süd-Seite

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

        // Spawn in allen Base/Extension Blöcken
        for (int i = 0; i < 6; i++) {
            BlockPos blockPos = below.north(i);
            BlockEntity entity = level.getBlockEntity(blockPos);

            if (entity instanceof WashplantBaseBlockEntity ||
                    entity instanceof WashplantExtensionBlockEntity) {

                // Random Position in der Rinne
                double x = blockPos.getX() + 0.2 + level.random.nextDouble() * 0.6;
                double y = blockPos.getY() + 7.0 / 16.0 + 0.1; // Leicht über Wasser
                double z = blockPos.getZ() + level.random.nextDouble();

                level.sendParticles(
                        ParticleTypes.WAX_ON, // Goldener Glitzer
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
        tag.putInt("washingTicks", washingTicks);
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        fillLevel = tag.getFloat("fillLevel");
        isWashing = tag.getBoolean("isWashing");
        targetFillLevel = tag.getFloat("targetFillLevel");
        washingTicks = tag.getInt("washingTicks");
    }

}