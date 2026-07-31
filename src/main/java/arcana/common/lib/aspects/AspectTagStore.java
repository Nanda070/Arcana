package arcana.common.lib.aspects;

import arcana.api.aspects.AspectList;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

public final class AspectTagStore {
    private static final Map<ResourceLocation, AspectList> ITEM_TAGS = new HashMap<>();
    private static final Map<ResourceLocation, AspectList> ENTITY_TAGS = new HashMap<>();

    private AspectTagStore() {
    }

    public static void clear() {
        ITEM_TAGS.clear();
        ENTITY_TAGS.clear();
    }

    public static void registerItem(ResourceLocation id, AspectList aspects) {
        if (id != null && aspects != null && aspects.size() > 0) {
            ITEM_TAGS.put(id, aspects.copy());
        }
    }

    public static void registerItem(Item item, AspectList aspects) {
        ResourceLocation id = ForgeRegistries.ITEMS.getKey(item);
        if (id != null) {
            registerItem(id, aspects);
        }
    }

    public static void registerEntity(ResourceLocation id, AspectList aspects) {
        if (id != null && aspects != null && aspects.size() > 0) {
            ENTITY_TAGS.put(id, aspects.copy());
        }
    }

    public static AspectList getItemAspects(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return new AspectList();
        }
        ResourceLocation id = ForgeRegistries.ITEMS.getKey(stack.getItem());
        if (id == null) {
            return new AspectList();
        }
        AspectList list = ITEM_TAGS.get(id);
        return list == null ? new AspectList() : list.copy();
    }

    public static AspectList getEntityAspects(ResourceLocation id) {
        if (id == null) {
            return new AspectList();
        }
        AspectList list = ENTITY_TAGS.get(id);
        return list == null ? new AspectList() : list.copy();
    }
}
