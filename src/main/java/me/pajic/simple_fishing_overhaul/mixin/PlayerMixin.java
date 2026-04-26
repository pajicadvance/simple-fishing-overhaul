package me.pajic.simple_fishing_overhaul.mixin;

import me.pajic.simple_fishing_overhaul.util.PlayerExtension;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public class PlayerMixin implements PlayerExtension {

	@Unique boolean shouldCastFishingRod = false;

	@Override
	public void sfo$setShouldCastFishingRod(boolean shouldCastFishingRod) {
		this.shouldCastFishingRod = shouldCastFishingRod;
	}

	@Override
	public boolean sfo$getShouldCastFishingRod() {
		return shouldCastFishingRod;
	}

	@Inject(
			method = "addAdditionalSaveData",
			at = @At("TAIL")
	)
	private void save(ValueOutput output, CallbackInfo ci) {
		output.putBoolean("ShouldCastFishingRod", shouldCastFishingRod);
	}

	@Inject(
			method = "readAdditionalSaveData",
			at = @At("TAIL")
	)
	private void load(ValueInput input, CallbackInfo ci) {
		shouldCastFishingRod = input.getBooleanOr("ShouldCastFishingRod", false);
	}
}
