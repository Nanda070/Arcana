package arcana.client;

import arcana.Arcana;
import arcana.api.aspects.Aspect;
import arcana.api.aspects.AspectList;
import arcana.common.items.ItemThaumometer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Arcana.MODID, value = Dist.CLIENT)
public final class ThaumometerOverlayEvents {
    private ThaumometerOverlayEvents() {
    }

    @SubscribeEvent
    public static void onRenderOverlay(RenderGuiOverlayEvent.Post event) {
        if (event.getOverlay() != VanillaGuiOverlay.CROSSHAIR.type()) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null || mc.options.hideGui) {
            return;
        }
        if (!holdingThaumometer(player) || !ClientScanTarget.hasAspects()) {
            return;
        }

        AspectList list = ClientScanTarget.aspects();
        GuiGraphics graphics = event.getGuiGraphics();
        int sw = event.getWindow().getGuiScaledWidth();
        int sh = event.getWindow().getGuiScaledHeight();
        int x = sw / 2 + 12;
        int y = sh / 2 - list.size() * 8;
        for (Aspect aspect : list.getAspectsSortedByAmount()) {
            AspectIconRenderer.blit(graphics, x, y, aspect, 14);
            graphics.drawString(mc.font, aspect.getName() + " x" + list.getAmount(aspect), x + 16, y + 3, 0xFFFFFF, true);
            y += 16;
        }
    }

    private static boolean holdingThaumometer(Player player) {
        ItemStack main = player.getMainHandItem();
        ItemStack off = player.getOffhandItem();
        return main.getItem() instanceof ItemThaumometer || off.getItem() instanceof ItemThaumometer;
    }
}
