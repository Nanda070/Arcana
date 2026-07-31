package arcana.common.lib.research.theorycraft;

import arcana.api.research.theorycraft.ResearchTableData;
import arcana.api.research.theorycraft.TheorycraftCard;
import java.util.Random;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

/** Simplified: consume a common machine/tool item for ARTIFICE progress. */
public class CardMindOverMatter extends TheorycraftCard {

    private static final ItemStack[] OPTIONS = {
            new ItemStack(Blocks.ANVIL),
            new ItemStack(Blocks.DISPENSER),
            new ItemStack(Blocks.DROPPER),
            new ItemStack(Blocks.ENCHANTING_TABLE),
            new ItemStack(Blocks.ENDER_CHEST),
            new ItemStack(Blocks.JUKEBOX),
            new ItemStack(Blocks.DAYLIGHT_DETECTOR),
            new ItemStack(Blocks.PISTON),
            new ItemStack(Blocks.HOPPER),
            new ItemStack(Blocks.STICKY_PISTON),
            new ItemStack(Items.MAP),
            new ItemStack(Items.COMPASS),
            new ItemStack(Items.COMPARATOR),
            new ItemStack(Items.CLOCK)
    };

    ItemStack stack = ItemStack.EMPTY;

    @Override
    public CompoundTag serialize() {
        CompoundTag nbt = super.serialize();
        nbt.put("stack", stack.save(new CompoundTag()));
        return nbt;
    }

    @Override
    public void deserialize(CompoundTag nbt) {
        super.deserialize(nbt);
        stack = ItemStack.of(nbt.getCompound("stack"));
    }

    @Override
    public boolean initialize(Player player, ResearchTableData data) {
        Random r = new Random(getSeed());
        stack = OPTIONS[r.nextInt(OPTIONS.length)].copy();
        return !stack.isEmpty();
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
        return Component.translatable("card.mindmatter.name").getString();
    }

    @Override
    public String getLocalizedText() {
        return Component.translatable("card.mindmatter.text", 15).getString();
    }

    @Override
    public ItemStack[] getRequiredItems() {
        return new ItemStack[] { stack };
    }

    @Override
    public boolean[] getRequiredItemsConsumed() {
        return new boolean[] { true };
    }

    @Override
    public boolean activate(Player player, ResearchTableData data) {
        data.addTotal(getResearchCategory(), 15);
        return true;
    }
}
