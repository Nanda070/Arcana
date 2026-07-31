package arcana.common.items;

import arcana.api.aspects.Aspect;
import arcana.common.blockentities.AlembicBlockEntity;
import arcana.common.blockentities.WardedJarBlockEntity;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

/** Blank label: apply to jar/alembic to lock aspect filter to current contents. */
public class ItemLabel extends Item {
    public ItemLabel() {
        super(new Item.Properties().stacksTo(64));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        BlockPos pos = context.getClickedPos();
        BlockEntity be = level.getBlockEntity(pos);
        Aspect filter = null;
        if (be instanceof WardedJarBlockEntity jar) {
            filter = jar.getAspect();
            if (filter == null) {
                return InteractionResult.FAIL;
            }
            jar.setAspectFilter(filter);
        } else if (be instanceof AlembicBlockEntity alembic) {
            filter = alembic.getAspect();
            if (filter == null) {
                return InteractionResult.FAIL;
            }
            alembic.setAspectFilter(filter);
        } else {
            return InteractionResult.PASS;
        }

        if (context.getPlayer() != null && !context.getPlayer().getAbilities().instabuild) {
            context.getItemInHand().shrink(1);
        }
        if (context.getPlayer() != null) {
            context.getPlayer().displayClientMessage(
                    Component.translatable("arcana.label.applied", filter.getName()), true);
        }
        return InteractionResult.CONSUME;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tip, TooltipFlag flag) {
        tip.add(Component.translatable("arcana.label.tooltip").withStyle(ChatFormatting.GRAY));
    }
}
