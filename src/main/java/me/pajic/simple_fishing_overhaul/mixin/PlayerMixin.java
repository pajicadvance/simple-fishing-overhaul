package me.pajic.simple_fishing_overhaul.mixin;

import me.pajic.simple_fishing_overhaul.util.PlayerExtension;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Player.class)
public class PlayerMixin implements PlayerExtension {

	@Unique boolean shouldCastFishingRod = false;
	@Unique int remainingCastTime = 0;

	@Override
	public void sfo$setShouldCastFishingRod(boolean shouldCastFishingRod) {
		this.shouldCastFishingRod = shouldCastFishingRod;
	}

	@Override
	public boolean sfo$getShouldCastFishingRod() {
		return shouldCastFishingRod;
	}

	@Override
	public void sfo$setRemainingCastTime(int remainingCastTime) {
		this.remainingCastTime = remainingCastTime;
	}

	@Override
	public int sfo$getRemainingCastTime() {
		return remainingCastTime;
	}
}
