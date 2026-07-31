package arcana.client;

import arcana.Arcana;
import arcana.api.aspects.Aspect;
import arcana.client.gui.ArcaneWorkbenchScreen;
import arcana.client.gui.EssentiaSmelterScreen;
import arcana.client.gui.FocalManipulatorScreen;
import arcana.client.gui.GolemJobScreen;
import arcana.client.gui.ResearchTableScreen;
import arcana.client.hud.AuraHudOverlay;
import arcana.client.render.ArcanaGolemRenderer;
import arcana.client.render.BrainyZombieRenderer;
import arcana.client.render.CrucibleRenderer;
import arcana.client.render.EldritchGuardianRenderer;
import arcana.client.render.MindSpiderRenderer;
import arcana.client.render.FocusProjectileRenderer;
import arcana.client.render.WispRenderer;
import arcana.client.render.WardedJarRenderer;
import arcana.registry.ModBlockEntities;
import arcana.registry.ModBlocks;
import arcana.registry.ModEntities;
import arcana.registry.ModItems;
import arcana.registry.ModMenus;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.FoliageColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = Arcana.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ClientModEvents {
    private ClientModEvents() {
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            MenuScreens.register(ModMenus.ARCANE_WORKBENCH.get(), ArcaneWorkbenchScreen::new);
            MenuScreens.register(ModMenus.RESEARCH_TABLE.get(), ResearchTableScreen::new);
            MenuScreens.register(ModMenus.ESSENTIA_SMELTER.get(), EssentiaSmelterScreen::new);
            MenuScreens.register(ModMenus.FOCAL_MANIPULATOR.get(), FocalManipulatorScreen::new);
            MenuScreens.register(ModMenus.GOLEM_JOB.get(), GolemJobScreen::new);
            ItemBlockRenderTypes.setRenderLayer(ModBlocks.CINDERPEARL.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(ModBlocks.SHIMMERLEAF.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(ModBlocks.CRYSTAL_CLUSTER_AER.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(ModBlocks.CRYSTAL_CLUSTER_TERRA.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(ModBlocks.CRYSTAL_CLUSTER_IGNIS.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(ModBlocks.CRYSTAL_CLUSTER_AQUA.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(ModBlocks.CRYSTAL_CLUSTER_ORDO.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(ModBlocks.CRYSTAL_CLUSTER_PERDITIO.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(ModBlocks.FOCAL_MANIPULATOR.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(ModBlocks.GOLEM_SEAL.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(ModBlocks.ALEMBIC.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(ModBlocks.GREATWOOD_LEAVES.get(), RenderType.cutoutMipped());
            ItemBlockRenderTypes.setRenderLayer(ModBlocks.GREATWOOD_SAPLING.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(ModBlocks.SILVERWOOD_LEAVES.get(), RenderType.cutoutMipped());
            ItemBlockRenderTypes.setRenderLayer(ModBlocks.SILVERWOOD_SAPLING.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(ModBlocks.AURA_NODE.get(), RenderType.cutout());
        });
    }

    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.BRAINY_ZOMBIE.get(), BrainyZombieRenderer::new);
        event.registerEntityRenderer(ModEntities.ELDRITCH_GUARDIAN.get(), EldritchGuardianRenderer::new);
        event.registerEntityRenderer(ModEntities.ELDRITCH_WARDEN.get(),
                context -> new EldritchGuardianRenderer<>(context, 1.3f));
        event.registerEntityRenderer(ModEntities.WISP.get(), WispRenderer::new);
        event.registerEntityRenderer(ModEntities.MIND_SPIDER.get(), MindSpiderRenderer::new);
        event.registerEntityRenderer(ModEntities.GOLEM.get(), ArcanaGolemRenderer::new);
        event.registerEntityRenderer(ModEntities.FOCUS_PROJECTILE.get(), FocusProjectileRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.WARDED_JAR.get(), WardedJarRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.CRUCIBLE.get(), CrucibleRenderer::new);
    }

    @SubscribeEvent
    public static void onRegisterOverlays(RegisterGuiOverlaysEvent event) {
        event.registerAboveAll("aura", AuraHudOverlay.INSTANCE);
    }

    @SubscribeEvent
    public static void onBlockColors(RegisterColorHandlersEvent.Block event) {
        event.register((state, level, pos, tintIndex) -> tintOf(state.getBlock()),
                ModBlocks.CRYSTAL_CLUSTER_AER.get(),
                ModBlocks.CRYSTAL_CLUSTER_TERRA.get(),
                ModBlocks.CRYSTAL_CLUSTER_IGNIS.get(),
                ModBlocks.CRYSTAL_CLUSTER_AQUA.get(),
                ModBlocks.CRYSTAL_CLUSTER_ORDO.get(),
                ModBlocks.CRYSTAL_CLUSTER_PERDITIO.get());
        event.register((state, level, pos, tintIndex) ->
                        level != null && pos != null
                                ? BiomeColors.getAverageFoliageColor(level, pos)
                                : FoliageColor.getDefaultColor(),
                ModBlocks.GREATWOOD_LEAVES.get(),
                ModBlocks.SILVERWOOD_LEAVES.get());
    }

    @SubscribeEvent
    public static void onItemColors(RegisterColorHandlersEvent.Item event) {
        event.register((stack, tintIndex) -> tintIndex > 0 ? -1 : (Aspect.AIR.getColor() | 0xFF000000), ModItems.CRYSTAL_AER.get());
        event.register((stack, tintIndex) -> tintIndex > 0 ? -1 : (Aspect.EARTH.getColor() | 0xFF000000), ModItems.CRYSTAL_TERRA.get());
        event.register((stack, tintIndex) -> tintIndex > 0 ? -1 : (Aspect.FIRE.getColor() | 0xFF000000), ModItems.CRYSTAL_IGNIS.get());
        event.register((stack, tintIndex) -> tintIndex > 0 ? -1 : (Aspect.WATER.getColor() | 0xFF000000), ModItems.CRYSTAL_AQUA.get());
        event.register((stack, tintIndex) -> tintIndex > 0 ? -1 : (Aspect.ORDER.getColor() | 0xFF000000), ModItems.CRYSTAL_ORDO.get());
        event.register((stack, tintIndex) -> tintIndex > 0 ? -1 : (Aspect.ENTROPY.getColor() | 0xFF000000), ModItems.CRYSTAL_PERDITIO.get());
        event.register((stack, tintIndex) -> tintOf(stack.getItem()),
                ModItems.CRYSTAL_CLUSTER_AER.get(),
                ModItems.CRYSTAL_CLUSTER_TERRA.get(),
                ModItems.CRYSTAL_CLUSTER_IGNIS.get(),
                ModItems.CRYSTAL_CLUSTER_AQUA.get(),
                ModItems.CRYSTAL_CLUSTER_ORDO.get(),
                ModItems.CRYSTAL_CLUSTER_PERDITIO.get());
        event.register((stack, tintIndex) -> FoliageColor.getDefaultColor(),
                ModItems.GREATWOOD_LEAVES.get(),
                ModItems.SILVERWOOD_LEAVES.get());
    }

    private static int tintOf(Object blockOrItem) {
        if (blockOrItem instanceof arcana.common.blocks.CrystalClusterBlock cluster) {
            return cluster.getAspect().getColor() | 0xFF000000;
        }
        if (blockOrItem instanceof net.minecraft.world.item.BlockItem blockItem
                && blockItem.getBlock() instanceof arcana.common.blocks.CrystalClusterBlock cluster) {
            return cluster.getAspect().getColor() | 0xFF000000;
        }
        return -1;
    }
}
