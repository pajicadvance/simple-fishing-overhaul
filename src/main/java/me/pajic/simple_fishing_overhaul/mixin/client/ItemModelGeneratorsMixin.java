package me.pajic.simple_fishing_overhaul.mixin.client;

import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import me.pajic.simple_fishing_overhaul.item.ModItems;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@MixinEnvironment(type = MixinEnvironment.Env.CLIENT)
@Mixin(ItemModelGenerators.class)
public abstract class ItemModelGeneratorsMixin {

	@Shadow public abstract void generateFishingRod(Item item);

	@Inject(
			method = "run",
			at = @At("TAIL")
	)
	private void registerModels(CallbackInfo ci) {
		generateFishingRod(ModItems.NETHERITE_FISHING_ROD);
	}
}
