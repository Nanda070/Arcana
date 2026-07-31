package arcana.client.render;

import arcana.Arcana;
import arcana.common.golems.ArcanaGolem;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;

public class ArcanaGolemRenderer extends HumanoidMobRenderer<ArcanaGolem, HumanoidModel<ArcanaGolem>> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(Arcana.MODID, "textures/entity/golems/mat_wood.png");

    public ArcanaGolemRenderer(EntityRendererProvider.Context context) {
        super(context, new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER)), 0.4f);
    }

    @Override
    public ResourceLocation getTextureLocation(ArcanaGolem entity) {
        return TEXTURE;
    }
}
