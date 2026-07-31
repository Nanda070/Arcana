package arcana.common.crafting;

import arcana.api.aspects.Aspect;
import arcana.api.aspects.AspectList;
import arcana.api.capabilities.ArcanaCapabilities;
import arcana.registry.ModRecipes;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Map;
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

public class CrucibleRecipe implements Recipe<Container> {
    private final ResourceLocation id;
    private final String research;
    private final Ingredient catalyst;
    private final AspectList aspects;
    private final ItemStack result;

    public CrucibleRecipe(ResourceLocation id, String research, Ingredient catalyst, AspectList aspects, ItemStack result) {
        this.id = id;
        this.research = research == null ? "" : research;
        this.catalyst = catalyst;
        this.aspects = aspects == null ? new AspectList() : aspects;
        this.result = result;
    }

    public String getResearch() {
        return research;
    }

    public Ingredient getCatalyst() {
        return catalyst;
    }

    public AspectList getAspects() {
        return aspects;
    }

    public boolean matches(AspectList inTank, ItemStack catalystStack, Player player) {
        if (!catalyst.test(catalystStack)) {
            return false;
        }
        if (!research.isEmpty() && (player == null || !ArcanaCapabilities.getKnowledge(player).isResearchKnown(research))) {
            return false;
        }
        for (Aspect tag : aspects.getAspects()) {
            if (inTank.getAmount(tag) < aspects.getAmount(tag)) {
                return false;
            }
        }
        return true;
    }

    public void consumeAspects(AspectList inTank) {
        for (Aspect tag : aspects.getAspects()) {
            inTank.remove(tag, aspects.getAmount(tag));
        }
    }

    @Override
    public boolean matches(Container container, Level level) {
        return false; // matched manually by crucible BE
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
        return ModRecipes.CRUCIBLE_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.CRUCIBLE_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<CrucibleRecipe> {
        @Override
        public CrucibleRecipe fromJson(ResourceLocation id, JsonObject json) {
            String research = GsonHelper.getAsString(json, "research", "");
            Ingredient catalyst = Ingredient.fromJson(json.get("catalyst"));
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
            return new CrucibleRecipe(id, research, catalyst, aspects, result);
        }

        @Override
        public CrucibleRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            String research = buf.readUtf();
            Ingredient catalyst = Ingredient.fromNetwork(buf);
            AspectList aspects = new AspectList();
            int n = buf.readVarInt();
            for (int i = 0; i < n; i++) {
                Aspect a = Aspect.getAspect(buf.readUtf());
                int amt = buf.readVarInt();
                if (a != null) {
                    aspects.add(a, amt);
                }
            }
            ItemStack result = buf.readItem();
            return new CrucibleRecipe(id, research, catalyst, aspects, result);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, CrucibleRecipe recipe) {
            buf.writeUtf(recipe.research);
            recipe.catalyst.toNetwork(buf);
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
