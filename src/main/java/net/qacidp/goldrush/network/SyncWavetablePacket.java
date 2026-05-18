package net.qacidp.goldrush.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.qacidp.goldrush.block.entity.WavetableBlockEntity;

import static net.qacidp.goldrush.Goldrush.MODID;

public record SyncWavetablePacket(BlockPos pos, boolean isProcessing, int processingTicks) implements CustomPacketPayload {

    public static final Type<SyncWavetablePacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(MODID, "sync_wavetable"));

    public static final StreamCodec<ByteBuf, SyncWavetablePacket> STREAM_CODEC =
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC, SyncWavetablePacket::pos,
                    ByteBufCodecs.BOOL, SyncWavetablePacket::isProcessing,
                    ByteBufCodecs.INT, SyncWavetablePacket::processingTicks,
                    SyncWavetablePacket::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(SyncWavetablePacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() != null && context.player().level() != null) {
                BlockEntity entity = context.player().level().getBlockEntity(packet.pos());
                if (entity instanceof WavetableBlockEntity wavetable) {
                    wavetable.setProcessingClient(packet.isProcessing(), packet.processingTicks());
                }
            }
        });
    }
}