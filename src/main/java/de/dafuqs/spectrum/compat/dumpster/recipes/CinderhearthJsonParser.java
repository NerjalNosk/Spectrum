package de.dafuqs.spectrum.compat.dumpster.recipes;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.recipe.GatedSpectrumRecipe;
import de.dafuqs.spectrum.recipe.SpectrumRecipeTypes;
import de.dafuqs.spectrum.recipe.cinderhearth.CinderhearthRecipe;
import mc.recraftors.dumpster.recipes.TargetRecipeType;
import net.minecraft.recipe.Recipe;

/**
 * Implements <a href="https://modrinth.com/mod/dumpster">Dumpster</a>
 * compat for {@link CinderhearthRecipe}
 * @author Nerjal Nosk
 */
@TargetRecipeType(CinderhearthJsonParser.TYPE)
public final class CinderhearthJsonParser implements GatedRecipeJsonParser {
    public static final String TYPE = SpectrumCommon.MOD_ID + ":" + SpectrumRecipeTypes.CINDERHEARTH_ID;
    private CinderhearthRecipe recipe;

    @Override
    public GatedSpectrumRecipe getRecipe() {
        return recipe;
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public boolean in(Recipe<?> recipe) {
        if (recipe instanceof CinderhearthRecipe c) {
            this.recipe = c;
            return true;
        }
        return false;
    }

    @Override
    public JsonObject toJson() {
        if (this.recipe == null) {
            return null;
        }
        JsonObject main = GatedRecipeJsonParser.super.toJson();
        GatedRecipeJsonParser.addIngredient(main, this.recipe.getIngredients());
        main.add("time", new JsonPrimitive(recipe.getCraftingTime()));
        main.add("experience", new JsonPrimitive(recipe.getExperience()));
        JsonArray arr = new JsonArray();
        this.recipe.getOutputsWithChance().forEach(e -> {
            JsonObject c = GatedRecipeJsonParser.resultObject(e.getLeft());
            c.add("chance", new JsonPrimitive(e.getRight()));
            arr.add(c);
        });
        main.add("results", arr);
        return main;
    }
}
