package arcana.common.worldgen;

import arcana.registry.ModBlocks;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

/**
 * Rare 3×3 eldritch stone platform with an aura node on the center.
 */
public class EldritchObeliskFeature extends Feature<NoneFeatureConfiguration> {
    public EldritchObeliskFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        BlockState below = level.getBlockState(origin.below());
        if (!below.canOcclude()) {
            return false;
        }

        BlockState stone = ModBlocks.ELDRITCH_STONE.get().defaultBlockState();
        BlockState node = ModBlocks.AURA_NODE.get().defaultBlockState();

        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                BlockPos platform = origin.offset(dx, 0, dz);
                if (level.getBlockState(platform).canBeReplaced() || level.isEmptyBlock(platform)) {
                    level.setBlock(platform, stone, 2);
                }
            }
        }

        BlockPos nodePos = origin.above();
        if (level.getBlockState(nodePos).canBeReplaced() || level.isEmptyBlock(nodePos)) {
            level.setBlock(nodePos, node, 2);
        }
        return true;
    }
}
