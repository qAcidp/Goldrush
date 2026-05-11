package net.qacidp.goldrush.event;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.qacidp.goldrush.data.ModAttachments;
import net.qacidp.goldrush.data.PlayerGoldData;
import net.qacidp.goldrush.network.SyncPlayerGoldPacket;

import static net.qacidp.goldrush.Goldrush.MODID;

@EventBusSubscriber(modid = MODID)
public class PlayerEvents {

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            PlayerGoldData goldData = player.getData(ModAttachments.PLAYER_GOLD);

            // Synchronisiere Gold zum Client beim Login
            PacketDistributor.sendToPlayer(player, new SyncPlayerGoldPacket(goldData.getTotalGold()));
        }
    }
}