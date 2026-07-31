package arcana.common.lib.research;

import arcana.api.aspects.Aspect;
import arcana.api.aspects.AspectHelper;
import arcana.api.aspects.AspectList;
import arcana.api.research.IScanThing;
import arcana.api.research.ScanningManager;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;

public class ScanGeneric implements IScanThing {
    @Override
    public boolean checkThing(Player player, Object object) {
        AspectList list = aspectsOf(player, object);
        return list != null && list.size() > 0;
    }

    @Override
    public String getResearchKey(Player player, Object object) {
        if (object instanceof Entity entity && !(object instanceof ItemEntity)) {
            ResourceLocation id = EntityType.getKey(entity.getType());
            return id == null ? null : "!" + id;
        }
        ItemStack stack = stackOf(player, object);
        if (stack.isEmpty()) {
            return null;
        }
        ResourceLocation id = ForgeRegistries.ITEMS.getKey(stack.getItem());
        return id == null ? null : "!" + id;
    }

    public static AspectList aspectsOf(Player player, Object object) {
        if (object instanceof Entity entity && !(object instanceof ItemEntity)) {
            return AspectHelper.getEntityAspects(entity);
        }
        ItemStack stack = stackOf(player, object);
        return AspectHelper.getObjectAspects(stack);
    }

    public static ItemStack stackOf(Player player, Object object) {
        if (object instanceof ItemStack stack) {
            return stack;
        }
        if (object instanceof ItemEntity itemEntity) {
            return itemEntity.getItem();
        }
        if (object instanceof BlockPos pos && player.level() != null) {
            BlockState state = player.level().getBlockState(pos);
            return state.getBlock().asItem().getDefaultInstance();
        }
        return ItemStack.EMPTY;
    }
}
