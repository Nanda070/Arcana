package arcana.api.items;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface IVisDiscountGear {
    /** Percent vis discount while worn (e.g. 5 = 5%). */
    int getVisDiscount(ItemStack stack, Player player);
}
