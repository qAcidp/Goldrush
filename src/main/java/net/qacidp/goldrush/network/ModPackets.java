package net.qacidp.goldrush.network;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import static net.qacidp.goldrush.Goldrush.MODID;
import net.qacidp.goldrush.network.SyncWashplantFillPacket;
import net.qacidp.goldrush.network.SyncWashplantBasePacket;
import net.qacidp.goldrush.network.SyncWavetablePacket;

@EventBusSubscriber(modid = MODID)
public class ModPackets {

    @SubscribeEvent
    public static void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");

        registrar.playToClient(
                SyncPlayerGoldPacket.TYPE,
                SyncPlayerGoldPacket.STREAM_CODEC,
                SyncPlayerGoldPacket::handle
        );

        registrar.playToClient(
                SyncWashplantFillPacket.TYPE,
                SyncWashplantFillPacket.STREAM_CODEC,
                SyncWashplantFillPacket::handle
        );

        registrar.playToClient(
                SyncWashplantBasePacket.TYPE,
                SyncWashplantBasePacket.STREAM_CODEC,
                SyncWashplantBasePacket::handle
        );

        registrar.playToClient(
                SyncWashplantMatPacket.TYPE,
                SyncWashplantMatPacket.STREAM_CODEC,
                SyncWashplantMatPacket::handle
        );

        registrar.playToClient(
                SyncWavetablePacket.TYPE,
                SyncWavetablePacket.STREAM_CODEC,
                SyncWavetablePacket::handle
        );

    }
}