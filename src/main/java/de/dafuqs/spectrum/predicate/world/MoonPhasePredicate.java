package de.dafuqs.spectrum.predicate.world;

import com.google.gson.*;
import net.minecraft.server.world.*;
import net.minecraft.util.math.*;

public class MoonPhasePredicate implements WorldConditionPredicate {
	public static final MoonPhasePredicate ANY = new MoonPhasePredicate(null);

	public final Integer moonPhase;

	public MoonPhasePredicate(Integer moonPhase) {
		this.moonPhase = moonPhase;
	}

	public static MoonPhasePredicate fromJson(JsonObject json) {
        if (json == null || json.isJsonNull()) return ANY;
		JsonElement jsonElement = json.get("moon_phase");
		String s = jsonElement.getAsString();
		if ("full_moon".equals(s)) {
			return new MoonPhasePredicate(0);
		} else if ("new_moon".equals(s)) {
			return new MoonPhasePredicate(4);
		} else {
			return new MoonPhasePredicate(jsonElement.getAsInt());
		}
	}

	public static JsonObject toJson(WorldConditionPredicate predicate) {
		if (! (predicate instanceof MoonPhasePredicate m) || predicate == ANY) {
			return null;
		}
		JsonElement e = switch (m.moonPhase) {
			case 0 -> new JsonPrimitive("full_moon");
			case 4 -> new JsonPrimitive("new_moon");
			default -> new JsonPrimitive(m.moonPhase);
		};
		JsonObject o = new JsonObject();
		o.add("moon_phase", e);
		return o;
	}

	@Override
	public boolean test(ServerWorld world, BlockPos pos) {
		if (this == ANY) return true;
		return this.moonPhase == world.getMoonPhase();
	}
	
}