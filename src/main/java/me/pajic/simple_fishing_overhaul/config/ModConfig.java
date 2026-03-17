package me.pajic.simple_fishing_overhaul.config;

import me.fzzyhmstrs.fzzy_config.annotations.Version;
import me.fzzyhmstrs.fzzy_config.config.Config;
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedBoolean;
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedDouble;
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedFloat;
import me.pajic.simple_fishing_overhaul.SFO;

@Version(version = 1)
public class ModConfig extends Config {
    public ModConfig() {
        super(SFO.CONFIG_RL);
    }

	public ValidatedDouble catchRadius = new ValidatedDouble(2, 4, 2);
	public ValidatedFloat intrigueChance = new ValidatedFloat(0.33F, 1, 0);
	public ValidatedDouble intrigueRadius = new ValidatedDouble(10, 16, 8);
	public ValidatedBoolean fishDropNoLoot = new ValidatedBoolean();
	public ValidatedBoolean allowFishingWithoutFish = new ValidatedBoolean(false);
}
