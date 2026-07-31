package arcana.client.gui;

import arcana.api.casters.FocusPackage;
import arcana.common.menu.FocalManipulatorMenu;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class FocalManipulatorScreen extends AbstractContainerScreen<FocalManipulatorMenu> {
    private static final ResourceLocation BG = new ResourceLocation("arcana", "textures/gui/focal_manipulator.png");

    public FocalManipulatorScreen(FocalManipulatorMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageHeight = 166;
    }

    @Override
    protected void init() {
        super.init();
        int x = leftPos;
        int y = topPos;
        // Media row
        addButton(x + 4, y + 34, 28, "Tch", FocalManipulatorMenu.BTN_TOUCH);
        addButton(x + 34, y + 34, 28, "Prj", FocalManipulatorMenu.BTN_PROJECTILE);
        addButton(x + 64, y + 34, 28, "Blt", FocalManipulatorMenu.BTN_BOLT);
        addButton(x + 94, y + 34, 28, "Cld", FocalManipulatorMenu.BTN_CLOUD);
        addButton(x + 126, y + 34, 46, "Compose", FocalManipulatorMenu.BTN_COMPOSE);
        // Effects row 1
        addButton(x + 4, y + 52, 24, "Fire", FocalManipulatorMenu.BTN_FIRE);
        addButton(x + 30, y + 52, 26, "Frst", FocalManipulatorMenu.BTN_FROST);
        addButton(x + 58, y + 52, 26, "Shk", FocalManipulatorMenu.BTN_SHOCK);
        addButton(x + 86, y + 52, 26, "Erth", FocalManipulatorMenu.BTN_EARTH);
        addButton(x + 114, y + 52, 24, "Heal", FocalManipulatorMenu.BTN_HEAL);
        addButton(x + 140, y + 52, 24, "Air", FocalManipulatorMenu.BTN_AIR);
        // Effects row 2
        addButton(x + 4, y + 68, 28, "Brk", FocalManipulatorMenu.BTN_BREAK);
        addButton(x + 34, y + 68, 28, "Crs", FocalManipulatorMenu.BTN_CURSE);
        addButton(x + 64, y + 68, 28, "Xch", FocalManipulatorMenu.BTN_EXCHANGE);
        addButton(x + 94, y + 68, 28, "Flx", FocalManipulatorMenu.BTN_FLUX);
        addButton(x + 124, y + 68, 28, "Rft", FocalManipulatorMenu.BTN_RIFT);
        addButton(x + 154, y + 68, 18, "Sct", FocalManipulatorMenu.BTN_SCATTER);
    }

    private void addButton(int x, int y, int w, String label, int buttonId) {
        addRenderableWidget(Button.builder(Component.literal(label), b -> {
            if (minecraft != null && minecraft.gameMode != null) {
                minecraft.gameMode.handleInventoryButtonClick(menu.containerId, buttonId);
            }
        }).bounds(x, y, w, 14).build());
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        graphics.blit(BG, leftPos, topPos, 0, 0, imageWidth, imageHeight);
        drawFocusGraph(graphics);
    }

    /** ROOT → medium → effect(s); expands beyond 3 boxes for multi-effect packages. */
    private void drawFocusGraph(GuiGraphics graphics) {
        List<String> graphNodes = new ArrayList<>();
        graphNodes.add("ROOT");

        FocusPackage programmed = menu.getFocusPackage();
        String medium;
        List<String> effects;
        if (programmed != null && !programmed.getNodes().isEmpty()) {
            medium = programmed.getMedium();
            effects = programmed.getEffectNodes();
        } else {
            medium = menu.getBlockEntity().getSelectedMedium();
            effects = menu.getBlockEntity().getSelectedEffects();
        }
        graphNodes.add(FocusPackage.shortLabel(medium));
        if (programmed != null && programmed.hasNode(FocusPackage.MOD_SCATTER)) {
            graphNodes.add("SCT");
        } else if (programmed == null && menu.getBlockEntity().isScatterEnabled()) {
            graphNodes.add("SCT");
        }
        if (effects.isEmpty()) {
            graphNodes.add("FIRE");
        } else {
            for (String e : effects) {
                graphNodes.add(FocusPackage.shortLabel(e));
            }
        }

        int boxW = Math.min(32, Math.max(22, 150 / Math.max(3, graphNodes.size())));
        int boxH = 10;
        int my = topPos + 6;
        int startX = leftPos + 8;
        int gap = 4;
        int totalW = graphNodes.size() * boxW + (graphNodes.size() - 1) * gap;
        if (totalW > imageWidth - 16) {
            boxW = Math.max(18, (imageWidth - 16 - (graphNodes.size() - 1) * gap) / graphNodes.size());
            totalW = graphNodes.size() * boxW + (graphNodes.size() - 1) * gap;
        }
        int x = startX + Math.max(0, (imageWidth - 16 - totalW) / 2);

        for (int i = 0; i < graphNodes.size(); i++) {
            int color = i == 0 ? 0xFF5A4A2A : (i == 1 ? 0xFF3A5A7A : 0xFF6A3A5A);
            drawNode(graphics, x, my, boxW, boxH, graphNodes.get(i), color);
            if (i < graphNodes.size() - 1) {
                int lineY = my + boxH / 2;
                graphics.fill(x + boxW, lineY - 1, x + boxW + gap, lineY + 1, 0xFF886644);
            }
            x += boxW + gap;
        }
    }

    private void drawNode(GuiGraphics graphics, int x, int y, int w, int h, String label, int fill) {
        graphics.fill(x - 1, y - 1, x + w + 1, y + h + 1, 0xFFE2C07A);
        graphics.fill(x, y, x + w, y + h, fill);
        String text = label.length() > 5 ? label.substring(0, 5) : label;
        graphics.drawCenteredString(this.font, text, x + w / 2, y + 1, 0xFFECECEC);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(this.font, this.title.getString(),
                this.titleLabelX, this.titleLabelY, 0x404040, false);
        String complexity = menu.getComplexityMax() > 0
                ? menu.getComplexityUsed() + "/" + menu.getComplexityMax()
                : String.valueOf(menu.getComplexityUsed());
        graphics.drawString(this.font, "Cx " + complexity + "  " + menu.getBlockEntity().selectionLabel(),
                this.titleLabelX, this.inventoryLabelY - 10, 0x5A3A7A, false);
        graphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 0x404040, false);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
    }
}
