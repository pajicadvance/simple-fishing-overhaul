package me.pajic.simple_fishing_overhaul.mixin;

import net.minecraft.world.entity.projectile.FishingHook;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(FishingHook.class)
public interface FishingHookAccessor {

	@Accessor("lureSpeed")
	int sfo$getLureSpeed();

	@Accessor("nibble")
	int sfo$getNibble();

	@Accessor("nibble")
	void sfo$setNibble(int nibble);

	@Accessor("timeUntilLured")
	void sfo$setTimeUntilLured(int timeUntilLured);

	@Accessor("timeUntilHooked")
	int sfo$getTimeUntilHooked();

	@Accessor("timeUntilHooked")
	void sfo$setTimeUntilHooked(int timeUntilHooked);
}
