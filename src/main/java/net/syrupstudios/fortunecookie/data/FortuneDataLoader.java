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

            List<Fortune.FortuneEffect> effects = new ArrayList<>();
            if (json.has("effects") && json.get("effects").isJsonArray()) {
                JsonArray effectsArray = json.getAsJsonArray("effects");

                for (JsonElement element : effectsArray) {
                    if (element.isJsonObject()) {
                        JsonObject effectObj = element.getAsJsonObject();
                        Fortune.FortuneEffect effect = parseEffect(effectObj, resourceId);
                        if (effect != null) {
                            effects.add(effect);
                        }
                    }
                }
            }

            return new Fortune(fortuneText, aura, effects, weight);

        } catch (Exception e) {
            LOGGER.error("Error parsing fortune from {}: {}", resourceId, e.getMessage());
            return null;
        }
    }

    private Fortune.FortuneEffect parseEffect(JsonObject effectJson, Identifier resourceId) {
        try {
            if (!effectJson.has("effect")) {
                LOGGER.warn("Effect in {} missing 'effect' field", resourceId);
                return null;
            }

            String effectId = effectJson.get("effect").getAsString();
            StatusEffect effect = Fortune.FortuneEffect.parseEffect(effectId);

            if (effect == null) {
                LOGGER.warn("Unknown effect '{}' in {}", effectId, resourceId);
                return null;
            }

            // 600 ticks = 30 seconds
            int duration = 600;
            if (effectJson.has("duration")) {
                duration = effectJson.get("duration").getAsInt();
            }

            // default to 0 = level 1
            int amplifier = 0;
            if (effectJson.has("amplifier")) {
                amplifier = effectJson.get("amplifier").getAsInt();
            }

            return new Fortune.FortuneEffect(effect, duration, amplifier);

        } catch (Exception e) {
            LOGGER.error("Error parsing effect in {}: {}", resourceId, e.getMessage());
            return null;
        }
    }
}