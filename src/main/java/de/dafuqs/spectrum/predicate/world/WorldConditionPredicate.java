package de.dafuqs.spectrum.predicate.world;

import com.google.gson.*;
import net.minecraft.server.world.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import org.jetbrains.annotations.*;

import java.util.*;

public interface WorldConditionPredicate {

	WorldConditionPredicate ANY = (world, pos) -> true;

	interface Deserializer {
		WorldConditionPredicate deserialize(JsonObject json);
	}

	interface Serializer {
		JsonObject serialize(WorldConditionPredicate predicate);
	}

	final class Deserializers {
		public static final Map<String, Deserializer> TYPES = new HashMap<>();

		public static final Deserializer ANY = register("any", (json) -> WorldConditionPredicate.ANY);
		public static final Deserializer DIMENSION = register("dimension", DimensionPredicate::fromJson);
		public static final Deserializer MOON_PHASE = register("moon_phase", MoonPhasePredicate::fromJson);
		public static final Deserializer TIME_OF_DAY = register("time_of_day", TimeOfDayPredicate::fromJson);
		public static final Deserializer WEATHER = register("weather", WeatherPredicate::fromJson);
		public static final Deserializer COMMAND = register("command", CommandPredicate::fromJson);
	}

	final class Serializers {
		public static final Map<String, Serializer> TYPES = new HashMap<>();

		public static final Serializer ANY = registerSerializer("any", a -> null);
		public static final Serializer DIMENSION = registerSerializer("dimension", DimensionPredicate::toJson);
		public static final Serializer MOON_PHASE = registerSerializer("moon_phase", MoonPhasePredicate::toJson);
		public static final Serializer TIME_OF_DAY = registerSerializer("time_of_day", TimeOfDayPredicate::toJson);
		public static final Serializer WEATHER = registerSerializer("weather", WeatherPredicate::toJson);
		public static final Serializer COMMAND = registerSerializer("command", CommandPredicate::toJson);
	}
	
	static WorldConditionPredicate.Deserializer register(String id, WorldConditionPredicate.Deserializer deserializer) {
		Deserializers.TYPES.put(id, deserializer);
		return deserializer;
	}

	static WorldConditionPredicate.Serializer registerSerializer(String id, WorldConditionPredicate.Serializer serializer) {
		Serializers.TYPES.put(id, serializer);
		return serializer;
	}

	static WorldConditionPredicate fromJson(@Nullable JsonElement json) {
		if (json instanceof JsonObject jsonObject) {
			String string = JsonHelper.getString(jsonObject, "type", null);
			if (string == null) {
				return ANY;
			} else {
				Deserializer deserializer = Deserializers.TYPES.get(string);
				if (deserializer == null) {
					throw new JsonSyntaxException("Unknown sub-predicate type: " + string);
				} else {
					return deserializer.deserialize(jsonObject);
				}
			}
		}
		return ANY;
	}

	static JsonObject toJson(@Nullable WorldConditionPredicate predicate) {
		if (predicate == null || predicate == ANY) return null;
		for (Map.Entry<String, Serializer> entry : Serializers.TYPES.entrySet()) {
			JsonObject o = entry.getValue().serialize(predicate);
			if (o != null) {
				o.add("type", new JsonPrimitive(entry.getKey()));
				return o;
			}
		}
		return null;
	}

	boolean test(ServerWorld world, BlockPos pos);
}
