package arcana.common.blockentities;

import arcana.common.blocks.EssentiaOnewayTubeBlock;
import arcana.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/** Essentia flows only forward along facing (input from opposite, output to facing). */
public class EssentiaOnewayTubeBlockEntity extends EssentiaTubeBlockEntity {
    public EssentiaOnewayTubeBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ESSENTIA_ONEWAY_TUBE.get(), pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, EssentiaOnewayTubeBlockEntity tube) {
        EssentiaTubeBlockEntity.serverTick(level, pos, state, tube);
    }

    private Direction facing() {
        return getBlockState().getValue(EssentiaOnewayTubeBlock.FACING);
    }

    @Override
    public boolean canInputFrom(Direction face) {
        return face == facing().getOpposite();
    }

    @Override
    public boolean canOutputTo(Direction face) {
        return face == facing();
    }
}
