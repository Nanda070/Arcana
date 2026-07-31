package arcana.api.research;

import arcana.api.aspects.AspectList;
import java.util.Collection;
import java.util.LinkedHashMap;
import net.minecraft.locale.Language;
import net.minecraft.resources.ResourceLocation;

public final class ResearchCategories {
    public static final LinkedHashMap<String, ResearchCategory> researchCategories = new LinkedHashMap<>();

    private ResearchCategories() {
    }

    public static ResearchCategory getResearchCategory(String key) {
        return researchCategories.get(key);
    }

    public static String getCategoryName(String key) {
        return Language.getInstance().getOrDefault("tc.research_category." + key);
    }

    public static ResearchEntry getResearch(String key) {
        for (ResearchCategory cat : researchCategories.values()) {
            ResearchEntry entry = cat.research.get(key);
            if (entry != null) {
                return entry;
            }
            for (ResearchEntry ri : cat.research.values()) {
                if (ri.getKey().equals(key)) {
                    return ri;
                }
            }
        }
        return null;
    }

    public static ResearchCategory registerCategory(String key, String researchkey, AspectList formula,
                                                    ResourceLocation icon, ResourceLocation background) {
        if (getResearchCategory(key) == null) {
            ResearchCategory rl = new ResearchCategory(key, researchkey, formula, icon, background);
            researchCategories.put(key, rl);
            return rl;
        }
        return null;
    }

    public static ResearchCategory registerCategory(String key, String researchkey, AspectList formula,
                                                    ResourceLocation icon, ResourceLocation background,
                                                    ResourceLocation background2) {
        if (getResearchCategory(key) == null) {
            ResearchCategory rl = new ResearchCategory(key, researchkey, formula, icon, background, background2);
            researchCategories.put(key, rl);
            return rl;
        }
        return null;
    }

    public static Collection<ResearchCategory> getCategories() {
        return researchCategories.values();
    }
}
