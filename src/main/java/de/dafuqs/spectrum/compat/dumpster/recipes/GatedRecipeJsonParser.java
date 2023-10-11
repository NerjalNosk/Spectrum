package de.dafuqs.spectrum.compat.dumpster.recipes;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import de.dafuqs.spectrum.helpers.NbtHelper;
import de.dafuqs.spectrum.recipe.GatedRecipeSerializer;
import de.dafuqs.spectrum.recipe.GatedSpectrumRecipe;
import mc.recraftors.dumpster.recipes.RecipeJsonParser;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.registry.Registry;

public interface GatedRecipeJsonParser extends RecipeJsonParser {
    GatedSpectrumRecipe getRecipe();
    String getType();

    @Override
    default JsonObject toJson() {
        JsonObject main = new JsonObject();
        main.add("type", new JsonPrimitive(getType()));
        RecipeJsonParser.addGroup(main, getRecipe());
        main.add("secret", new JsonPrimitive(getRecipe().secret));
        main.add(GatedRecipeSerializer.REQUIRED_ADVANCEMENT_KEY, new JsonPrimitive(getRecipe().requiredAdvancementIdentifier.toString()));
        return main;
    }

    static void addIngredient(JsonObject object, DefaultedList<Ingredient> ingredient) {
        if (ingredient.isEmpty()) return;
        if (ingredient.size() == 1) {
            object.add("ingredient", ingredient.get(0).toJson());
        } else {
            JsonArray arr = new JsonArray();
            ingredient.forEach(i -> arr.add(i.toJson()));
            object.add("ingredient", arr);
        }
    }

    static JsonObject resultObject(ItemStack stack) {
        JsonObject res = new JsonObject();
        res.add("item", new JsonPrimitive(Registry.ITEM.getId(stack.getItem()).toString()));
        if (stack.getCount() > 1) {
            res.add("count", new JsonPrimitive(stack.getCount()));
        }
        if (stack.hasNbt()) {
            res.add("nbt", NbtHelper.asJson(stack.getNbt()));
        }
        return res;
    }
}
