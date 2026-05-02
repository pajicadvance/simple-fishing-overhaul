package me.pajic.simple_fishing_overhaul.config;

import me.fzzyhmstrs.fzzy_config.annotations.Version;
import me.fzzyhmstrs.fzzy_config.config.Config;
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedBoolean;
import me.pajic.simple_fishing_overhaul.SFO;

@Version(version = 1)
public class ModClientConfig extends Config {

	public ModClientConfig() {
		super(SFO.id("client_config"));
	}

	public ValidatedBoolean castBar = new ValidatedBoolean();
	public ValidatedBoolean customFirstPersonAnimation = new ValidatedBoolean();
	public ValidatedBoolean customThirdPersonAnimation = new ValidatedBoolean();
}
