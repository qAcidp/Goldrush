package net.qacidp.goldrush.event;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.ChunkWatchEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.qacidp.goldrush.block.entity.WashplantBaseBlockEntity;
import net.qacidp.goldrush.block.entity.WashplantExtensionBlockEntity;
import net.qacidp.goldrush.block.entity.WashplantHeadBlockEntity;
import net.qacidp.goldrush.block.entity.WavetableBlockEntity;
import net.qacidp.goldrush.network.SyncWashplantFillPacket;
import net.qacidp.goldrush.network.SyncWashplantMatPacket;
import net.qacidp.goldrush.network.SyncWavetablePacket;

import static net.qacidp.goldrush.Goldrush.MODID;

@EventBusSubscriber(modid = MODID)
public class ChunkWatchEventHandler {

    @SubscribeEvent
    public static void onChunkWatch(ChunkWatchEvent.Sent event) {
        ServerLevel level = event.getLevel();
        ChunkPos chunkPos = event.getPos();
        LevelChunk chunk = level.getChunk(chunkPos.x, chunkPos.z);

        for (BlockEntity be : chunk.getBlockEntities().values()) {
            if (be instanceof WashplantHeadBlockEntity headEntity) {
                PacketDistributor.sendToPlayer(event.getPlayer(),
                        new SyncWashplantFillPacket(headEntity.getBlockPos(),
                                headEntity.getFillLevel(),
                                headEntity.isWashing()));
            } else if (be instanceof WashplantBaseBlockEntity baseEntity && baseEntity.hasMat()) {
                PacketDistributor.sendToPlayer(event.getPlayer(),
                        new SyncWashplantMatPacket(baseEntity.getBlockPos(), true,
                                baseEntity.getMatWashCycles(),
                                baseEntity.getMatMaterialPoints(), false));
            } else if (be instanceof WashplantExtensionBlockEntity extEntity && extEntity.hasMat()) {
                PacketDistributor.sendToPlayer(event.getPlayer(),
                        new SyncWashplantMatPacket(extEntity.getBlockPos(), true,
                                extEntity.getMatWashCycles(),
                                extEntity.getMatMaterialPoints(), true));

            } else if (be instanceof WavetableBlockEntity wavetable && wavetable.isProcessing()) {
            PacketDistributor.sendToPlayer(event.getPlayer(),
                    new SyncWavetablePacket(wavetable.getBlockPos(),
                            wavetable.isProcessing(),
                            wavetable.getProcessingTicks()));
        }

        }
    }
}