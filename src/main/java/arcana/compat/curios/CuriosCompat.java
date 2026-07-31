package arcana.compat.curios;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.ModList;

/**
 * Soft Curios bridge — Curios types only referenced from {@link CuriosVisHelper}.
 */
public final class CuriosCompat {
    private static final boolean LOADED = ModList.get().isLoaded("curios");

    private CuriosCompat() {
    }

    public static boolean isLoaded() {
        return LOADED;
    }

    public static int getVisDiscount(Player player) {
        if (!LOADED) {
            return 0;
        }
        return CuriosVisHelper.getEquippedVisDiscount(player);
    }

    /** G26: recharge local aura while a vis amulet is equipped in Curios. */
    public static void tryRecharge(Player player) {
        if (!LOADED) {
            return;
        }
        CuriosVisHelper.tryRecharge(player);
    }
}
