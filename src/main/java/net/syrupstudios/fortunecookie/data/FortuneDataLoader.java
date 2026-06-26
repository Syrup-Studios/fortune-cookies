package net.syrupstudios.fortunecookie.data;

import com.google.gson.*;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.syrupstudios.fortunecookie.FortuneCookieMod;
import net.syrupstudios.fortunecookie.FortuneManager;
import net.syrupstudios.fortunecookie.config.FortuneConfig;
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
    private static final String POSITIVE_FORTUNES_DIRECTORY = "fortunes/positive";
    private static final String NEGATIVE_FORTUNES_DIRECTORY = "fortunes/negative";
    private static final String NEUTRAL_FORTUNES_DIRECTORY = "fortunes/neutral";
    private static final String DEFAULT_FORTUNES_DIRECTORY = "default_fortunes";

    private static FortuneDataLoader INSTANCE;

    public static void register() {
        INSTANCE = new FortuneDataLoader();
        ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(INSTANCE);
    }

    public static FortuneDataLoader getInstance() {
        return INSTANCE;
    }

    @Override
    public ResourceLocation getFabricId() {
        return new ResourceLocation(FortuneCookieMod.MOD_ID, "fortune_loader");
    }

    @Override
    public void onResourceManagerReload(ResourceManager manager) {
        List<Fortune> loadedFortunes = new ArrayList<>();

        // listResources replaces findResources in Mojang mappings
        manager.listResources(POSITIVE_FORTUNES_DIRECTORY, path -> path.getPath().endsWith(".json"))
                .forEach((identifier, resource) ->
                        loadResources(identifier, resource, loadedFortunes));
        manager.listResources(NEUTRAL_FORTUNES_DIRECTORY, path -> path.getPath().endsWith(".json"))
                .forEach((identifier, resource) ->
                        loadResources(identifier, resource, loadedFortunes));
        manager.listResources(NEGATIVE_FORTUNES_DIRECTORY, path -> path.getPath().endsWith(".json"))
                .forEach((identifier, resource) ->
                        loadResources(identifier, resource, loadedFortunes));

        if(FortuneConfig.USE_DEFAULTS){
            manager.listResources(DEFAULT_FORTUNES_DIRECTORY, path -> path.getPath().endsWith(".json"))
                    .forEach((identifier, resource) ->
                            loadResources(identifier, resource, loadedFortunes));
        }

        LOGGER.info("Loaded {} fortunes from datapacks", loadedFortunes.size());

        FortuneManager.setFortunes(loadedFortunes);
    }

    private void loadResources(ResourceLocation identifier, Resource resource, List<Fortune> loadedFortunes) {
        try (InputStream stream = resource.open(); // open() replaces getInputStream()
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
    }

    private Fortune parseFortuneJson(JsonObject json, ResourceLocation resourceId) {
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

    private void attemptEffectParsing(ResourceLocation resourceId, JsonElement element, List<Effect> effects) {
        if (element.isJsonObject()) {
            Effect effect = parseEffect(element.getAsJsonObject(), resourceId);
            if (effect != null) {
                effects.add(effect);
            }
        }
    }

    private Effect parseEffect(JsonObject effectJson, ResourceLocation resourceId) {
        try {
            if (!effectJson.has("effect")) {
                LOGGER.warn("Effect in {} missing 'effect' field", resourceId);
                return null;
            }

            String effectId = effectJson.get("effect").getAsString();
            MobEffect effect = Effect.parseEffect(effectId);

            if (effect == null) {
                LOGGER.warn("Unknown effect '{}' in {}", effectId, resourceId);
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
            LOGGER.error("Error parsing effect in {}: {}", resourceId, e.getMessage());
            return null;
        }
    }
}