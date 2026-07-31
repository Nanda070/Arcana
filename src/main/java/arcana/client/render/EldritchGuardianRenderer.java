package arcana.client.render;

import arcana.Arcana;
import arcana.common.entities.EldritchGuardian;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;

public class EldritchGuardianRenderer<T extends EldritchGuardian> extends HumanoidMobRenderer<T, HumanoidModel<T>> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(Arcana.MODID, "textures/entity/eldritch_guardian.png");
    private static final int MIN_BLOCK_LIGHT = 10;
    private final float renderScale;

    public EldritchGuardianRenderer(EntityRendererProvider.Context context) {
        this(context, 1.1f);
    }

    public EldritchGuardianRenderer(EntityRendererProvider.Context context, float renderScale) {
        super(context, new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER)), 0.5f * renderScale);
        this.renderScale = renderScale;
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return TEXTURE;
    }

    /** The texture is very dark, so keep it lit well enough to read against unlit terrain. */
    @Override
    protected int getBlockLightLevel(T entity, BlockPos pos) {
        return Math.max(MIN_BLOCK_LIGHT, super.getBlockLightLevel(entity, pos));
    }

    @Override
    protected void scale(T entity, PoseStack poseStack, float partialTick) {
        super.scale(entity, poseStack, partialTick);
        poseStack.scale(renderScale, renderScale, renderScale);
    }
}
