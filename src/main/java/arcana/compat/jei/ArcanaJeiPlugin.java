package arcana.compat.jei;

import arcana.Arcana;
import arcana.common.crafting.ArcaneShapedRecipe;
import arcana.common.crafting.CrucibleRecipe;
import arcana.common.crafting.InfusionRecipe;
import arcana.registry.ModItems;
import arcana.registry.ModRecipes;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;

@JeiPlugin
public class ArcanaJeiPlugin implements IModPlugin {
    public static final RecipeType<ArcaneShapedRecipe> ARCANE_SHAPED =
            RecipeType.create(Arcana.MODID, "arcane_shaped", ArcaneShapedRecipe.class);
    public static final RecipeType<CrucibleRecipe> CRUCIBLE =
            RecipeType.create(Arcana.MODID, "crucible", CrucibleRecipe.class);
    public static final RecipeType<InfusionRecipe> INFUSION =
            RecipeType.create(Arcana.MODID, "infusion", InfusionRecipe.class);

    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(Arcana.MODID, "jei");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IGuiHelper guiHelper = registration.getJeiHelpers().getGuiHelper();
        registration.addRecipeCategories(
                new ArcaneShapedCategory(guiHelper),
                new CrucibleCategory(guiHelper),
                new InfusionCategory(guiHelper));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) {
            return;
        }
        RecipeManager recipes = mc.level.getRecipeManager();
        registration.addRecipes(ARCANE_SHAPED, recipes.getAllRecipesFor(ModRecipes.ARCANE_SHAPED_TYPE.get()));
        registration.addRecipes(CRUCIBLE, recipes.getAllRecipesFor(ModRecipes.CRUCIBLE_TYPE.get()));
        registration.addRecipes(INFUSION, recipes.getAllRecipesFor(ModRecipes.INFUSION_TYPE.get()));

        registration.addIngredientInfo(new ItemStack(ModItems.RESEARCH_TABLE.get()), VanillaTypes.ITEM_STACK,
                Component.translatable("jei.arcana.info.research_table"));
        registration.addIngredientInfo(new ItemStack(ModItems.INFUSION_MATRIX.get()), VanillaTypes.ITEM_STACK,
                Component.translatable("jei.arcana.info.infusion_matrix"));
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(ModItems.ARCANE_WORKBENCH.get()), ARCANE_SHAPED);
        registration.addRecipeCatalyst(new ItemStack(ModItems.CRUCIBLE.get()), CRUCIBLE);
        registration.addRecipeCatalyst(new ItemStack(ModItems.INFUSION_MATRIX.get()), INFUSION);
    }
}
