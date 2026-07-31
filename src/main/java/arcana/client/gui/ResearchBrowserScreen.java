package arcana.client.gui;

import arcana.api.capabilities.ArcanaCapabilities;
import arcana.api.capabilities.IPlayerKnowledge;
import arcana.api.research.ResearchCategories;
import arcana.api.research.ResearchCategory;
import arcana.api.research.ResearchEntry;
import arcana.common.lib.research.ResearchManager;
import arcana.common.network.PacketHandler;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

/**
 * Research map browser (D1 / G13 / G16): nodes by displayColumn/Row, category cycle, parchment fill.
 */
public class ResearchBrowserScreen extends Screen {
    private static final int NODE = 24;
    private static final int NODE_SIZE = 18;
    private static final int PARCHMENT = 0xFFE8D9B0;

    private final List<ResearchCategory> categories = new ArrayList<>();
    private int categoryIndex;
    private ResearchCategory category;
    private int panX;
    private int panY;
    private boolean dragging;
    private int dragStartX;
    private int dragStartY;
    private int panStartX;
    private int panStartY;
    private final List<ResearchEntry> nodes = new ArrayList<>();
    private Map<String, String> nodeLabels = Map.of();

    public ResearchBrowserScreen() {
        super(Component.translatable("item.arcana.thaumonomicon"));
    }

    @Override
    protected void init() {
        categories.clear();
        categories.addAll(ResearchCategories.getCategories());
        categoryIndex = 0;
        category = categories.isEmpty() ? null : categories.get(0);
        panX = width / 2;
        panY = height / 2;
        rebuild();
        addRenderableWidget(Button.builder(Component.literal("<"), b -> cycleCategory(-1))
                .bounds(28, 24, 20, 20).build());
        addRenderableWidget(Button.builder(Component.literal(">"), b -> cycleCategory(1))
                .bounds(width - 48, 24, 20, 20).build());
    }

    private void cycleCategory(int delta) {
        if (categories.isEmpty()) {
            return;
        }
        categoryIndex = (categoryIndex + delta + categories.size()) % categories.size();
        category = categories.get(categoryIndex);
        rebuild();
    }

    private void rebuild() {
        nodes.clear();
        if (category == null) {
            return;
        }
        Player player = minecraft != null ? minecraft.player : null;
        if (player == null) {
            return;
        }
        IPlayerKnowledge knowledge = ArcanaCapabilities.getKnowledge(player);
        for (ResearchEntry entry : category.research.values()) {
            if (entry.hasMeta(ResearchEntry.EnumResearchMeta.HIDDEN)
                    && !knowledge.isResearchKnown(entry.getKey())
                    && !canUnlock(player, entry)) {
                continue;
            }
            nodes.add(entry);
        }
        nodeLabels = ResearchGuiUtil.uniqueLabels(nodes.stream().map(ResearchEntry::getKey).toList());
    }

    private boolean canUnlock(Player player, ResearchEntry entry) {
        return ResearchManager.doesPlayerHaveRequisites(player, entry.getKey());
    }

    private int screenX(ResearchEntry entry) {
        return panX + entry.getDisplayColumn() * NODE;
    }

    private int screenY(ResearchEntry entry) {
        return panY + entry.getDisplayRow() * NODE;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 262) {
            cycleCategory(1);
            return true;
        }
        if (keyCode == 263) {
            cycleCategory(-1);
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics);
        // G16 parchment fill
        graphics.fill(20, 20, width - 20, height - 20, PARCHMENT);
        graphics.fill(22, 22, width - 22, height - 22, 0x44C4A66A);
        graphics.drawCenteredString(font, title, width / 2, 28, 0xFF5A3A18);
        graphics.drawCenteredString(font,
                Component.translatable("tc.research_category." + (category != null ? category.key : "BASICS")),
                width / 2, 42, 0xFF7A5A30);

        Player player = minecraft != null ? minecraft.player : null;
        if (player == null) {
            super.render(graphics, mouseX, mouseY, partialTick);
            return;
        }
        IPlayerKnowledge knowledge = ArcanaCapabilities.getKnowledge(player);

