package arcana.common.items.casters;

import arcana.api.aura.AuraHelper;
import arcana.api.capabilities.ArcanaCapabilities;
import arcana.api.casters.FocusEngine;
import arcana.api.casters.FocusPackage;
import arcana.api.casters.ICaster;
import arcana.common.lib.events.GogglesHelper;
import arcana.config.ArcanaConfig;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class ItemCaster extends Item implements ICaster {
    private static final int NBT_COOLDOWN_TICKS = 10;

    public ItemCaster() {
        super(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON));
    }

    @Override
    public boolean consumeVis(ItemStack casterStack, Player player, float amount, boolean simulate) {
        amount *= (float) ArcanaConfig.COMMON.focusVisCostMultiplier.get().doubleValue();
        int discount = GogglesHelper.getVisDiscount(player);
        if (discount > 0) {
            amount *= (100 - discount) / 100.0f;
        }
        amount = Math.max(amount, 0.1f);
        float available = AuraHelper.getVis(player.level(), player.blockPosition());
        if (available < amount) {
            return false;
        }
        if (simulate) {
            return true;
        }
        float left = amount - AuraHelper.drainVis(player.level(), player.blockPosition(), amount, false);
        return left <= 0.001f;
    }

    @Override
    public ItemStack getFocusStack(ItemStack casterStack) {
        CompoundTag tag = casterStack.getTagElement("focus");
        if (tag == null) {
            return ItemStack.EMPTY;
        }
        return ItemStack.of(tag);
    }

    @Override
    public void setFocus(ItemStack casterStack, ItemStack focus) {
        if (focus == null || focus.isEmpty()) {
            CompoundTag tag = casterStack.getTag();
            if (tag != null) {
                tag.remove("focus");
            }
        } else {
            casterStack.getOrCreateTag().put("focus", focus.save(new CompoundTag()));
        }
    }

    public ItemFocus getFocus(ItemStack casterStack) {
        ItemStack focus = getFocusStack(casterStack);
        if (!focus.isEmpty() && focus.getItem() instanceof ItemFocus itemFocus) {
            return itemFocus;
        }
        return null;
    }

    private static boolean isNbtOnCooldown(ItemStack stack, Level level) {
        CompoundTag tag = stack.getTag();
        if (tag == null || !tag.contains("lastCast")) {
            return false;
        }
        return level.getGameTime() - tag.getLong("lastCast") < NBT_COOLDOWN_TICKS;
    }

    private static void markCast(ItemStack stack, Level level) {
        stack.getOrCreateTag().putLong("lastCast", level.getGameTime());
    }

    private static long remainingCooldown(ItemStack stack, Level level) {
        if (stack.getTag() == null || !stack.getTag().contains("lastCast")) {
            return 0;
        }
        long left = NBT_COOLDOWN_TICKS - (level.getGameTime() - stack.getTag().getLong("lastCast"));
        return Math.max(0, left);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack casterStack = player.getItemInHand(hand);
        InteractionHand other = hand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
        ItemStack otherStack = player.getItemInHand(other);

        // Sneak + focus in other hand: attach / replace focus
        if (player.isShiftKeyDown() && otherStack.getItem() instanceof ItemFocus) {
            if (!level.isClientSide) {
                ItemStack previous = getFocusStack(casterStack);
                setFocus(casterStack, otherStack.copy());
                otherStack.shrink(1);
                if (!previous.isEmpty() && !player.getInventory().add(previous)) {
                    player.drop(previous, false);
                }
            }
            return InteractionResultHolder.sidedSuccess(casterStack, level.isClientSide);
        }

        // Sneak with empty other hand: eject focus
        if (player.isShiftKeyDown() && otherStack.isEmpty() && getFocus(casterStack) != null) {
            if (!level.isClientSide) {
                ItemStack focus = getFocusStack(casterStack);
                setFocus(casterStack, ItemStack.EMPTY);
                if (!player.getInventory().add(focus)) {
                    player.drop(focus, false);
                }
            }
            return InteractionResultHolder.sidedSuccess(casterStack, level.isClientSide);
        }

        ItemFocus focus = getFocus(casterStack);
        ItemStack focusStack = getFocusStack(casterStack);
        if (focus == null || ItemFocus.getPackage(focusStack) == null
                || CasterManager.isOnCooldown(player) || isNbtOnCooldown(casterStack, level)) {
            return InteractionResultHolder.pass(casterStack);
        }

        if (!level.isClientSide) {
            var knowledge = ArcanaCapabilities.getKnowledge(player);
            if (!knowledge.isResearchComplete("FIRSTSTEPS") && !knowledge.isResearchKnown("BASEAUROMANCY")) {
                player.displayClientMessage(Component.translatable("arcana.cast.locked"), true);
                return InteractionResultHolder.fail(casterStack);
            }
            FocusPackage preview = ItemFocus.getPackage(focusStack);
            if (preview != null) {
                for (String node : preview.getNodes()) {
                    String req = FocusPackage.researchForNode(node);
                    if (!knowledge.isResearchKnown(req) && !knowledge.isResearchComplete(req)) {
                        if ("BASEAUROMANCY".equals(req) && knowledge.isResearchComplete("FIRSTSTEPS")) {
                            continue;
                        }
                        player.displayClientMessage(Component.translatable(
                                "arcana.cast.node_locked", req), true);
                        return InteractionResultHolder.fail(casterStack);
                    }
                }
            }
        }

        int activation = Math.max(NBT_COOLDOWN_TICKS, focus.getActivationTime(focusStack));
        CasterManager.setCooldown(player, activation);
        markCast(casterStack, level);
        if (level.isClientSide) {
            return InteractionResultHolder.success(casterStack);
        }

        FocusPackage core = ItemFocus.getPackage(focusStack);
        if (consumeVis(casterStack, player, focus.getVisCost(focusStack), false)) {
            FocusEngine.castFocusPackage(player, core);
            playCastFx(player);
            player.swing(hand);
            return InteractionResultHolder.success(casterStack);
        }
        return InteractionResultHolder.fail(casterStack);
    }

    private static void playCastFx(Player player) {
        Level level = player.level();
        level.playSound(null, player.blockPosition().above(), SoundEvents.ILLUSIONER_CAST_SPELL,
                SoundSource.PLAYERS, 0.7f, 1.0f + (float) (player.getRandom().nextGaussian() * 0.05));
        if (level instanceof ServerLevel server) {
            Vec3 look = player.getLookAngle();
            Vec3 at = player.getEyePosition().add(look.scale(1.2));
            server.sendParticles(ParticleTypes.CRIT, at.x, at.y, at.z, 10, 0.15, 0.15, 0.15, 0.08);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        ItemStack focus = getFocusStack(stack);
        if (!focus.isEmpty() && focus.getItem() instanceof ItemFocus itemFocus) {
            tooltip.add(Component.literal("Vis cost: " + String.format("%.1f", itemFocus.getVisCost(focus)))
                    .withStyle(ChatFormatting.AQUA));
            tooltip.add(focus.getHoverName().copy().withStyle(ChatFormatting.GREEN, ChatFormatting.ITALIC));
            itemFocus.appendHoverText(focus, level, tooltip, flag);
        } else {
            tooltip.add(Component.literal("No focus (sneak + focus in offhand)").withStyle(ChatFormatting.GRAY));
        }
        if (level != null) {
            long left = remainingCooldown(stack, level);
            if (left > 0) {
                tooltip.add(Component.translatable("arcana.caster.cooldown", left)
                        .withStyle(ChatFormatting.DARK_GRAY));
            }
        }
    }
}
