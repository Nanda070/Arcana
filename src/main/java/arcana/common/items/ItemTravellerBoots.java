package arcana.common.items;

import java.util.List;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

/** J5: boots with step-assist and slight speed when worn. */
public class ItemTravellerBoots extends ArmorItem {
    private static final UUID SPEED_UUID = UUID.fromString("a7c3e9b1-4d2f-4a8e-9c1b-6e5d4f3a2b10");
    private static final AttributeModifier SPEED_MOD =
            new AttributeModifier(SPEED_UUID, "Traveller boots speed", 0.08, AttributeModifier.Operation.ADDITION);

    public ItemTravellerBoots() {
        super(ThaumiumArmorMaterial.INSTANCE, Type.BOOTS,
                new Properties().rarity(Rarity.UNCOMMON).durability(400));
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, net.minecraft.world.entity.Entity entity, int slotId, boolean isSelected) {
        if (level.isClientSide || !(entity instanceof Player player)) {
            return;
        }
        boolean worn = player.getInventory().getArmor(0) == stack;
        AttributeInstance speed = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speed == null) {
            return;
        }
        if (worn) {
            player.setMaxUpStep(1.0f);
            if (!speed.hasModifier(SPEED_MOD)) {
                speed.addTransientModifier(SPEED_MOD);
            }
        } else if (speed.hasModifier(SPEED_MOD)) {
            speed.removeModifier(SPEED_MOD);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("item.arcana.traveller_boots.desc").withStyle(ChatFormatting.GRAY));
        super.appendHoverText(stack, level, tooltip, flag);
    }
}
