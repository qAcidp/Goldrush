package net.qacidp.goldrush.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.qacidp.goldrush.data.ModAttachments;
import net.qacidp.goldrush.data.PlayerGoldData;

import static net.qacidp.goldrush.Goldrush.MODID;

public record SyncPlayerGoldPacket(float totalGold) implements CustomPacketPayload {

    public static final Type<SyncPlayerGoldPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(MODID, "sync_player_gold"));

    public static final StreamCodec<ByteBuf, SyncPlayerGoldPacket> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.FLOAT,
                    SyncPlayerGoldPacket::totalGold,
                    SyncPlayerGoldPacket::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(SyncPlayerGoldPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() != null) {
                PlayerGoldData goldData = context.player().getData(ModAttachments.PLAYER_GOLD);
                // Direktes Setzen statt addGold
                goldData.deserializeNBT(context.player().registryAccess(),
                        new net.minecraft.nbt.CompoundTag() {{
                            putFloat("totalGold", packet.totalGold());
                        }}
                );
            }
        });
    }
}