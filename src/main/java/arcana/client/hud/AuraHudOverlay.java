package arcana.client.hud;

import arcana.client.ClientAuraCache;
import arcana.common.items.casters.ItemCaster;
import arcana.common.lib.events.GogglesHelper;
import arcana.registry.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

public final class AuraHudOverlay implements IGuiOverlay {
    public static final AuraHudOverlay INSTANCE = new AuraHudOverlay();

    private AuraHudOverlay() {
    }

    @Override
    public void render(ForgeGui gui, GuiGraphics graphics, float partialTick, int screenWidth, int screenHeight) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null || mc.options.hideGui || !ClientAuraCache.isFresh()) {
            return;
        }
        if (!isAuraViewer(player.getMainHandItem())
                && !isAuraViewer(player.getOffhandItem())
                && !GogglesHelper.hasGoggles(player)) {
            return;
        }

        int x = 8;
        int y = screenHeight - 48;
        int barW = 90;
        int barH = 6;

        float vis = ClientAuraCache.getVis();
        float flux = ClientAuraCache.getFlux();
        short base = ClientAuraCache.getBase();
        float max = Math.max(base, Math.max(vis + flux, 1.0f));

        graphics.fill(x - 2, y - 12, x + barW + 2, y + barH + 14, 0x88000000);
        graphics.drawString(mc.font, String.format("Vis %.1f / %d", vis, (int) base), x, y - 10, 0x55FFFF, false);

        int visW = (int) (barW * Math.min(1.0f, vis / max));
        int fluxW = (int) (barW * Math.min(1.0f, flux / max));
        graphics.fill(x, y, x + barW, y + barH, 0xFF222222);
        graphics.fill(x, y, x + visW, y + barH, 0xFF33CCFF);
        if (fluxW > 0) {
            graphics.fill(x, y + barH + 2, x + fluxW, y + barH + 2 + barH, 0xFFAA44FF);
            graphics.drawString(mc.font, String.format("Flux %.1f", flux), x, y + barH + 4 + barH, 0xCC88FF, false);
        }
    }

    private static boolean isAuraViewer(ItemStack stack) {
        return stack.getItem() instanceof ItemCaster || stack.is(ModItems.THAUMOMETER.get());
    }
}
