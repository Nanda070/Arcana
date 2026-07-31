package arcana.compat.jei;

import arcana.api.aspects.Aspect;
import arcana.common.crafting.ArcaneShapedRecipe;
import arcana.registry.ModItems;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public class ArcaneShapedCategory implements IRecipeCategory<ArcaneShapedRecipe> {
    private final IDrawable background;
    private final IDrawable icon;

    public ArcaneShapedCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createBlankDrawable(150, 72);
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(ModItems.ARCANE_WORKBENCH.get()));
    }

    @Override
    public RecipeType<ArcaneShapedRecipe> getRecipeType() {
        return ArcanaJeiPlugin.ARCANE_SHAPED;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("jei.arcana.arcane_shaped");
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, ArcaneShapedRecipe recipe, IFocusGroup focuses) {
        var ingredients = recipe.getIngredients();
        int width = recipe.getWidth();
        int height = recipe.getHeight();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Ingredient ingredient = ingredients.get(x + y * width);
                builder.addSlot(RecipeIngredientRole.INPUT, 1 + x * 18, 1 + y * 18)
                        .addIngredients(ingredient);
            }
        }

        int crystalX = 64;
        for (Aspect aspect : recipe.getCrystals().getAspects()) {
            var crystal = ArcaneShapedRecipe.crystalItem(aspect);
            if (crystal == null) {
                continue;
            }
            ItemStack stack = new ItemStack(crystal, Math.max(1, recipe.getCrystals().getAmount(aspect)));
            builder.addSlot(RecipeIngredientRole.CATALYST, crystalX, 1)
                    .addItemStack(stack);
            crystalX += 18;
        }

        builder.addSlot(RecipeIngredientRole.OUTPUT, 120, 19)
                .addItemStack(recipe.getResultItem(access()));
    }

    @Override
    public void draw(ArcaneShapedRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics graphics,
                     double mouseX, double mouseY) {
        var font = Minecraft.getInstance().font;
        graphics.drawString(font, "Vis: " + recipe.getVis(), 64, 40, 0x4040A0, false);
        if (!recipe.getResearch().isEmpty()) {
            graphics.drawString(font, recipe.getResearch(), 64, 52, 0x606060, false);
        }
    }

    private static net.minecraft.core.RegistryAccess access() {
        Minecraft mc = Minecraft.getInstance();
        return mc.level != null ? mc.level.registryAccess() : net.minecraft.core.RegistryAccess.EMPTY;
    }
}
