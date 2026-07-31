package arcana.client;

import arcana.api.aspects.Aspect;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;

public final class AspectIconRenderer {
    private AspectIconRenderer() {
    }

    public static void blit(GuiGraphics graphics, int x, int y, Aspect aspect, int size) {
        if (aspect == null) {
            return;
        }
        int color = aspect.getColor();
        float r = ((color >> 16) & 0xFF) / 255.0f;
        float g = ((color >> 8) & 0xFF) / 255.0f;
        float b = (color & 0xFF) / 255.0f;
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(r, g, b, 1.0f);
        graphics.blit(aspect.getImage(), x, y, 0, 0, size, size, size, size);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
    }
}
