package de.dafuqs.spectrum.predicate.world;

import com.google.gson.*;
import net.minecraft.server.world.*;
import net.minecraft.util.math.*;

import java.util.*;

public class WeatherPredicate implements WorldConditionPredicate {
	public static final WeatherPredicate ANY = new WeatherPredicate(null);

	public enum WeatherCondition {
		CLEAR_SKY,
		RAIN, // rain or thunder
		STRICT_RAIN, // rain without thunder
		THUNDER,
		NOT_THUNDER
	}

	public final WeatherCondition weatherCondition;

	public WeatherPredicate(WeatherCondition weatherCondition) {
		this.weatherCondition = weatherCondition;
	}

	public static WeatherPredicate fromJson(JsonObject json) {
        if (json == null || json.isJsonNull()) return ANY;
		return new WeatherPredicate(WeatherCondition.valueOf(json.get("weather_condition").getAsString().toUpperCase(Locale.ROOT)));
	}

	public static JsonObject toJson(WorldConditionPredicate predicate) {
		if (! (predicate instanceof WeatherPredicate w) || predicate == ANY) {
			return null;
		}
		JsonObject o = new JsonObject();
		o.add("weather_condition", new JsonPrimitive(w.weatherCondition.name()));
		return o;
	}

	@Override
	public boolean test(ServerWorld world, BlockPos pos) {
		if (this == ANY) return true;
		switch (this.weatherCondition) {
			case CLEAR_SKY -> {
				return !world.isRaining();
			}
			case RAIN -> {
				return world.isRaining();
			}
			case STRICT_RAIN -> {
				return world.isRaining() && !world.isThundering();
			}
			case THUNDER -> {
				return world.isThundering();
			}
			case NOT_THUNDER -> {
				return !world.isThundering();
			}
		}
		return true;
	}
}