package com.mentality.stormsurvivalbackpackdeathcompat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.fabricmc.api.ModInitializer;

public final class StormSurvivalBackpackDeathCompat implements ModInitializer {
    public static final String MOD_ID = "storm_survival_backpack_death_compat";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public StormSurvivalBackpackDeathCompat() {
    }

    @Override
    public void onInitialize() {
        LOGGER.info("[Storm Backpack Death Compat] initialized");
    }
}
