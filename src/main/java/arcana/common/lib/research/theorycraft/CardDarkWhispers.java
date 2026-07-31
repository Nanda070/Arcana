package arcana.common.lib.research.theorycraft;

import arcana.api.capabilities.IPlayerWarp;
import arcana.api.research.ResearchCategories;
import arcana.api.research.theorycraft.ResearchTableData;
import arcana.api.research.theorycraft.TheorycraftCard;
import arcana.common.lib.events.WarpHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;

public class CardDarkWhispers extends TheorycraftCard {

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
        return Component.translatable("card.darkwhisper.name").getString();
    }

    @Override
    public String getLocalizedText() {
        return Component.translatable("card.darkwhisper.text").getString();
    }

    @Override
    public boolean activate(Player player, ResearchTableData data) {
        int l = player.experienceLevel;
        player.giveExperienceLevels(-(10 + l));
        if (l > 0) {
            for (String k : ResearchCategories.researchCategories.keySet()) {
                if (player.getRandom().nextBoolean()) {
                    continue;
                }
                data.addTotal(k, Mth.nextInt(player.getRandom(), 0, Math.max(1, (int) Math.sqrt(l))));
            }
        }
        data.addTotal("ELDRITCH", Mth.nextInt(player.getRandom(), Math.max(1, l / 5), Math.max(5, l / 2)));
        WarpHelper.addWarpToPlayer(player, Math.max(1, (int) Math.sqrt(l)), IPlayerWarp.EnumWarpType.NORMAL);
        if (player.getRandom().nextBoolean()) {
            data.bonusDraws++;
        }
        return true;
    }
}
