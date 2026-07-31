package arcana.common.lib.events;

import arcana.Arcana;
import arcana.api.capabilities.ArcanaCapabilities;
import arcana.api.capabilities.IPlayerWarp;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Warp nightmares — interrupt sleep when permanent warp &gt; 20.
 */
@Mod.EventBusSubscriber(modid = Arcana.MODID)
public final class WarpNightmareEvents {
    private WarpNightmareEvents() {
    }

    @SubscribeEvent
    public static void onPlayerSleep(PlayerSleepInBedEvent event) {
        Player player = event.getEntity();
        if (player.level().isClientSide) {
            return;
        }
        IPlayerWarp warp = ArcanaCapabilities.getWarp(player);
        int permanent = warp.get(IPlayerWarp.EnumWarpType.PERMANENT);
        if (permanent <= 20) {
            return;
        }
        if (player.getRandom().nextFloat() >= Math.min(0.8f, 0.2f + permanent / 60.0f)) {
            return;
        }
        event.setResult(Player.BedSleepingProblem.OTHER_PROBLEM);
        MindSpiderSpawnHelper.spawnNear(player, 1 + player.getRandom().nextInt(2));
        player.displayClientMessage(
                Component.translatable("warp.text.nightmare")
                        .withStyle(ChatFormatting.DARK_PURPLE, ChatFormatting.ITALIC),
                true);
    }
}
