package arcana.client.render;

import arcana.api.aspects.Aspect;
import arcana.common.blockentities.WardedJarBlockEntity;
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

public class WardedJarRenderer implements BlockEntityRenderer<WardedJarBlockEntity> {
    private static final ResourceLocation WATER = new ResourceLocation("minecraft", "block/water_still");

    public WardedJarRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(WardedJarBlockEntity jar, float partialTick, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight, int packedOverlay) {
        Aspect aspect = jar.getAspect();
        int amount = jar.getAmount();
        if (aspect == null || amount <= 0) {
            // still show filter ring hint via empty skip
            return;
        }

        float fill = Math.min(1.0f, amount / (float) WardedJarBlockEntity.CAPACITY);
        float min = 3.0f / 16.0f;
        float max = 13.0f / 16.0f;
        float y0 = 2.0f / 16.0f;
        float y1 = y0 + (12.0f / 16.0f) * fill;

        int color = aspect.getColor();
        float r = ((color >> 16) & 0xFF) / 255.0f;
        float g = ((color >> 8) & 0xFF) / 255.0f;
        float b = (color & 0xFF) / 255.0f;
        float a = 0.85f;

        TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(WATER);
        VertexConsumer consumer = buffer.getBuffer(RenderType.translucent());
        Matrix4f matrix = poseStack.last().pose();

        float u0 = sprite.getU0();
        float u1 = sprite.getU1();
        float v0 = sprite.getV0();
        float v1 = sprite.getV1();

        // top surface is enough to read fill level clearly
        vertex(consumer, matrix, min, y1, min, u0, v0, r, g, b, a, packedLight);
        vertex(consumer, matrix, max, y1, min, u1, v0, r, g, b, a, packedLight);
        vertex(consumer, matrix, max, y1, max, u1, v1, r, g, b, a, packedLight);
        vertex(consumer, matrix, min, y1, max, u0, v1, r, g, b, a, packedLight);
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
