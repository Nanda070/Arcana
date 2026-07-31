package arcana.common.lib.research.theorycraft;

import arcana.api.research.theorycraft.ResearchTableData;
import arcana.api.research.theorycraft.TheorycraftCard;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

/** Simplified: no ink/paper table slots — pure GOLEMANCY progress. */
public class CardScripting extends TheorycraftCard {

    @Override
    public int getInspirationCost() {
        return 1;
    }

    @Override
    public String getResearchCategory() {
        return "GOLEMANCY";
    }

    @Override
    public String getLocalizedName() {
        return Component.translatable("card.scripting.name").getString();
    }

    @Override
    public String getLocalizedText() {
        return Component.translatable("card.scripting.text").getString();
    }

    @Override
    public boolean activate(Player player, ResearchTableData data) {
        data.addTotal(getResearchCategory(), 25);
        return true;
    }
}
