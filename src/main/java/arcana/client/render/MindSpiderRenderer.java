package arcana.client.render;

import arcana.common.entities.MindSpider;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.SpiderRenderer;
import net.minecraft.resources.ResourceLocation;

public class MindSpiderRenderer extends SpiderRenderer<MindSpider> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("textures/entity/spider/spider.png");

    public MindSpiderRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(MindSpider entity) {
        return TEXTURE;
    }
}
