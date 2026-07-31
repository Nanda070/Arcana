package arcana.common.entities;

import arcana.Arcana;
import arcana.common.golems.ArcanaGolem;
import arcana.registry.ModEntities;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Arcana.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ModEntityAttributes {
    private ModEntityAttributes() {
    }

    @SubscribeEvent
    public static void onAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.BRAINY_ZOMBIE.get(), BrainyZombie.createAttributes().build());
        event.put(ModEntities.ELDRITCH_GUARDIAN.get(), EldritchGuardian.createAttributes().build());
        event.put(ModEntities.ELDRITCH_WARDEN.get(), EldritchWarden.createAttributes().build());
        event.put(ModEntities.CRIMSON_CULTIST.get(), CrimsonCultist.createAttributes().build());
        event.put(ModEntities.WISP.get(), Wisp.createAttributes().build());
        event.put(ModEntities.MIND_SPIDER.get(), MindSpider.createAttributes().build());
        event.put(ModEntities.GOLEM.get(), ArcanaGolem.createAttributes().build());
    }
}
