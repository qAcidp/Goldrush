package net.qacidp.goldrush.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.PacketDistributor;
import net.qacidp.goldrush.block.ModBlocks;
import net.qacidp.goldrush.block.wavetable.WavetableBlock;
import net.qacidp.goldrush.data.ModAttachments;
import net.qacidp.goldrush.data.PlayerGoldData;
import net.qacidp.goldrush.item.ModItems;
import net.qacidp.goldrush.network.SyncPlayerGoldPacket;
import net.qacidp.goldrush.network.SyncWavetablePacket;

public class WavetableBlockEntity extends BlockEntity {

    private static final int TOTAL_PROCESSING_TICKS = 200; // 10 Sekunden

    private boolean isProcessing = false;
    private int processingTicks = 0;
    private float goldGramsToProcess = 0f;
    private float goldGramsDispensed = 0f;
    private java.util.UUID ownerUUID = null;

    public WavetableBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.WAVETABLE_BLOCK_ENTITY.get(), pos, state);
    }

    public boolean isProcessing() { return isProcessing; }
    public int getProcessingTicks() { return processingTicks; }
    public float getGoldGramsToProcess() { return goldGramsToProcess; }

    public void startProcessing(float goldGrams, java.util.UUID playerUUID) {
        this.isProcessing = true;
        this.processingTicks = 0;
        this.goldGramsToProcess = goldGrams;
        this.goldGramsDispensed = 0f;
        this.ownerUUID = playerUUID;
        setChanged();
    }

    public static void tick(Level level, BlockPos pos, BlockState state, WavetableBlockEntity entity) {
        if (!entity.isProcessing) return;
        if (level.isClientSide) return;

        entity.processingTicks++;

        // Partikel alle 5 Ticks
        if (entity.processingTicks % 5 == 0) {
            PacketDistributor.sendToPlayersTrackingChunk((ServerLevel) level,
                    new net.minecraft.world.level.ChunkPos(pos),
                    new SyncWavetablePacket(pos, true, entity.processingTicks));
            spawnWaterParticles((ServerLevel) level, pos);
        }

        // Gold gleichmäßig über 200 Ticks verteilen
        float goldPerTick = entity.goldGramsToProcess / TOTAL_PROCESSING_TICKS;
        float goldThisTick = goldPerTick;

        // Spieler finden und Gold geben
        if (entity.ownerUUID != null) {
            net.minecraft.world.entity.player.Player player = level.getPlayerByUUID(entity.ownerUUID);
            if (player instanceof ServerPlayer serverPlayer) {
                PlayerGoldData goldData = serverPlayer.getData(ModAttachments.PLAYER_GOLD);
                goldData.addGold(goldThisTick);
                entity.goldGramsDispensed += goldThisTick;
                PacketDistributor.sendToPlayer(serverPlayer,
                        new SyncPlayerGoldPacket(goldData.getTotalGold()));
            }
        }

        // Fertig
        if (entity.processingTicks >= TOTAL_PROCESSING_TICKS) {
            entity.isProcessing = false;
            entity.processingTicks = 0;
            entity.goldGramsToProcess = 0f;
            entity.goldGramsDispensed = 0f;

            PacketDistributor.sendToPlayersTrackingChunk((ServerLevel) level,
                    new net.minecraft.world.level.ChunkPos(pos),
                    new SyncWavetablePacket(pos, false, 0));
            System.out.println("[WAVETABLE] Verarbeitung fertig. Gesamt ausgezahlt: " + entity.goldGramsDispensed + "g");
        }

        entity.setChanged();
    }

    private static void spawnWaterParticles(ServerLevel level, BlockPos pos) {
        for (int i = 0; i < 2; i++) {
            double x = pos.getX() + 0.2 + level.random.nextDouble() * 0.6;
            double y = pos.getY() + 16.0 / 16.0;
            double z = pos.getZ() + 0.2 + level.random.nextDouble() * 0.6;

            level.sendParticles(ParticleTypes.DRIPPING_WATER, x, y, z, 1, 0, 0, 0, 0.1);
        }
    }

    public void setProcessingClient(boolean processing, int ticks) {
        this.isProcessing = processing;
        this.processingTicks = ticks;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        tag.putBoolean("isProcessing", isProcessing);
        tag.putInt("processingTicks", processingTicks);
        tag.putFloat("goldGramsToProcess", goldGramsToProcess);
        tag.putFloat("goldGramsDispensed", goldGramsDispensed);
        if (ownerUUID != null) {
            tag.putUUID("ownerUUID", ownerUUID);
        }
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        isProcessing = tag.getBoolean("isProcessing");
        processingTicks = tag.getInt("processingTicks");
        goldGramsToProcess = tag.getFloat("goldGramsToProcess");
        goldGramsDispensed = tag.getFloat("goldGramsDispensed");
        if (tag.hasUUID("ownerUUID")) {
            ownerUUID = tag.getUUID("ownerUUID");
        }
    }
}