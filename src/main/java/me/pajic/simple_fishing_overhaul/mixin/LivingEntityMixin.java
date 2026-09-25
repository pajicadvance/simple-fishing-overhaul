package me.pajic.simple_fishing_overhaul.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import me.pajic.simple_fishing_overhaul.SFO;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.fish.AbstractFish;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @ModifyExpressionValue(
            method = "dropAllDeathLoot",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/LivingEntity;shouldDropLoot(Lnet/minecraft/server/level/ServerLevel;)Z"
            )
    )
    private boolean noLootFromFish(boolean original) {
        return (!((LivingEntity) (Object) this instanceof AbstractFish) || !SFO.CONFIG.fishDropNoLoot.get()) && original;
    }
}
