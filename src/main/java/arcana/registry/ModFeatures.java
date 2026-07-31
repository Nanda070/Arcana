package arcana.registry;

import arcana.Arcana;
import arcana.common.worldgen.CrystalClusterFeature;
import arcana.common.worldgen.EldritchObeliskFeature;
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

    private ModFeatures() {
    }
}
