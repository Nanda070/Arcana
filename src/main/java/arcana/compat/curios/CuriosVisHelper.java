package arcana.compat.curios;

import arcana.api.aura.AuraHelper;
import arcana.api.items.IVisDiscountGear;
import arcana.common.items.baubles.ItemAmuletVis;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.CuriosApi;

/** Loaded only when Curios is present (via CuriosCompat gate). */
final class CuriosVisHelper {
    private CuriosVisHelper() {
    }

    static int getEquippedVisDiscount(Player player) {
        return CuriosApi.getCuriosInventory(player).map(handler -> {
            int total = 0;
            for (var entry : handler.getCurios().values()) {
                for (int i = 0; i < entry.getStacks().getSlots(); i++) {
                    ItemStack stack = entry.getStacks().getStackInSlot(i);
                    if (!stack.isEmpty() && stack.getItem() instanceof IVisDiscountGear gear) {
                        total += Math.max(0, gear.getVisDiscount(stack, player));
                    }
                }
            }
            return total;
        }).orElse(0);
    }

    static void tryRecharge(Player player) {
        if (player.level().isClientSide || player.tickCount % 40 != 0) {
            return;
        }
        boolean equipped = CuriosApi.getCuriosInventory(player).map(handler -> {
            for (var entry : handler.getCurios().values()) {
                for (int i = 0; i < entry.getStacks().getSlots(); i++) {
                    if (entry.getStacks().getStackInSlot(i).getItem() instanceof ItemAmuletVis) {
                        return true;
                    }
                }
            }
            return false;
        }).orElse(false);
        if (equipped) {
            AuraHelper.addVis(player.level(), player.blockPosition(), 0.25f);
        }
    }
}
