package de.dafuqs.spectrum.compat.dumpster.recipes;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.helpers.NbtHelper;
import de.dafuqs.spectrum.recipe.GatedRecipeSerializer;
import de.dafuqs.spectrum.recipe.SpectrumRecipeTypes;
import de.dafuqs.spectrum.recipe.anvil_crushing.AnvilCrushingRecipe;
import mc.recraftors.dumpster.recipes.RecipeJsonParser;
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
public final class AnvilCrushingJsonParser implements RecipeJsonParser {
    public static final String TYPE = SpectrumCommon.MOD_ID + ":" + SpectrumRecipeTypes.ANVIL_CRUSHING_ID;
    private AnvilCrushingRecipe recipe;

    @Override
    public boolean in(Recipe<?> recipe) {
        if (recipe instanceof AnvilCrushingRecipe a) {
            this.recipe = a;
            return true;
        }
        return false;
    }

    @Override
    public JsonObject toJson() {
        if (recipe == null) {
            return null;
        }
        JsonObject main = new JsonObject();
        main.add("type", new JsonPrimitive(TYPE));
        if (this.recipe.group != null && !this.recipe.group.isEmpty()) {
            main.add("group", new JsonPrimitive(this.recipe.group));
        }
        main.add("secret", new JsonPrimitive(this.recipe.secret));
        main.add(GatedRecipeSerializer.REQUIRED_ADVANCEMENT_KEY, new JsonPrimitive(recipe.requiredAdvancementIdentifier.toString()));
        if (this.recipe.getIngredients().size() > 1) {
            JsonArray ingredients = new JsonArray();
            this.recipe.getIngredients().forEach(i -> ingredients.add(i.toJson()));
            main.add("ingredient", ingredients);
        } else {
            main.add("ingredient", this.recipe.getIngredients().get(0).toJson());
        }
        JsonObject res = new JsonObject();
        res.add("item", new JsonPrimitive(Registry.ITEM.getId(this.recipe.getOutput().getItem()).toString()));
        if (this.recipe.getOutput().getCount() > 1) {
            res.add("count", new JsonPrimitive(this.recipe.getOutput().getCount()));
        }
        if (this.recipe.getOutput().hasNbt()) {
            res.add("nbt", NbtHelper.asJson(this.recipe.getOutput().getNbt()));
        }
        main.add("result", res);
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
