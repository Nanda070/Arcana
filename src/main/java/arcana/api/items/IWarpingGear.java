package arcana.api.items;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * Armor, held items, or curios that add warp while equipped or held.
 * Also supports NBT byte tag {@code Arcana.WARP} (stacks with this interface).
 */
public interface IWarpingGear {
    int getWarp(ItemStack stack, Player player);
}
