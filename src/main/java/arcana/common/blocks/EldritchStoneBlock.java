package arcana.common.blocks;

import arcana.api.capabilities.IPlayerWarp;
import arcana.common.lib.events.WarpHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;

/**
 * Rare deep stone laced with void. Mining without silk scars the mind slightly.
 */
public class EldritchStoneBlock extends Block {
    public EldritchStoneBlock() {
        super(BlockBehaviour.Properties.of()
                .mapColor(MapColor.COLOR_PURPLE)
                .requiresCorrectToolForDrops()
                .strength(3.0f, 9.0f)
                .sound(SoundType.STONE));
    }

    @Override
    public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state,
                              net.minecraft.world.level.block.entity.BlockEntity be,
                              net.minecraft.world.item.ItemStack tool) {
        super.playerDestroy(level, player, pos, state, be, tool);
        if (!level.isClientSide && !player.isCreative()
                && !net.minecraft.world.item.enchantment.EnchantmentHelper.hasSilkTouch(tool)) {
            WarpHelper.addWarpToPlayer(player, 1, IPlayerWarp.EnumWarpType.TEMPORARY);
        }
    }
}
