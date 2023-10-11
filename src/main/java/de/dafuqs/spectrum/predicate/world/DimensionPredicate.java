package de.dafuqs.spectrum.predicate.world;

import com.google.gson.*;
import net.minecraft.server.world.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.util.registry.*;
import net.minecraft.world.*;

import java.util.*;

public class DimensionPredicate implements WorldConditionPredicate {
	public static final DimensionPredicate ANY = new DimensionPredicate(null);
	
	public final List<RegistryKey<World>> dimensionKeys;
	
	public DimensionPredicate(List<RegistryKey<World>> dimensionKeys) {
		this.dimensionKeys = dimensionKeys;
	}
	
	public static DimensionPredicate fromJson(JsonObject json) {
        if (json == null || json.isJsonNull()) return ANY;
		List<RegistryKey<World>> dimensionKeys = new ArrayList<>();
		for (JsonElement element : json.get("worlds").getAsJsonArray()) {
			dimensionKeys.add(RegistryKey.of(Registry.WORLD_KEY, Identifier.tryParse(element.getAsString())));
		}
		return new DimensionPredicate(dimensionKeys);
	}

	public static JsonObject toJson(WorldConditionPredicate predicate) {
		if (!(predicate instanceof DimensionPredicate d) || predicate == ANY) {
			return null;
		}
		JsonArray worlds = new JsonArray();
		for (RegistryKey<World> dim : d.dimensionKeys) {
			worlds.add(dim.getValue().toString());
		}
		JsonObject o = new JsonObject();
		o.add("worlds", worlds);
		return o;
	}
	
	@Override
	public boolean test(ServerWorld world, BlockPos pos) {
		if (this == ANY) return true;
		return this.dimensionKeys.contains(world.getRegistryKey());
	}
	
}