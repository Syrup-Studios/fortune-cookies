package net.syrupstudios.fortunecookie;

import net.syrupstudios.fortunecookie.constants.Aura;
import net.syrupstudios.fortunecookie.data.Fortune;
import net.syrupstudios.fortunecookie.data.FortuneDataLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FortuneManager {
    private static final Random RANDOM = new Random();
    private static final List<Fortune> FORTUNES = new ArrayList<>();

    public static void initialize() {
        FortuneDataLoader.register();
    }

    public static void setFortunes(List<Fortune> fortunes) {
        FORTUNES.clear();
        FORTUNES.addAll(fortunes);
        System.out.println("[Fortune Cookies] FortuneManager now has " + FORTUNES.size() + " fortunes");
    }

    public static Fortune getRandomFortune() {
        if (FORTUNES.isEmpty()) {
            return new Fortune("No fortunes loaded! Please add a fortune datapack.", Aura.NEUTRAL, new ArrayList<>(), 10);
        }

        int totalWeight = FORTUNES.stream()
                .mapToInt(Fortune::getWeight)
                .sum();

        if (totalWeight <= 0) {
            return FORTUNES.get(RANDOM.nextInt(FORTUNES.size()));
        }

        int randomWeight = RANDOM.nextInt(totalWeight);
        int currentWeight = 0;

        for (Fortune fortune : FORTUNES) {
            currentWeight += fortune.getWeight();
            if (randomWeight < currentWeight) {
                return fortune;
            }
        }

        return FORTUNES.get(FORTUNES.size() - 1);
    }


    public static List<Fortune> getAllFortunes() {
        return new ArrayList<>(FORTUNES);
    }
}