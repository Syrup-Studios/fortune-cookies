package net.syrupstudios.fortunecookie.data;

import com.google.gson.*;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.syrupstudios.fortunecookie.FortuneCookieMod;
import net.syrupstudios.fortunecookie.FortuneManager;
import net.syrupstudios.fortunecookie.constants.Aura;
import net.syrupstudios.fortunecookie.constants.Effect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class FortuneDataLoader implements SimpleSynchronousResourceReloadListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(FortuneDataLoader.class);
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String FORTUNE_DIRECTORY = "fortunes";

    private static FortuneDataLoader INSTANCE;

    public static void register() {
        INSTANCE = new FortuneDataLoader();
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(INSTANCE);
    }

    public static FortuneDataLoader getInstance() {
        return INSTANCE;
    }

    @Override
    public Identifier getFabricId() {
        return new Identifier(FortuneCookieMod.MOD_ID, "fortune_loader");
    }

    @Override
    public void reload(ResourceManager manager) {
        List<Fortune> loadedFortunes = new ArrayList<>();

        // Find all fortune JSON files across all namespaces (this tripped me up a bunch when testing)
        manager.findResources(FORTUNE_DIRECTORY, path -> path.getPath().endsWith(".json"))
                .forEach((identifier, resource) -> {
                    try (InputStream stream = resource.getInputStream();
                         InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {

                        JsonObject json = GSON.fromJson(reader, JsonObject.class);
                        Fortune fortune = parseFortuneJson(json, identifier);

                        if (fortune != null) {
                            loadedFortunes.add(fortune);
                            LOGGER.info("Loaded fortune from: {}", identifier);
                        }
                    } catch (Exception e) {
                        LOGGER.error("Error loading fortune from {}: {}", identifier, e.getMessage());
                    }
                });

        LOGGER.info("Loaded {} fortunes from datapacks", loadedFortunes.size());

        FortuneManager.setFortunes(loadedFortunes);
    }

    private Fortune parseFortuneJson(JsonObject json, Identifier resourceId) {
        try {
            if (!json.has("fortune")) {
                LOGGER.error("Fortune file {} missing required 'fortune' field", resourceId);
                return null;
            }

            String fortuneText = json.get("fortune").getAsString();

            Aura aura = Aura.NEUTRAL;
            if (json.has("aura")) {
                try {
                    aura = Aura.valueOf(json.get("aura").getAsString().toUpperCase());
                } catch (IllegalArgumentException e) {
                    LOGGER.warn("Invalid aura in {}, defaulting to NEUTRAL", resourceId);
                }
            }

            int weight = 10;
            if (json.has("weight")) {
                weight = json.get("weight").getAsInt();
            }

            List<Effect> effects = new ArrayList<>();
            if (json.has("effects") && json.get("effects").isJsonArray()) {
                JsonArray effectsArray = json.getAsJsonArray("effects");
                effectsArray.forEach(element -> attemptEffectParsing(resourceId, element, effects));
            }

            return new Fortune(fortuneText, aura, effects, weight);

        } catch (Exception e) {
            LOGGER.error("Error parsing fortune from {}: {}", resourceId, e.getMessage());
            return null;
        }
    }

    private void attemptEffectParsing(Identifier resourceId, JsonElement element, List<Effect> effects) {
        if (element.isJsonObject()) {
            Effect effect = parseEffect(element.getAsJsonObject(), resourceId);
            if (effect != null) {
                effects.add(effect);
            }
        }
    }

    private Effect parseEffect(JsonObject effectJson, Identifier resourceId) {
        try {
            if (!effectJson.has("statusEffect")) {
                LOGGER.warn("Effect in {} missing 'statusEffect' field", resourceId);
                return null;
            }

            String effectId = effectJson.get("statusEffect").getAsString();
            StatusEffect effect = Effect.parseEffect(effectId);

            if (effect == null) {
                LOGGER.warn("Unknown statusEffect '{}' in {}", effectId, resourceId);
                return null;
            }

            // 600 ticks = 30 seconds, multiply json duration by 20 to compute seconds -> ticks
            int duration = 600;
            if (effectJson.has("duration")) {
                duration = effectJson.get("duration").getAsInt()*20;
            }

            // default to 0 = level 1
            int amplifier = 0;
            if (effectJson.has("amplifier")) {
                amplifier = effectJson.get("amplifier").getAsInt();
            }

            return new Effect(effect, duration, amplifier);

        } catch (Exception e) {
            LOGGER.error("Error parsing statusEffect in {}: {}", resourceId, e.getMessage());
            return null;
        }
    }
}