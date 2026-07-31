package arcana.common.blockentities;

import arcana.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class LampOfGrowthBlockEntity extends BlockEntity {
    public LampOfGrowthBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.LAMP_OF_GROWTH.get(), pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, LampOfGrowthBlockEntity be) {
        if (!(level instanceof ServerLevel server) || server.getGameTime() % 80 != 0) {
            return;
        }
        if (server.random.nextFloat() > 0.35f) {
            return;
        }
        BlockPos target = pos.offset(
                server.random.nextInt(9) - 4,
                server.random.nextInt(3) - 1,
                server.random.nextInt(9) - 4);
        BlockState cropState = server.getBlockState(target);
        Block block = cropState.getBlock();
        if (block instanceof CropBlock crop && !crop.isMaxAge(cropState)) {
            crop.growCrops(server, target, cropState);
            return;
        }
        if (block instanceof BonemealableBlock growable
                && growable.isValidBonemealTarget(server, target, cropState, false)
                && growable.isBonemealSuccess(server, server.random, target, cropState)) {
            growable.performBonemeal(server, server.random, target, cropState);
        }
    }
}
