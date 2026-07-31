package arcana.common.lib.research.theorycraft;

import arcana.api.capabilities.IPlayerWarp;
import arcana.api.research.theorycraft.ResearchTableData;
import arcana.api.research.theorycraft.TheorycraftCard;
import arcana.common.lib.events.WarpHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;

public class CardAwareness extends TheorycraftCard {

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
        return Component.translatable("card.awareness.name").getString();
    }

    @Override
    public String getLocalizedText() {
        return Component.translatable("card.awareness.text").getString();
    }

    @Override
    public boolean activate(Player player, ResearchTableData data) {
        data.addTotal(getResearchCategory(), 20);
        if (player.getRandom().nextFloat() < 0.33f) {
            data.addTotal("ELDRITCH", Mth.nextInt(player.getRandom(), 1, 5));
            WarpHelper.addWarpToPlayer(player, 1, IPlayerWarp.EnumWarpType.NORMAL);
        }
        return true;
    }
}
