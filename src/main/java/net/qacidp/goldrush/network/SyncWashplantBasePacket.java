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

public record SyncWashplantBasePacket(BlockPos pos, boolean isWashing, boolean isExtension) implements CustomPacketPayload {

    public static final Type<SyncWashplantBasePacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(MODID, "sync_washplant_base"));

    public static final StreamCodec<ByteBuf, SyncWashplantBasePacket> STREAM_CODEC =
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC,
                    SyncWashplantBasePacket::pos,
                    ByteBufCodecs.BOOL,
                    SyncWashplantBasePacket::isWashing,
                    ByteBufCodecs.BOOL,
                    SyncWashplantBasePacket::isExtension,
                    SyncWashplantBasePacket::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(SyncWashplantBasePacket packet, IPayloadContext context) {
        System.out.println("=== CLIENT: Received packet for pos=" + packet.pos() +
                ", washing=" + packet.isWashing() +
                ", isExtension=" + packet.isExtension() + " ===");

        context.enqueueWork(() -> {
            if (context.player() != null && context.player().level() != null) {
                BlockEntity entity = context.player().level().getBlockEntity(packet.pos());
                System.out.println("  Found entity: " + (entity != null ? entity.getClass().getSimpleName() : "NULL"));

                if (packet.isExtension() && entity instanceof WashplantExtensionBlockEntity extEntity) {
                    extEntity.setWashing(packet.isWashing());
                    System.out.println("  -> Extension washing set to: " + extEntity.isWashing());
                } else if (!packet.isExtension() && entity instanceof WashplantBaseBlockEntity baseEntity) {
                    baseEntity.setWashing(packet.isWashing());
                    System.out.println("  -> Base washing set to: " + baseEntity.isWashing());
                } else {
                    System.out.println("  -> MISMATCH or NULL!");
                }
            }
        });
    }
}