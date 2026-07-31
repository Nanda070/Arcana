package arcana.common.lib.research.theorycraft;

import arcana.api.research.theorycraft.ResearchTableData;
import arcana.api.research.theorycraft.TheorycraftCard;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;

public class CardEnchantment extends TheorycraftCard {

    @Override
    public int getInspirationCost() {
        return 1;
    }

    @Override
    public boolean isAidOnly() {
        return true;
    }

    @Override
    public String getLocalizedName() {
        return Component.translatable("card.enchantment.name").getString();
    }

    @Override
    public String getLocalizedText() {
        return Component.translatable("card.enchantment.text").getString();
    }

    @Override
    public boolean activate(Player player, ResearchTableData data) {
        if (player.experienceLevel < 5) {
            return false;
        }
        player.giveExperienceLevels(-5);
        data.addTotal("INFUSION", Mth.nextInt(player.getRandom(), 15, 20));
        data.addTotal("AUROMANCY", Mth.nextInt(player.getRandom(), 15, 20));
        return true;
    }
}
