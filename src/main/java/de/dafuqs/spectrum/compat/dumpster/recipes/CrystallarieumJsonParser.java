package de.dafuqs.spectrum.compat.dumpster.recipes;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.recipe.GatedSpectrumRecipe;
import de.dafuqs.spectrum.recipe.RecipeUtils;
import de.dafuqs.spectrum.recipe.SpectrumRecipeTypes;
import de.dafuqs.spectrum.recipe.crystallarieum.CrystallarieumCatalyst;
import de.dafuqs.spectrum.recipe.crystallarieum.CrystallarieumRecipe;
import mc.recraftors.dumpster.recipes.TargetRecipeType;
import net.minecraft.recipe.Recipe;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.registry.Registry;

/**
 * Implements <a href="https://modrinth.com/mod/dumpster">Dumpster</a>
 * compat for {@link CrystallarieumRecipe}
 * @author Nerjal Nosk
 */
@TargetRecipeType(CrystallarieumJsonParser.TYPE)
public class CrystallarieumJsonParser implements GatedRecipeJsonParser {
    public static final String TYPE = SpectrumCommon.MOD_ID + ":" + SpectrumRecipeTypes.CRYSTALLARIEUM_ID;
    private CrystallarieumRecipe recipe;

    @Override
    public GatedSpectrumRecipe getRecipe() {
        return this.recipe;
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public boolean in(Recipe<?> recipe) {
        if (recipe instanceof CrystallarieumRecipe c) {
            this.recipe = c;
            return true;
        }
        return false;
    }

    @Override
    public JsonObject toJson() {
        JsonObject main = GatedRecipeJsonParser.super.toJson();
        GatedRecipeJsonParser.addIngredient(main, this.recipe.getIngredients());
        JsonArray stages = new JsonArray();
        this.recipe.getGrowthStages().forEach(s -> stages.add(RecipeUtils.blockStateToString(s)));
        main.add("growth_stage_states", stages);
        main.add("seconds_per_growth_stage", new JsonPrimitive(this.recipe.getSecondsPerGrowthStage()));
        main.add("ink_color", new JsonPrimitive(this.recipe.getInkColor().toString()));
        double tier = Math.log(this.recipe.getInkPerSecond())/Math.log(2); // get power of 2
        main.add("ink_cost_tier", new JsonPrimitive(Math.floor(tier)-1));
        main.add("grows_without_catalyst", new JsonPrimitive(this.recipe.growsWithoutCatalyst()));
        if (!this.recipe.getCatalysts().isEmpty()) {
            JsonArray catalysts = new JsonArray(this.recipe.getCatalysts().size());
            this.recipe.getCatalysts().forEach(c -> catalysts.add(catalystToJson(c)));
            main.add("catalysts", catalysts);
        }
        if (!this.recipe.getAdditionalOutputs().isEmpty()) {
            JsonArray additional = new JsonArray(this.recipe.getAdditionalOutputs().size());
            this.recipe.getAdditionalOutputs().forEach(s -> additional.add(Registry.ITEM.getId(s.getItem()).toString()));
            main.add("additional_recipe_manager_outputs", additional);
        }
        return main;
    }

    private static JsonElement catalystToJson(CrystallarieumCatalyst catalyst) {
        JsonObject main = new JsonObject();
        GatedRecipeJsonParser.addIngredient(main, DefaultedList.copyOf(catalyst.ingredient));
        main.add("growth_acceleration_mod", new JsonPrimitive(catalyst.growthAccelerationMod));
        main.add("ink_consumption_mod", new JsonPrimitive(catalyst.inkConsumptionMod));
        main.add("consume_chance_per_second", new JsonPrimitive(catalyst.consumeChancePerSecond));
        return main;
    }
}
