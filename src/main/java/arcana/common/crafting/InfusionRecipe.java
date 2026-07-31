package arcana.common.crafting;

import arcana.api.aspects.Aspect;
import arcana.api.aspects.AspectList;
import arcana.api.capabilities.ArcanaCapabilities;
import arcana.registry.ModRecipes;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.crafting.CraftingHelper;

public class InfusionRecipe implements Recipe<Container> {
    private final ResourceLocation id;
    private final String research;
    private final int vis;
    private final int instability;
    private final Ingredient central;
    private final NonNullList<Ingredient> components;
    private final AspectList aspects;
    private final ItemStack result;

    public InfusionRecipe(ResourceLocation id, String research, int vis, int instability, Ingredient central,
                          NonNullList<Ingredient> components, AspectList aspects, ItemStack result) {
        this.id = id;
        this.research = research == null ? "" : research;
        this.vis = vis;
        this.instability = Math.max(0, instability);
        this.central = central;
        this.components = components;
        this.aspects = aspects == null ? new AspectList() : aspects;
        this.result = result;
    }

    public String getResearch() {
        return research;
    }

    public int getVis() {
        return vis;
    }

    public int getInstability() {
        return instability;
    }

    public Ingredient getCentral() {
        return central;
    }

    public NonNullList<Ingredient> getComponents() {
        return components;
    }

    public int getComponentCount() {
        return components.size();
    }

    public AspectList getAspects() {
        return aspects;
    }

    public boolean matchesInfusion(ItemStack center, List<ItemStack> peripherals, Player player) {
        if (!research.isEmpty() && (player == null || !ArcanaCapabilities.getKnowledge(player).isResearchKnown(research))) {
            return false;
        }
        if (!central.test(center)) {
            return false;
        }
        List<ItemStack> remaining = new ArrayList<>();
        for (ItemStack stack : peripherals) {
            if (!stack.isEmpty()) {
                remaining.add(stack.copy());
            }
        }
        for (Ingredient component : components) {
            boolean found = false;
            for (int i = 0; i < remaining.size(); i++) {
                if (component.test(remaining.get(i))) {
                    remaining.remove(i);
                    found = true;
                    break;
                }
            }
            if (!found) {
                return false;
            }
        }
        return remaining.isEmpty();
    }

    @Override
    public boolean matches(Container container, Level level) {
        return false;
    }

    @Override
    public ItemStack assemble(Container container, RegistryAccess access) {
        return result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int w, int h) {
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess access) {
        return result;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.INFUSION_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.INFUSION_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<InfusionRecipe> {
        @Override
        public InfusionRecipe fromJson(ResourceLocation id, JsonObject json) {
            String research = GsonHelper.getAsString(json, "research", "");
            int vis = GsonHelper.getAsInt(json, "vis", 0);
            int instability = GsonHelper.getAsInt(json, "instability", 0);
            Ingredient central = Ingredient.fromJson(json.get("central"));
            NonNullList<Ingredient> components = NonNullList.create();
            JsonArray arr = GsonHelper.getAsJsonArray(json, "components");
            for (JsonElement el : arr) {
                components.add(Ingredient.fromJson(el));
            }
            AspectList aspects = new AspectList();
            if (json.has("aspects")) {
                JsonObject a = GsonHelper.getAsJsonObject(json, "aspects");
                for (Map.Entry<String, JsonElement> e : a.entrySet()) {
                    Aspect aspect = Aspect.getAspect(e.getKey());
                    if (aspect != null) {
                        aspects.add(aspect, e.getValue().getAsInt());
                    }
                }
            }
            ItemStack result = CraftingHelper.getItemStack(GsonHelper.getAsJsonObject(json, "result"), true);
            return new InfusionRecipe(id, research, vis, instability, central, components, aspects, result);
        }

        @Override
        public InfusionRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            String research = buf.readUtf();
            int vis = buf.readVarInt();
            int instability = buf.readVarInt();
            Ingredient central = Ingredient.fromNetwork(buf);
            int n = buf.readVarInt();
            NonNullList<Ingredient> components = NonNullList.withSize(n, Ingredient.EMPTY);
            for (int i = 0; i < n; i++) {
                components.set(i, Ingredient.fromNetwork(buf));
            }
            AspectList aspects = new AspectList();
            int an = buf.readVarInt();
            for (int i = 0; i < an; i++) {
                Aspect a = Aspect.getAspect(buf.readUtf());
                int amt = buf.readVarInt();
                if (a != null) {
                    aspects.add(a, amt);
                }
            }
            ItemStack result = buf.readItem();
            return new InfusionRecipe(id, research, vis, instability, central, components, aspects, result);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, InfusionRecipe recipe) {
            buf.writeUtf(recipe.research);
            buf.writeVarInt(recipe.vis);
            buf.writeVarInt(recipe.instability);
            recipe.central.toNetwork(buf);
            buf.writeVarInt(recipe.components.size());
            for (Ingredient ingredient : recipe.components) {
                ingredient.toNetwork(buf);
            }
            Aspect[] aspects = recipe.aspects.getAspects();
            buf.writeVarInt(aspects.length);
            for (Aspect a : aspects) {
                buf.writeUtf(a.getTag());
                buf.writeVarInt(recipe.aspects.getAmount(a));
            }
            buf.writeItem(recipe.result);
        }
    }
}
