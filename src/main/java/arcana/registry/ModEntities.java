package arcana.registry;

import arcana.Arcana;
import arcana.common.entities.BrainyZombie;
import arcana.common.entities.EldritchGuardian;
import arcana.common.entities.EldritchWarden;
import arcana.common.entities.MindSpider;
import arcana.common.entities.Wisp;
import arcana.common.entities.projectile.FocusProjectile;
import arcana.common.golems.ArcanaGolem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, Arcana.MODID);

    public static final RegistryObject<EntityType<BrainyZombie>> BRAINY_ZOMBIE = ENTITY_TYPES.register("brainy_zombie",
            () -> EntityType.Builder.of(BrainyZombie::new, MobCategory.MONSTER)
                    .sized(0.6f, 1.95f)
                    .clientTrackingRange(8)
                    .build(new ResourceLocation(Arcana.MODID, "brainy_zombie").toString()));

    public static final RegistryObject<EntityType<EldritchGuardian>> ELDRITCH_GUARDIAN =
            ENTITY_TYPES.register("eldritch_guardian",
                    () -> EntityType.Builder.of(EldritchGuardian::new, MobCategory.MONSTER)
                            .sized(0.6f, 1.95f)
                            .clientTrackingRange(10)
                            .fireImmune()
                            .build(new ResourceLocation(Arcana.MODID, "eldritch_guardian").toString()));

    public static final RegistryObject<EntityType<EldritchWarden>> ELDRITCH_WARDEN =
            ENTITY_TYPES.register("eldritch_warden",
                    () -> EntityType.Builder.of(EldritchWarden::new, MobCategory.MONSTER)
                            .sized(0.72f, 2.34f)
                            .clientTrackingRange(12)
                            .fireImmune()
                            .build(new ResourceLocation(Arcana.MODID, "eldritch_warden").toString()));

    public static final RegistryObject<EntityType<Wisp>> WISP = ENTITY_TYPES.register("wisp",
            () -> EntityType.Builder.of(Wisp::new, MobCategory.MONSTER)
                    .sized(0.6f, 0.6f)
                    .clientTrackingRange(10)
                    .fireImmune()
                    .build(new ResourceLocation(Arcana.MODID, "wisp").toString()));

    public static final RegistryObject<EntityType<MindSpider>> MIND_SPIDER = ENTITY_TYPES.register("mind_spider",
            () -> EntityType.Builder.of(MindSpider::new, MobCategory.MONSTER)
                    .sized(0.7f, 0.5f)
                    .clientTrackingRange(8)
                    .build(new ResourceLocation(Arcana.MODID, "mind_spider").toString()));

    public static final RegistryObject<EntityType<ArcanaGolem>> GOLEM = ENTITY_TYPES.register("golem",
            () -> EntityType.Builder.of(ArcanaGolem::new, MobCategory.CREATURE)
                    .sized(0.6f, 1.0f)
                    .clientTrackingRange(10)
                    .build(new ResourceLocation(Arcana.MODID, "golem").toString()));

    public static final RegistryObject<EntityType<FocusProjectile>> FOCUS_PROJECTILE = ENTITY_TYPES.register("focus_projectile",
            () -> EntityType.Builder.<FocusProjectile>of(FocusProjectile::new, MobCategory.MISC)
                    .sized(0.25f, 0.25f)
                    .clientTrackingRange(8)
                    .updateInterval(1)
                    .build(new ResourceLocation(Arcana.MODID, "focus_projectile").toString()));

    private ModEntities() {
    }
}