        for (ResearchEntry entry : nodes) {
            String[] parents = entry.getParentsClean();
            if (parents == null) {
                continue;
            }
            int x1 = screenX(entry) + NODE_SIZE / 2;
            int y1 = screenY(entry) + NODE_SIZE / 2;
            for (String parentKey : parents) {
                ResearchEntry parent = ResearchCategories.getResearch(parentKey);
                if (parent == null || !nodes.contains(parent)) {
                    continue;
                }
                int x0 = screenX(parent) + NODE_SIZE / 2;
                int y0 = screenY(parent) + NODE_SIZE / 2;
                int color = knowledge.isResearchComplete(parentKey) ? 0xFF66AAFF : 0xFF887766;
                graphics.fill(Math.min(x0, x1), y0 - 1, Math.max(x0, x1), y0 + 1, color);
                graphics.fill(x1 - 1, Math.min(y0, y1), x1 + 1, Math.max(y0, y1), color);
            }
        }

        ResearchEntry hovered = null;
        for (ResearchEntry entry : nodes) {
            int x = screenX(entry);
            int y = screenY(entry);
            IPlayerKnowledge.EnumResearchStatus status = knowledge.getResearchStatus(entry.getKey());
            int color = switch (status) {
                case COMPLETE -> 0xFF3CAA3C;
                case IN_PROGRESS -> 0xFFC9A227;
                default -> canUnlock(player, entry) ? 0xFF5555AA : 0xFF555544;
            };
            graphics.fill(x, y, x + NODE_SIZE, y + NODE_SIZE, color);
            graphics.fill(x + 1, y + 1, x + NODE_SIZE - 1, y + NODE_SIZE - 1, 0xFF2A2010);
            // G16: ResearchEntry has no icon field — skip item icon draw
            String label = nodeLabels.getOrDefault(entry.getKey(),
                    entry.getKey().substring(0, Math.min(3, entry.getKey().length())));
            graphics.drawCenteredString(font, label, x + NODE_SIZE / 2, y + 5, 0xFFECECEC);

            if (mouseX >= x && mouseX < x + NODE_SIZE && mouseY >= y && mouseY < y + NODE_SIZE) {
                hovered = entry;
            }
        }

        if (hovered != null) {
            List<Component> tip = new ArrayList<>();
            tip.add(Component.literal(hovered.getLocalizedName()).withStyle(ChatFormatting.WHITE));
            tip.add(ResearchGuiUtil.statusText(knowledge.getResearchStatus(hovered.getKey()))
                    .copy().withStyle(ChatFormatting.GRAY));
            graphics.renderComponentTooltip(font, tip, mouseX, mouseY);
        }

        graphics.drawString(font, "←/→ categories · Drag to pan · Click node", 28, height - 32, 0xFF5A4A30, false);
        super.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 1 || button == 2) {
            dragging = true;
            dragStartX = (int) mouseX;
            dragStartY = (int) mouseY;
            panStartX = panX;
            panStartY = panY;
            return true;
        }
        if (button == 0) {
            Player player = minecraft != null ? minecraft.player : null;
            if (player == null) {
                return super.mouseClicked(mouseX, mouseY, button);
            }
            for (ResearchEntry entry : nodes) {
                int x = screenX(entry);
                int y = screenY(entry);
                if (mouseX >= x && mouseX < x + NODE_SIZE && mouseY >= y && mouseY < y + NODE_SIZE) {
                    IPlayerKnowledge knowledge = ArcanaCapabilities.getKnowledge(player);
                    if (!knowledge.isResearchKnown(entry.getKey()) && canUnlock(player, entry)) {
                        PacketHandler.CHANNEL.sendToServer(new arcana.common.network.PacketProgressResearch(entry.getKey()));
                        rebuild();
                    }
                    minecraft.setScreen(new ResearchPageScreen(entry, this));
                    return true;
                }
            }
            dragging = true;
            dragStartX = (int) mouseX;
            dragStartY = (int) mouseY;
            panStartX = panX;
            panStartY = panY;
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        dragging = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (dragging) {
            panX = panStartX + (int) mouseX - dragStartX;
            panY = panStartY + (int) mouseY - dragStartY;
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
