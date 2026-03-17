package me.pajic.simple_fishing_overhaul.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import me.pajic.simple_fishing_overhaul.util.AbstractFishExtension;
import net.minecraft.world.entity.animal.fish.AbstractFish;
import net.minecraft.world.entity.projectile.FishingHook;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractFish.FishMoveControl.class)
public class FishMoveControlMixin {

	@Shadow @Final private AbstractFish fish;

	@Inject(
			method = "tick",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/entity/animal/fish/AbstractFish;setYRot(F)V"
			)
	)
	private void moveFasterWhileLured(CallbackInfo ci, @Local(ordinal = 0) double d, @Local(ordinal = 1) double e, @Local(ordinal = 2) double g) {
		FishingHook hook = ((AbstractFishExtension) fish).sfo$getHook();
		if (hook != null && !hook.isRemoved()) {
			double h = Math.sqrt(d * d + e * e + g * g);
			double x = fish.getSpeed() * (d / h) * 0.01;
			double z = fish.getSpeed() * (g / h) * 0.01;
			fish.setDeltaMovement(fish.getDeltaMovement().add(x, 0.0, z));
		}
	}
}
