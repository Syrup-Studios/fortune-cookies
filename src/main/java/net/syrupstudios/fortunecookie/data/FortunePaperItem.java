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
    public InteractionResultHolder<ItemStack> use(Level level, Player user, InteractionHand hand) {
        // dynamically grab the item stack from the hand currently in use
        // this stops the mod from eating whatever item is in the main hand when used from the off-hand
        ItemStack heldItem = user.getItemInHand(hand);

        // only trigger action if the player is crouching
        if (user.isCrouching()) {
            if (!level.isClientSide && user instanceof ServerPlayer player) {
                CompoundTag tag = heldItem.getTag();

                // ensure the fortune paper they are using hasn't been used yet
                if (!hasFortune(tag)) {
                    Fortune fortune = FortuneManager.getRandomFortune();
                    heldItem.shrink(1); // Safely shrinks the item stack in the correct hand

                    // give a new custom fortune paper to the player
                    ItemStack fortunePaper = new ItemStack(FortuneCookieMod.FORTUNE_PAPER);
                    FortunePaperItem.setFortune(fortunePaper, fortune.getFortuneValue(), fortune.getAura());

                    if (!player.getInventory().add(fortunePaper)) {
                        player.drop(fortunePaper, false);
                    }

                    // apply effects to the player
                    applyFortuneEffects(player, fortune);

                    // open fortune UI
                    openFortuneUI(player, fortune.getFortuneValue());
                }
                // once fortune paper has already been opened once, simply re-open the UI
                else {
                    openFortuneUI(player, tag.getString("fortune"));
                }
            }
            return InteractionResultHolder.success(heldItem);
        }

        // return pass if the player isn't crouching, allowing normal interactions
        // (like opening chests or placing blocks) instead of overriding right-clicks
        return InteractionResultHolder.pass(heldItem);
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
            // no effects defined - this fortune won't apply any effects
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
        CompoundTag tag = stack.getTag();

        // Check `hasFortune(tag)` instead of `tag != null`.
        // If another mod or an anvil sets an NBT tag on a blank paper, this prevents it from
        // trying to read a missing "aura" field and crashing.
        if (hasFortune(tag)) {
            String auraStr = tag.getString("aura");
            Aura aura = Aura.NEUTRAL;

            // Unsafe Enum Lookup Wrapper
            // Prevents client-side crashes if invalid NBT data is loaded or introduced via /give commands
            try {
                aura = Aura.valueOf(auraStr.toUpperCase());
            } catch (IllegalArgumentException | NullPointerException e) {
                // Safely falls back to NEUTRAL if the string doesn't match an enum constant
            }

            String fortuneText = tag.getString("fortune");

            switch (aura) {
                case POSITIVE:
                    tooltip.add(Component.literal("Positive Aura").withStyle(style -> style.withBold(true).withColor(0x66FFFF)));
                    tooltip.add(Component.literal("\"" + fortuneText + "\"").withStyle(style -> style.withItalic(true).withColor(0xCCFFFF)));
                    break;
                case NEGATIVE:
                    tooltip.add(Component.literal("Negative Aura").withStyle(style -> style.withBold(true).withColor(0xCC0000)));
                    tooltip.add(Component.literal("\"" + fortuneText + "\"").withStyle(style -> style.withItalic(true).withColor(0xFF5555)));
                    break;
                default:
                    tooltip.add(Component.literal("Neutral Aura").withStyle(style -> style.withBold(true).withColor(0xFFFF99)));
                    tooltip.add(Component.literal("\"" + fortuneText + "\"").withStyle(style -> style.withItalic(true).withColor(0xC0C0C0)));
            }
        } else {
            tooltip.add(Component.literal("\"" + DEFAULT_MESSAGE + "\"").withStyle(style ->
                    style.withItalic(true).withColor(0xFFD700)));
        }
    }
}