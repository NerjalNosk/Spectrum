package de.dafuqs.spectrum.registries;

import de.dafuqs.spectrum.*;
import de.dafuqs.spectrum.items.tools.*;
import net.fabricmc.fabric.api.event.registry.*;
import net.minecraft.registry.*;

public class SpectrumRegistries {
	
	public static final RegistryKey<Registry<GlassArrowVariant>> GLASS_ARROW_VARIANT_KEY = SpectrumRegistries.createRegistryKey("glass_arrow_variant");
	public static final Registry<GlassArrowVariant> GLASS_ARROW_VARIANT = FabricRegistryBuilder.createSimple(GLASS_ARROW_VARIANT_KEY)
			.attribute(RegistryAttribute.SYNCED)
			.buildAndRegister();
	
	private static <T> RegistryKey<Registry<T>> createRegistryKey(String id) {
		return RegistryKey.ofRegistry(SpectrumCommon.locate(id));
	}
	
}
