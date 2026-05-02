package me.pajic.simple_fishing_overhaul.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import me.pajic.simple_fishing_overhaul.SFOClient;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.FishingRodItem;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@MixinEnvironment(type = MixinEnvironment.Env.CLIENT)
@Mixin(HumanoidModel.class)
public abstract class HumanoidModelMixin<T extends HumanoidRenderState> {

	@Inject(
			method = "poseRightArm",
			at = @At("TAIL")
	)
	private void fishingRodRightArmPose(T state, CallbackInfo ci) {
		if (sfo$animate() && state.rightArmPose == HumanoidModel.ArmPose.ITEM && state.rightHandItemStack.getItem() instanceof FishingRodItem) {
			float castUseTicks = state.ticksUsingItem(HumanoidArm.RIGHT);
			if (castUseTicks > 0) {
				float armXModifier = Mth.clampedLerp(castUseTicks / 60, 0, 1);
				HumanoidModel<?> self = (HumanoidModel<?>) (Object) this;
				self.rightArm.yRot = -0.1F + self.head.yRot;
				self.leftArm.yRot = 0.1F + self.head.yRot + 0.4F;
				self.rightArm.xRot = (float) (-Math.PI / 2) + self.head.xRot - armXModifier;
				self.leftArm.xRot = (float) (-Math.PI / 2) + self.head.xRot - armXModifier;
			}
		}
	}

	@Inject(
			method = "poseLeftArm",
			at = @At("TAIL")
	)
	private void fishingRodLeftArmPose(T state, CallbackInfo ci) {
		if (sfo$animate() && state.leftArmPose == HumanoidModel.ArmPose.ITEM && state.leftHandItemStack.getItem() instanceof FishingRodItem) {
			float castUseTicks = state.ticksUsingItem(HumanoidArm.LEFT);
			if (castUseTicks > 0) {
				float armXModifier = Mth.clampedLerp(castUseTicks / 60, 0, 1);
				HumanoidModel<?> self = (HumanoidModel<?>) (Object) this;
				self.rightArm.yRot = -0.1F + self.head.yRot - 0.4F;
				self.leftArm.yRot = 0.1F + self.head.yRot;
				self.rightArm.xRot = (float) (-Math.PI / 2) + self.head.xRot - armXModifier;
				self.leftArm.xRot = (float) (-Math.PI / 2) + self.head.xRot - armXModifier;
			}
		}
	}

	@ModifyExpressionValue(
			method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/HumanoidRenderState;)V",
			at = @At(
					value = "FIELD",
					target = "Lnet/minecraft/client/renderer/entity/state/HumanoidRenderState;rightArmPose:Lnet/minecraft/client/model/HumanoidModel$ArmPose;",
					opcode = Opcodes.GETFIELD
			)
	)
	private HumanoidModel.ArmPose fishingRodRightArmCheck(HumanoidModel.ArmPose original, @Local(argsOnly = true, name = "state") final T state) {
		return sfo$animate() && state.rightHandItemStack.getItem() instanceof FishingRodItem ? HumanoidModel.ArmPose.BOW_AND_ARROW : original;
	}

	@ModifyExpressionValue(
			method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/HumanoidRenderState;)V",
			at = @At(
					value = "FIELD",
					target = "Lnet/minecraft/client/renderer/entity/state/HumanoidRenderState;leftArmPose:Lnet/minecraft/client/model/HumanoidModel$ArmPose;",
					opcode = Opcodes.GETFIELD
			)
	)
	private HumanoidModel.ArmPose fishingRodLeftArmCheck(HumanoidModel.ArmPose original, @Local(argsOnly = true, name = "state") final T state) {
		return sfo$animate() && state.leftHandItemStack.getItem() instanceof FishingRodItem ? HumanoidModel.ArmPose.BOW_AND_ARROW : original;
	}

	@Unique private boolean sfo$animate() {
		return SFOClient.CONFIG.customThirdPersonAnimation.get();
	}
}
