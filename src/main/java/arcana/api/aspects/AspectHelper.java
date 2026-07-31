package arcana.api.aspects;

import arcana.common.lib.aspects.AspectTagStore;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

public final class AspectHelper {
    private AspectHelper() {
    }

    public static AspectList cullTags(AspectList temp) {
        return cullTags(temp, 7);
    }

    public static AspectList cullTags(AspectList temp, int cap) {
        AspectList temp2 = new AspectList();
        for (Aspect tag : temp.getAspects()) {
            if (tag != null) {
                temp2.add(tag, temp.getAmount(tag));
            }
        }
        while (temp2.size() > cap) {
            Aspect lowest = null;
            float low = Short.MAX_VALUE;
            for (Aspect tag : temp2.getAspects()) {
                if (tag == null) {
                    continue;
                }
                float ta = temp2.getAmount(tag);
                if (tag.isPrimal()) {
                    ta *= 0.9f;
                } else if (tag.getComponents() != null && tag.getComponents().length == 2) {
                    if (!tag.getComponents()[0].isPrimal()) {
                        ta *= 1.1f;
                    }
                    if (!tag.getComponents()[1].isPrimal()) {
                        ta *= 1.1f;
                    }
                }
                if (ta < low) {
                    low = ta;
                    lowest = tag;
                }
            }
            if (lowest == null) {
                break;
            }
            temp2.aspects.remove(lowest);
        }
        return temp2;
    }

    public static AspectList getObjectAspects(ItemStack stack) {
        return AspectTagStore.getItemAspects(stack);
    }

    public static AspectList getEntityAspects(Entity entity) {
        if (entity == null) {
            return new AspectList();
        }
        ResourceLocation id = EntityType.getKey(entity.getType());
        return AspectTagStore.getEntityAspects(id);
    }

    public static AspectList generateTags(ItemStack stack) {
        return getObjectAspects(stack);
    }

    public static ResourceLocation itemKey(ItemStack stack) {
        return ForgeRegistries.ITEMS.getKey(stack.getItem());
    }
}
