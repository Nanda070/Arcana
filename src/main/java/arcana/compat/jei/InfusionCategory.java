package arcana.compat.jei;

import arcana.api.aspects.Aspect;
import arcana.common.crafting.ArcaneShapedRecipe;
import arcana.common.crafting.InfusionRecipe;
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

public class InfusionCategory implements IRecipeCategory<InfusionRecipe> {
    private final IDrawable background;
    private final IDrawable icon;

    public InfusionCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createBlankDrawable(150, 72);
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(ModItems.INFUSION_MATRIX.get()));
    }

    @Override
    public RecipeType<InfusionRecipe> getRecipeType() {
        return ArcanaJeiPlugin.INFUSION;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("jei.arcana.infusion");
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
    public void setRecipe(IRecipeLayoutBuilder builder, InfusionRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 1, 19)
                .addIngredients(recipe.getCentral());

        int x = 28;
        for (Ingredient component : recipe.getComponents()) {
            builder.addSlot(RecipeIngredientRole.INPUT, x, 1)
                    .addIngredients(component);
            x += 18;
        }

        int aspectX = 28;
        for (Aspect aspect : recipe.getAspects().getAspects()) {
            var crystal = ArcaneShapedRecipe.crystalItem(aspect);
            if (crystal != null) {
                ItemStack stack = new ItemStack(crystal, Math.max(1, recipe.getAspects().getAmount(aspect)));
                builder.addSlot(RecipeIngredientRole.CATALYST, aspectX, 37).addItemStack(stack);
            }
            aspectX += 18;
        }

        builder.addSlot(RecipeIngredientRole.OUTPUT, 120, 19)
                .addItemStack(recipe.getResultItem(access()));
    }

    @Override
    public void draw(InfusionRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics graphics,
                     double mouseX, double mouseY) {
        var font = Minecraft.getInstance().font;
        graphics.drawString(font, "Vis: " + recipe.getVis(), 1, 56, 0x4040A0, false);
        if (recipe.getInstability() > 0) {
            graphics.drawString(font, "Inst: " + recipe.getInstability(), 50, 56, 0x804040, false);
        }
        if (!recipe.getResearch().isEmpty()) {
            graphics.drawString(font, recipe.getResearch(), 100, 56, 0x606060, false);
        }
    }

    private static net.minecraft.core.RegistryAccess access() {
        Minecraft mc = Minecraft.getInstance();
        return mc.level != null ? mc.level.registryAccess() : net.minecraft.core.RegistryAccess.EMPTY;
    }
}
