package arcana.client.render;

import arcana.api.aspects.Aspect;
import arcana.api.aspects.AspectList;
import arcana.common.blockentities.CrucibleBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;

public class CrucibleRenderer implements BlockEntityRenderer<CrucibleBlockEntity> {
    private static final ResourceLocation WATER = new ResourceLocation("minecraft", "block/water_still");

    public CrucibleRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(CrucibleBlockEntity crucible, float partialTick, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight, int packedOverlay) {
        int fluid = crucible.getTank().getFluidAmount();
        if (fluid <= 0) {
            return;
        }

        float fill = fluid / (float) crucible.getTank().getCapacity();
        float y = 4.0f / 16.0f + (10.0f / 16.0f) * fill;
        float min = 2.0f / 16.0f;
        float max = 14.0f / 16.0f;

        float r = 0.25f;
        float g = 0.45f;
        float b = 0.85f;
        float a = 0.75f;

        AspectList aspects = crucible.getAspects();
        if (aspects != null && aspects.size() > 0) {
            Aspect top = aspects.getAspectsSortedByAmount()[0];
            int color = top.getColor();
            float mix = Math.min(1.0f, aspects.visSize() / 50.0f);
            r = lerp(r, ((color >> 16) & 0xFF) / 255.0f, mix);
            g = lerp(g, ((color >> 8) & 0xFF) / 255.0f, mix);
            b = lerp(b, (color & 0xFF) / 255.0f, mix);
            a = crucible.isBoiling() ? 0.9f : 0.85f;
        }

        TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(WATER);
        VertexConsumer consumer = buffer.getBuffer(RenderType.translucent());
        Matrix4f matrix = poseStack.last().pose();
        float u0 = sprite.getU0();
        float u1 = sprite.getU1();
        float v0 = sprite.getV0();
        float v1 = sprite.getV1();

        vertex(consumer, matrix, min, y, min, u0, v0, r, g, b, a, packedLight);
        vertex(consumer, matrix, max, y, min, u1, v0, r, g, b, a, packedLight);
        vertex(consumer, matrix, max, y, max, u1, v1, r, g, b, a, packedLight);
        vertex(consumer, matrix, min, y, max, u0, v1, r, g, b, a, packedLight);
    }

    private static float lerp(float a, float b, float t) {
        return a + (b - a) * t;
    }

    private static void vertex(VertexConsumer consumer, Matrix4f matrix,
                               float x, float y, float z, float u, float v,
                               float r, float g, float b, float a, int light) {
        consumer.vertex(matrix, x, y, z)
                .color(r, g, b, a)
                .uv(u, v)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(light)
                .normal(0.0f, 1.0f, 0.0f)
                .endVertex();
    }
}
