package arcana.common.items.baubles;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

/**
 * J5: inventory charm — once per ~6000 ticks, if health is low, grant absorption.
 * Works without Curios runtime; optional charm tag for Curios soft-dep.
 */
public class ItemCharmUndying extends Item {
    private static final String LAST_PROC = "ArcanaCharmProc";
    private static final int COOLDOWN = 6000;

    public ItemCharmUndying() {
        super(new Item.Properties().stacksTo(1).rarity(Rarity.RARE));
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (level.isClientSide || !(entity instanceof Player player)) {
            return;
        }
        if (player.getHealth() > player.getMaxHealth() * 0.35f) {
            return;
        }
        CompoundTag tag = stack.getOrCreateTag();
        long last = tag.getLong(LAST_PROC);
        long now = level.getGameTime();
        if (now - last < COOLDOWN) {
            return;
        }
        tag.putLong(LAST_PROC, now);
        player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 200, 1));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("item.arcana.charm_undying.desc").withStyle(ChatFormatting.GRAY));
    }
}
