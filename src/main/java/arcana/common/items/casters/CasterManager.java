package arcana.common.items.casters;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.world.entity.player.Player;

public final class CasterManager {
    private static final Map<UUID, Long> COOLDOWN = new HashMap<>();

    private CasterManager() {
    }

    public static boolean isOnCooldown(Player player) {
        Long until = COOLDOWN.get(player.getUUID());
        return until != null && player.level().getGameTime() < until;
    }

    public static void setCooldown(Player player, int ticks) {
        COOLDOWN.put(player.getUUID(), player.level().getGameTime() + ticks);
    }
}
