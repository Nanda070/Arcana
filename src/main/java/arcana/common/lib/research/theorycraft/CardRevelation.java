package arcana.common.lib.research.theorycraft;

import arcana.api.capabilities.IPlayerWarp;
import arcana.api.research.ResearchCategories;
import arcana.api.research.theorycraft.ResearchTableData;
import arcana.api.research.theorycraft.TheorycraftCard;
import arcana.common.lib.events.WarpHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;

public class CardRevelation extends TheorycraftCard {

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
        return Component.translatable("card.revelation.name").getString();
    }

    @Override
    public String getLocalizedText() {
        return Component.translatable("card.revelation.text").getString();
    }

    @Override
    public boolean activate(Player player, ResearchTableData data) {
        String[] s = ResearchCategories.researchCategories.keySet().toArray(new String[0]);
        if (s.length > 0) {
            data.addTotal(s[player.getRandom().nextInt(s.length)], Mth.nextInt(player.getRandom(), 5, 10));
        }
        data.addTotal("ELDRITCH", 30);
        WarpHelper.addWarpToPlayer(player, 5, IPlayerWarp.EnumWarpType.TEMPORARY);
        WarpHelper.addWarpToPlayer(player, 1, IPlayerWarp.EnumWarpType.NORMAL);
        data.penaltyStart++;
        return true;
    }
}
