package arcana.common.blockentities;

import arcana.api.aspects.Aspect;
import arcana.api.aspects.IEssentiaTransport;
import arcana.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * G20: Every 40 ticks, take 1 essentia from the block behind and push to the block in front
 * (or an adjacent transport on either side of the facing axis).
 */
public class BellowsBlockEntity extends BlockEntity {
    private int tickCounter;

    public BellowsBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.BELLOWS.get(), pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, BellowsBlockEntity bellows) {
        if (++bellows.tickCounter % 40 != 0) {
            return;
        }
        Direction facing = state.getValue(HorizontalDirectionalBlock.FACING);
        BlockEntity fromBe = level.getBlockEntity(pos.relative(facing.getOpposite()));
        BlockEntity toBe = level.getBlockEntity(pos.relative(facing));
        if (!(fromBe instanceof IEssentiaTransport from) || !(toBe instanceof IEssentiaTransport to)) {
            return;
        }
        Direction fromFace = facing;
        Direction toFace = facing.getOpposite();
        if (!from.canOutputTo(fromFace) || !to.canInputFrom(toFace)) {
            return;
        }
        Aspect type = from.getEssentiaType(fromFace);
        if (type == null || from.getEssentiaAmount(fromFace) <= 0) {
            return;
        }
        // Respect closed valves / filter tubes via canInputFrom + addEssentia rejects
        if (!to.canInputFrom(toFace)) {
            return;
        }
        int taken = from.takeEssentia(type, 1, fromFace);
        if (taken <= 0) {
            return;
        }
        int added = to.addEssentia(type, taken, toFace);
        if (added < taken) {
            from.addEssentia(type, taken - added, fromFace);
        }
    }
}
