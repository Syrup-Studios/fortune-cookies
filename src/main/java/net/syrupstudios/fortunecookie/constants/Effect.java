package net.syrupstudios.fortunecookie.constants;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;

/**
 * Inner record to represent a status effect with duration and amplifier
 *
 * @param duration  in ticks (20 ticks = 1 second)
 * @param amplifier 0 = level 1, 1 = level 2, etc.
 */
public record Effect(MobEffect mobEffect, int duration, int amplifier) {

    /**
     * Parse a MobEffect from a string identifier
     *
     * @param effectId The effect identifier (e.g., "minecraft:speed")
     * @return The MobEffect, or null if not found
     */
    public static MobEffect parseEffect(String effectId) {
        try {
            ResourceLocation id = new ResourceLocation(effectId);
            return BuiltInRegistries.MOB_EFFECT.get(id);
        } catch (Exception e) {
            return null;
        }
    }
}