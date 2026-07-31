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

public class ItemGoggles extends ArmorItem implements IGoggles, IVisDiscountGear {
    public ItemGoggles() {
        super(GogglesArmorMaterial.INSTANCE, Type.HELMET,
                new Properties().rarity(Rarity.RARE).durability(350));
    }

    @Override
    public boolean showIngamePopups(ItemStack stack, LivingEntity player) {
        return true;
    }

    @Override
    public int getVisDiscount(ItemStack stack, Player player) {
        return 5;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("item.arcana.goggles.desc").withStyle(ChatFormatting.DARK_PURPLE));
        tooltip.add(Component.translatable("tc.visdiscount", 5).withStyle(ChatFormatting.DARK_PURPLE));
        super.appendHoverText(stack, level, tooltip, flag);
    }
}
