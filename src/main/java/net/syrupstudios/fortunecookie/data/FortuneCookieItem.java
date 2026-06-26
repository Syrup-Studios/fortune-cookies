package net.syrupstudios.fortunecookie.data;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.syrupstudios.fortunecookie.FortuneCookieMod;

public class FortuneCookieItem extends Item {
    public FortuneCookieItem(Properties properties) {
        super(properties);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity user) {
        ItemStack result = super.finishUsingItem(stack, level, user);

        if (!level.isClientSide && user instanceof ServerPlayer player) {
            // Give fortune paper to player
            ItemStack fortunePaper = new ItemStack(FortuneCookieMod.FORTUNE_PAPER);
            if (!player.getInventory().add(fortunePaper)) {
                player.drop(fortunePaper, false);
            }
        }
        return result;
    }
}