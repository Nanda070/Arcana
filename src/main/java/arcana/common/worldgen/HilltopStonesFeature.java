package arcana.common.worldgen;

import arcana.registry.ModBlocks;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

/**
 * Small hilltop cluster of 3–5 mossy/eldritch pillars.
 */
public class HilltopStonesFeature extends Feature<NoneFeatureConfiguration> {
    public HilltopStonesFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        RandomSource random = context.random();
        if (origin.getY() < level.getSeaLevel() + 8) {
            return false;
        }
        BlockState below = level.getBlockState(origin.below());
        if (!below.canOcclude()) {
            return false;
        }

        int pillars = 3 + random.nextInt(3);
        for (int i = 0; i < pillars; i++) {
            int dx = random.nextInt(5) - 2;
            int dz = random.nextInt(5) - 2;
            BlockPos base = origin.offset(dx, 0, dz);
            if (!level.getBlockState(base.below()).canOcclude()) {
                continue;
            }
            int height = 2 + random.nextInt(3);
            BlockState pillar = random.nextBoolean()
                    ? ModBlocks.ELDRITCH_STONE.get().defaultBlockState()
                    : Blocks.MOSSY_COBBLESTONE.defaultBlockState();
            for (int y = 0; y < height; y++) {
                BlockPos pos = base.above(y);
                if (level.getBlockState(pos).canBeReplaced() || level.isEmptyBlock(pos)) {
                    level.setBlock(pos, pillar, 2);
                }
            }
        }
        return true;
    }
}
