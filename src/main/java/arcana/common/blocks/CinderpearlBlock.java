package arcana.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.common.PlantType;

public class CinderpearlBlock extends BushBlock {
    public CinderpearlBlock() {
        super(BlockBehaviour.Properties.of()
                .mapColor(MapColor.COLOR_ORANGE)
                .noCollission()
                .instabreak()
                .sound(SoundType.GRASS)
                .lightLevel(state -> 7)
                .offsetType(BlockBehaviour.OffsetType.XZ));
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return state.is(Blocks.SAND) || state.is(Blocks.RED_SAND) || state.is(Blocks.DIRT)
                || state.is(Blocks.GRASS_BLOCK) || state.is(Blocks.TERRACOTTA)
                || state.is(Blocks.ORANGE_TERRACOTTA) || state.is(Blocks.RED_TERRACOTTA)
                || state.is(Blocks.YELLOW_TERRACOTTA) || state.is(Blocks.WHITE_TERRACOTTA);
    }

    @Override
    public PlantType getPlantType(BlockGetter level, BlockPos pos) {
        return PlantType.DESERT;
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (random.nextBoolean()) {
            double x = pos.getX() + 0.5 + (random.nextDouble() - random.nextDouble()) * 0.1;
            double y = pos.getY() + 0.6 + (random.nextDouble() - random.nextDouble()) * 0.1;
            double z = pos.getZ() + 0.5 + (random.nextDouble() - random.nextDouble()) * 0.1;
            level.addParticle(ParticleTypes.SMOKE, x, y, z, 0.0, 0.0, 0.0);
            level.addParticle(ParticleTypes.FLAME, x, y, z, 0.0, 0.0, 0.0);
        }
    }
}
