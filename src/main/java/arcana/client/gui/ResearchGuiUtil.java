package arcana.client.gui;

import arcana.api.capabilities.IPlayerKnowledge;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.network.chat.Component;

/**
 * Shared presentation helpers for the research screens.
 */
public final class ResearchGuiUtil {
    private static final int LABEL_LENGTH = 3;

    private ResearchGuiUtil() {
    }

    public static Component statusText(IPlayerKnowledge.EnumResearchStatus status) {
        return switch (status) {
            case COMPLETE -> Component.translatable("research.status.complete");
            case IN_PROGRESS -> Component.translatable("research.status.in_progress");
            default -> Component.translatable("research.status.unknown");
        };
    }

    /**
     * Builds short node labels that stay unique: keys sharing a prefix are distinguished by the first
     * character where they diverge, so FOCUSPROJECTILE and FOCUSELEMENTAL become FOP and FOE.
     */
    public static Map<String, String> uniqueLabels(List<String> keys) {
        Map<String, List<String>> byPrefix = new HashMap<>();
        for (String key : keys) {
            byPrefix.computeIfAbsent(prefix(key, LABEL_LENGTH), k -> new ArrayList<>()).add(key);
        }

        Map<String, String> labels = new HashMap<>();
        Set<String> used = new HashSet<>();
        for (List<String> group : byPrefix.values()) {
            int divergence = group.size() > 1 ? commonPrefixLength(group) : 0;
            for (String key : group) {
                labels.put(key, claim(candidate(key, divergence), used));
            }
        }
        return labels;
    }

    private static String candidate(String key, int divergence) {
        if (divergence <= 0 || divergence >= key.length() || key.length() < LABEL_LENGTH) {
            return prefix(key, LABEL_LENGTH);
        }
        return key.substring(0, LABEL_LENGTH - 1) + key.charAt(divergence);
    }

    private static String claim(String candidate, Set<String> used) {
        String label = candidate;
        for (int suffix = 2; !used.add(label); suffix++) {
            label = prefix(candidate, LABEL_LENGTH - 1) + suffix;
        }
        return label;
    }

    private static String prefix(String key, int length) {
        return key.substring(0, Math.min(length, key.length()));
    }

    private static int commonPrefixLength(List<String> keys) {
        String first = keys.get(0);
        int length = first.length();
        for (String key : keys) {
            length = Math.min(length, key.length());
            int i = 0;
            while (i < length && first.charAt(i) == key.charAt(i)) {
                i++;
            }
            length = i;
        }
        return length;
    }
}
