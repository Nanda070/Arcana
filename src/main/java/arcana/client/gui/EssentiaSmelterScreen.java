package arcana.client.gui;

import arcana.common.menu.EssentiaSmelterMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class EssentiaSmelterScreen extends AbstractContainerScreen<EssentiaSmelterMenu> {
    private static final ResourceLocation BG = new ResourceLocation("arcana", "textures/gui/essentia_smelter.png");

    public EssentiaSmelterScreen(EssentiaSmelterMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        graphics.blit(BG, x, y, 0, 0, imageWidth, imageHeight);

        if (menu.getBurnTime() > 0 && menu.getBurnTimeTotal() > 0) {
            int h = menu.getBurnTime() * 13 / menu.getBurnTimeTotal();
            graphics.fill(x + 56, y + 36 + 12 - h, x + 70, y + 49, 0xFFE08040);
        }
        if (menu.getCookTimeTotal() > 0) {
            int cook = menu.getCookTime() * 24 / menu.getCookTimeTotal();
            if (cook > 0) {
                graphics.fill(x + 79, y + 34, x + 79 + cook + 1, y + 50, 0xFF80A0E0);
            }
        }
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        super.renderLabels(graphics, mouseX, mouseY);
        graphics.drawString(font, "Essentia: " + menu.getVisStored() + "/" + 256, 90, 56, 0x4040A0, false);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
    }
}
