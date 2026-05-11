package net.qacidp.goldrush.client.hud;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.qacidp.goldrush.data.ModAttachments;
import net.qacidp.goldrush.data.PlayerGoldData;
import net.minecraft.resources.ResourceLocation;

import static net.qacidp.goldrush.Goldrush.MODID;

@EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
public class GoldHudOverlay {

    @SubscribeEvent
    public static void registerOverlay(RegisterGuiLayersEvent event) {
        event.registerAbove(VanillaGuiLayers.HOTBAR,
                ResourceLocation.fromNamespaceAndPath(MODID, "gold_display"),
                (guiGraphics, deltaTracker) -> {
                    renderGoldOverlay(guiGraphics, deltaTracker);
                });
    }

    private static void renderGoldOverlay(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;

        if (player == null) return;

        PlayerGoldData goldData = player.getData(ModAttachments.PLAYER_GOLD);
        float totalGold = goldData.getTotalGold();

        // Position oben links
        int x = 10;
        int y = 10;

        // Hintergrund (halbtransparentes Schwarz)
        guiGraphics.fill(x - 2, y - 2, x + 120, y + 12, 0x80000000);

        // Nach dem Hintergrund, vor dem Text:
        guiGraphics.pose().pushPose();
        guiGraphics.pose().scale(0.5f, 0.5f, 0.5f);
        guiGraphics.renderItem(new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.GOLD_NUGGET), (x - 6) * 2, (y - 6) * 2);
        guiGraphics.pose().popPose();

        // Gold-Text
        String goldText = String.format("§6Gold: §e%.2fg", totalGold);
        guiGraphics.drawString(mc.font, goldText, x, y, 0xFFFFFF, true);
    }
}