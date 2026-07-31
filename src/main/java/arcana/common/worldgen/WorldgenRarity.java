package arcana.common.worldgen;

import arcana.config.ArcanaConfig;
import net.minecraft.util.RandomSource;

/**
 * Shared L5 worldgen rarity gate. Higher multiplier → more skips (rarer).
 * Values below 1.0 cannot exceed JSON rarity_filter density.
 */
public final class WorldgenRarity {
    private WorldgenRarity() {
    }

    /** @return true if placement should proceed */
    public static boolean allow(RandomSource random) {
        double mult = ArcanaConfig.COMMON.worldgenStructureRarityMultiplier.get();
        if (mult <= 1.0D) {
            return true;
        }
        return random.nextDouble() <= (1.0D / mult);
    }
}
