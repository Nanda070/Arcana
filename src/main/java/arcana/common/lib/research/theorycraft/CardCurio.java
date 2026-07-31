package arcana.common.lib.research.theorycraft;

import arcana.api.research.ResearchCategories;
import arcana.api.research.theorycraft.ResearchTableData;
import arcana.api.research.theorycraft.TheorycraftCard;
import java.util.ArrayList;
import java.util.Random;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/** Uses paper when no dedicated curio item exists in Arcana. */
public class CardCurio extends TheorycraftCard {

    ItemStack curio = ItemStack.EMPTY;

    @Override
    public CompoundTag serialize() {
        CompoundTag nbt = super.serialize();
        nbt.put("stack", curio.save(new CompoundTag()));
        return nbt;
    }

    @Override
    public void deserialize(CompoundTag nbt) {
        super.deserialize(nbt);
        curio = ItemStack.of(nbt.getCompound("stack"));
    }

    @Override
    public int getInspirationCost() {
        return 1;
    }

    @Override
    public String getLocalizedName() {
        return Component.translatable("card.curio.name").getString();
    }

    @Override
    public String getLocalizedText() {
        return Component.translatable("card.curio.text").getString();
    }

    @Override
    public ItemStack[] getRequiredItems() {
        return new ItemStack[] { curio };
    }

    @Override
    public boolean[] getRequiredItemsConsumed() {
        return new boolean[] { true };
    }

    @Override
    public boolean initialize(Player player, ResearchTableData data) {
        Random r = new Random(getSeed());
        ArrayList<ItemStack> curios = new ArrayList<>();
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (!stack.isEmpty() && stack.is(Items.PAPER)) {
                ItemStack c = stack.copy();
                c.setCount(1);
                curios.add(c);
            }
        }
        if (!curios.isEmpty()) {
            curio = curios.get(r.nextInt(curios.size()));
        }
        return !curio.isEmpty();
    }

    @Override
    public boolean activate(Player player, ResearchTableData data) {
        data.addTotal("BASICS", 5);
        String[] s = ResearchCategories.researchCategories.keySet().toArray(new String[0]);
        data.addTotal(s[player.getRandom().nextInt(s.length)], 5);
        data.addTotal("BASICS", Mth.nextInt(player.getRandom(), 25, 35));
        if (player.getRandom().nextBoolean()) {
            data.bonusDraws++;
        }
        if (player.getRandom().nextBoolean()) {
            data.bonusDraws++;
        }
        return true;
    }
}
