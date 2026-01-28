package net.syrupstudios.fortunecookie.constants;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public enum Effect {
    RANDOM(null, "random"),
    SPEED(StatusEffects.SPEED, "speed"),
    HASTE(StatusEffects.HASTE, "haste"),
    STRENGTH(StatusEffects.STRENGTH, "strength"),
    INSTANT_HEALTH(StatusEffects.INSTANT_HEALTH, "instant_health"),
    JUMP_BOOST(StatusEffects.JUMP_BOOST, "jump_boost"),
    REGENERATION(StatusEffects.REGENERATION, "regeneration"),
    RESISTANCE(StatusEffects.RESISTANCE, "resistance"),
    FIRE_RESISTANCE(StatusEffects.FIRE_RESISTANCE, "fire_resistance"),
    WATER_BREATHING(StatusEffects.WATER_BREATHING, "water_breathing"),
    INVISIBILITY(StatusEffects.INVISIBILITY, "invisibility"),
    NIGHT_VISION(StatusEffects.NIGHT_VISION, "night_vision"),
    HEALTH_BOOST(StatusEffects.HEALTH_BOOST, "health_boost"),
    ABSORPTION(StatusEffects.ABSORPTION, "absorption"),
    SATURATION(StatusEffects.SATURATION, "saturation"),
    LUCK(StatusEffects.LUCK, "luck"),
    SLOW_FALLING(StatusEffects.SLOW_FALLING, "slow_falling"),
    CONDUIT_POWER(StatusEffects.CONDUIT_POWER, "conduit_power"),
    DOLPHINS_GRACE(StatusEffects.DOLPHINS_GRACE, "dolphins_grace"),
    HERO_OF_THE_VILLAGE(StatusEffects.HERO_OF_THE_VILLAGE, "hero_of_the_village"),
    GLOWING(StatusEffects.GLOWING, "glowing"),
    BAD_OMEN(StatusEffects.BAD_OMEN, "bad_omen"),
    SLOWNESS(StatusEffects.SLOWNESS, "slowness"),
    MINING_FATIGUE(StatusEffects.MINING_FATIGUE, "mining_fatigue"),
    INSTANT_DAMAGE(StatusEffects.INSTANT_DAMAGE, "instant_damage"),
    NAUSEA(StatusEffects.NAUSEA, "nausea"),
    BLINDNESS(StatusEffects.BLINDNESS, "blindness"),
    HUNGER(StatusEffects.HUNGER, "hunger"),
    WEAKNESS(StatusEffects.WEAKNESS, "weakness"),
    POISON(StatusEffects.POISON, "poison"),
    WITHER(StatusEffects.WITHER, "wither"),
    LEVITATION(StatusEffects.LEVITATION, "levitation"),
    UNLUCK(StatusEffects.UNLUCK, "unluck"),
    DARKNESS(StatusEffects.DARKNESS, "darkness");

    private final StatusEffect statusEffect;
    private final String id;

    public static final List<StatusEffect> POSITIVE_EFFECTS = Stream.of(
            SPEED,
            HASTE,
            STRENGTH,
            INSTANT_HEALTH,
            JUMP_BOOST,
            REGENERATION,
            RESISTANCE,
            FIRE_RESISTANCE,
            WATER_BREATHING,
            INVISIBILITY,
            NIGHT_VISION,
            HEALTH_BOOST,
            ABSORPTION,
            SATURATION,
            LUCK,
            SLOW_FALLING,
            CONDUIT_POWER,
            DOLPHINS_GRACE,
            HERO_OF_THE_VILLAGE
    ).map(Effect::getStatusEffect).toList();

    public static final List<StatusEffect> NEUTRAL_EFFECTS = Stream.of(
            GLOWING,
            BAD_OMEN
    ).map(Effect::getStatusEffect).toList();

    public static final List<StatusEffect> NEGATIVE_EFFECTS = Stream.of(
            SLOWNESS,
            MINING_FATIGUE,
            INSTANT_DAMAGE,
            NAUSEA,
            BLINDNESS,
            HUNGER,
            WEAKNESS,
            POISON,
            WITHER,
            LEVITATION,
            UNLUCK,
            DARKNESS
    ).map(Effect::getStatusEffect).toList();

    Effect(StatusEffect statusEffect, String id) {
        this.statusEffect = statusEffect;
        this.id = id;
    }

    public StatusEffect getStatusEffect() { return this.statusEffect; }

    public static Optional<Effect> effectFromJson(String json) {
        return Arrays.stream(Effect.values())
                .filter(effect -> effect.id.equals(json.toLowerCase()))
                .findFirst();
    }
}
