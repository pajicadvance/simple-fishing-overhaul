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
	private void moveFasterWhileLured(
			CallbackInfo ci,
			@Local(name = "xd") double xd,
			@Local(name = "yd") double yd,
			@Local(name = "zd") double zd
	) {
		FishingHook hook = ((AbstractFishExtension) fish).sfo$getHook();
		if (hook != null && !hook.isRemoved()) {
			double h = Math.sqrt(xd * xd + yd * yd + zd * zd);
			double x = fish.getSpeed() * (xd / h) * 0.01;
			double z = fish.getSpeed() * (zd / h) * 0.01;
			fish.setDeltaMovement(fish.getDeltaMovement().add(x, 0.0, z));
		}
	}
}
