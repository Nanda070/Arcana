package arcana.client;

import arcana.api.aspects.Aspect;
import arcana.api.aspects.AspectList;
import arcana.common.lib.research.ScanGeneric;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public final class ClientScanTarget {
    private static Object target;

    private ClientScanTarget() {
    }

    public static void set(Object object) {
        target = object;
    }

    public static Object get() {
        return target;
    }

    public static AspectList aspects() {
        Player player = Minecraft.getInstance().player;
        if (player == null || target == null) {
            return new AspectList();
        }
        return ScanGeneric.aspectsOf(player, target);
    }

    public static Vec3 worldPos() {
        if (target instanceof Entity entity) {
            return entity.position().add(0, entity.getBbHeight() + 0.3, 0);
        }
        if (target instanceof BlockPos pos) {
            return Vec3.atCenterOf(pos).add(0, 0.6, 0);
        }
        return null;
    }

    public static boolean hasAspects() {
        AspectList list = aspects();
        return list != null && list.size() > 0;
    }
}
