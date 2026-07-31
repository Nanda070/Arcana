package arcana.common.lib.research.theorycraft;

import arcana.api.capabilities.IPlayerWarp;
import arcana.api.research.theorycraft.ResearchTableData;
import arcana.api.research.theorycraft.TheorycraftCard;
import arcana.common.lib.events.WarpHelper;
import java.util.Random;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/**
 * Simplified celestial notes → paper stand-ins.
 * Skip CELESTIALSCANNING gate (not always present in adapted trees).
 */
public class CardCelestial extends TheorycraftCard {

    int md1;
    int md2;
    String cat = "BASICS";

    @Override
    public CompoundTag serialize() {
        CompoundTag nbt = super.serialize();
        nbt.putInt("md1", md1);
        nbt.putInt("md2", md2);
        nbt.putString("cat", cat);
        return nbt;
    }

    @Override
    public void deserialize(CompoundTag nbt) {
        super.deserialize(nbt);
        md1 = nbt.getInt("md1");
        md2 = nbt.getInt("md2");
        cat = nbt.getString("cat");
    }

    @Override
    public String getResearchCategory() {
        return cat;
    }

    @Override
    public boolean initialize(Player player, ResearchTableData data) {
        if (data.categoryTotals.isEmpty()) {
            return false;
        }
        Random r = new Random(getSeed());
        md1 = r.nextInt(13);
        md2 = md1;
        while (md1 == md2) {
            md2 = r.nextInt(13);
        }
        int hVal = 0;
        String hKey = "BASICS";
        for (String category : data.categoryTotals.keySet()) {
            int q = data.getTotal(category);
            if (q > hVal) {
                hVal = q;
                hKey = category;
            }
        }
        cat = hKey;
        return cat != null;
    }

    @Override
    public int getInspirationCost() {
        return 1;
    }

    @Override
    public String getLocalizedName() {
        return Component.translatable("card.celestial.name").getString();
    }

    @Override
    public String getLocalizedText() {
        return Component.translatable("card.celestial.text",
                ChatFormatting.BOLD
                        + Component.translatable("tc.research_category." + cat).getString()
                        + ChatFormatting.RESET).getString();
    }

    @Override
    public ItemStack[] getRequiredItems() {
        return new ItemStack[] { new ItemStack(Items.PAPER), new ItemStack(Items.PAPER) };
    }

    @Override
    public boolean[] getRequiredItemsConsumed() {
        return new boolean[] { true, true };
    }

    @Override
    public boolean activate(Player player, ResearchTableData data) {
        data.addTotal(getResearchCategory(), Mth.nextInt(player.getRandom(), 25, 50));
        boolean sun = md1 == 0 || md2 == 0;
        boolean moon = md1 > 4 || md2 > 4;
        boolean stars = (md1 > 0 && md1 < 5) || (md2 > 0 && md2 < 5);
        if (stars) {
            int amt = Mth.nextInt(player.getRandom(), 0, 5);
            data.addTotal("ELDRITCH", amt * 2);
            WarpHelper.addWarpToPlayer(player, Math.max(1, amt), IPlayerWarp.EnumWarpType.TEMPORARY);
        }
        if (sun) {
            data.penaltyStart++;
        }
        if (moon) {
            data.bonusDraws++;
        }
        return true;
    }
}
