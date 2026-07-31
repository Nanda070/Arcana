package arcana.common.events;

import arcana.Arcana;
import arcana.api.aura.AuraHelper;
import arcana.common.items.casters.ItemCaster;
import arcana.common.network.PacketHandler;
import arcana.registry.ModItems;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Arcana.MODID)
public final class AuraSyncEvents {
    private AuraSyncEvents() {
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.player.level().isClientSide) {
            return;
        }
        if (!(event.player instanceof ServerPlayer player) || player.tickCount % 10 != 0) {
            return;
        }
        if (!isAuraViewer(player.getMainHandItem()) && !isAuraViewer(player.getOffhandItem())) {
            return;
        }
        short base = (short) AuraHelper.getAuraBase(player.level(), player.blockPosition());
        float vis = AuraHelper.getVis(player.level(), player.blockPosition());
        float flux = AuraHelper.getFlux(player.level(), player.blockPosition());
        PacketHandler.syncAura(player, base, vis, flux);
    }

    private static boolean isAuraViewer(ItemStack stack) {
        return stack.getItem() instanceof ItemCaster || stack.is(ModItems.THAUMOMETER.get());
    }
}
