package net.syrupstudios.fortunecookie.config;

import eu.midnightdust.lib.config.MidnightConfig;

import java.util.List;

public class FortuneConfig extends MidnightConfig {
    public static final String FORTUNES = "Fortunes";

    @Comment(category = FORTUNES, centered = true, name = "Select whether or not you would like default fortunes in addition") public static Comment fortuneMenuHeader;
    @Comment(category = FORTUNES, centered = true, name = "to your custom datapack loaded fortunes.") public static Comment fortuneMenuHeader2;
    @Entry(category = FORTUNES, name="Use default fortunes") public static Boolean USE_DEFAULTS = true;

}
