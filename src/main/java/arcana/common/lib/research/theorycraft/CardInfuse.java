package arcana.common.lib.research.theorycraft;

import arcana.api.aspects.Aspect;
import arcana.api.aspects.AspectHelper;
import arcana.api.research.theorycraft.ResearchTableData;
import arcana.api.research.theorycraft.TheorycraftCard;
import arcana.registry.ModItems;
import java.util.Random;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

/** Simplified: no phial requirement (Arcana has no filled phials yet). */
public class CardInfuse extends TheorycraftCard {

    Aspect aspect;
    ItemStack stack = ItemStack.EMPTY;
    private static final ItemStack[] OPTIONS = new ItemStack[] {
            new ItemStack(ModItems.ALUMENTUM.get()),
            new ItemStack(ModItems.NITOR.get()),
            new ItemStack(ModItems.SALIS_MUNDUS.get()),
            new ItemStack(ModItems.THAUMIUM_INGOT.get()),
            new ItemStack(Items.GOLD_INGOT),
            new ItemStack(Items.IRON_INGOT),
            new ItemStack(Items.DIAMOND),
            new ItemStack(Items.EMERALD),
            new ItemStack(Items.BLAZE_ROD),
            new ItemStack(Items.LEATHER),
            new ItemStack(Blocks.WHITE_WOOL),
            new ItemStack(Items.BRICK),
            new ItemStack(Items.ARROW),
            new ItemStack(Items.EGG),
            new ItemStack(Items.FEATHER),
            new ItemStack(Items.GLOWSTONE_DUST),
            new ItemStack(Items.REDSTONE),
            new ItemStack(Items.GHAST_TEAR),
            new ItemStack(Items.GUNPOWDER),
            new ItemStack(Items.BOW),
            new ItemStack(Items.QUARTZ),
            new ItemStack(Items.APPLE)
    };

    @Override
    public CompoundTag serialize() {
        CompoundTag nbt = super.serialize();
        nbt.putString("aspect", aspect == null ? "" : aspect.getTag());
        nbt.put("stack", stack.save(new CompoundTag()));
        return nbt;
    }

    @Override
    public void deserialize(CompoundTag nbt) {
        super.deserialize(nbt);
        aspect = Aspect.getAspect(nbt.getString("aspect"));
        stack = ItemStack.of(nbt.getCompound("stack"));
    }

    @Override
    public boolean initialize(Player player, ResearchTableData data) {
        Random r = new Random(getSeed());
        var compounds = Aspect.getCompoundAspects();
        if (compounds.isEmpty()) {
            return false;
        }
        aspect = compounds.get(r.nextInt(compounds.size()));
        stack = OPTIONS[r.nextInt(OPTIONS.length)].copy();
        return aspect != null && !stack.isEmpty();
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
        return Component.translatable("card.infuse.name").getString();
    }

    @Override
    public String getLocalizedText() {
        return Component.translatable("card.infuse.text",
                ChatFormatting.BOLD + (aspect == null ? "?" : aspect.getName()) + ChatFormatting.RESET,
                stack.getHoverName().getString(),
                getVal()).getString();
    }

    private int getVal() {
        int q = 10;
        try {
            var al = AspectHelper.getObjectAspects(stack);
            if (al != null) {
                q += (int) (Math.sqrt(al.visSize()) * 1.5);
            }
        } catch (Exception ignored) {
        }
        return q;
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
        data.addTotal(getResearchCategory(), getVal());
        return true;
    }
}
