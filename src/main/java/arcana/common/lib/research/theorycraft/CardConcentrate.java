package arcana.common.lib.research.theorycraft;

import arcana.api.aspects.Aspect;
import arcana.api.research.theorycraft.ResearchTableData;
import arcana.api.research.theorycraft.TheorycraftCard;
import arcana.registry.ModItems;
import java.util.Random;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/** Uses primal crystals (Arcana has no compound crystals yet). */
public class CardConcentrate extends TheorycraftCard {

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
        var primals = Aspect.getPrimalAspects();
        if (primals.isEmpty()) {
            return false;
        }
        aspect = primals.get(r.nextInt(primals.size()));
        return aspect != null;
    }

    @Override
    public int getInspirationCost() {
        return 1;
    }

    @Override
    public String getResearchCategory() {
        return "ALCHEMY";
    }

    @Override
    public String getLocalizedName() {
        return Component.translatable("card.concentrate.name").getString();
    }

    @Override
    public String getLocalizedText() {
        return Component.translatable("card.concentrate.text",
                ChatFormatting.BOLD + (aspect == null ? "?" : aspect.getName())
                        + ChatFormatting.RESET).getString();
    }

    @Override
    public ItemStack[] getRequiredItems() {
        if (aspect == null) {
            return new ItemStack[] { ItemStack.EMPTY };
        }
        return new ItemStack[] { new ItemStack(ModItems.crystalFor(aspect).get()) };
    }

    @Override
    public boolean activate(Player player, ResearchTableData data) {
        data.addTotal(getResearchCategory(), 15);
        data.bonusDraws++;
        if (player.getRandom().nextFloat() < 0.33f) {
            data.addInspiration(1);
        }
        return true;
    }
}
