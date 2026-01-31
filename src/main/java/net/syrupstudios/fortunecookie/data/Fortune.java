package net.syrupstudios.fortunecookie.data;

import net.syrupstudios.fortunecookie.constants.Aura;
import net.syrupstudios.fortunecookie.constants.Effect;

import java.util.ArrayList;
import java.util.List;

public class Fortune {
    private final String fortune;
    private final Aura aura;
    private final List<Effect> effects;
    private final int weight;

    public Fortune(String fortuneValue, Aura aura, List<Effect> effects, int weight) {
        this.fortune = fortuneValue;
        this.aura = aura;
        this.effects = effects != null ? effects : new ArrayList<>();
        this.weight = weight;
    }

    public Aura getAura() {
        return aura;
    }

    public String getFortuneValue() {
        return fortune;
    }

    public List<Effect> getEffects() {
        return effects;
    }

    public int getWeight() {
        return weight;
    }

    public boolean hasCustomEffects() {
        return !effects.isEmpty();
    }

}