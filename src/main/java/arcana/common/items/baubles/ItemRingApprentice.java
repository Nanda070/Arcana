package arcana.common.items.baubles;

import arcana.api.items.IVisDiscountGear;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class ItemRingApprentice extends Item implements IVisDiscountGear {
    public ItemRingApprentice() {
        super(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON));
    }

    @Override
    public int getVisDiscount(ItemStack stack, Player player) {
        return 5;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tc.visdiscount", 5).withStyle(ChatFormatting.DARK_PURPLE));
        tooltip.add(Component.translatable("item.arcana.ring_apprentice.desc").withStyle(ChatFormatting.GRAY));
    }
}
