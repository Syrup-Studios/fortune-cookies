package net.syrupstudios.fortunecookie;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.resources.ResourceLocation;

public class FortunePacketHandler {
    public static final ResourceLocation FORTUNE_PACKET_ID = new ResourceLocation(FortuneCookieMod.MOD_ID, "fortune");

    public static void sendFortuneToClient(ServerPlayer player, String fortune) {
        FriendlyByteBuf buf = PacketByteBufs.create();
        buf.writeUtf(fortune);
        ServerPlayNetworking.send(player, FORTUNE_PACKET_ID, buf);
    }
}