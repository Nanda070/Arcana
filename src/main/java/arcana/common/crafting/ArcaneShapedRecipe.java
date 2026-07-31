package arcana.common.crafting;

import arcana.api.aspects.Aspect;
import arcana.api.aspects.AspectList;
import arcana.api.capabilities.ArcanaCapabilities;
import arcana.common.blockentities.ArcaneWorkbenchBlockEntity;
import arcana.registry.ModItems;
import arcana.registry.ModRecipes;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.crafting.CraftingHelper;

public class ArcaneShapedRecipe implements Recipe<Container> {
    private final ResourceLocation id;
    private final String research;
    private final int vis;
    private final AspectList crystals;
    private final int width;
    private final int height;
    private final NonNullList<Ingredient> ingredients;
    private final ItemStack result;

    public ArcaneShapedRecipe(ResourceLocation id, String research, int vis, AspectList crystals,
                              int width, int height, NonNullList<Ingredient> ingredients, ItemStack result) {
        this.id = id;
        this.research = research == null ? "" : research;
        this.vis = vis;
        this.crystals = crystals == null ? new AspectList() : crystals;
        this.width = width;
        this.height = height;
        this.ingredients = ingredients;
        this.result = result;
    }

    public String getResearch() {
        return research;
    }

    public int getVis() {
        return vis;
    }

