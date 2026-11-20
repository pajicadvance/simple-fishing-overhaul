package me.pajic.simple_fishing_overhaul.fishing;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.fish.AbstractFish;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.level.block.Blocks;

public class FishingParticles {

	public static void spawnApproachParticles(ServerLevel level, FishingHook hook, AbstractFish fish) {
		double dx = hook.getX() - fish.getX();
		double dz = hook.getZ() - fish.getZ();
		double length = Math.sqrt(dx * dx + dz * dz);
		if (length < 1.0E-4) return;
		float sin = (float) (dx / length);
		float cos = (float) (dz / length);
		if (hook.getRandom().nextFloat() < 0.15F) level.sendParticles(
                ParticleTypes.BUBBLE,
                fish.getX(), fish.getY() + fish.getBbHeight() * 0.5, fish.getZ(),
                1, sin, 0.1, cos, 0.0
        );
		double surfaceY = Mth.floor(hook.getY()) + 1.0;
		if (surfaceY - fish.getY() > 3) return;
		if (!level.getBlockState(BlockPos.containing(fish.getX(), surfaceY - 1, fish.getZ())).is(Blocks.WATER)) return;
		float xSpeed = sin * 0.04F;
		float zSpeed = cos * 0.04F;
		level.sendParticles(
                ParticleTypes.FISHING,
                fish.getX(), surfaceY, fish.getZ(),
                0, zSpeed, 0.01, -xSpeed, 1.0
        );
		level.sendParticles(
                ParticleTypes.FISHING,
                fish.getX(), surfaceY, fish.getZ(),
                0, -zSpeed, 0.01, xSpeed, 1.0
        );
	}
}
