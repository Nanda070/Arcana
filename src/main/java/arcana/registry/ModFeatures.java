package arcana.registry;

import arcana.Arcana;
import arcana.common.worldgen.CrystalClusterFeature;
import arcana.common.worldgen.CultistCampFeature;
import arcana.common.worldgen.EldritchObeliskFeature;
import arcana.common.worldgen.EldritchRingFeature;
import arcana.common.worldgen.FluxPatchFeature;
import arcana.common.worldgen.HilltopStonesFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.BlockStateConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModFeatures {
    public static final DeferredRegister<Feature<?>> FEATURES =
            DeferredRegister.create(ForgeRegistries.FEATURES, Arcana.MODID);

    public static final RegistryObject<Feature<BlockStateConfiguration>> CRYSTAL_CLUSTER =
            FEATURES.register("crystal_cluster", () -> new CrystalClusterFeature(BlockStateConfiguration.CODEC));

    public static final RegistryObject<Feature<NoneFeatureConfiguration>> ELDRITCH_OBELISK =
            FEATURES.register("eldritch_obelisk", () -> new EldritchObeliskFeature(NoneFeatureConfiguration.CODEC));

    public static final RegistryObject<Feature<NoneFeatureConfiguration>> ELDRITCH_RING =
            FEATURES.register("eldritch_ring", () -> new EldritchRingFeature(NoneFeatureConfiguration.CODEC));

    public static final RegistryObject<Feature<NoneFeatureConfiguration>> HILLTOP_STONES =
            FEATURES.register("hilltop_stones", () -> new HilltopStonesFeature(NoneFeatureConfiguration.CODEC));

    public static final RegistryObject<Feature<NoneFeatureConfiguration>> CULTIST_CAMP =
            FEATURES.register("cultist_camp", () -> new CultistCampFeature(NoneFeatureConfiguration.CODEC));

    public static final RegistryObject<Feature<NoneFeatureConfiguration>> FLUX_PATCH =
            FEATURES.register("flux_patch", () -> new FluxPatchFeature(NoneFeatureConfiguration.CODEC));

    private ModFeatures() {
    }
}
