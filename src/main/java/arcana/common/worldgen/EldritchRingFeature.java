package arcana.common.worldgen;

import arcana.registry.ModBlocks;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

/**
 * Rare circle of eldritch stone (radius ~5) with air center and optional node/pedestal.
 */
public class EldritchRingFeature extends Feature<NoneFeatureConfiguration> {
    private static final int RADIUS = 5;

    public EldritchRingFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        RandomSource random = context.random();
        // L5: config rarity multiplier (higher = rarer). JSON rarity_filter still applies.
        if (!WorldgenRarity.allow(random)) {
            return false;
        }
        BlockState below = level.getBlockState(origin.below());
        if (!below.canOcclude()) {
            return false;
        }

        BlockState stone = ModBlocks.ELDRITCH_STONE.get().defaultBlockState();
        for (int dx = -RADIUS; dx <= RADIUS; dx++) {
            for (int dz = -RADIUS; dz <= RADIUS; dz++) {
                int distSq = dx * dx + dz * dz;
                if (distSq < (RADIUS - 1) * (RADIUS - 1) || distSq > RADIUS * RADIUS) {
                    continue;
                }
                BlockPos pos = origin.offset(dx, 0, dz);
                if (level.getBlockState(pos).canBeReplaced() || level.isEmptyBlock(pos)) {
                    level.setBlock(pos, stone, 2);
                }
            }
        }

        BlockPos center = origin.above();
        if (level.getBlockState(center).canBeReplaced() || level.isEmptyBlock(center)) {
            if (random.nextBoolean()) {
                level.setBlock(center, ModBlocks.AURA_NODE.get().defaultBlockState(), 2);
            } else {
                level.setBlock(center, ModBlocks.PEDESTAL.get().defaultBlockState(), 2);
            }
        }
        return true;
    }
}
