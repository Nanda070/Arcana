package arcana.common.worldgen;

import arcana.common.blocks.CrystalClusterBlock;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.BlockStateConfiguration;

/**
 * Places a crystal cluster in air next to sturdy stone within a small cube (TC6-style).
 */
public class CrystalClusterFeature extends Feature<BlockStateConfiguration> {
    public CrystalClusterFeature(Codec<BlockStateConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<BlockStateConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        RandomSource random = context.random();
        BlockState toPlace = context.config().state;
        boolean placed = false;

        for (int attempt = 0; attempt < 12; attempt++) {
            BlockPos pos = origin.offset(
                    random.nextInt(5) - 2,
                    random.nextInt(5) - 2,
                    random.nextInt(5) - 2);
            if (!level.isEmptyBlock(pos)) {
                continue;
            }
            if (!CrystalClusterBlock.touchesSupport(level, pos)) {
                continue;
            }
            if (!toPlace.canSurvive(level, pos)) {
                continue;
            }
            BlockState placedState = toPlace.hasProperty(CrystalClusterBlock.AGE)
                    ? toPlace.setValue(CrystalClusterBlock.AGE, 3)
                    : toPlace;
            level.setBlock(pos, placedState, 2);
            placed = true;
            if (random.nextInt(3) == 0) {
                break;
            }
        }
        return placed;
    }
}
