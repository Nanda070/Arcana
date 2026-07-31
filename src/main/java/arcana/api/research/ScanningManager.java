package arcana.api.research;

import arcana.api.capabilities.ArcanaCapabilities;
import arcana.api.capabilities.IPlayerKnowledge;
import arcana.common.lib.research.ResearchManager;
import arcana.common.network.PacketHandler;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public final class ScanningManager {
    private static final List<IScanThing> THINGS = new ArrayList<>();

    private ScanningManager() {
    }

    public static void clear() {
        THINGS.clear();
    }

    public static void addScannableThing(IScanThing thing) {
        THINGS.add(thing);
    }

    public static boolean isThingStillScannable(Player player, Object object) {
        IPlayerKnowledge knowledge = ArcanaCapabilities.getKnowledge(player);
        for (IScanThing thing : THINGS) {
            if (thing.checkThing(player, object)) {
                String key = thing.getResearchKey(player, object);
                if (key != null && !key.isEmpty() && !knowledge.isResearchKnown(key)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void scanTheThing(Player player, Object object) {
        if (player.level().isClientSide) {
            return;
        }
        boolean any = false;
        boolean unlocked = false;
        boolean hadAspects = false;
        for (IScanThing thing : THINGS) {
            if (!thing.checkThing(player, object)) {
                continue;
            }
            any = true;
            if (thing instanceof arcana.common.lib.research.ScanGeneric) {
                hadAspects = true;
            }
            String key = thing.getResearchKey(player, object);
            if (key == null || key.isEmpty()) {
                continue;
            }
            IPlayerKnowledge knowledge = ArcanaCapabilities.getKnowledge(player);
            if (!knowledge.isResearchKnown(key)) {
                ResearchManager.completeResearch(player, key);
                thing.onSuccess(player, object);
                unlocked = true;
            }
        }
        // I6: +4 OBSERVATION once per successful scan of anything with aspects
        if (any && hadAspects) {
            ResearchManager.grantScanObservation(player);
        }
        if (player instanceof ServerPlayer serverPlayer) {
            if (unlocked) {
                PacketHandler.syncKnowledge(serverPlayer);
                player.displayClientMessage(Component.translatable("arcana.scan.success"), true);
            } else if (any) {
                player.displayClientMessage(Component.translatable("arcana.scan.known"), true);
            } else {
                player.displayClientMessage(Component.translatable("arcana.scan.nothing"), true);
            }
        }
    }
}
