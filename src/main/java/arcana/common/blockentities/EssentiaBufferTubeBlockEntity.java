package arcana.common.blockentities;

import arcana.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/** Buffer tube that holds up to 8 essentia units. */
public class EssentiaBufferTubeBlockEntity extends EssentiaTubeBlockEntity {
    public static final int CAPACITY = 8;

    public EssentiaBufferTubeBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ESSENTIA_BUFFER_TUBE.get(), pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, EssentiaBufferTubeBlockEntity tube) {
        EssentiaTubeBlockEntity.serverTick(level, pos, state, tube);
    }

    @Override
    protected int getCapacity() {
        return CAPACITY;
    }
}
