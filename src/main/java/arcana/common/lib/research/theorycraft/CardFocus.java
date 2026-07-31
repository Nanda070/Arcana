package arcana.common.lib.research.theorycraft;

import arcana.api.research.theorycraft.ResearchTableData;
import arcana.api.research.theorycraft.TheorycraftCard;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

public class CardFocus extends TheorycraftCard {

    @Override
    public int getInspirationCost() {
        return 1;
    }

    @Override
    public String getResearchCategory() {
        return "AUROMANCY";
    }

    @Override
    public String getLocalizedName() {
        return Component.translatable("card.focus.name").getString();
    }

    @Override
    public String getLocalizedText() {
        return Component.translatable("card.focus.text").getString();
    }

    @Override
    public boolean activate(Player player, ResearchTableData data) {
        data.addTotal(getResearchCategory(), 15);
        data.bonusDraws++;
        return true;
    }
}
