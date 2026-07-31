package arcana.client.gui;

import arcana.api.capabilities.ArcanaCapabilities;
import arcana.api.capabilities.IPlayerKnowledge;
import arcana.api.research.ResearchEntry;
import arcana.api.research.ResearchStage;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

public class ResearchPageScreen extends Screen {
    private final ResearchEntry entry;
    private final Screen parent;

    public ResearchPageScreen(ResearchEntry entry, Screen parent) {
        super(Component.literal(entry.getLocalizedName()));
        this.entry = entry;
        this.parent = parent;
    }

    @Override
    protected void init() {
        addRenderableWidget(Button.builder(Component.translatable("gui.back"), b -> minecraft.setScreen(parent))
                .bounds(width / 2 - 50, height - 28, 100, 20).build());
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics);
        graphics.fill(width / 2 - 160, 24, width / 2 + 160, height - 40, 0xDD1A1020);
        String name = entry.getLocalizedName();
        graphics.drawCenteredString(font, name, width / 2, 36, 0xFFE2C07A);
        if (name.equals(entry.getName())) {
            // No translation available, so the raw key is the only identification we can offer.
            graphics.drawCenteredString(font, entry.getKey(), width / 2, 50, 0xFF888888);
        }

        Player player = minecraft != null ? minecraft.player : null;
        int stage = 0;
        IPlayerKnowledge.EnumResearchStatus status = IPlayerKnowledge.EnumResearchStatus.UNKNOWN;
        if (player != null) {
            IPlayerKnowledge knowledge = ArcanaCapabilities.getKnowledge(player);
            stage = Math.max(0, knowledge.getResearchStage(entry.getKey()));
            status = knowledge.getResearchStatus(entry.getKey());
        }
        Component statusLine = Component.translatable("research.page.status",
                ResearchGuiUtil.statusText(status), stage);
        graphics.drawCenteredString(font, statusLine, width / 2, 64, 0xFFCCCCCC);

        ResearchStage[] stages = entry.getStages();
        int y = 84;
        if (stages != null) {
            for (int i = 0; i < stages.length; i++) {
                boolean unlocked = stage > i || status == IPlayerKnowledge.EnumResearchStatus.COMPLETE;
                int color = unlocked ? 0xFFECECEC : 0xFF555555;
                String text = unlocked ? stages[i].getLocalizedText() : "???";
                for (var line : font.split(Component.literal(text), 280)) {
                    graphics.drawString(font, line, width / 2 - 140, y, color, false);
                    y += 12;
                }
                y += 6;
            }
        }
        super.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
