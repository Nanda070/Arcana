package arcana.common.worldgen;

import arcana.common.entities.CrimsonCultist;
import arcana.registry.ModBlocks;
import arcana.registry.ModEntities;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.resources.ResourceLocation;
import arcana.Arcana;

/**
 * Mini crimson cult camp: 5×5 greatwood platform, fences, loot chest, banner stub.
 */
public class CultistCampFeature extends Feature<NoneFeatureConfiguration> {
    public static final ResourceLocation CAMP_LOOT =
            new ResourceLocation(Arcana.MODID, "chests/cultist_camp");

    public CultistCampFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        RandomSource random = context.random();
        BlockState below = level.getBlockState(origin.below());
        if (!below.canOcclude()) {
            return false;
        }

        BlockState planks = ModBlocks.GREATWOOD_PLANKS.get().defaultBlockState();
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                BlockPos platform = origin.offset(dx, 0, dz);
                if (level.getBlockState(platform).canBeReplaced() || level.isEmptyBlock(platform)
                        || level.getBlockState(platform).canOcclude()) {
                    level.setBlock(platform, planks, 2);
                }
            }
        }

        BlockState fence = Blocks.OAK_FENCE.defaultBlockState();
        int[][] posts = {{-2, -2}, {-2, 2}, {2, -2}, {2, 2}};
        for (int[] post : posts) {
            BlockPos postPos = origin.offset(post[0], 1, post[1]);
            if (level.getBlockState(postPos).canBeReplaced() || level.isEmptyBlock(postPos)) {
                level.setBlock(postPos, fence, 2);
            }
        }

        BlockPos bannerPos = origin.offset(0, 1, -2);
        if (level.getBlockState(bannerPos).canBeReplaced() || level.isEmptyBlock(bannerPos)) {
            level.setBlock(bannerPos, Blocks.RED_WOOL.defaultBlockState(), 2);
        }

        BlockPos chestPos = origin.offset(1, 1, 0);
        if (level.getBlockState(chestPos).canBeReplaced() || level.isEmptyBlock(chestPos)) {
            level.setBlock(chestPos, Blocks.CHEST.defaultBlockState().setValue(ChestBlock.FACING, Direction.WEST), 2);
            BlockEntity be = level.getBlockEntity(chestPos);
            if (be instanceof ChestBlockEntity chest) {
                chest.setLootTable(CAMP_LOOT, random.nextLong());
            }
        }

        ServerLevel server = level instanceof ServerLevel sl ? sl : null;
        if (server == null && level instanceof net.minecraft.server.level.WorldGenRegion region) {
            server = region.getLevel();
        }
        if (server != null) {
            int count = 1 + random.nextInt(2);
            for (int i = 0; i < count; i++) {
                CrimsonCultist cultist = ModEntities.CRIMSON_CULTIST.get().create(server);
                if (cultist == null) {
                    break;
                }
                double x = origin.getX() + random.nextDouble() * 3.0 - 1.5;
                double z = origin.getZ() + random.nextDouble() * 3.0 - 1.5;
                cultist.moveTo(x, origin.getY() + 1.0, z, random.nextFloat() * 360.0f, 0.0f);
                cultist.setPersistenceRequired();
                server.addFreshEntity(cultist);
            }
        }
        return true;
    }
}
