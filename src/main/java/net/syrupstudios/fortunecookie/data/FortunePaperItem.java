package net.syrupstudios.fortunecookie.data;

import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.level.Level;
import net.syrupstudios.fortunecookie.*;
import net.syrupstudios.fortunecookie.constants.Aura;
import net.syrupstudios.fortunecookie.constants.Effect;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FortunePaperItem extends Item {
    private static final String DEFAULT_MESSAGE = "Your fortune awaits.. Crouch + Use to discover your fate!";

    public FortunePaperItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player user, InteractionHand hand){
        // Standard check for use to ensure we are dealing with an actual player, and that they are crouching
        if (!level.isClientSide && user instanceof ServerPlayer player && user.isCrouching()) {
            // Ensure the fortune paper they are using hasn't been used yet
            if (!hasFortune(player.getMainHandItem().getTag())) {
                Fortune fortune = FortuneManager.getRandomFortune();
                player.getMainHandItem().shrink(1);

                // Give fortune paper to player
                ItemStack fortunePaper = new ItemStack(FortuneCookieMod.FORTUNE_PAPER);
                FortunePaperItem.setFortune(fortunePaper, fortune.getFortuneValue(), fortune.getAura());

                if (!player.getInventory().add(fortunePaper)) {
                    player.drop(fortunePaper, false);
                }

                // Apply effects to the player
                applyFortuneEffects(player, fortune);

                // Open fortune UI
                openFortuneUI(player, fortune.getFortuneValue());
            }
            //once fortune paper has already been opened once, simply open UI
            else {
                openFortuneUI(player, player.getMainHandItem().getTag().getString("fortune"));
            }
        }
        return InteractionResultHolder.success(user.getItemInHand(hand));
    }

    /**
     * Apply effects from a fortune to a player
     * All effects must be defined in the datapack
     */
    private void applyFortuneEffects(ServerPlayer player, Fortune fortune) {
        if (fortune.hasCustomEffects()) {
            for (Effect effect : fortune.getEffects()) {
                player.addEffect(new MobEffectInstance(
                        effect.mobEffect(),
                        effect.duration(),
                        effect.amplifier()
                ));
            }
        } else {
            // No effects defined - this fortune won't apply any effects
            System.out.println("[Fortune Cookies] Warning: Fortune '" + fortune.getFortuneValue() + "' has no effects defined!");
        }
    }

    private static void openFortuneUI(ServerPlayer player, String fortune) {
        FortunePacketHandler.sendFortuneToClient(player, fortune);
    }

    public static void setFortune(ItemStack stack, String fortune, Aura aura) {
        CompoundTag nbt = stack.getOrCreateTag();
        nbt.putString("aura", aura.toString());
        nbt.putString("fortune", fortune);
    }

    private static boolean hasFortune(CompoundTag nbt) {
        return nbt != null && nbt.contains("fortune");
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag context) {
        if(stack.getTag() != null) {
            String aura = stack.getTag().getString("aura");
            switch(Aura.valueOf(aura)) {
                case POSITIVE:
                    tooltip.add(Component.literal("Positive Aura").withStyle(style -> style.withBold(true).withColor(0x66FFFF)));
                    tooltip.add(Component.literal("\"" + stack.getTag().getString("fortune") + "\"").withStyle(style -> style.withItalic(true).withColor(0xCCFFFF)));
                    break;
                case NEGATIVE:
                    tooltip.add(Component.literal("Negative Aura").withStyle(style -> style.withBold(true).withColor(0xCC0000)));
                    tooltip.add(Component.literal("\"" + stack.getTag().getString("fortune") + "\"").withStyle(style -> style.withItalic(true).withColor(0xFF5555)));
                    break;
                default:
                    tooltip.add(Component.literal("Neutral Aura").withStyle(style -> style.withBold(true).withColor(0xFFFF99)));
                    tooltip.add(Component.literal("\"" + stack.getTag().getString("fortune") + "\"").withStyle(style -> style.withItalic(true).withColor(0xC0C0C0)));
            }
        }
        else {
            tooltip.add(Component.literal("\"" + DEFAULT_MESSAGE + "\"").withStyle(style ->
                    style.withItalic(true).withColor(0xFFD700)));
        }
    }
}