package arcana.api.research;

import java.util.Arrays;
import net.minecraft.locale.Language;

public class ResearchEntry {
    private String key;
    private String category;
    private String name;
    private String[] parents;
    private String[] siblings;
    private int displayColumn;
    private int displayRow;
    private EnumResearchMeta[] meta;
    private ResearchStage[] stages;

    public enum EnumResearchMeta {
        ROUND, SPIKY, REVERSE, HIDDEN, AUTOUNLOCK, HEX
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public String getLocalizedName() {
        return Language.getInstance().getOrDefault(getName());
    }

    public void setName(String name) {
        this.name = name;
    }

    public String[] getParents() {
        return parents;
    }

    public String[] getParentsClean() {
        if (parents == null) {
            return null;
        }
        String[] out = getParentsStripped();
        for (int q = 0; q < out.length; q++) {
            if (out[q].contains("@")) {
                out[q] = out[q].substring(0, out[q].indexOf('@'));
            }
        }
        return out;
    }

    public String[] getParentsStripped() {
        if (parents == null) {
            return null;
        }
        String[] out = new String[parents.length];
        for (int q = 0; q < out.length; q++) {
            out[q] = parents[q];
            if (out[q].startsWith("~")) {
                out[q] = out[q].substring(1);
            }
        }
        return out;
    }

    public void setParents(String[] parents) {
        this.parents = parents;
    }

    public String[] getSiblings() {
        return siblings;
    }

    public void setSiblings(String[] siblings) {
        this.siblings = siblings;
    }

    public int getDisplayColumn() {
        return displayColumn;
    }

    public void setDisplayColumn(int displayColumn) {
        this.displayColumn = displayColumn;
    }

    public int getDisplayRow() {
        return displayRow;
    }

    public void setDisplayRow(int displayRow) {
        this.displayRow = displayRow;
    }

    public EnumResearchMeta[] getMeta() {
        return meta;
    }

    public void setMeta(EnumResearchMeta[] meta) {
        this.meta = meta;
    }

    public boolean hasMeta(EnumResearchMeta m) {
        return meta != null && Arrays.asList(meta).contains(m);
    }

    public ResearchStage[] getStages() {
        return stages;
    }

    public void setStages(ResearchStage[] stages) {
        this.stages = stages;
    }
}
