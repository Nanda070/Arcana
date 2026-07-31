package arcana.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

/**
 * J3: sneak+RMB stores this mirror position in player persistent data;
 * RMB another mirror teleports to the stored position (same dimension).
 */
public class MagicMirrorBlock extends Block {
    private static final String TAG = "ArcanaMirrorLink";

    public MagicMirrorBlock(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                 InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        if (player.isShiftKeyDown()) {
            var data = player.getPersistentData();
            var tag = data.getCompound(TAG);
            tag.putInt("X", pos.getX());
            tag.putInt("Y", pos.getY());
            tag.putInt("Z", pos.getZ());
            tag.putString("Dim", level.dimension().location().toString());
            data.put(TAG, tag);
            player.displayClientMessage(Component.translatable("arcana.mirror.linked", pos.getX(), pos.getY(), pos.getZ()), true);
            return InteractionResult.CONSUME;
        }
        var tag = player.getPersistentData().getCompound(TAG);
        if (!tag.contains("X")) {
            player.displayClientMessage(Component.translatable("arcana.mirror.no_link"), true);
            return InteractionResult.CONSUME;
        }
        ResourceLocation dimId = ResourceLocation.tryParse(tag.getString("Dim"));
        if (dimId == null || !level.dimension().location().equals(dimId)) {
            player.displayClientMessage(Component.translatable("arcana.mirror.wrong_dim"), true);
            return InteractionResult.FAIL;
        }
        BlockPos dest = new BlockPos(tag.getInt("X"), tag.getInt("Y"), tag.getInt("Z"));
        if (dest.equals(pos)) {
            player.displayClientMessage(Component.translatable("arcana.mirror.same"), true);
            return InteractionResult.CONSUME;
        }
        if (player instanceof ServerPlayer serverPlayer && level instanceof ServerLevel) {
            serverPlayer.teleportTo(dest.getX() + 0.5, dest.getY() + 1.0, dest.getZ() + 0.5);
            player.displayClientMessage(Component.translatable("arcana.mirror.teleport"), true);
        }
        return InteractionResult.CONSUME;
    }
}
