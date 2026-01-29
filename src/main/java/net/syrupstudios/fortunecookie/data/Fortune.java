package net.syrupstudios.fortunecookie.data;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class Fortune {
    private final String fortune;
    private final LuckEffect luckEffect;
    private final List<FortuneEffect> effects;
    private final int weight;

    public Fortune(String fortuneValue, LuckEffect luckEffect, List<FortuneEffect> effects, int weight) {
        this.fortune = fortuneValue;
        this.luckEffect = luckEffect;
        this.effects = effects != null ? effects : new ArrayList<>();
        this.weight = weight;
    }

    public LuckEffect getLuckEffect() {
        return luckEffect;
    }

    public String getFortuneValue() {
        return fortune;
    }

    public List<FortuneEffect> getEffects() {
        return effects;
    }

    public int getWeight() {
        return weight;
    }

    public boolean hasCustomEffects() {
        return !effects.isEmpty();
    }

    /**
     * Inner class to represent a status effect with duration and amplifier
     */
    public static class FortuneEffect {
        private final StatusEffect effect;
        private final int duration; // in ticks (20 ticks = 1 second)
        private final int amplifier; // 0 = level 1, 1 = level 2, etc.

        public FortuneEffect(StatusEffect effect, int duration, int amplifier) {
            this.effect = effect;
            this.duration = duration;
            this.amplifier = amplifier;
        }

        public StatusEffect getEffect() {
            return effect;
        }

        public int getDuration() {
            return duration;
        }

        public int getAmplifier() {
            return amplifier;
        }

        /**
         * Parse a StatusEffect from a string identifier
         * @param effectId The effect identifier (e.g., "minecraft:speed")
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
}