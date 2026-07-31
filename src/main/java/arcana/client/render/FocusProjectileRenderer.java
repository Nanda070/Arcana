package arcana.client.render;

import arcana.common.entities.projectile.FocusProjectile;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

/** Visuals are client particles on the entity; no mesh. */
public class FocusProjectileRenderer extends EntityRenderer<FocusProjectile> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("minecraft", "textures/particle/glow.png");

    public FocusProjectileRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(FocusProjectile entity) {
        return TEXTURE;
    }
}
