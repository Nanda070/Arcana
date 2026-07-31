package arcana.compat.jei;

import arcana.api.aspects.Aspect;
import arcana.common.crafting.ArcaneShapedRecipe;
import arcana.common.crafting.CrucibleRecipe;
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

public class CrucibleCategory implements IRecipeCategory<CrucibleRecipe> {
    private final IDrawable background;
    private final IDrawable icon;

    public CrucibleCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createBlankDrawable(140, 54);
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(ModItems.CRUCIBLE.get()));
    }

    @Override
    public RecipeType<CrucibleRecipe> getRecipeType() {
        return ArcanaJeiPlugin.CRUCIBLE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("jei.arcana.crucible");
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
    public void setRecipe(IRecipeLayoutBuilder builder, CrucibleRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 1, 1)
                .addIngredients(recipe.getCatalyst());

        int x = 28;
        for (Aspect aspect : recipe.getAspects().getAspects()) {
            var crystal = ArcaneShapedRecipe.crystalItem(aspect);
            if (crystal != null) {
                ItemStack stack = new ItemStack(crystal, Math.max(1, recipe.getAspects().getAmount(aspect)));
                builder.addSlot(RecipeIngredientRole.CATALYST, x, 1).addItemStack(stack);
            }
            x += 18;
        }

        builder.addSlot(RecipeIngredientRole.OUTPUT, 110, 1)
                .addItemStack(recipe.getResultItem(access()));
    }

    @Override
    public void draw(CrucibleRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics graphics,
                     double mouseX, double mouseY) {
        var font = Minecraft.getInstance().font;
        StringBuilder aspects = new StringBuilder();
        for (Aspect aspect : recipe.getAspects().getAspects()) {
            if (aspects.length() > 0) {
                aspects.append(", ");
            }
            aspects.append(aspect.getTag()).append('x').append(recipe.getAspects().getAmount(aspect));
        }
        graphics.drawString(font, aspects.toString(), 1, 28, 0x8040A0, false);
        if (!recipe.getResearch().isEmpty()) {
            graphics.drawString(font, recipe.getResearch(), 1, 40, 0x606060, false);
        }
    }

    private static net.minecraft.core.RegistryAccess access() {
        Minecraft mc = Minecraft.getInstance();
        return mc.level != null ? mc.level.registryAccess() : net.minecraft.core.RegistryAccess.EMPTY;
    }
}
