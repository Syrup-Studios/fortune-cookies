package net.syrupstudios.fortunecookie.constants;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

/**
 * Inner record to represent a status statusEffect with duration and amplifier
 *
 * @param duration  in ticks (20 ticks = 1 second)
 * @param amplifier 0 = level 1, 1 = level 2, etc.
 */
public record Effect(StatusEffect statusEffect, int duration, int amplifier) {

    /**
     * Parse a StatusEffect from a string identifier
     *
     * @param effectId The statusEffect identifier (e.g., "minecraft:speed")
     * @return The StatusEffect, or null if not found
     */
    public static StatusEffect parseEffect(String effectId) {
        try {
            Identifier id = new Identifier(effectId);
            return Registries.STATUS_EFFECT.get(id);
        } catch (Exception e) {
            return null;
        }
    }
}
