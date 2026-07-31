package arcana.api.research;

import arcana.api.aspects.AspectList;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;

public class ResearchCategory {
    public int minDisplayColumn;
    public int minDisplayRow;
    public int maxDisplayColumn;
    public int maxDisplayRow;

    public ResourceLocation icon;
    public ResourceLocation background;
    public ResourceLocation background2;

    public String researchKey;
    public String key;
    public AspectList formula;

    public final Map<String, ResearchEntry> research = new HashMap<>();

    public ResearchCategory(String key, String researchKey, AspectList formula,
                            ResourceLocation icon, ResourceLocation background) {
        this(key, researchKey, formula, icon, background, null);
    }

    public ResearchCategory(String key, String researchKey, AspectList formula,
                            ResourceLocation icon, ResourceLocation background, ResourceLocation background2) {
        this.key = key;
        this.researchKey = researchKey;
        this.formula = formula;
        this.icon = icon;
        this.background = background;
        this.background2 = background2;
    }
}
