package me.pajic.simple_fishing_overhaul.fishing;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.ItemStack;

public record FishingCatch(ObjectArrayList<ItemStack> items, boolean caughtFish) {}
