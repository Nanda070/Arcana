package arcana;

import arcana.api.aspects.Aspect;
import arcana.command.ArcanaCommands;
import arcana.common.lib.research.ResearchManager;
import arcana.common.network.PacketHandler;
import arcana.config.ArcanaConfig;
import arcana.registry.ModBlockEntities;
import arcana.registry.ModBlocks;
import arcana.registry.ModCreativeTabs;
import arcana.registry.ModEntities;
import arcana.registry.ModFeatures;
import arcana.registry.ModItems;
import arcana.registry.ModMenus;
import arcana.registry.ModRecipes;
import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(Arcana.MODID)
public class Arcana {
    public static final String MODID = "arcana";
    public static final Logger LOGGER = LogUtils.getLogger();

    public Arcana(FMLJavaModLoadingContext context) {
        IEventBus modBus = context.getModEventBus();

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ArcanaConfig.COMMON_SPEC);

        ModBlocks.BLOCKS.register(modBus);
        ModItems.ITEMS.register(modBus);
        ModEntities.ENTITY_TYPES.register(modBus);
        ModBlockEntities.BLOCK_ENTITIES.register(modBus);
        ModMenus.MENUS.register(modBus);
        ModRecipes.RECIPE_TYPES.register(modBus);
        ModRecipes.SERIALIZERS.register(modBus);
        ModFeatures.FEATURES.register(modBus);
        ModCreativeTabs.CREATIVE_MODE_TABS.register(modBus);

        modBus.addListener(this::commonSetup);
        MinecraftForge.EVENT_BUS.addListener(this::onRegisterCommands);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            PacketHandler.register();
            ResearchManager.bootstrap();
        });
        int primals = Aspect.getPrimalAspects().size();
        int compounds = Aspect.getCompoundAspects().size();
        LOGGER.info("Arcana loaded — aspects {}/{}/{}; full M0–M14 stack ready",
                primals, compounds, Aspect.aspects.size());
    }

    private void onRegisterCommands(RegisterCommandsEvent event) {
        ArcanaCommands.register(event.getDispatcher());
    }
}
