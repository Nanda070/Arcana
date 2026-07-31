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

public class ItemAmuletVis extends Item implements IVisDiscountGear {
    public ItemAmuletVis() {
        super(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON));
    }

    @Override
    public int getVisDiscount(ItemStack stack, Player player) {
        return 8;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tc.visdiscount", 8).withStyle(ChatFormatting.DARK_PURPLE));
        tooltip.add(Component.translatable("item.arcana.amulet_vis.desc").withStyle(ChatFormatting.GRAY));
    }
}
