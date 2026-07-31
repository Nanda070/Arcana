package arcana.api.research;

import net.minecraft.locale.Language;

/** Minimal stage data for M5. Craft/obtain gates arrive with crafting module. */
public class ResearchStage {
    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getLocalizedText() {
        if (text == null || text.isEmpty()) {
            return "";
        }
        return Language.getInstance().getOrDefault(text);
    }
}
