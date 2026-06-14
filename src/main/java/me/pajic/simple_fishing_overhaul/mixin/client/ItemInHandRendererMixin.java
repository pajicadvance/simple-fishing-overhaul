package me.pajic.simple_fishing_overhaul.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import me.pajic.simple_fishing_overhaul.SFOClient;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@MixinEnvironment(type = MixinEnvironment.Env.CLIENT)
@Mixin(ItemInHandRenderer.class)
public abstract class ItemInHandRendererMixin {

	@Inject(
			method = /*? if 26.1.2 {*//*"renderArmWithItem"*//*?} else {*/"submitArmWithItem"/*?}*/,
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/renderer/ItemInHandRenderer;renderItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;I)V"
			)
	)
	private void fishingRodAnimation(
			AbstractClientPlayer player,
			float frameInterp,
			float xRot,
			InteractionHand hand,
			float attack,
			ItemStack itemStack,
			float inverseArmHeight,
			PoseStack poseStack,
			SubmitNodeCollector submitNodeCollector,
			int lightCoords,
			CallbackInfo ci,
			@Local(name = "arm") HumanoidArm arm
	) {
		if (SFOClient.CONFIG.customFirstPersonAnimation.get() && itemStack.getItem() instanceof FishingRodItem && player.isUsingItem() && (player.getUseItemRemainingTicks() - 1) > 0 && player.getUsedItemHand() == hand) {
			int invert = arm == HumanoidArm.RIGHT ? 1 : -1;
			poseStack.translate(invert * -0.2785682F, 0.18344387F, 0.15731531F);
			poseStack.mulPose(Axis.XP.rotationDegrees(-13.935F));
			poseStack.mulPose(Axis.ZP.rotationDegrees(invert * -9.785F));
			float timeHeld = itemStack.getUseDuration(player) - (player.getUseItemRemainingTicks() - frameInterp);
			float power = timeHeld / 20.0F;
			poseStack.mulPose(Axis.XP.rotationDegrees(power * 20F));
			poseStack.translate(0, 0, power * 0.04F);
		}
	}
}
