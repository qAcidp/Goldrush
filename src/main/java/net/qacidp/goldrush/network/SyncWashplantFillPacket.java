package net.qacidp.goldrush.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.qacidp.goldrush.block.entity.WashplantHeadBlockEntity;

import static net.qacidp.goldrush.Goldrush.MODID;

public record SyncWashplantFillPacket(BlockPos pos, float fillLevel, boolean isWashing) implements CustomPacketPayload {

    public static final Type<SyncWashplantFillPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(MODID, "sync_washplant_fill"));

    public static final StreamCodec<ByteBuf, SyncWashplantFillPacket> STREAM_CODEC =
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC,
                    SyncWashplantFillPacket::pos,
                    ByteBufCodecs.FLOAT,
                    SyncWashplantFillPacket::fillLevel,
                    ByteBufCodecs.BOOL,
                    SyncWashplantFillPacket::isWashing,
                    SyncWashplantFillPacket::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(SyncWashplantFillPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() != null && context.player().level() != null) {
                BlockEntity entity = context.player().level().getBlockEntity(packet.pos());
                if (entity instanceof WashplantHeadBlockEntity headEntity) {
                    headEntity.setFillLevelClient(packet.fillLevel(), packet.isWashing());
                }
            }
        });
    }
}