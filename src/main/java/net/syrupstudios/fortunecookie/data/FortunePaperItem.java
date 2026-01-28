package net.syrupstudios.fortunecookie.data;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.syrupstudios.fortunecookie.*;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FortunePaperItem extends Item {
    private static final String DEFAULT_MESSAGE = "Your fortune awaits.. Crouch + Use to discover your fate!";

    public FortunePaperItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand){
        // Standard check for use to ensure we are dealing with an actual player, and that they are crouching
        if (!world.isClient && user instanceof ServerPlayerEntity player && user.isSneaking()) {
            // Ensure the fortune paper they are using hasn't been used yet
            if (!hasFortune(player.getMainHandStack().getNbt())) {
                Fortune fortune = FortuneManager.getRandomFortune();
                player.getMainHandStack().decrement(1);

                // Give fortune paper to player
                ItemStack fortunePaper = new ItemStack(FortuneCookieMod.FORTUNE_PAPER);
                FortunePaperItem.setFortune(fortunePaper, fortune.getFortuneValue(), fortune.getLuckEffect());

                if (!player.getInventory().insertStack(fortunePaper)) {
                    player.dropItem(fortunePaper, false);
                }

                // Apply effects to the player
                applyFortuneEffects(player, fortune);

                // Open fortune UI
                openFortuneUI(player, fortune.getFortuneValue());
            }
            //once fortune paper has already been opened once, simply open UI
            else {
                openFortuneUI(player, player.getMainHandStack().getNbt().getString("fortune"));
            }
        }
        return TypedActionResult.success(user.getStackInHand(hand));
    }

    /**
     * Apply effects from a fortune to a player
     * All effects must be defined in the datapack
     */
    private void applyFortuneEffects(ServerPlayerEntity player, Fortune fortune) {
        if (fortune.hasCustomEffects()) {
            for (Fortune.FortuneEffect effect : fortune.getEffects()) {
                player.addStatusEffect(new StatusEffectInstance(
                        effect.getEffect(),
                        effect.getDuration(),
                        effect.getAmplifier()
                ));
            }
        } else {
            // No effects defined - this fortune won't apply any effects
            System.out.println("[Fortune Cookies] Warning: Fortune '" + fortune.getFortuneValue() + "' has no effects defined!");
        }
    }

    private static void openFortuneUI(ServerPlayerEntity player, String fortune) {
        FortunePacketHandler.sendFortuneToClient(player, fortune);
    }

    public static void setFortune(ItemStack stack, String fortune, LuckEffect luckEffect) {
        NbtCompound nbt = stack.getOrCreateNbt();
        nbt.putString("luck_effect", luckEffect.toString());
        nbt.putString("fortune", fortune);
    }

    private static boolean hasFortune(NbtCompound nbt) {
        return nbt != null && nbt.contains("fortune");
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        if(stack.getNbt()!= null) {
            String luckString = stack.getNbt().getString("luck_effect");
            switch(LuckEffect.valueOf(luckString)) {
                case GOOD:
                    tooltip.add(Text.literal("Positive Aura").styled(style -> style.withBold(true).withColor(0x66FFFF)));
                    tooltip.add(Text.literal("\"" +stack.getNbt().getString("fortune")+ "\"").styled(style -> style.withItalic(true).withColor(0xCCFFFF)));
                    break;
                case BAD:
                    tooltip.add(Text.literal("Negative Aura").styled(style -> style.withBold(true).withColor(0xCC0000)));
                    tooltip.add(Text.literal("\"" +stack.getNbt().getString("fortune")+ "\"").styled(style -> style.withItalic(true).withColor(0xFF5555)));
                    break;
                default:
                    tooltip.add(Text.literal("Neutral Aura").styled(style -> style.withBold(true).withColor(0xFFFF99)));
                    tooltip.add(Text.literal("\"" +stack.getNbt().getString("fortune")+ "\"").styled(style -> style.withItalic(true).withColor(0xC0C0C0)));
            }
        }
        else {
            tooltip.add(Text.literal("\"" + DEFAULT_MESSAGE + "\"").styled(style ->
                    style.withItalic(true).withColor(0xFFD700)));
        }
    }
}