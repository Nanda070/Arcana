package arcana.registry;

import arcana.Arcana;
import arcana.common.crafting.ArcaneShapedRecipe;
import arcana.common.crafting.CrucibleRecipe;
import arcana.common.crafting.InfusionRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModRecipes {
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES =
            DeferredRegister.create(Registries.RECIPE_TYPE, Arcana.MODID);
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, Arcana.MODID);

    public static final RegistryObject<RecipeType<ArcaneShapedRecipe>> ARCANE_SHAPED_TYPE =
            RECIPE_TYPES.register("arcane_shaped", () -> new RecipeType<>() {
                @Override
                public String toString() {
                    return Arcana.MODID + ":arcane_shaped";
                }
            });

    public static final RegistryObject<RecipeSerializer<ArcaneShapedRecipe>> ARCANE_SHAPED_SERIALIZER =
            SERIALIZERS.register("arcane_shaped", ArcaneShapedRecipe.Serializer::new);

    public static final RegistryObject<RecipeType<CrucibleRecipe>> CRUCIBLE_TYPE =
            RECIPE_TYPES.register("crucible", () -> new RecipeType<>() {
                @Override
                public String toString() {
                    return Arcana.MODID + ":crucible";
                }
            });

    public static final RegistryObject<RecipeSerializer<CrucibleRecipe>> CRUCIBLE_SERIALIZER =
            SERIALIZERS.register("crucible", CrucibleRecipe.Serializer::new);

    public static final RegistryObject<RecipeType<InfusionRecipe>> INFUSION_TYPE =
            RECIPE_TYPES.register("infusion", () -> new RecipeType<>() {
                @Override
                public String toString() {
                    return Arcana.MODID + ":infusion";
                }
            });

    public static final RegistryObject<RecipeSerializer<InfusionRecipe>> INFUSION_SERIALIZER =
            SERIALIZERS.register("infusion", InfusionRecipe.Serializer::new);

    private ModRecipes() {
    }
}
