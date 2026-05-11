package net.qacidp.goldrush.event;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.qacidp.goldrush.command.GoldCommands;

import static net.qacidp.goldrush.Goldrush.MODID;

@EventBusSubscriber(modid = MODID)
public class CommandEvents {

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        GoldCommands.register(event.getDispatcher());
    }
}