    public AspectList getCrystals() {
        return crystals;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    @Override
    public boolean matches(Container container, Level level) {
        if (!(container instanceof ArcaneWorkbenchContainer inv)) {
            return false;
        }
        for (int y = 0; y <= 3 - height; y++) {
            for (int x = 0; x <= 3 - width; x++) {
                if (matchesPattern(inv, x, y, false) || matchesPattern(inv, x, y, true)) {
                    return matchesCrystals(inv);
                }
            }
        }
        return false;
    }

    private boolean matchesPattern(ArcaneWorkbenchContainer inv, int xOffset, int yOffset, boolean mirrored) {
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                Ingredient ingredient = Ingredient.EMPTY;
                int rx = x - xOffset;
                int ry = y - yOffset;
                if (rx >= 0 && ry >= 0 && rx < width && ry < height) {
                    if (mirrored) {
                        ingredient = ingredients.get(width - rx - 1 + ry * width);
                    } else {
                        ingredient = ingredients.get(rx + ry * width);
                    }
                }
                if (!ingredient.test(inv.getCraftItem(x + y * 3))) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean matchesCrystals(ArcaneWorkbenchContainer inv) {
        for (Aspect aspect : crystals.getAspects()) {
            int needed = crystals.getAmount(aspect);
            Item crystal = crystalItem(aspect);
            if (crystal == null) {
                return false;
            }
            boolean found = false;
            for (int i = 0; i < 6; i++) {
                ItemStack stack = inv.getCrystalItem(i);
                if (!stack.isEmpty() && stack.is(crystal) && stack.getCount() >= needed) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                return false;
            }
        }
        return true;
    }

    public boolean canPlayerCraft(Player player, int availableVis) {
        if (availableVis < vis) {
            return false;
        }
        // D1.4: research gate (known is enough to craft while researching)
        if (research.isEmpty()) {
            return true;
        }
        return ArcanaCapabilities.getKnowledge(player).isResearchKnown(research);
    }

    public static Item crystalItem(Aspect aspect) {
        return switch (aspect.getTag()) {
            case "aer" -> ModItems.CRYSTAL_AER.get();
            case "terra" -> ModItems.CRYSTAL_TERRA.get();
            case "ignis" -> ModItems.CRYSTAL_IGNIS.get();
            case "aqua" -> ModItems.CRYSTAL_AQUA.get();
            case "ordo" -> ModItems.CRYSTAL_ORDO.get();
            case "perditio" -> ModItems.CRYSTAL_PERDITIO.get();
            default -> null;
        };
    }

    @Override
    public ItemStack assemble(Container container, RegistryAccess access) {
        return result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int w, int h) {
        return w >= width && h >= height;
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
        return ModRecipes.ARCANE_SHAPED_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.ARCANE_SHAPED_TYPE.get();
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return ingredients;
    }

    public void consumeCrystals(ArcaneWorkbenchBlockEntity be) {
        for (Aspect aspect : crystals.getAspects()) {
            int needed = crystals.getAmount(aspect);
            Item crystal = crystalItem(aspect);
            for (int i = 0; i < 6; i++) {
                ItemStack stack = be.getItems().getStackInSlot(9 + i);
                if (!stack.isEmpty() && stack.is(crystal) && stack.getCount() >= needed) {
                    stack.shrink(needed);
                    break;
                }
            }
        }
    }

    public void consumeGrid(ArcaneWorkbenchBlockEntity be) {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = be.getItems().getStackInSlot(i);
            if (!stack.isEmpty()) {
                stack.shrink(1);
            }
        }
    }

    public interface ArcaneWorkbenchContainer extends Container {
        ItemStack getCraftItem(int index);

        ItemStack getCrystalItem(int index);
    }

    public static class Serializer implements RecipeSerializer<ArcaneShapedRecipe> {
        @Override
        public ArcaneShapedRecipe fromJson(ResourceLocation id, JsonObject json) {
            String research = GsonHelper.getAsString(json, "research", "");
            int vis = GsonHelper.getAsInt(json, "vis", 0);
            AspectList crystals = new AspectList();
            if (json.has("crystals")) {
                JsonObject c = GsonHelper.getAsJsonObject(json, "crystals");
                for (Map.Entry<String, JsonElement> e : c.entrySet()) {
                    Aspect aspect = Aspect.getAspect(e.getKey());
                    if (aspect != null) {
                        crystals.add(aspect, e.getValue().getAsInt());
                    }
                }
            }

            Map<Character, Ingredient> key = new HashMap<>();
            key.put(' ', Ingredient.EMPTY);
            JsonObject keyJson = GsonHelper.getAsJsonObject(json, "key");
            for (Map.Entry<String, JsonElement> entry : keyJson.entrySet()) {
                if (entry.getKey().length() != 1) {
                    throw new IllegalArgumentException("Invalid key: " + entry.getKey());
                }
                key.put(entry.getKey().charAt(0), Ingredient.fromJson(entry.getValue()));
            }

            var patternArray = GsonHelper.getAsJsonArray(json, "pattern");
            String[] pattern = new String[patternArray.size()];
            for (int i = 0; i < patternArray.size(); i++) {
                pattern[i] = GsonHelper.convertToString(patternArray.get(i), "pattern[" + i + "]");
            }
            int width = pattern[0].length();
            int height = pattern.length;
            NonNullList<Ingredient> ingredients = NonNullList.withSize(width * height, Ingredient.EMPTY);
            for (int y = 0; y < height; y++) {
                String line = pattern[y];
                if (line.length() != width) {
                    throw new IllegalArgumentException("Pattern lines must be equal width");
                }
                for (int x = 0; x < width; x++) {
                    char ch = line.charAt(x);
                    Ingredient ing = key.get(ch);
                    if (ing == null) {
                        throw new IllegalArgumentException("Pattern references undefined symbol '" + ch + "'");
                    }
                    ingredients.set(x + y * width, ing);
                }
            }

            ItemStack result = CraftingHelper.getItemStack(GsonHelper.getAsJsonObject(json, "result"), true);
            return new ArcaneShapedRecipe(id, research, vis, crystals, width, height, ingredients, result);
        }

        @Override
        public ArcaneShapedRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            String research = buf.readUtf();
            int vis = buf.readVarInt();
            AspectList crystals = new AspectList();
            int crystalCount = buf.readVarInt();
            for (int i = 0; i < crystalCount; i++) {
                Aspect a = Aspect.getAspect(buf.readUtf());
                int amt = buf.readVarInt();
                if (a != null) {
                    crystals.add(a, amt);
                }
            }
            int width = buf.readVarInt();
            int height = buf.readVarInt();
            NonNullList<Ingredient> ingredients = NonNullList.withSize(width * height, Ingredient.EMPTY);
            for (int i = 0; i < ingredients.size(); i++) {
                ingredients.set(i, Ingredient.fromNetwork(buf));
            }
            ItemStack result = buf.readItem();
            return new ArcaneShapedRecipe(id, research, vis, crystals, width, height, ingredients, result);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, ArcaneShapedRecipe recipe) {
            buf.writeUtf(recipe.research);
            buf.writeVarInt(recipe.vis);
            Aspect[] aspects = recipe.crystals.getAspects();
            buf.writeVarInt(aspects.length);
            for (Aspect a : aspects) {
                buf.writeUtf(a.getTag());
                buf.writeVarInt(recipe.crystals.getAmount(a));
            }
            buf.writeVarInt(recipe.width);
            buf.writeVarInt(recipe.height);
            for (Ingredient ingredient : recipe.ingredients) {
                ingredient.toNetwork(buf);
            }
            buf.writeItem(recipe.result);
        }
    }
}
