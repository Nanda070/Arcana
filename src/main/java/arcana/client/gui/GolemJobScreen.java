package arcana.client.gui;

import arcana.common.menu.GolemJobMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class GolemJobScreen extends AbstractContainerScreen<GolemJobMenu> {
    private static final ResourceLocation BG = new ResourceLocation("arcana", "textures/gui/golem_job.png");

    public GolemJobScreen(GolemJobMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    @Override
    protected void init() {
        super.init();
        int x = leftPos + 18;
        int y = topPos + 28;
        addJobButton(x, y, "Idle", GolemJobMenu.BTN_IDLE);
        addJobButton(x + 70, y, "Gather", GolemJobMenu.BTN_GATHER);
        addJobButton(x, y + 24, "Guard", GolemJobMenu.BTN_GUARD);
        addJobButton(x + 70, y + 24, "Fill", GolemJobMenu.BTN_FILL);
        addJobButton(x + 35, y + 48, "Empty", GolemJobMenu.BTN_EMPTY);
    }

    private void addJobButton(int x, int y, String label, int buttonId) {
        addRenderableWidget(Button.builder(Component.literal(label), b -> {
            if (minecraft != null && minecraft.gameMode != null) {
                minecraft.gameMode.handleInventoryButtonClick(menu.containerId, buttonId);
            }
        }).bounds(x, y, 64, 20).build());
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        graphics.blit(BG, leftPos, topPos, 0, 0, imageWidth, imageHeight);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 0xE2C07A, false);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
    }
}
