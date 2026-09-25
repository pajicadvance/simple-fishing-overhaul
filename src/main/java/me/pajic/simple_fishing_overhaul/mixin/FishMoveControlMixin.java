package me.pajic.simple_fishing_overhaul.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import me.pajic.simple_fishing_overhaul.util.AbstractFishExtension;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.animal.fish.AbstractFish;
import net.minecraft.world.entity.projectile.FishingHook;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractFish.FishMoveControl.class)
public abstract class FishMoveControlMixin<T extends AbstractFish> extends MoveControl/*? >26.1.2 {*/<T>/*?}*/ {

	public FishMoveControlMixin(T mob) {
		super(mob);
	}

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
		FishingHook hook = ((AbstractFishExtension) mob).sfo$getHook();
		if (hook != null && !hook.isRemoved()) {
			double h = Math.sqrt(xd * xd + yd * yd + zd * zd);
			double x = mob.getSpeed() * (xd / h) * 0.01;
			double z = mob.getSpeed() * (zd / h) * 0.01;
			mob.setDeltaMovement(mob.getDeltaMovement().add(x, 0.0, z));
		}
	}
}
