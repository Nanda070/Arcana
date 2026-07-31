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
        this.imageHeight = 186;
    }

    @Override
    protected void init() {
        super.init();
        int x = leftPos + 10;
        int y = topPos + 20;
        addJobButton(x, y, "Idle", GolemJobMenu.BTN_IDLE);
        addJobButton(x + 82, y, "Gather", GolemJobMenu.BTN_GATHER);
        addJobButton(x, y + 22, "Guard", GolemJobMenu.BTN_GUARD);
        addJobButton(x + 82, y + 22, "Fill", GolemJobMenu.BTN_FILL);
        addJobButton(x, y + 44, "Empty", GolemJobMenu.BTN_EMPTY);
        addJobButton(x + 82, y + 44, "Harvest", GolemJobMenu.BTN_HARVEST);
        addJobButton(x, y + 66, "Use", GolemJobMenu.BTN_USE);
        addJobButton(x + 82, y + 66, "Butcher", GolemJobMenu.BTN_BUTCHER);
    }

    private void addJobButton(int x, int y, String label, int buttonId) {
        addRenderableWidget(Button.builder(Component.literal(label), b -> {
            if (minecraft != null && minecraft.gameMode != null) {
                minecraft.gameMode.handleInventoryButtonClick(menu.containerId, buttonId);
            }
        }).bounds(x, y, 76, 20).build());
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        graphics.blit(BG, leftPos, topPos, 0, 0, imageWidth, Math.min(imageHeight, 166));
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
