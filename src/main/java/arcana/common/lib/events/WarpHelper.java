package arcana.common.lib.events;

import arcana.api.capabilities.ArcanaCapabilities;
import arcana.api.capabilities.IPlayerWarp;
import arcana.api.items.IWarpingGear;
import java.util.UUID;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public final class WarpHelper {
    public static final String NBT_WARP = "Arcana.WARP";

    private static UUID gearCachePlayer;
    private static int gearCacheTick = Integer.MIN_VALUE;
    private static int gearCacheValue;

    private WarpHelper() {
    }

    public static int getFinalWarp(ItemStack stack, Player player) {
        if (stack == null || stack.isEmpty()) {
            return 0;
        }
        int warp = 0;
        if (stack.getItem() instanceof IWarpingGear gear) {
            warp += gear.getWarp(stack, player);
        }
        if (stack.hasTag() && stack.getTag().contains(NBT_WARP)) {
            warp += stack.getTag().getByte(NBT_WARP);
        }
        return warp;
    }

    /** Gear warp cached for the same player tick (armor/hand scan is the hot path). */
    public static int getGearWarp(Player player) {
        UUID id = player.getUUID();
        int tick = player.tickCount;
        if (tick == gearCacheTick && id.equals(gearCachePlayer)) {
            return gearCacheValue;
        }
        int total = 0;
        total += getFinalWarp(player.getMainHandItem(), player);
        total += getFinalWarp(player.getOffhandItem(), player);
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (slot.getType() != EquipmentSlot.Type.ARMOR) {
                continue;
            }
            total += getFinalWarp(player.getItemBySlot(slot), player);
        }
        gearCachePlayer = id;
        gearCacheTick = tick;
        gearCacheValue = total;
        return total;
    }

    /**
     * Add/reduce warp. Positive amounts refresh the warp event counter (TC6 style).
     */
    public static int addWarpToPlayer(Player player, int amount, IPlayerWarp.EnumWarpType type) {
        IPlayerWarp warp = ArcanaCapabilities.getWarp(player);
        int result = warp.add(type, amount);
        if (amount > 0) {
            int total = warp.get(IPlayerWarp.EnumWarpType.PERMANENT)
                    + warp.get(IPlayerWarp.EnumWarpType.NORMAL)
                    + warp.get(IPlayerWarp.EnumWarpType.TEMPORARY);
            warp.setCounter(total);
        }
        return result;
    }
}
