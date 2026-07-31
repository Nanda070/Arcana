package arcana.client.render;

import arcana.common.entities.CrimsonCultist;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;

/**
 * Humanoid cultist using zombie model (crimson via darker scale for captains).
 */
public class CrimsonCultistRenderer extends HumanoidMobRenderer<CrimsonCultist, HumanoidModel<CrimsonCultist>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("textures/entity/zombie/zombie.png");

    public CrimsonCultistRenderer(EntityRendererProvider.Context context) {
        super(context, new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER)), 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(CrimsonCultist entity) {
        return TEXTURE;
    }

    @Override
    protected void scale(CrimsonCultist entity, PoseStack poseStack, float partialTick) {
        super.scale(entity, poseStack, partialTick);
        if (entity.isCaptain()) {
            poseStack.scale(1.15f, 1.15f, 1.15f);
        }
    }
}
