package de.dafuqs.spectrum.compat.dumpster.recipes;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.recipe.GatedSpectrumRecipe;
import de.dafuqs.spectrum.recipe.SpectrumRecipeTypes;
import de.dafuqs.spectrum.recipe.ink_converting.InkConvertingRecipe;
import mc.recraftors.dumpster.recipes.TargetRecipeType;
import net.minecraft.recipe.Recipe;

@TargetRecipeType(InkConvertingJsonParser.TYPE)
public final class InkConvertingJsonParser implements GatedRecipeJsonParser {
    public static final String TYPE = SpectrumCommon.MOD_ID + ":" + SpectrumRecipeTypes.MUD_CONVERTING_ID;
    private InkConvertingRecipe recipe;

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
        if (recipe instanceof InkConvertingRecipe i) {
            this.recipe = i;
            return InResult.SUCCESS;
        }
        return InResult.FAILURE;
    }

    @Override
    public JsonObject toJson() {
        JsonObject main = GatedRecipeJsonParser.super.toJson();
        GatedRecipeJsonParser.addIngredient(main, this.recipe.getIngredients());
        main.add("color", new JsonPrimitive(this.recipe.getInkColor().toString()));
        main.add("amount", new JsonPrimitive(this.recipe.getInkAmount()));
        return main;
    }
}
