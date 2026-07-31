package arcana.client.render;

import arcana.common.entities.Wisp;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

/** Meshless — coloured dust particles are spawned on the client entity tick. */
public class WispRenderer extends EntityRenderer<Wisp> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("minecraft", "textures/particle/glow.png");

    public WispRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 0.0f;
    }

    @Override
    public ResourceLocation getTextureLocation(Wisp entity) {
        return TEXTURE;
    }
}
