package arcana.common.lib.events;

import arcana.Arcana;
import arcana.api.capabilities.ArcanaCapabilities;
import arcana.api.capabilities.IPlayerWarp;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Arcana.MODID)
public final class PlayerWarpTickEvents {
    private static final int WARP_CHECK_INTERVAL = 2000;
    /** Temporary warp: −1 / 30s. */
    private static final int TEMP_DECAY_INTERVAL = 600;
    /** Sticky (NORMAL) warp: −1 / 5 min. */
    private static final int STICKY_DECAY_INTERVAL = 6000;

    private PlayerWarpTickEvents() {
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.player.level().isClientSide) {
            return;
        }
        if (!(event.player instanceof ServerPlayer player)) {
            return;
        }
        arcana.compat.curios.CuriosCompat.tryRecharge(player);

        IPlayerWarp warp = ArcanaCapabilities.getWarp(player);
        if (player.tickCount % TEMP_DECAY_INTERVAL == 0) {
            if (warp.get(IPlayerWarp.EnumWarpType.TEMPORARY) > 0) {
                warp.reduce(IPlayerWarp.EnumWarpType.TEMPORARY, 1);
            }
        }
        if (player.tickCount % STICKY_DECAY_INTERVAL == 0) {
            if (warp.get(IPlayerWarp.EnumWarpType.NORMAL) > 0) {
                warp.reduce(IPlayerWarp.EnumWarpType.NORMAL, 1);
            }
        }

        if (player.tickCount % WARP_CHECK_INTERVAL != 0) {
            WispSpawnHelper.tryFluxSpawn(player);
            return;
        }
        WarpEvents.checkWarpEvent(player);
        WispSpawnHelper.tryFluxSpawn(player);
    }
}
