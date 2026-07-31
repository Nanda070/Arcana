package arcana.common.items;

import arcana.api.items.IGoggles;
import arcana.api.items.IVisDiscountGear;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

/** J5: improved goggles — reveal + higher vis discount. */
public class ItemGogglesAdvanced extends ArmorItem implements IGoggles, IVisDiscountGear {
    public ItemGogglesAdvanced() {
        super(GogglesArmorMaterial.INSTANCE, Type.HELMET,
                new Properties().rarity(Rarity.EPIC).durability(500));
    }

    @Override
    public boolean showIngamePopups(ItemStack stack, LivingEntity player) {
        return true;
    }

    @Override
    public int getVisDiscount(ItemStack stack, Player player) {
        return 10;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("item.arcana.goggles_advanced.desc").withStyle(ChatFormatting.DARK_PURPLE));
        tooltip.add(Component.translatable("tc.visdiscount", 10).withStyle(ChatFormatting.DARK_PURPLE));
        super.appendHoverText(stack, level, tooltip, flag);
    }
}
