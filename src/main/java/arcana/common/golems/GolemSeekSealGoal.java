package arcana.common.golems;

import arcana.registry.ModBlocks;
import java.util.EnumSet;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class GolemSeekSealGoal extends Goal {
    private static final int SEARCH_RANGE = 16;
    private static final double ARRIVE_DIST_SQ = 9.0;

    private final ArcanaGolem golem;
    private BlockPos sealPos;
    private int recalcCooldown;

    public GolemSeekSealGoal(ArcanaGolem golem) {
        this.golem = golem;
        setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (golem.isFollowing()) {
            return false;
        }
        if (--recalcCooldown > 0 && sealPos != null && isSeal(sealPos)) {
            return true;
        }
        recalcCooldown = 20;
        sealPos = findNearestSeal();
        return sealPos != null;
    }

    @Override
    public boolean canContinueToUse() {
        return !golem.isFollowing() && sealPos != null && isSeal(sealPos);
    }

    @Override
    public void stop() {
        golem.getNavigation().stop();
        sealPos = null;
    }

    @Override
    public void tick() {
        if (sealPos == null) {
            return;
        }
        BlockState state = golem.level().getBlockState(sealPos);
        if (!(state.getBlock() instanceof SealBlock)) {
            sealPos = null;
            return;
        }
        Vec3 target = Vec3.atCenterOf(sealPos);
        if (golem.distanceToSqr(target) > ARRIVE_DIST_SQ) {
            golem.getNavigation().moveTo(target.x, target.y, target.z, 1.0);
        } else {
            golem.getNavigation().stop();
            golem.setJob(SealBlock.jobOf(state));
        }
    }

    private boolean isSeal(BlockPos pos) {
        return golem.level().getBlockState(pos).is(ModBlocks.GOLEM_SEAL.get());
    }

    private BlockPos findNearestSeal() {
        BlockPos origin = golem.blockPosition();
        BlockPos best = null;
        double bestDist = Double.MAX_VALUE;
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        for (int dx = -SEARCH_RANGE; dx <= SEARCH_RANGE; dx++) {
            for (int dy = -4; dy <= 4; dy++) {
                for (int dz = -SEARCH_RANGE; dz <= SEARCH_RANGE; dz++) {
                    cursor.set(origin.getX() + dx, origin.getY() + dy, origin.getZ() + dz);
                    if (!golem.level().getBlockState(cursor).is(ModBlocks.GOLEM_SEAL.get())) {
                        continue;
                    }
                    double d = golem.distanceToSqr(Vec3.atCenterOf(cursor));
                    if (d < bestDist) {
                        bestDist = d;
                        best = cursor.immutable();
                    }
                }
            }
        }
        return best;
    }
}
