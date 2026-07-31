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

public class ShimmerleafBlock extends BushBlock {
    public ShimmerleafBlock() {
        super(BlockBehaviour.Properties.of()
                .mapColor(MapColor.COLOR_LIGHT_BLUE)
                .noCollission()
                .instabreak()
                .sound(SoundType.GRASS)
                .lightLevel(state -> 4)
                .offsetType(BlockBehaviour.OffsetType.XZ));
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return state.is(Blocks.GRASS_BLOCK) || state.is(Blocks.DIRT) || state.is(Blocks.PODZOL)
                || state.is(Blocks.MOSS_BLOCK) || state.is(Blocks.COARSE_DIRT);
    }

    @Override
    public PlantType getPlantType(BlockGetter level, BlockPos pos) {
        return PlantType.PLAINS;
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (random.nextInt(4) == 0) {
            double x = pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.3;
            double y = pos.getY() + 0.5 + random.nextDouble() * 0.3;
            double z = pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.3;
            level.addParticle(ParticleTypes.END_ROD, x, y, z, 0.0, 0.01, 0.0);
        }
    }
}
