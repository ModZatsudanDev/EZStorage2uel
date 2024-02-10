package modzatsudan.ezstorage.crafting;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import modzatsudan.ezstorage.ref.RefStrings;
import modzatsudan.ezstorage.registry.RegistryHelper;

/** Helps with crafting recipe management */
public class RecipeHelper {

    private static final List<IRecipe> RECIPES = RegistryHelper.RECIPES_TO_REGISTER;
    private static int recipeCounter = 0;

    /** Add a generic recipe */
    public static void addRecipe(IRecipe recipe) {
        RECIPES.add(recipe.setRegistryName(new ResourceLocation(RefStrings.MODID, "recipe" + recipeCounter++)));
    }

    /** Add a shaped ore recipe */
    public static void addShapedRecipe(ItemStack result, Object... recipe) {
        addRecipe(
                new ShapedOreRecipe(new ResourceLocation(RefStrings.MODID, "recipe" + recipeCounter), result, recipe));
    }

    /** Add a shapeless ore recipe */
    public static void addShapelessRecipe(ItemStack result, Object... recipe) {
        addRecipe(new ShapelessOreRecipe(new ResourceLocation(RefStrings.MODID, "recipe" + recipeCounter), result,
                recipe));
    }
}
