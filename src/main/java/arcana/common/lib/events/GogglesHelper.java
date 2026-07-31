package arcana.common.lib.events;

import arcana.api.items.IGoggles;
import arcana.api.items.IVisDiscountGear;
import arcana.common.items.VoidRobeArmorItem;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public final class GogglesHelper {
    private GogglesHelper() {
    }

    public static boolean hasGoggles(Player player) {
        if (isGoggles(player.getMainHandItem(), player) || isGoggles(player.getOffhandItem(), player)) {
            return true;
        }
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (slot.getType() != EquipmentSlot.Type.ARMOR) {
                continue;
            }
            if (isGoggles(player.getItemBySlot(slot), player)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isGoggles(ItemStack stack, Player player) {
        return !stack.isEmpty()
                && stack.getItem() instanceof IGoggles goggles
                && goggles.showIngamePopups(stack, player);
    }

    /** Count equipped void robe armor pieces (G23 set bonus). */
    public static int countVoidRobePieces(Player player) {
        int count = 0;
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (slot.getType() != EquipmentSlot.Type.ARMOR) {
                continue;
            }
            if (player.getItemBySlot(slot).getItem() instanceof VoidRobeArmorItem) {
                count++;
            }
        }
        return count;
    }

    public static boolean hasVoidRobeSetBonus(Player player) {
        return countVoidRobePieces(player) >= 2;
    }

    public static int getVisDiscount(Player player) {
        int discount = 0;
        discount += discountOf(player.getMainHandItem(), player);
        discount += discountOf(player.getOffhandItem(), player);
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (slot.getType() != EquipmentSlot.Type.ARMOR) {
                continue;
            }
            discount += discountOf(player.getItemBySlot(slot), player);
        }
        discount += arcana.compat.curios.CuriosCompat.getVisDiscount(player);
        return Math.min(discount, 75);
    }

    private static int discountOf(ItemStack stack, Player player) {
        if (stack.isEmpty() || !(stack.getItem() instanceof IVisDiscountGear gear)) {
            return 0;
        }
        return Math.max(0, gear.getVisDiscount(stack, player));
    }
}
