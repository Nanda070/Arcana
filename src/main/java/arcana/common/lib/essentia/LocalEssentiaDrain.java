package arcana.common.lib.essentia;

import arcana.api.aspects.Aspect;
import arcana.api.aspects.AspectList;
import arcana.api.aspects.IAspectSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public final class LocalEssentiaDrain {
    private LocalEssentiaDrain() {
    }

    public static boolean hasEnough(Level level, BlockPos center, AspectList needed, int range) {
        AspectList available = collect(level, center, range);
        for (Aspect aspect : needed.getAspects()) {
            if (available.getAmount(aspect) < needed.getAmount(aspect)) {
                return false;
            }
        }
        return true;
    }

    public static boolean drain(Level level, BlockPos center, AspectList needed, int range) {
        if (!hasEnough(level, center, needed, range)) {
            return false;
        }
        for (Aspect aspect : needed.getAspects()) {
            int remaining = needed.getAmount(aspect);
            BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
            for (int dx = -range; dx <= range && remaining > 0; dx++) {
                for (int dy = -range; dy <= range && remaining > 0; dy++) {
                    for (int dz = -range; dz <= range && remaining > 0; dz++) {
                        cursor.set(center.getX() + dx, center.getY() + dy, center.getZ() + dz);
                        BlockEntity be = level.getBlockEntity(cursor);
                        if (!(be instanceof IAspectSource source)) {
                            continue;
                        }
                        while (remaining > 0 && source.takeFromContainer(aspect, 1)) {
                            remaining--;
                        }
                    }
                }
            }
            if (remaining > 0) {
                return false;
            }
        }
        return true;
    }

    private static AspectList collect(Level level, BlockPos center, int range) {
        AspectList list = new AspectList();
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        for (int dx = -range; dx <= range; dx++) {
            for (int dy = -range; dy <= range; dy++) {
                for (int dz = -range; dz <= range; dz++) {
                    cursor.set(center.getX() + dx, center.getY() + dy, center.getZ() + dz);
                    BlockEntity be = level.getBlockEntity(cursor);
                    if (be instanceof IAspectSource source) {
                        list.add(source.getAspects());
                    }
                }
            }
        }
        return list;
    }
}
