package arcana.client.gui;

import arcana.api.aspects.Aspect;
import arcana.common.menu.ArcaneWorkbenchMenu;
import arcana.registry.ModRecipes;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class ArcaneWorkbenchScreen extends AbstractContainerScreen<ArcaneWorkbenchMenu> {
    private static final ResourceLocation BG = new ResourceLocation("arcana", "textures/gui/arcane_workbench.png");

    public ArcaneWorkbenchScreen(ArcaneWorkbenchMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
        this.titleLabelX = 8;
        this.titleLabelY = 6;
        this.inventoryLabelX = 8;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        graphics.blit(BG, x, y, 0, 0, imageWidth, imageHeight);
        graphics.fill(x + 7, y + 16, x + 25, y + 70, 0x66000000);
        graphics.fill(x + 151, y + 16, x + 169, y + 70, 0x66000000);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        super.renderLabels(graphics, mouseX, mouseY);
        String vis = "Vis: " + menu.getVis();
        graphics.drawString(font, vis, imageWidth - 8 - font.width(vis), titleLabelY, 0x3F3F9F, false);

        if (minecraft == null || minecraft.level == null) {
            return;
        }
        var opt = minecraft.level.getRecipeManager()
                .getRecipeFor(ModRecipes.ARCANE_SHAPED_TYPE.get(), menu, minecraft.level);
        opt.ifPresent(recipe -> {
            String research = recipe.getResearch().isEmpty() ? "-" : recipe.getResearch();
            graphics.drawString(font, "Need: " + research, 90, 56, 0x7A5A20, false);
            StringBuilder crystals = new StringBuilder();
            for (Aspect a : recipe.getCrystals().getAspects()) {
                if (crystals.length() > 0) {
                    crystals.append(' ');
                }
                crystals.append(a.getTag(), 0, Math.min(3, a.getTag().length()))
                        .append('x').append(recipe.getCrystals().getAmount(a));
            }
            if (crystals.length() > 0) {
                graphics.drawString(font, crystals.toString(), 90, 66, 0x5A7A9F, false);
            }
        });
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
    }
}
