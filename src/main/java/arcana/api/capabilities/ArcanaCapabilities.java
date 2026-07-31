package arcana.api.capabilities;

import javax.annotation.Nonnull;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

public final class ArcanaCapabilities {
    public static final Capability<IPlayerKnowledge> KNOWLEDGE = CapabilityManager.get(new CapabilityToken<>() {});
    public static final Capability<IPlayerWarp> WARP = CapabilityManager.get(new CapabilityToken<>() {});

    private ArcanaCapabilities() {
    }

    @Nonnull
    public static IPlayerKnowledge getKnowledge(@Nonnull Player player) {
        return player.getCapability(KNOWLEDGE).orElseThrow(() ->
                new IllegalStateException("Missing Arcana knowledge capability for " + player.getName().getString()));
    }

    @Nonnull
    public static IPlayerWarp getWarp(@Nonnull Player player) {
        return player.getCapability(WARP).orElseThrow(() ->
                new IllegalStateException("Missing Arcana warp capability for " + player.getName().getString()));
    }

    public static boolean knowsResearch(@Nonnull Player player, @Nonnull String... research) {
        for (String r : research) {
            if (r.contains("&&")) {
                if (!knowsResearch(player, r.split("&&"))) {
                    return false;
                }
            } else if (r.contains("||")) {
                boolean any = false;
                for (String str : r.split("\\|\\|")) {
                    if (knowsResearch(player, str)) {
                        any = true;
                        break;
                    }
                }
                if (!any) {
                    return false;
                }
            } else if (!getKnowledge(player).isResearchKnown(r)) {
                return false;
            }
        }
        return true;
    }

    public static boolean knowsResearchStrict(@Nonnull Player player, @Nonnull String... research) {
        for (String r : research) {
            if (r.contains("&&")) {
                if (!knowsResearchStrict(player, r.split("&&"))) {
                    return false;
                }
            } else if (r.contains("||")) {
                boolean any = false;
                for (String str : r.split("\\|\\|")) {
                    if (knowsResearchStrict(player, str)) {
                        any = true;
                        break;
                    }
                }
                if (!any) {
                    return false;
                }
            } else if (r.contains("@")) {
                if (!getKnowledge(player).isResearchKnown(r)) {
                    return false;
                }
            } else if (!getKnowledge(player).isResearchComplete(r)) {
                return false;
            }
        }
        return true;
    }
}
