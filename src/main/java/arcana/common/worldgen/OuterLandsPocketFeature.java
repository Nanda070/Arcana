package arcana.common.worldgen;

import arcana.common.entities.CrimsonCultist;
import arcana.common.entities.Wisp;
import arcana.registry.ModBlocks;
import arcana.registry.ModEntities;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

/**
 * Rare Outer Lands pocket (TC6 biome spirit, no dimension): ~16×16 eldritch/end stone pad,
 * flux goo circle + wisp, optional portal, Crimson Cultists and Endermen.
 * <p>
 * Full biome injection needs TerraBlender later — see README Outer Lands note.
 */
public class OuterLandsPocketFeature extends Feature<NoneFeatureConfiguration> {
    private static final int HALF = 7;

    public OuterLandsPocketFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        RandomSource random = context.random();
        if (!WorldgenRarity.allow(random)) {
            return false;
        }
        BlockState below = level.getBlockState(origin.below());
        if (!below.canOcclude()) {
            return false;
        }

        BlockState eldritch = ModBlocks.ELDRITCH_STONE.get().defaultBlockState();
        BlockState endStone = Blocks.END_STONE.defaultBlockState();
        BlockState flux = ModBlocks.FLUX_GOO.get().defaultBlockState();

        for (int dx = -HALF; dx <= HALF; dx++) {
            for (int dz = -HALF; dz <= HALF; dz++) {
                int distSq = dx * dx + dz * dz;
                if (distSq > HALF * HALF) {
                    continue;
                }
                BlockPos floor = origin.offset(dx, 0, dz);
                BlockState stone = random.nextFloat() < 0.55f ? eldritch : endStone;
                if (level.getBlockState(floor).canBeReplaced() || level.isEmptyBlock(floor)
                        || level.getBlockState(floor).canOcclude()) {
                    level.setBlock(floor, stone, 2);
                }
                // Clear a short air column for the pocket feel
                for (int dy = 1; dy <= 4; dy++) {
                    BlockPos air = floor.above(dy);
                    if (!level.getBlockState(air).canBeReplaced() && !level.isEmptyBlock(air)) {
                        if (level.getBlockState(air).getDestroySpeed(level, air) >= 0) {
                            level.setBlock(air, Blocks.AIR.defaultBlockState(), 2);
                        }
                    }
                }
                if (distSq > 9 && random.nextFloat() < 0.12f) {
                    BlockPos gooPos = floor.above();
                    if (level.getBlockState(gooPos).canBeReplaced() || level.isEmptyBlock(gooPos)) {
                        level.setBlock(gooPos, flux, 2);
                    }
                }
            }
        }

        // Flux goo circle (rift footprint) around origin
        int circleR = 3;
        for (int dx = -circleR; dx <= circleR; dx++) {
            for (int dz = -circleR; dz <= circleR; dz++) {
                int distSq = dx * dx + dz * dz;
                if (distSq < (circleR - 1) * (circleR - 1) || distSq > circleR * circleR) {
                    continue;
                }
                BlockPos gooPos = origin.offset(dx, 1, dz);
                if (level.getBlockState(gooPos).canBeReplaced() || level.isEmptyBlock(gooPos)) {
                    level.setBlock(gooPos, flux, 2);
                }
            }
        }

        BlockPos center = origin.above();
        if (random.nextFloat() < 0.45f
                && (level.getBlockState(center).canBeReplaced() || level.isEmptyBlock(center))) {
            level.setBlock(center, ModBlocks.OUTER_LANDS_PORTAL.get().defaultBlockState(), 2);
        } else if (level.getBlockState(center).canBeReplaced() || level.isEmptyBlock(center)) {
            level.setBlock(center, ModBlocks.AURA_NODE.get().defaultBlockState(), 2);
        }

        ServerLevel server = level instanceof ServerLevel sl ? sl
                : (level instanceof WorldGenRegion region ? region.getLevel() : null);
        if (server != null) {
            Wisp wisp = ModEntities.WISP.get().create(server);
            if (wisp != null) {
                wisp.moveTo(origin.getX() + 0.5, origin.getY() + 2.0, origin.getZ() + 0.5,
                        random.nextFloat() * 360.0f, 0.0f);
                wisp.setPersistenceRequired();
                server.addFreshEntity(wisp);
            }
            int cultists = 1 + random.nextInt(3);
            for (int i = 0; i < cultists; i++) {
                CrimsonCultist cultist = ModEntities.CRIMSON_CULTIST.get().create(server);
                if (cultist == null) {
                    break;
                }
                double x = origin.getX() + random.nextDouble() * 8.0 - 4.0;
                double z = origin.getZ() + random.nextDouble() * 8.0 - 4.0;
                cultist.moveTo(x, origin.getY() + 1.0, z, random.nextFloat() * 360.0f, 0.0f);
                cultist.setPersistenceRequired();
                server.addFreshEntity(cultist);
            }
            int endermen = 1 + random.nextInt(2);
            for (int i = 0; i < endermen; i++) {
                EnderMan enderman = EntityType.ENDERMAN.create(server);
                if (enderman == null) {
                    break;
                }
                double x = origin.getX() + random.nextDouble() * 8.0 - 4.0;
                double z = origin.getZ() + random.nextDouble() * 8.0 - 4.0;
                enderman.moveTo(x, origin.getY() + 1.0, z, random.nextFloat() * 360.0f, 0.0f);
                enderman.setPersistenceRequired();
                server.addFreshEntity(enderman);
            }
        }
        return true;
    }
}
