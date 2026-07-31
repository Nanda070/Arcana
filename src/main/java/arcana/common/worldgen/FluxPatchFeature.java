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
 * Places 3–8 flux goo patches on the surface.
 */
public class FluxPatchFeature extends Feature<NoneFeatureConfiguration> {
    public FluxPatchFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        RandomSource random = context.random();
        BlockState below = level.getBlockState(origin.below());
        if (!below.canOcclude()) {
            return false;
        }

        BlockState goo = ModBlocks.FLUX_GOO.get().defaultBlockState();
        int count = 3 + random.nextInt(6);
        boolean placed = false;
        for (int i = 0; i < count; i++) {
            int dx = random.nextInt(7) - 3;
            int dz = random.nextInt(7) - 3;
            BlockPos pos = origin.offset(dx, 0, dz);
            BlockState ground = level.getBlockState(pos.below());
            if (!ground.canOcclude()) {
                continue;
            }
            if (level.getBlockState(pos).canBeReplaced() || level.isEmptyBlock(pos)) {
                level.setBlock(pos, goo, 2);
                placed = true;
            }
        }
        return placed;
    }
}
