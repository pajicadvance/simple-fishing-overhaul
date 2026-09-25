package me.pajic.simple_fishing_overhaul.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import me.pajic.simple_fishing_overhaul.SFOClient;
import me.pajic.simple_fishing_overhaul.util.ModClientUtil;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//~ if <26.3 'renderer.state.level.PlayerRenderState' -> 'player.AbstractClientPlayer'
import net.minecraft.client.renderer.state.level.PlayerRenderState;

//~ if <26.3 'FirstPersonHandsAndItemsRenderer' -> 'ItemInHandRenderer' {
import net.minecraft.client.renderer.FirstPersonHandsAndItemsRenderer;

@Mixin(FirstPersonHandsAndItemsRenderer.class)
//~}
public abstract class ItemInHandRendererMixin {

	@Inject(
			method = {"submitArmWithItem", "renderArmWithItem"},
			at = {
                    @At(
                            value = "INVOKE",
                            target = "Lnet/minecraft/client/renderer/ItemInHandRenderer;renderItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;I)V"
                    ),
                    @At(
                            value = "INVOKE",
                            target = "Lnet/minecraft/client/renderer/item/ItemStackRenderState;submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;III)V"
                    )
            }
	)
	private void fishingRodAnimation(
			CallbackInfo ci,
			@Local(name = "arm") HumanoidArm arm,
            @Local(argsOnly = true) ItemStack itemStack,
            @Local(argsOnly = true) PoseStack poseStack,
            @Local(argsOnly = true, ordinal = 0) float partialTicks,
            @Local(argsOnly = true) InteractionHand hand,
            //~ if <26.3 'PlayerRenderState' -> 'AbstractClientPlayer'
            @Local(argsOnly = true) PlayerRenderState player
	) {
        if (SFOClient.CONFIG.customFirstPersonAnimation.get() && itemStack.getItem() instanceof FishingRodItem) {
            //~ if <26.3 'player.avatarRenderState != null && player.avatarRenderState.isUsingItem' -> 'player.isUsingItem()'
            boolean usingItem = player.avatarRenderState != null && player.avatarRenderState.isUsingItem;
            //~ if <26.3 'player.firstPersonHandsAndItems.useItemRemainingTicks' -> 'player.getUseItemRemainingTicks()'
            int remainingTicks = player.firstPersonHandsAndItems.useItemRemainingTicks;
            //~ if <26.3 'player.avatarRenderState != null ? player.avatarRenderState.useItemHand : null' -> 'player.getUsedItemHand()'
            InteractionHand usedHand = player.avatarRenderState != null ? player.avatarRenderState.useItemHand : null;
            if (usingItem && (remainingTicks - 1) > 0 && usedHand == hand) {
                int invert = arm == HumanoidArm.RIGHT ? 1 : -1;
                //~ if <26.3 'hand == InteractionHand.MAIN_HAND ? player.firstPersonHandsAndItems.mainHandUseDuration : player.firstPersonHandsAndItems.offHandUseDuration' -> 'itemStack.getUseDuration(player)'
                int useDuration = hand == InteractionHand.MAIN_HAND ? player.firstPersonHandsAndItems.mainHandUseDuration : player.firstPersonHandsAndItems.offHandUseDuration;
                poseStack.translate(invert * -0.2785682F, 0.18344387F, 0.15731531F);
                ModClientUtil.rotate(poseStack, Axis.XP, -13.935F);
                ModClientUtil.rotate(poseStack, Axis.ZP, invert * -9.785F);
                float timeHeld = useDuration - (remainingTicks - partialTicks);
                float power = timeHeld / 20.0F;
                ModClientUtil.rotate(poseStack, Axis.XP, power * 20F);
                poseStack.translate(0, 0, power * 0.04F);
            }
        }
	}
}
