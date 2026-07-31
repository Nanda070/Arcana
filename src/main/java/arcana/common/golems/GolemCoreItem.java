package arcana.common.golems;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;

/** J4: right-click golem to apply gather-range or guard-damage upgrade. */
public class GolemCoreItem extends Item {
    public enum CoreType {
        GATHER,
        GUARD
    }

    private final CoreType type;

    public GolemCoreItem(CoreType type) {
        super(new Item.Properties().stacksTo(16).rarity(Rarity.UNCOMMON));
        this.type = type;
    }

    public CoreType getCoreType() {
        return type;
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
        if (!(target instanceof ArcanaGolem golem)) {
            return InteractionResult.PASS;
        }
        if (golem.getOwnerUUID() == null || !player.getUUID().equals(golem.getOwnerUUID())) {
            return InteractionResult.FAIL;
        }
        if (player.level().isClientSide) {
            return InteractionResult.SUCCESS;
        }
        if (type == CoreType.GATHER) {
            int range = golem.getGatherRange() + 2;
            golem.setGatherRange(Math.min(12, range));
            player.displayClientMessage(Component.translatable("arcana.golem.core.gather", golem.getGatherRange()), true);
        } else {
            AttributeInstance damage = golem.getAttribute(Attributes.ATTACK_DAMAGE);
            if (damage != null) {
                damage.setBaseValue(damage.getBaseValue() + 1.0);
            }
            golem.setGuardBonus(golem.getGuardBonus() + 1);
            player.displayClientMessage(Component.translatable("arcana.golem.core.guard", golem.getGuardBonus()), true);
        }
        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }
        return InteractionResult.CONSUME;
    }
}
