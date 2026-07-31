package arcana.client.gui;

import arcana.api.casters.FocusPackage;
import arcana.common.menu.FocalManipulatorMenu;
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
    }

    @Override
    protected void init() {
        super.init();
        int x = leftPos;
        int y = topPos;
        // Effect-then-medium order is allowed; buttons stay independent
        addButton(x + 8, y + 36, 40, "Touch", FocalManipulatorMenu.BTN_TOUCH);
        addButton(x + 50, y + 36, 40, "Proj", FocalManipulatorMenu.BTN_PROJECTILE);
        addButton(x + 108, y + 36, 60, "Compose", FocalManipulatorMenu.BTN_COMPOSE);
        addButton(x + 8, y + 56, 30, "Fire", FocalManipulatorMenu.BTN_FIRE);
        addButton(x + 40, y + 56, 34, "Frost", FocalManipulatorMenu.BTN_FROST);
        addButton(x + 76, y + 56, 34, "Shock", FocalManipulatorMenu.BTN_SHOCK);
        addButton(x + 112, y + 56, 34, "Earth", FocalManipulatorMenu.BTN_EARTH);
        addButton(x + 148, y + 56, 20, "H", FocalManipulatorMenu.BTN_HEAL);
    }

    private void addButton(int x, int y, int w, String label, int buttonId) {
        addRenderableWidget(Button.builder(Component.literal(label), b -> {
            if (minecraft != null && minecraft.gameMode != null) {
                minecraft.gameMode.handleInventoryButtonClick(menu.containerId, buttonId);
            }
        }).bounds(x, y, w, 16).build());
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        graphics.blit(BG, leftPos, topPos, 0, 0, imageWidth, imageHeight);
        drawFocusGraph(graphics);
    }

    /** I4: ROOT → medium → effect from selection or programmed package. */
    private void drawFocusGraph(GuiGraphics graphics) {
        FocusPackage pkg = menu.getFocusPackage();
        String medium = menu.getBlockEntity().getSelectedMedium();
        String effect = menu.getBlockEntity().getSelectedEffect();
        if (pkg != null && !pkg.getNodes().isEmpty()) {
            medium = pkg.getMedium();
            effect = pkg.getEffect();
        }
        int my = topPos + 16;
        int rootX = leftPos + 18;
        int medX = leftPos + 68;
        int effX = leftPos + 118;
        int boxW = 36;
        int boxH = 12;

        int lineY = my + boxH / 2;
        graphics.fill(rootX + boxW, lineY - 1, medX, lineY + 1, 0xFF886644);
        graphics.fill(medX + boxW, lineY - 1, effX, lineY + 1, 0xFF886644);

        drawNode(graphics, rootX, my, boxW, boxH, "ROOT", 0xFF5A4A2A);
        drawNode(graphics, medX, my, boxW, boxH,
                FocusPackage.MEDIUM_PROJECTILE.equals(medium) ? "PROJ" : "TOUCH",
                0xFF3A5A7A);
        String effLabel = switch (effect) {
            case FocusPackage.EFFECT_FROST -> "FROST";
            case FocusPackage.EFFECT_SHOCK -> "SHOCK";
            case FocusPackage.EFFECT_EARTH -> "EARTH";
            case FocusPackage.EFFECT_HEAL -> "HEAL";
            default -> "FIRE";
        };
        drawNode(graphics, effX, my, boxW, boxH, effLabel, 0xFF6A3A5A);
    }

    private void drawNode(GuiGraphics graphics, int x, int y, int w, int h, String label, int fill) {
        graphics.fill(x - 1, y - 1, x + w + 1, y + h + 1, 0xFFE2C07A);
        graphics.fill(x, y, x + w, y + h, fill);
        graphics.drawCenteredString(this.font, label, x + w / 2, y + 2, 0xFFECECEC);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(this.font, this.title.getString() + " [" + menu.getBlockEntity().selectionLabel() + "]",
                this.titleLabelX, this.titleLabelY, 0x404040, false);
        String complexity = menu.getComplexityMax() > 0
                ? menu.getComplexityUsed() + "/" + menu.getComplexityMax()
                : String.valueOf(menu.getComplexityUsed());
        graphics.drawString(this.font, "Complexity " + complexity, this.titleLabelX, this.titleLabelY + 10, 0x5A3A7A, false);
        graphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 0x404040, false);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
    }
}
