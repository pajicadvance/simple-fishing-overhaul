package me.pajic.simple_fishing_overhaul.gametest.mixin;

import net.minecraft.world.entity.projectile.FishingHook;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(FishingHook.class)
public interface FishingHookAccessor {

	@Accessor("nibble")
	int sfotest$getNibble();

	@Accessor("nibble")
	void sfotest$setNibble(int nibble);

	@Accessor("timeUntilLured")
	int sfotest$getTimeUntilLured();

	@Accessor("timeUntilLured")
	void sfotest$setTimeUntilLured(int timeUntilLured);

	@Accessor("timeUntilHooked")
	int sfotest$getTimeUntilHooked();
}
