package de.dafuqs.spectrum.compat.dumpster.recipes;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.recipe.GatedSpectrumRecipe;
import de.dafuqs.spectrum.recipe.SpectrumRecipeTypes;
import de.dafuqs.spectrum.recipe.enchanter.EnchanterRecipe;
import mc.recraftors.dumpster.recipes.TargetRecipeType;
import net.minecraft.recipe.Recipe;

/**
 * Implements <a href="https://modrinth.com/mod/dumpster">Dumpster</a>
 * compat for {@link EnchanterRecipe}
 * @author Nerjal Nosk
 */
@TargetRecipeType(EnchanterJsonParser.TYPE)
public final class EnchanterJsonParser implements GatedRecipeJsonParser {
    public static final String TYPE = SpectrumCommon.MOD_ID + ":" + SpectrumRecipeTypes.ENCHANTER_ID;
    private EnchanterRecipe recipe;

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
        if (recipe instanceof EnchanterRecipe e) {
            this.recipe = e;
            return InResult.SUCCESS;
        }
        return InResult.FAILURE;
    }

    @Override
    public JsonObject toJson() {
        if (this.recipe == null) {
            return null;
        }
        JsonObject main = GatedRecipeJsonParser.super.toJson();
        GatedRecipeJsonParser.addIngredient(main, this.recipe.getIngredients());
        main.add("result", GatedRecipeJsonParser.resultObject(this.recipe.getOutput()));
        main.add("required_experience", new JsonPrimitive(this.recipe.getRequiredExperience()));
        if (this.recipe.getCraftingTime() != 200) {
            main.add("time", new JsonPrimitive(this.recipe.getCraftingTime()));
        }
        if (this.recipe.areYieldAndEfficiencyUpgradesDisabled()) {
            main.add("disable_yield_and_efficiency_upgrades", new JsonPrimitive(true));
        }
        return main;
    }
}
