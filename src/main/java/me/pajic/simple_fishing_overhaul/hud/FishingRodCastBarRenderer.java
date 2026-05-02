package me.pajic.simple_fishing_overhaul.hud;

import me.pajic.simple_fishing_overhaul.SFO;
import me.pajic.simple_fishing_overhaul.SFOClient;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.contextualbar.ContextualBarRenderer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.util.Mth;
import net.minecraft.world.item.FishingRodItem;
import org.jetbrains.annotations.NotNull;

public class FishingRodCastBarRenderer implements ContextualBarRenderer {
	private static FishingRodCastBarRenderer INSTANCE;
	private final Minecraft minecraft = Minecraft.getInstance();

	public FishingRodCastBarRenderer() {
	}

	public static FishingRodCastBarRenderer getInstance() {
		if (INSTANCE == null) INSTANCE = new FishingRodCastBarRenderer();
		return INSTANCE;
	}

	@Override
	public void extractBackground(@NotNull final GuiGraphicsExtractor graphics, @NotNull final DeltaTracker deltaTracker) {
		if (SFOClient.CONFIG.castBar.get() && minecraft.player != null && minecraft.player.getUseItem().getItem() instanceof FishingRodItem) {
			int left = left(minecraft.getWindow());
			int top = top(minecraft.getWindow());
			graphics.blitSprite(RenderPipelines.GUI_TEXTURED, SFO.id("hud/cast_bar_background"), left, top, 182, 5);
			int progress = Mth.lerpDiscrete(1 - (float) (minecraft.player.getUseItemRemainingTicks() - 1) / 60, 0, 182);
			if (progress > 0) {
				graphics.blitSprite(RenderPipelines.GUI_TEXTURED, SFO.id("hud/cast_bar_progress"), 182, 5, 0, 0, left, top, progress, 5);
			}
		}
	}

	@Override
	public void extractRenderState(@NotNull final GuiGraphicsExtractor graphics, @NotNull final DeltaTracker deltaTracker) {
	}
}
