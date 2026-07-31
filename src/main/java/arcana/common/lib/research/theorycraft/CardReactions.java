package arcana.common.lib.research.theorycraft;

import arcana.api.aspects.Aspect;
import arcana.api.research.theorycraft.ResearchTableData;
import arcana.api.research.theorycraft.TheorycraftCard;
import arcana.registry.ModItems;
import java.util.List;
import java.util.Random;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/** Simplified: present two primal crystals for ALCHEMY progress. */
public class CardReactions extends TheorycraftCard {

    Aspect aspect1;
    Aspect aspect2;

    @Override
    public CompoundTag serialize() {
        CompoundTag nbt = super.serialize();
        nbt.putString("aspect1", aspect1 == null ? "" : aspect1.getTag());
        nbt.putString("aspect2", aspect2 == null ? "" : aspect2.getTag());
        return nbt;
    }

    @Override
    public void deserialize(CompoundTag nbt) {
        super.deserialize(nbt);
        aspect1 = Aspect.getAspect(nbt.getString("aspect1"));
        aspect2 = Aspect.getAspect(nbt.getString("aspect2"));
    }

    @Override
    public boolean initialize(Player player, ResearchTableData data) {
        List<Aspect> primals = Aspect.getPrimalAspects();
        if (primals.size() < 2) {
            return false;
        }
        Random r = new Random(getSeed());
        int a = r.nextInt(primals.size());
        int b = a;
        while (b == a) {
            b = r.nextInt(primals.size());
        }
        aspect1 = primals.get(a);
        aspect2 = primals.get(b);
        return true;
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
        return Component.translatable("card.reactions.name").getString();
    }

    @Override
    public String getLocalizedText() {
        return Component.translatable("card.reactions.text",
                ChatFormatting.BOLD + (aspect1 == null ? "?" : aspect1.getName()) + ChatFormatting.RESET,
                ChatFormatting.BOLD + (aspect2 == null ? "?" : aspect2.getName()) + ChatFormatting.RESET).getString();
    }

    @Override
    public ItemStack[] getRequiredItems() {
        if (aspect1 == null || aspect2 == null) {
            return new ItemStack[] { ItemStack.EMPTY, ItemStack.EMPTY };
        }
        return new ItemStack[] {
                new ItemStack(ModItems.crystalFor(aspect1).get()),
                new ItemStack(ModItems.crystalFor(aspect2).get())
        };
    }

    @Override
    public boolean activate(Player player, ResearchTableData data) {
        data.addTotal(getResearchCategory(), 25);
        if (player.getRandom().nextFloat() < 0.33f) {
            data.addInspiration(1);
        }
        return true;
    }
}
