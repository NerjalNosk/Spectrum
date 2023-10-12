package de.dafuqs.spectrum.compat.dumpster.recipes;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.recipe.GatedSpectrumRecipe;
import de.dafuqs.spectrum.recipe.SpectrumRecipeTypes;
import de.dafuqs.spectrum.recipe.pedestal.ShapelessPedestalRecipe;
import mc.recraftors.dumpster.recipes.TargetRecipeType;
import net.minecraft.recipe.Recipe;

@TargetRecipeType(ShapelessPedestalJsonParser.TYPE)
public final class ShapelessPedestalJsonParser implements GatedRecipeJsonParser {
    public static final String TYPE = SpectrumCommon.MOD_ID + ":" + SpectrumRecipeTypes.SHAPELESS_PEDESTAL_RECIPE_ID;
    private ShapelessPedestalRecipe recipe;

    @Override
    public GatedSpectrumRecipe getRecipe() {
        return recipe;
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public InResult in(Recipe<?> recipe) {
        if (recipe instanceof ShapelessPedestalRecipe s) {
            this.recipe = s;
            return InResult.SUCCESS;
        }
        return InResult.FAILURE;
    }

    @Override
    @SuppressWarnings({"DuplicatedCode", "deprecation"})
    public JsonObject toJson() {
        JsonObject main = GatedRecipeJsonParser.super.toJson();
        main.add("result", GatedRecipeJsonParser.resultObject(this.recipe.getOutput()));
        main.add("tier", new JsonPrimitive(this.recipe.getTier().name().toLowerCase()));
        main.add("experience", new JsonPrimitive(this.recipe.getExperience()));
        main.add("time", new JsonPrimitive(this.recipe.getCraftingTime()));
        main.add("disable_yield_upgrades", new JsonPrimitive(this.recipe.areYieldUpgradesDisabled()));
        GatedRecipeJsonParser.addGemstonePowderInputs(main, this.recipe);
        main.add("skip_recipe_remainders", new JsonPrimitive(this.recipe.skipRecipeRemainders()));
        JsonObject t = new JsonObject();
        GatedRecipeJsonParser.addIngredient(t, this.recipe.getIngredients());
        main.add("ingredients", t.get("ingredient"));
        return main;
    }
}
