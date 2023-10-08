package de.dafuqs.spectrum.recipe;

import com.google.gson.*;
import net.minecraft.network.*;
import net.minecraft.recipe.*;
import net.minecraft.util.*;
import org.jetbrains.annotations.*;

public interface GatedRecipeSerializer<T extends Recipe<?>> extends RecipeSerializer<T> {
	String REQUIRED_ADVANCEMENT_KEY = "required_advancement";

	default String readGroup(JsonObject jsonObject) {
		return JsonHelper.getString(jsonObject, "group", "");
	}
	
	default boolean readSecret(JsonObject jsonObject) {
		return JsonHelper.getBoolean(jsonObject, "secret", false);
	}
	
	default Identifier readRequiredAdvancementIdentifier(JsonObject jsonObject) {
		if (JsonHelper.hasString(jsonObject, REQUIRED_ADVANCEMENT_KEY)) {
			return new Identifier(JsonHelper.getString(jsonObject, REQUIRED_ADVANCEMENT_KEY));
		}
		return null;
	}
	
	default void writeNullableIdentifier(PacketByteBuf buf, @Nullable Identifier identifier) {
		if (identifier == null) {
			buf.writeBoolean(false);
		} else {
			buf.writeBoolean(true);
			buf.writeIdentifier(identifier);
		}
	}
	
	default @Nullable Identifier readNullableIdentifier(PacketByteBuf buf) {
		boolean notNull = buf.readBoolean();
		if (notNull) {
			return buf.readIdentifier();
		}
		return null;
	}
	
}
