package arcana.common.lib.research.theorycraft;

import arcana.api.aspects.AspectHelper;
import arcana.api.research.theorycraft.ResearchTableData;
import arcana.api.research.theorycraft.TheorycraftCard;
import arcana.registry.ModItems;
import java.util.Random;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

public class CardTinker extends TheorycraftCard {

    ItemStack stack = ItemStack.EMPTY;
    private static final ItemStack[] OPTIONS = new ItemStack[] {
            new ItemStack(ModItems.THAUMOMETER.get()),
            new ItemStack(Blocks.ANVIL),
            new ItemStack(Blocks.ACTIVATOR_RAIL),
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
            new ItemStack(Items.TNT_MINECART),
            new ItemStack(Items.COMPARATOR),
            new ItemStack(Items.CLOCK)
    };

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

    private int getVal() {
        int q = 0;
        try {
            var al = AspectHelper.getObjectAspects(stack);
            if (al != null) {
                q += (int) Math.sqrt(al.visSize());
            }
        } catch (Exception ignored) {
        }
        return Math.max(q, 5);
    }

    @Override
    public String getLocalizedName() {
        return Component.translatable("card.tinker.name").getString();
    }

    @Override
    public String getLocalizedText() {
        int a = getVal() * 2;
        int b = a + 10;
        return Component.translatable("card.tinker.text", a, b).getString();
    }

    @Override
    public ItemStack[] getRequiredItems() {
        return new ItemStack[] { stack };
    }

    @Override
    public boolean activate(Player player, ResearchTableData data) {
        int q = getVal() * 2;
        data.addTotal(getResearchCategory(), Mth.nextInt(player.getRandom(), q, q + 10));
        return true;
    }
}
