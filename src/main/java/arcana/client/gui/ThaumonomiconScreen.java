package arcana.client.gui;

import arcana.api.aspects.Aspect;
import arcana.api.capabilities.ArcanaCapabilities;
import arcana.api.capabilities.IPlayerKnowledge;
import arcana.api.research.ResearchCategories;
import arcana.api.research.ResearchCategory;
import arcana.api.research.ResearchEntry;
import arcana.client.AspectIconRenderer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

public class ThaumonomiconScreen extends Screen {
    private final List<ResearchEntry> visible = new ArrayList<>();
    private final List<ResearchCategory> categories = new ArrayList<>();
    private int categoryIndex;
    private int scroll;

    public ThaumonomiconScreen() {
        super(Component.translatable("item.arcana.thaumonomicon"));
    }

    @Override
    protected void init() {
        categories.clear();
        categories.addAll(ResearchCategories.getCategories());
        if (categoryIndex >= categories.size()) {
            categoryIndex = 0;
        }
        rebuildList();
        addRenderableWidget(Button.builder(Component.literal("<"), b -> {
            if (!categories.isEmpty()) {
                categoryIndex = (categoryIndex - 1 + categories.size()) % categories.size();
                scroll = 0;
                rebuildList();
            }
        }).bounds(width / 2 - 140, 38, 20, 20).build());
        addRenderableWidget(Button.builder(Component.literal(">"), b -> {
            if (!categories.isEmpty()) {
                categoryIndex = (categoryIndex + 1) % categories.size();
                scroll = 0;
                rebuildList();
            }
        }).bounds(width / 2 + 120, 38, 20, 20).build());
        addRenderableWidget(Button.builder(Component.literal("▲"), b -> {
            scroll = Math.max(0, scroll - 1);
        }).bounds(width - 40, 40, 20, 20).build());
        addRenderableWidget(Button.builder(Component.literal("▼"), b -> {
            scroll = Math.min(Math.max(0, visible.size() - 1), scroll + 1);
        }).bounds(width - 40, 65, 20, 20).build());
        addRenderableWidget(Button.builder(Component.translatable("gui.done"), b -> onClose())
                .bounds(width / 2 - 50, height - 28, 100, 20).build());
    }

    private void rebuildList() {
        visible.clear();
        Player player = minecraft != null ? minecraft.player : null;
        if (player == null || categories.isEmpty()) {
            return;
        }
        IPlayerKnowledge knowledge = ArcanaCapabilities.getKnowledge(player);
        ResearchCategory cat = categories.get(categoryIndex);
        List<ResearchEntry> entries = new ArrayList<>(cat.research.values());
        entries.sort(Comparator.comparingInt(ResearchEntry::getDisplayColumn)
                .thenComparingInt(ResearchEntry::getDisplayRow));
        for (ResearchEntry entry : entries) {
            if (entry.hasMeta(ResearchEntry.EnumResearchMeta.HIDDEN)
                    && !knowledge.isResearchKnown(entry.getKey())) {
                continue;
            }
            visible.add(entry);
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 262) { // right
            if (!categories.isEmpty()) {
                categoryIndex = (categoryIndex + 1) % categories.size();
                scroll = 0;
                rebuildList();
            }
            return true;
        }
        if (keyCode == 263) { // left
            if (!categories.isEmpty()) {
                categoryIndex = (categoryIndex - 1 + categories.size()) % categories.size();
                scroll = 0;
                rebuildList();
            }
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics);
        graphics.fill(width / 2 - 160, 20, width / 2 + 160, height - 40, 0xCC1A1020);
        graphics.drawCenteredString(font, title, width / 2, 28, 0xFFE2C07A);

        String catKey = categories.isEmpty() ? "BASICS" : categories.get(categoryIndex).key;
        graphics.drawCenteredString(font,
                Component.translatable("tc.research_category." + catKey),
                width / 2, 42, 0xFFE2C07A);

        int ax = width / 2 - 70;
        int ay = 54;
        for (Aspect aspect : Aspect.getPrimalAspects()) {
            AspectIconRenderer.blit(graphics, ax, ay, aspect, 12);
            ax += 14;
        }

        Player player = minecraft != null ? minecraft.player : null;
        if (player == null) {
            super.render(graphics, mouseX, mouseY, partialTick);
            return;
        }
        IPlayerKnowledge knowledge = ArcanaCapabilities.getKnowledge(player);
        int y = 72;
        int shown = 0;
        for (int i = scroll; i < visible.size() && shown < 12; i++, shown++) {
            ResearchEntry entry = visible.get(i);
            IPlayerKnowledge.EnumResearchStatus status = knowledge.getResearchStatus(entry.getKey());
            int color = switch (status) {
                case COMPLETE -> 0xFF7CFF7C;
                case IN_PROGRESS -> 0xFFFFD27C;
                default -> 0xFFAAAAAA;
            };
            Component line = Component.translatable("research.browser.line",
                    entry.getLocalizedName(),
                    ResearchGuiUtil.statusText(status),
                    Math.max(0, knowledge.getResearchStage(entry.getKey())));
            graphics.drawString(font, line, width / 2 - 150, y, color, false);
            y += 12;
        }
        if (visible.isEmpty()) {
            graphics.drawCenteredString(font, "No research in this category.", width / 2, height / 2, 0xFFAAAAAA);
        }
        super.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
