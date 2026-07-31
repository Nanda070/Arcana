package arcana.client.gui;

import arcana.api.research.theorycraft.ResearchTableData;
import arcana.common.menu.ResearchTableMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class ResearchTableScreen extends AbstractContainerScreen<ResearchTableMenu> {
    private static final ResourceLocation BG = new ResourceLocation("arcana", "textures/gui/research_table.png");

    private Button drawButton;
    private Button completeButton;
    private Button startButton;
    private Button abortButton;
    private Button noteButton;
    private final Button[] cardButtons = new Button[3];

    public ResearchTableScreen(ResearchTableMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageWidth = 176;
        this.imageHeight = 186;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void init() {
        super.init();
        int x = leftPos;
        int y = topPos;

        startButton = addRenderableWidget(Button.builder(Component.translatable("arcana.research_table.start"), b ->
                click(ResearchTableMenu.BTN_START)
        ).bounds(x + 8, y + 48, 50, 20).build());

        drawButton = addRenderableWidget(Button.builder(Component.translatable("arcana.research_table.draw"), b ->
                click(ResearchTableMenu.BTN_DRAW)
        ).bounds(x + 62, y + 48, 50, 20).build());

        completeButton = addRenderableWidget(Button.builder(Component.translatable("arcana.research_table.complete"), b ->
                click(ResearchTableMenu.BTN_COMPLETE)
        ).bounds(x + 116, y + 48, 52, 20).build());

        abortButton = addRenderableWidget(Button.builder(Component.translatable("arcana.research_table.abort"), b ->
                click(ResearchTableMenu.BTN_ABORT)
        ).bounds(x + 8, y + 70, 50, 16).build());

        noteButton = addRenderableWidget(Button.builder(Component.translatable("arcana.research_table.note"), b ->
                click(ResearchTableMenu.BTN_NOTE)
        ).bounds(x + 116, y + 70, 52, 16).build());

        for (int i = 0; i < 3; i++) {
            final int idx = i;
            cardButtons[i] = addRenderableWidget(Button.builder(Component.literal("—"), b ->
                    click(ResearchTableMenu.BTN_CARD_0 + idx)
            ).bounds(x + 8 + i * 56, y + 18, 52, 28).build());
        }
        refreshWidgets();
    }

    private void click(int btn) {
        if (minecraft != null && minecraft.gameMode != null) {
            minecraft.gameMode.handleInventoryButtonClick(menu.containerId, btn);
        }
    }

    private void refreshWidgets() {
        boolean session = menu.hasSession();
        boolean complete = menu.isTheoryComplete();
        int cards = menu.getCardCount();

        startButton.visible = !session;
        startButton.active = !session;
        drawButton.visible = session && !complete;
        drawButton.active = session && !complete && cards == 0;
        completeButton.visible = session && complete;
        completeButton.active = session && complete;
        abortButton.visible = session && !complete;
        noteButton.visible = true;

        ResearchTableData data = menu.getTable().data;
        for (int i = 0; i < 3; i++) {
            boolean show = session && !complete && data != null && i < data.cardChoices.size();
            cardButtons[i].visible = show;
            cardButtons[i].active = show;
            if (show) {
                String name = data.cardChoices.get(i).card.getLocalizedName();
                if (name.length() > 14) {
                    name = name.substring(0, 12) + "…";
                }
                cardButtons[i].setMessage(Component.literal(name));
            } else {
                cardButtons[i].setMessage(Component.literal("—"));
            }
        }
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        refreshWidgets();
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        graphics.blit(BG, x, y, 0, 0, imageWidth, Math.min(imageHeight, 166));
        if (imageHeight > 166) {
            graphics.fill(x, y + 166, x + imageWidth, y + imageHeight, 0xFFC6C6C6);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(font, title, titleLabelX, titleLabelY, 0x404040, false);
        if (menu.hasSession()) {
            graphics.drawString(font,
                    Component.translatable("arcana.research_table.inspiration",
                            menu.getInspiration(), menu.getInspirationStart()),
                    8, 6, 0x3F3F7F, false);
            if (menu.getBonusDraws() > 0) {
                graphics.drawString(font,
                        Component.translatable("arcana.research_table.bonus_draws", menu.getBonusDraws()),
                        100, 6, 0x5F3F00, false);
            }
        } else {
            graphics.drawString(font,
                    Component.translatable("arcana.research_table.no_session"),
                    8, 6, 0x5F5F5F, false);
        }

        ResearchTableData data = menu.getTable().data;
        if (data != null && !data.categoryTotals.isEmpty()) {
            int yy = 88;
            int shown = 0;
            for (var e : data.categoryTotals.entrySet()) {
                if (shown >= 2) {
                    break;
                }
                String cat = Component.translatable("tc.research_category." + e.getKey()).getString();
                graphics.drawString(font, cat + ": " + e.getValue() + "%", 8 + shown * 84, yy, 0x3F5F3F, false);
                shown++;
            }
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);

        ResearchTableData data = menu.getTable().data;
        if (data != null) {
            for (int i = 0; i < data.cardChoices.size() && i < 3; i++) {
                Button btn = cardButtons[i];
                if (btn.visible && mouseX >= btn.getX() && mouseX < btn.getX() + btn.getWidth()
                        && mouseY >= btn.getY() && mouseY < btn.getY() + btn.getHeight()) {
                    graphics.renderTooltip(font,
                            Component.literal(data.cardChoices.get(i).card.getLocalizedText()),
                            mouseX, mouseY);
                }
            }
        }
    }
}
