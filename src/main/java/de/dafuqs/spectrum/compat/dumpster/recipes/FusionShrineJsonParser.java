package de.dafuqs.spectrum.compat.dumpster.recipes;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.predicate.world.WorldConditionPredicate;
import de.dafuqs.spectrum.recipe.GatedSpectrumRecipe;
import de.dafuqs.spectrum.recipe.SpectrumRecipeTypes;
import de.dafuqs.spectrum.recipe.fusion_shrine.FusionShrineRecipe;
import de.dafuqs.spectrum.recipe.fusion_shrine.FusionShrineRecipeWorldEffect;
import mc.recraftors.dumpster.recipes.TargetRecipeType;
import net.minecraft.recipe.Recipe;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.util.registry.Registry;

import java.util.List;

@TargetRecipeType(FluidConvertingJsonParser.TYPE)
public class FusionShrineJsonParser implements GatedRecipeJsonParser {
    public static final String TYPE = SpectrumCommon.MOD_ID + ":" + SpectrumRecipeTypes.FUSION_SHRINE_ID;
    private FusionShrineRecipe recipe;

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
        if (recipe instanceof FusionShrineRecipe f) {
            this.recipe = f;
            return InResult.SUCCESS;
        }
        return InResult.FAILURE;
    }

    @Override
    public JsonObject toJson() {
        JsonObject main = GatedRecipeJsonParser.super.toJson();
        JsonArray ing = new JsonArray();
        this.recipe.getIngredientStacks().forEach(s -> ing.add(s.getIngredient().toJson()));
        main.add("ingredients", ing);
        main.add("fluid", new JsonPrimitive(Registry.FLUID.getId(this.recipe.getFluidInput()).toString()));
        if (!this.recipe.getOutput().isEmpty())
            main.add("result", GatedRecipeJsonParser.resultObject(this.recipe.getOutput()));
        if (this.recipe.getExperience() > 0)
            main.add("experience", new JsonPrimitive(this.recipe.getExperience()));
        if (this.recipe.getCraftingTime() != 200)
            main.add("time", new JsonPrimitive(this.recipe.getCraftingTime()));
        main.add("disable_yield_upgrades", new JsonPrimitive(this.recipe.areYieldUpgradesDisabled()));
        main.add("play_crafting_finished_effects", new JsonPrimitive(this.recipe.shouldPlayCraftingFinishedEffects()));
        if (!this.recipe.getWorldConditions().isEmpty()) {
            JsonArray conds = new JsonArray();
            this.recipe.getWorldConditions().forEach(c -> conds.add(WorldConditionPredicate.toJson(c)));
        }
        String startEffect = FusionShrineRecipeWorldEffect.toString(this.recipe.getStartWorldEffect());
        if (startEffect != null) main.add("start_crafting_effect", new JsonPrimitive(startEffect));
        List<FusionShrineRecipeWorldEffect> duringEffects = this.recipe.getDuringWorldEffects();
        if (!duringEffects.isEmpty()) {
            JsonArray during = new JsonArray(duringEffects.size());
            duringEffects.forEach(e -> {
                String s = FusionShrineRecipeWorldEffect.toString(e);
                if (s != null) during.add(s);
            });
            main.add("during_crafting_effects", during);
        }
        String endEffect = FusionShrineRecipeWorldEffect.toString(this.recipe.getFinishWorldEffect());
        if (endEffect != null) main.add("finish_crafting_effect", new JsonPrimitive(endEffect));
        if (this.recipe.getDescription().isPresent()) {
            Text t = this.recipe.getDescription().get();
            if (t instanceof MutableText && t.getContent() instanceof TranslatableTextContent c) {
                main.add("description", new JsonPrimitive(c.getKey()));
            } else {
                main.add("description", new JsonPrimitive(t.getString()));
            }
        }
        main.add("copy_nbt", new JsonPrimitive(this.recipe.doCopyNbt()));
        return main;
    }
}
