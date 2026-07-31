package arcana.client.gui;

import arcana.api.aspects.Aspect;
import arcana.common.menu.WardedJarMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class WardedJarScreen extends AbstractContainerScreen<WardedJarMenu> {
    public WardedJarScreen(WardedJarMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        graphics.fill(x, y, x + imageWidth, y + imageHeight, 0xFF2A2030);
        graphics.fill(x + 4, y + 4, x + imageWidth - 4, y + 78, 0xFF3A3040);

        int amount = menu.getAmount();
        int capacity = menu.getCapacity();
        int barH = 52;
        int filled = amount <= 0 ? 0 : Math.max(1, amount * barH / capacity);
        Aspect aspect = menu.getJar().getAspect();
        int color = aspect == null ? 0xFF606070 : (0xFF000000 | aspect.getColor());
        int barX = x + 80;
        int barY = y + 18;
        graphics.fill(barX, barY, barX + 16, barY + barH, 0xFF101018);
        if (filled > 0) {
            graphics.fill(barX + 1, barY + barH - filled, barX + 15, barY + barH, color);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        super.renderLabels(graphics, mouseX, mouseY);
        Aspect aspect = menu.getJar().getAspect();
        String name = aspect == null ? Component.translatable("arcana.jar.empty").getString() : aspect.getName();
        graphics.drawString(font, name, 12, 20, 0xE0D0FF, false);
        graphics.drawString(font, menu.getAmount() + " / " + menu.getCapacity(), 12, 36, 0xA0A0C0, false);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
    }
}
