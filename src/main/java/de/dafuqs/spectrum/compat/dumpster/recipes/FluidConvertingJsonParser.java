package de.dafuqs.spectrum.compat.dumpster.recipes;

import com.google.gson.JsonObject;
import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.recipe.GatedSpectrumRecipe;
import de.dafuqs.spectrum.recipe.SpectrumRecipeTypes;
import de.dafuqs.spectrum.recipe.fluid_converting.FluidConvertingRecipe;
import mc.recraftors.dumpster.recipes.TargetRecipeType;
import net.minecraft.recipe.Recipe;
import net.minecraft.util.registry.Registry;

/**
 * Implements <a href="https://modrinth.com/mod/dumpster">Dumpster</a>
 * compat for {@link FluidConvertingRecipe}
 * @author Nerjal Nosk
 */
@TargetRecipeType(value = FluidConvertingJsonParser.TYPE, supports = {
        SpectrumCommon.MOD_ID+":"+SpectrumRecipeTypes.DRAGONROT_CONVERTING_ID,
        SpectrumCommon.MOD_ID+":"+SpectrumRecipeTypes.LIQUID_CRYSTAL_CONVERTING_ID,
        SpectrumCommon.MOD_ID+":"+SpectrumRecipeTypes.MIDNIGHT_SOLUTION_CONVERTING_ID,
        SpectrumCommon.MOD_ID+":"+SpectrumRecipeTypes.MUD_CONVERTING_ID
})
public class FluidConvertingJsonParser implements GatedRecipeJsonParser {
    public static final String TYPE = SpectrumCommon.MOD_ID + ":fluid_convert";
    private FluidConvertingRecipe recipe;

    @Override
    public GatedSpectrumRecipe getRecipe() {
        return recipe;
    }

    @Override
    @SuppressWarnings("DataFlowIssue")
    public String getType() {
        return Registry.RECIPE_TYPE.getId(recipe.getType()).toString();
    }

    @Override
    public InResult in(Recipe<?> recipe) {
        if (recipe instanceof FluidConvertingRecipe f) {
            this.recipe = f;
        }
        return InResult.FAILURE;
    }

    @Override
    public JsonObject toJson() {
        JsonObject main = GatedRecipeJsonParser.super.toJson();
        GatedRecipeJsonParser.addIngredient(main, this.recipe.getIngredients());
        main.add("result", GatedRecipeJsonParser.resultObject(this.recipe.getOutput()));
        return main;
    }
}
