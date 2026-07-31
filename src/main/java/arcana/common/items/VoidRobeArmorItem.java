package arcana.common.items;

import arcana.api.items.IVisDiscountGear;
import arcana.common.lib.events.GogglesHelper;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class VoidRobeArmorItem extends VoidArmorItem implements IVisDiscountGear {
    public VoidRobeArmorItem(ArmorMaterial material, Type type, Properties properties) {
        super(material, type, properties);
    }

    @Override
    public int getWarp(ItemStack stack, Player player) {
        int warp = 2;
        // Set bonus: +1 warp once when wearing 2+ robe pieces (applied on primary piece)
        if (GogglesHelper.hasVoidRobeSetBonus(player) && isPrimaryRobePiece(player)) {
            warp += 1;
        }
        return warp;
    }

    @Override
    public int getVisDiscount(ItemStack stack, Player player) {
        // Set bonus: +5% vis discount once when 2+ robe pieces equipped
        if (!GogglesHelper.hasVoidRobeSetBonus(player) || !isPrimaryRobePiece(player)) {
            return 0;
        }
        return 5;
    }

    private boolean isPrimaryRobePiece(Player player) {
        for (EquipmentSlot slot : new EquipmentSlot[]{
                EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET}) {
            ItemStack worn = player.getItemBySlot(slot);
            if (worn.getItem() instanceof VoidRobeArmorItem) {
                return worn.getItem() == this;
            }
        }
        return false;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("item.arcana.warping", 2).withStyle(ChatFormatting.DARK_PURPLE));
        tooltip.add(Component.translatable("item.arcana.void_robe.set_bonus").withStyle(ChatFormatting.GRAY));
    }
}
