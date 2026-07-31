package arcana.common.items;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

final class VoidRepair {
    private VoidRepair() {
    }

    static void tickRepair(ItemStack stack, Level level, Entity entity) {
        if (level.isClientSide || !stack.isDamaged() || entity == null || entity.tickCount % 20 != 0) {
            return;
        }
        if (entity instanceof LivingEntity) {
            stack.setDamageValue(Math.max(0, stack.getDamageValue() - 1));
        }
    }
}
