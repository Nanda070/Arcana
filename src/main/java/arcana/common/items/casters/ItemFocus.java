package arcana.common.items.casters;

import arcana.api.casters.FocusPackage;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class ItemFocus extends Item {
    private final int maxComplexity;

    public ItemFocus(int maxComplexity) {
        super(new Item.Properties().stacksTo(1).rarity(Rarity.RARE));
        this.maxComplexity = maxComplexity;
    }

    public static void setPackage(ItemStack focusStack, FocusPackage core) {
        focusStack.getOrCreateTag().put("package", core.serialize());
    }

    public static FocusPackage getPackage(ItemStack focusStack) {
        if (focusStack.isEmpty()) {
            return null;
        }
        CompoundTag tag = focusStack.getTagElement("package");
        if (tag == null) {
            return null;
        }
        FocusPackage p = new FocusPackage();
        p.deserialize(tag);
        return p;
    }

    public static ItemStack createProgrammed(ItemFocus item, FocusPackage pkg) {
        ItemStack stack = new ItemStack(item);
        setPackage(stack, pkg);
        return stack;
    }

    public float getVisCost(ItemStack focusStack) {
        FocusPackage p = getPackage(focusStack);
        return p == null ? 0.0f : p.getVisCost();
    }

    public int getActivationTime(ItemStack focusStack) {
        FocusPackage p = getPackage(focusStack);
        return p == null ? 0 : p.getActivationTicks();
    }

    public int getMaxComplexity() {
        return maxComplexity;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        FocusPackage p = getPackage(stack);
        if (p != null) {
            tooltip.add(Component.literal(String.format("%.1f", p.getVisCost()) + " vis")
                    .withStyle(ChatFormatting.AQUA));
            tooltip.add(Component.literal(p.describe()).withStyle(ChatFormatting.DARK_PURPLE));
        } else {
            tooltip.add(Component.literal("Blank focus").withStyle(ChatFormatting.GRAY));
        }
    }
}
