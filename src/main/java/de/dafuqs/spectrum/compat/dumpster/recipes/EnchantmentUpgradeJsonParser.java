package de.dafuqs.spectrum.compat.dumpster.recipes;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.recipe.GatedSpectrumRecipe;
import de.dafuqs.spectrum.recipe.SpectrumRecipeTypes;
import de.dafuqs.spectrum.recipe.enchantment_upgrade.EnchantmentUpgradeRecipe;
import mc.recraftors.dumpster.recipes.TargetRecipeType;
import net.minecraft.recipe.Recipe;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Implements <a href="https://modrinth.com/mod/dumpster">Dumpster</a>
 * compat for {@link EnchantmentUpgradeRecipe}
 * @author Nerjal Nosk
 */
@TargetRecipeType(EnchantmentUpgradeJsonParser.TYPE)
public final class EnchantmentUpgradeJsonParser implements GatedRecipeJsonParser {
    public static final String TYPE = SpectrumCommon.MOD_ID + ":" + SpectrumRecipeTypes.ENCHANTMENT_UPGRADE_ID;
    static final Pattern lastInt = Pattern.compile("\\D+(\\D+)$");
    private final Map<Identifier, JsonArray> recipeLevelsMap = new HashMap<>();
    private EnchantmentUpgradeRecipe recipe;
    private Identifier cId;

    @Override
    public GatedSpectrumRecipe getRecipe() {
        return this.recipe;
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public Identifier alternativeId() {
        return cId;
    }

    private Identifier levelBase(EnchantmentUpgradeRecipe r) {
        // get base ID from parsed recipe as ID_level_<number>
        String s = r.id.getPath();
        Matcher m = lastInt.matcher(s);
        if (m.find()) {
            int n = 7+m.group(1).length();
            return new Identifier(r.id.getNamespace(), s.substring(0, s.length()-n));
        }
        return r.id;
    }

    @Override
    public InResult in(Recipe<?> recipe) {
        if (recipe instanceof EnchantmentUpgradeRecipe e) {
            Identifier id = levelBase(e);
            this.recipe = e;
            this.cId = id;
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
        main.add("enchantment", new JsonPrimitive(Registry.ENCHANTMENT.getId(this.recipe.getEnchantment()).toString()));
        if (!recipeLevelsMap.containsKey(this.cId)) {
            recipeLevelsMap.put(this.cId, new JsonArray());
        }
        JsonArray levels = recipeLevelsMap.get(this.cId);
        JsonObject currentLevel = new JsonObject();
        currentLevel.add("experience", new JsonPrimitive(this.recipe.getRequiredExperience()));
        currentLevel.add("item", new JsonPrimitive(Registry.ITEM.getId(this.recipe.getRequiredItem()).toString()));
        currentLevel.add("item_count", new JsonPrimitive(this.recipe.getRequiredItemCount()));
        levels.add(currentLevel);
        main.add("levels", levels);
        return main;
    }

    @Override
    public void cycle() {
        recipeLevelsMap.clear();
    }
}
