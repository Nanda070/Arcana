package arcana.common.lib.events;

import arcana.common.entities.MindSpider;
import arcana.registry.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;

/**
 * Spawns fleeting mind spiders near a player (warp hallucination).
 */
public final class MindSpiderSpawnHelper {
    private MindSpiderSpawnHelper() {
    }

    public static void spawnNear(Player player, int count) {
        if (!(player.level() instanceof ServerLevel level)) {
            return;
        }
        RandomSource random = player.getRandom();
        BlockPos base = player.blockPosition();
        for (int n = 0; n < count; n++) {
            for (int attempt = 0; attempt < 24; attempt++) {
                int dx = Mth.randomBetweenInclusive(random, 3, 10) * (random.nextBoolean() ? 1 : -1);
                int dz = Mth.randomBetweenInclusive(random, 3, 10) * (random.nextBoolean() ? 1 : -1);
                int dy = Mth.randomBetweenInclusive(random, -1, 2);
                BlockPos pos = base.offset(dx, dy, dz);
                if (!level.getBlockState(pos.below()).isSolidRender(level, pos.below())) {
                    continue;
                }
                if (!level.getBlockState(pos).isAir() || !level.getBlockState(pos.above()).isAir()) {
                    continue;
                }
                MindSpider spider = ModEntities.MIND_SPIDER.get().create(level);
                if (spider == null) {
                    return;
                }
                spider.moveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, random.nextFloat() * 360.0f, 0.0f);
                spider.setHarmless(false);
                spider.setTarget(player);
                level.addFreshEntity(spider);
                break;
            }
        }
    }
}
