package de.dafuqs.spectrum.compat.dumpster.recipes;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.recipe.SpectrumRecipeTypes;
import de.dafuqs.spectrum.recipe.anvil_crushing.AnvilCrushingRecipe;
import mc.recraftors.dumpster.recipes.TargetRecipeType;
import net.minecraft.recipe.Recipe;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

/**
 * Implements <a href="https://modrinth.com/mod/dumpster">Dumpster</a>
 * compat for {@link de.dafuqs.spectrum.recipe.anvil_crushing.AnvilCrushingRecipe}
 * @author Nerjal Nosk
 */
@TargetRecipeType(AnvilCrushingJsonParser.TYPE)
public final class AnvilCrushingJsonParser implements GatedRecipeJsonParser {
    public static final String TYPE = SpectrumCommon.MOD_ID + ":" + SpectrumRecipeTypes.ANVIL_CRUSHING_ID;
    private AnvilCrushingRecipe recipe;

    @Override
    public AnvilCrushingRecipe getRecipe() {
        return recipe;
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public InResult in(Recipe<?> recipe) {
        if (recipe instanceof AnvilCrushingRecipe a) {
            this.recipe = a;
            return InResult.SUCCESS;
        }
        return InResult.FAILURE;
    }

    @Override
    public JsonObject toJson() {
        if (recipe == null) {
            return null;
        }
        JsonObject main = GatedRecipeJsonParser.super.toJson();
        GatedRecipeJsonParser.addIngredient(main, this.recipe.getIngredients());
        main.add("result", GatedRecipeJsonParser.resultObject(this.recipe.getOutput()));
        main.add("crushedItemsPerPointOfDamage", new JsonPrimitive(this.recipe.getCrushedItemsPerPointOfDamage()));
        main.add("experience", new JsonPrimitive(this.recipe.getExperience()));
        Identifier particleId = Registry.PARTICLE_TYPE.getId(this.recipe.getParticleEffect().getType());
        main.add("particleEffectIdentifier", new JsonPrimitive(particleId == null ? "" : particleId.toString()));
        if (this.recipe.getParticleCount() != 1) {
            main.add("particleCount", new JsonPrimitive(this.recipe.getParticleCount()));
        }
        Identifier soundId = Registry.SOUND_EVENT.getId(this.recipe.getSoundEvent());
        main.add("soundEventIdentifier", new JsonPrimitive(soundId == null ? "" : soundId.toString()));
        return main;
    }
}
