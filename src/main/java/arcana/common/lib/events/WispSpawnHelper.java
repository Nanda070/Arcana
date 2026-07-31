package arcana.common.lib.events;

import arcana.api.aura.AuraHelper;
import arcana.common.entities.Wisp;
import arcana.registry.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * Spawns wisps near players when local flux is high.
 */
public final class WispSpawnHelper {
    private WispSpawnHelper() {
    }

    public static void tryFluxSpawn(Player player) {
        if (player.level().isClientSide || player.tickCount % 100 != 0) {
            return;
        }
        Level level = player.level();
        float flux = AuraHelper.getFlux(level, player.blockPosition());
        if (flux < 15.0f) {
            return;
        }
        RandomSource random = player.getRandom();
        // Higher flux → higher chance (cap ~8% per check)
        int chance = Math.min(40, 5 + (int) flux);
        if (random.nextInt(500) > chance) {
            return;
        }
        long nearby = level.getEntitiesOfClass(Wisp.class, player.getBoundingBox().inflate(32.0)).size();
        if (nearby >= 3) {
            return;
        }
        spawnNear(player, 1);
    }

    public static void spawnNear(Player player, int count) {
        if (!(player.level() instanceof ServerLevel level)) {
            return;
        }
        RandomSource random = player.getRandom();
        BlockPos base = player.blockPosition();
        for (int n = 0; n < count; n++) {
            for (int attempt = 0; attempt < 24; attempt++) {
                int dx = Mth.randomBetweenInclusive(random, 4, 12) * (random.nextBoolean() ? 1 : -1);
                int dz = Mth.randomBetweenInclusive(random, 4, 12) * (random.nextBoolean() ? 1 : -1);
                int dy = Mth.randomBetweenInclusive(random, 1, 5);
                BlockPos pos = base.offset(dx, dy, dz);
                if (!level.getBlockState(pos).isAir() || !level.getBlockState(pos.above()).isAir()) {
                    continue;
                }
                Wisp wisp = ModEntities.WISP.get().create(level);
                if (wisp == null) {
                    return;
                }
                wisp.moveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, random.nextFloat() * 360.0f, 0.0f);
                wisp.randomizeAspect(random);
                wisp.setTarget(player);
                level.addFreshEntity(wisp);
                break;
            }
        }
    }
}
