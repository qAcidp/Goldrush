package net.qacidp.goldrush.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.qacidp.goldrush.block.entity.WashplantBaseBlockEntity;
import net.qacidp.goldrush.block.entity.WashplantExtensionBlockEntity;

import static net.qacidp.goldrush.Goldrush.MODID;

public record SyncWashplantMatPacket(BlockPos pos, boolean hasMat, int washCycles, int materialPoints, boolean isExtension) implements CustomPacketPayload {

    public static final Type<SyncWashplantMatPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(MODID, "sync_washplant_mat"));

    public static final StreamCodec<ByteBuf, SyncWashplantMatPacket> STREAM_CODEC =
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC,
                    SyncWashplantMatPacket::pos,
                    ByteBufCodecs.BOOL,
                    SyncWashplantMatPacket::hasMat,
                    ByteBufCodecs.INT,
                    SyncWashplantMatPacket::washCycles,
                    ByteBufCodecs.INT,
                    SyncWashplantMatPacket::materialPoints,
                    ByteBufCodecs.BOOL,
                    SyncWashplantMatPacket::isExtension,
                    SyncWashplantMatPacket::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(SyncWashplantMatPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() != null && context.player().level() != null) {
                BlockEntity entity = context.player().level().getBlockEntity(packet.pos());

                if (packet.isExtension() && entity instanceof WashplantExtensionBlockEntity extEntity) {
                    extEntity.setHasMat(packet.hasMat());
                    extEntity.setMatWashCycles(packet.washCycles());
                    extEntity.setMatMaterialPoints(packet.materialPoints());
                } else if (!packet.isExtension() && entity instanceof WashplantBaseBlockEntity baseEntity) {
                    baseEntity.setHasMat(packet.hasMat());
                    baseEntity.setMatWashCycles(packet.washCycles());
                    baseEntity.setMatMaterialPoints(packet.materialPoints());
                }
            }
        });
    }
}