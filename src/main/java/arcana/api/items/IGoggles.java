package arcana.api.items;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

/**
 * Equipped or held items that enable essentia/aura popups like Goggles of Revealing.
 */
public interface IGoggles {
    boolean showIngamePopups(ItemStack stack, LivingEntity player);
}
