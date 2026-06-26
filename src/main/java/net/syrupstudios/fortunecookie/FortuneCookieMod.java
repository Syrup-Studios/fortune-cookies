package net.syrupstudios.fortunecookie;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.syrupstudios.fortunecookie.data.FortuneCookieItem;
import net.syrupstudios.fortunecookie.data.FortuneDataLoader;
import net.syrupstudios.fortunecookie.data.FortunePaperItem;

public class FortuneCookieMod implements ModInitializer {
    public static final String MOD_ID = "luckyducks_fortunecookies";

    public static final Item FORTUNE_COOKIE = new FortuneCookieItem(
            new FabricItemSettings()
                    .food(new FoodProperties.Builder()
                            .nutrition(2)
                            .saturationMod(0.3f)
                            .alwaysEat()
                            .build())
    );

    public static final Item FORTUNE_PAPER = new FortunePaperItem(
            new FabricItemSettings()
    );

    @Override
    public void onInitialize() {
        FortuneDataLoader.register();

        FortuneManager.initialize();

        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(MOD_ID, "fortune_cookie"), FORTUNE_COOKIE);
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(MOD_ID, "fortune_paper"), FORTUNE_PAPER);

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.FOOD_AND_DRINKS).register(content -> {
            content.accept(FORTUNE_COOKIE);
        });

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.INGREDIENTS).register(content -> {
            content.accept(FORTUNE_PAPER);
        });

        // Register datapack reload event to reload fortunes
        // NOTE: Do NOT use SERVER_STARTED - the datapacks haven't loaded yet!
        // The FortuneDataLoader.reload() method will automatically call FortuneManager.reloadFortunes()
        ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, resourceManager, success) -> {
            if (success) {
                System.out.println("[Fortune Cookies] Data pack reload completed, fortunes should now be loaded");
            }
        });
    }
}