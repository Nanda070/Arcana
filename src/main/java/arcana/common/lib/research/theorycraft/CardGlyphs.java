package arcana.common.lib.research.theorycraft;

import arcana.api.capabilities.IPlayerWarp;
import arcana.api.research.ResearchCategories;
import arcana.api.research.theorycraft.ResearchTableData;
import arcana.api.research.theorycraft.TheorycraftCard;
import arcana.common.lib.events.WarpHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;

public class CardGlyphs extends TheorycraftCard {

    @Override
    public boolean isAidOnly() {
        return true;
    }

    @Override
    public int getInspirationCost() {
        return 1;
    }

    @Override
    public String getResearchCategory() {
        return "ELDRITCH";
    }

    @Override
    public String getLocalizedName() {
        return Component.translatable("card.glyph.name").getString();
    }

    @Override
    public String getLocalizedText() {
        return Component.translatable("card.glyph.text").getString();
    }

    @Override
    public boolean activate(Player player, ResearchTableData data) {
        String[] s = ResearchCategories.researchCategories.keySet().toArray(new String[0]);
        if (s.length > 0) {
            data.addTotal(s[player.getRandom().nextInt(s.length)], Mth.nextInt(player.getRandom(), 10, 20));
        }
        data.addTotal("ELDRITCH", Mth.nextInt(player.getRandom(), 10, 20));
        WarpHelper.addWarpToPlayer(player, 5, IPlayerWarp.EnumWarpType.TEMPORARY);
        return true;
    }
}
