package de.dafuqs.spectrum.compat.REI;

import de.dafuqs.spectrum.inventories.*;
import me.shedaniel.rei.api.client.registry.screen.*;
import net.fabricmc.api.*;
import net.minecraft.client.gui.screen.*;
import net.minecraft.util.*;

@Environment(EnvType.CLIENT)
public class REIOverlayDecider implements OverlayDecider {
	
	public static final OverlayDecider INSTANCE = new REIOverlayDecider();
	
	@Override
	public <R extends Screen> boolean isHandingScreen(Class<R> screen) {
		return screen.getPackageName().startsWith("de.dafuqs.spectrum");
	}
	
	@Override
	public <R extends Screen> ActionResult shouldScreenBeOverlaid(R screen) {
		if (screen instanceof PaintbrushScreen) {
			return ActionResult.FAIL;
		}
		return ActionResult.PASS;
	}
	
}
