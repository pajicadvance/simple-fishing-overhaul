package me.pajic.simple_fishing_overhaul.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import me.pajic.simple_fishing_overhaul.util.FishingUtil;
import me.pajic.simple_fishing_overhaul.util.PlayerExtension;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(FishingRodItem.class)
public abstract class FishingRodItemMixin extends Item {

	public FishingRodItemMixin(Properties properties) {
		super(properties);
	}

	@Shadow
	public abstract @NotNull InteractionResult use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand);

	@SuppressWarnings("MixinExtrasOperationParameters")
	@WrapMethod(method = "use")
	private InteractionResult wrapUse(Level level, Player player, InteractionHand hand, Operation<InteractionResult> original) {
		if (FishingUtil.shouldChargeCast(player)) {
			player.startUsingItem(hand);
			return InteractionResult.CONSUME;
		} else {
			((PlayerExtension) player).sfo$setShouldCastFishingRod(false);
			return original.call(level, player, hand);
		}
	}

	@Override
	public boolean releaseUsing(@NotNull ItemStack itemStack, @NotNull Level level, @NotNull LivingEntity entity, int remainingTime) {
		if (entity instanceof Player player && FishingUtil.shouldChargeCast(player)) {
			((PlayerExtension) player).sfo$setShouldCastFishingRod(true);
			use(level, player, player.getUsedItemHand());
			player.getCooldowns().addCooldown(itemStack, 30);
			return true;
		}
		return super.releaseUsing(itemStack, level, entity, remainingTime);
	}

	@Override
	public int getUseDuration(@NotNull ItemStack itemStack, @NotNull LivingEntity user) {
		return user instanceof Player player && FishingUtil.shouldChargeCast(player) ? 61 : super.getUseDuration(itemStack, user);
	}

	@Override
	public void onUseTick(@NotNull Level level, @NotNull LivingEntity livingEntity, @NotNull ItemStack itemStack, int ticksRemaining) {
		if (livingEntity instanceof Player player) {
			((PlayerExtension) player).sfo$setRemainingCastTime(ticksRemaining - 1);
			if (ticksRemaining == 1) {
				player.releaseUsingItem();
				player.getCooldowns().addCooldown(itemStack, 30);
			}
		}
	}

	@Override
	public @NotNull ItemUseAnimation getUseAnimation(@NotNull ItemStack itemStack) {
		return ItemUseAnimation.BOW;
	}
}
