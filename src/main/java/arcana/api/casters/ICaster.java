package arcana.api.casters;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface ICaster {
    boolean consumeVis(ItemStack casterStack, Player player, float amount, boolean simulate);

    ItemStack getFocusStack(ItemStack casterStack);

    void setFocus(ItemStack casterStack, ItemStack focus);
}
