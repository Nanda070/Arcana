package arcana.common.capabilities;

import arcana.Arcana;
import arcana.api.capabilities.ArcanaCapabilities;
import arcana.api.capabilities.IPlayerKnowledge;
import arcana.api.capabilities.IPlayerWarp;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Arcana.MODID)
public final class PlayerCapabilityEvents {
    public static final ResourceLocation KNOWLEDGE_ID = new ResourceLocation(Arcana.MODID, "knowledge");
    public static final ResourceLocation WARP_ID = new ResourceLocation(Arcana.MODID, "warp");

    private PlayerCapabilityEvents() {
    }

    @SubscribeEvent
    public static void attachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player) {
            event.addCapability(KNOWLEDGE_ID, new PlayerKnowledge.Provider());
            event.addCapability(WARP_ID, new PlayerWarp.Provider());
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        event.getOriginal().reviveCaps();
        try {
            IPlayerKnowledge oldKnowledge = ArcanaCapabilities.getKnowledge(event.getOriginal());
            IPlayerKnowledge newKnowledge = ArcanaCapabilities.getKnowledge(event.getEntity());
            newKnowledge.deserializeNBT(oldKnowledge.serializeNBT());

            IPlayerWarp oldWarp = ArcanaCapabilities.getWarp(event.getOriginal());
            IPlayerWarp newWarp = ArcanaCapabilities.getWarp(event.getEntity());
            if (event.isWasDeath()) {
                newWarp.clear();
                newWarp.set(IPlayerWarp.EnumWarpType.PERMANENT, oldWarp.get(IPlayerWarp.EnumWarpType.PERMANENT));
                newWarp.set(IPlayerWarp.EnumWarpType.NORMAL, oldWarp.get(IPlayerWarp.EnumWarpType.NORMAL));
                newWarp.setCounter(oldWarp.getCounter());
            } else {
                newWarp.deserializeNBT(oldWarp.serializeNBT());
            }
        } finally {
            event.getOriginal().invalidateCaps();
        }
    }
}
