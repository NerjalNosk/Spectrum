package de.dafuqs.spectrum.compat.dumpster.recipes;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.recipe.GatedSpectrumRecipe;
import de.dafuqs.spectrum.recipe.SpectrumRecipeTypes;
import de.dafuqs.spectrum.recipe.pedestal.ShapedPedestalRecipe;
import mc.recraftors.dumpster.recipes.TargetRecipeType;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;

import java.util.Map;

@TargetRecipeType(ShapedPedestalJsonParser.TYPE)
public final class ShapedPedestalJsonParser implements GatedRecipeJsonParser {
    public static final String TYPE = SpectrumCommon.MOD_ID + ":" + SpectrumRecipeTypes.SHAPED_PEDESTAL_RECIPE_ID;
    private ShapedPedestalRecipe recipe;

    @Override
    public GatedSpectrumRecipe getRecipe() {
        return this.recipe;
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public InResult in(Recipe<?> recipe) {
        if (recipe instanceof ShapedPedestalRecipe s) {
            this.recipe = s;
            return InResult.SUCCESS;
        }
        return InResult.FAILURE;
    }

    @Override
    public JsonObject toJson() {
        JsonObject main = GatedRecipeJsonParser.super.toJson();
        main.add("result", GatedRecipeJsonParser.resultObject(this.recipe.getOutput()));
        main.add("tier", new JsonPrimitive(this.recipe.getTier().name().toLowerCase()));
        main.add("experience", new JsonPrimitive(this.recipe.getExperience()));
        main.add("time", new JsonPrimitive(this.recipe.getCraftingTime()));
        main.add("disable_yield_upgrades", new JsonPrimitive(this.recipe.areYieldUpgradesDisabled()));
        GatedRecipeJsonParser.addGemstonePowderInputs(main, this.recipe);
        main.add("skip_recipe_remainders", new JsonPrimitive(this.recipe.skipRecipeRemainders()));
        // copied yet again the ugly shaped recipe parsing to pattern and keys D:
        // See mc.recraftors.dumpster.recipes.ShapedCraftingJsonParser#toJson
        JsonArray pattern = new JsonArray();
        JsonObject keys = new JsonObject();
        for (int i = 0; i < recipe.getIngredientStacks().size(); i++) {
            Ingredient in = recipe.getIngredientStacks().get(i).getIngredient();
            if (in.isEmpty()) continue;
            if (keys.entrySet().stream().map(e -> e.getValue().getAsJsonObject()).anyMatch(e -> e.equals(in.toJson())))
                continue;
            keys.add(((char) i+61)+"", in.toJson());
        }
        for (int i = 0; i < recipe.getHeight(); i++) {
            StringBuilder s = new StringBuilder();
            for (int j = 0; j < recipe.getWidth(); j++) {
                Ingredient in = recipe.getIngredientStacks().get(recipe.getWidth() * i + j).getIngredient();
                if (in.isEmpty()) s.append(" ");
                else {
                    s.append(keys.entrySet().stream()
                            .filter(e -> e.getValue().getAsJsonObject().equals(in.toJson()))
                            .findFirst().map(Map.Entry::getKey).orElse(" "));
                }
            }
            pattern.add(s.toString());
        }
        main.add("pattern", pattern);
        main.add("key", keys);
        return main;
    }
}
