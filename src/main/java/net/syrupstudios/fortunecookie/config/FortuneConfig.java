package net.syrupstudios.fortunecookie.config;

import eu.midnightdust.lib.config.MidnightConfig;

public class FortuneConfig extends MidnightConfig {
    public static final String FORTUNES = "Fortunes";

    @Comment(category = FORTUNES, centered = true) public static Comment fortuneMenuHeader;
    @Comment(category = FORTUNES, centered = true) public static Comment fortuneMenuHeader2;
    @Entry(category = FORTUNES, name="Use default fortunes") public static Boolean USE_DEFAULTS = true;
}