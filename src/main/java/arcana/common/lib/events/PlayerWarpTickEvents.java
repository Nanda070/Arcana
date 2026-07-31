package arcana.common.lib.events;

import arcana.Arcana;
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
        if (player.tickCount % WARP_CHECK_INTERVAL != 0) {
            WispSpawnHelper.tryFluxSpawn(player);
            return;
        }
        WarpEvents.checkWarpEvent(player);
        WispSpawnHelper.tryFluxSpawn(player);
    }
}
