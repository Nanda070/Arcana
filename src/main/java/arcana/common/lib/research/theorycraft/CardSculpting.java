package arcana.common.lib.research.theorycraft;

import arcana.api.research.theorycraft.ResearchTableData;
import arcana.api.research.theorycraft.TheorycraftCard;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class CardSculpting extends TheorycraftCard {

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
        return Component.translatable("card.sculpting.name").getString();
    }

    @Override
    public String getLocalizedText() {
        return Component.translatable("card.sculpting.text").getString();
    }

    @Override
    public ItemStack[] getRequiredItems() {
        return new ItemStack[] { new ItemStack(Items.CLAY_BALL) };
    }

    @Override
    public boolean[] getRequiredItemsConsumed() {
        return new boolean[] { true };
    }

    @Override
    public boolean activate(Player player, ResearchTableData data) {
        data.addTotal(getResearchCategory(), 20);
        data.bonusDraws++;
        return true;
    }
}
