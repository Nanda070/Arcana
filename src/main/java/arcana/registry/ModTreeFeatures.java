package arcana.registry;

import arcana.Arcana;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

/**
 * Datapack configured-feature keys used by saplings and biome placement.
 */
public final class ModTreeFeatures {
    public static final ResourceKey<ConfiguredFeature<?, ?>> GREATWOOD =
            ResourceKey.create(Registries.CONFIGURED_FEATURE, new ResourceLocation(Arcana.MODID, "greatwood"));

    public static final ResourceKey<ConfiguredFeature<?, ?>> SILVERWOOD =
            ResourceKey.create(Registries.CONFIGURED_FEATURE, new ResourceLocation(Arcana.MODID, "silverwood"));

    private ModTreeFeatures() {
    }
}
