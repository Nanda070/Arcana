package arcana.common.lib.events;

import arcana.Arcana;
import arcana.api.capabilities.ArcanaCapabilities;
import arcana.api.capabilities.IPlayerWarp;
import arcana.config.ArcanaConfig;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Arcana.MODID)
public final class PlayerWarpTickEvents {
    private static final int WARP_CHECK_INTERVAL = 2000;

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
        int tempInterval = Math.max(1, ArcanaConfig.COMMON.tempWarpDecayTicks.get());
        int stickyInterval = Math.max(1, ArcanaConfig.COMMON.stickyWarpDecayTicks.get());
        if (player.tickCount % tempInterval == 0) {
            if (warp.get(IPlayerWarp.EnumWarpType.TEMPORARY) > 0) {
                warp.reduce(IPlayerWarp.EnumWarpType.TEMPORARY, 1);
            }
        }
        if (player.tickCount % stickyInterval == 0) {
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
