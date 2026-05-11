package net.qacidp.goldrush.data;

import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

import static net.qacidp.goldrush.Goldrush.MODID;

public class ModAttachments {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, MODID);

    public static final Supplier<AttachmentType<PlayerGoldData>> PLAYER_GOLD =
            ATTACHMENT_TYPES.register("player_gold",
                    () -> AttachmentType.serializable(PlayerGoldData::new).build());

    public static void register(net.neoforged.bus.api.IEventBus modEventBus) {
        ATTACHMENT_TYPES.register(modEventBus);
    }
}