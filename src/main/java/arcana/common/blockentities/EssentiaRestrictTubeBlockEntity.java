package arcana.common.blockentities;

import arcana.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/** Tube that relays less suction (harder pull path). */
public class EssentiaRestrictTubeBlockEntity extends EssentiaTubeBlockEntity {
    public EssentiaRestrictTubeBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ESSENTIA_RESTRICT_TUBE.get(), pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, EssentiaRestrictTubeBlockEntity tube) {
        EssentiaTubeBlockEntity.serverTick(level, pos, state, tube);
    }

    @Override
    protected int modifySuction(int raw) {
        return Math.max(0, raw / 3);
    }
}
