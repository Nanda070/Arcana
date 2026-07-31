package arcana.common.lib.research.theorycraft;

import arcana.api.aspects.Aspect;
import arcana.api.research.theorycraft.ResearchTableData;
import arcana.api.research.theorycraft.TheorycraftCard;
import java.util.Random;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

/** Simplified: no phial item required. */
public class CardChannel extends TheorycraftCard {

    Aspect aspect;

    @Override
    public CompoundTag serialize() {
        CompoundTag nbt = super.serialize();
        nbt.putString("aspect", aspect == null ? "" : aspect.getTag());
        return nbt;
    }

    @Override
    public void deserialize(CompoundTag nbt) {
        super.deserialize(nbt);
        aspect = Aspect.getAspect(nbt.getString("aspect"));
    }

    @Override
    public boolean initialize(Player player, ResearchTableData data) {
        Random r = new Random(getSeed());
        var compounds = Aspect.getCompoundAspects();
        if (compounds.isEmpty()) {
            return false;
        }
        aspect = compounds.get(r.nextInt(compounds.size()));
        return aspect != null;
    }

    @Override
    public int getInspirationCost() {
        return 1;
    }

    @Override
    public String getResearchCategory() {
        return "ARTIFICE";
    }

    @Override
    public String getLocalizedName() {
        return Component.translatable("card.channel.name",
                ChatFormatting.DARK_BLUE + (aspect == null ? "?" : aspect.getName())
                        + ChatFormatting.RESET + "" + ChatFormatting.BOLD).getString();
    }

    @Override
    public String getLocalizedText() {
        return Component.translatable("card.channel.text",
                ChatFormatting.BOLD + (aspect == null ? "?" : aspect.getName())
                        + ChatFormatting.RESET).getString();
    }

    @Override
    public boolean activate(Player player, ResearchTableData data) {
        data.addTotal(getResearchCategory(), 25);
        return true;
    }
}
