package arcana.client.gui;

import arcana.common.golems.GolemJob;
import arcana.common.menu.GolemJobMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;

/**
 * Scrollable 2-column job picker covering all {@link GolemJob} values.
 */
public class GolemJobScreen extends AbstractContainerScreen<GolemJobMenu> {
    private static final ResourceLocation BG = new ResourceLocation("arcana", "textures/gui/golem_job.png");
    private static final int VISIBLE_ROWS = 6;
    private static final int ROW_H = 18;
    private static final int BTN_W = 76;

    private int scroll;

    public GolemJobScreen(GolemJobMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    @Override
    protected void init() {
        super.init();
        rebuildButtons();
    }

    private void rebuildButtons() {
        clearWidgets();
        int x0 = leftPos + 10;
        int y0 = topPos + 18;
        GolemJob[] jobs = GolemJob.values();
        int maxScroll = Math.max(0, (jobs.length + 1) / 2 - VISIBLE_ROWS);
        scroll = Mth.clamp(scroll, 0, maxScroll);
        int start = scroll * 2;
        int end = Math.min(jobs.length, start + VISIBLE_ROWS * 2);
        for (int i = start; i < end; i++) {
            int local = i - start;
            int col = local % 2;
            int row = local / 2;
            GolemJob job = jobs[i];
            int bx = x0 + col * (BTN_W + 6);
            int by = y0 + row * ROW_H;
            final int jobId = job.ordinal();
            addRenderableWidget(Button.builder(Component.literal(job.shortLabel()), b -> {
                if (minecraft != null && minecraft.gameMode != null) {
                    minecraft.gameMode.handleInventoryButtonClick(menu.containerId, jobId);
                }
            }).bounds(bx, by, BTN_W, 16).build());
        }
        // scroll controls
        addRenderableWidget(Button.builder(Component.literal("<"), b -> {
            scroll = Math.max(0, scroll - 1);
            rebuildButtons();
        }).bounds(leftPos + 10, topPos + 130, 20, 16).build());
        addRenderableWidget(Button.builder(Component.literal(">"), b -> {
            scroll = Math.min(maxScroll, scroll + 1);
            rebuildButtons();
        }).bounds(leftPos + 146, topPos + 130, 20, 16).build());
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        GolemJob[] jobs = GolemJob.values();
        int maxScroll = Math.max(0, (jobs.length + 1) / 2 - VISIBLE_ROWS);
        int next = Mth.clamp(scroll - (int) Math.signum(delta), 0, maxScroll);
        if (next != scroll) {
            scroll = next;
            rebuildButtons();
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        graphics.blit(BG, leftPos, topPos, 0, 0, imageWidth, Math.min(imageHeight, 166));
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 0xE2C07A, false);
        graphics.drawString(this.font, "Scroll: " + (scroll + 1), 36, 134, 0xAAAAAA, false);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
    }
}
