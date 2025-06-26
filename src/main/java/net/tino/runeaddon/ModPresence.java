package net.tino.runeaddon;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class ModPresence {
    private static final int RECEIVE_MESSAGE_TIMEOUT = 20 * 30; // 20 ticks per second, 30 seconds
    public static final Map<UUID, Integer> SERVER_LOGIN_TICKS = new HashMap<>();
    public static final Set<UUID> PLAYERS_WITH_RUNE_ADDON = new HashSet<>();
    public static boolean serverHasRuneAddon = false;
    public static int clientLoginTicks = 0;
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(VaultFiltersRuneAddon.MOD_ID, "main"),
            () -> "1",
            o -> true,
            o -> true
    );

    public static void init() {
        CHANNEL.messageBuilder(ModPresence.Message.class, 0)
                .encoder(ModPresence.Message::encoder)
                .decoder(ModPresence.Message::decoder)
                .consumer(ModPresence.Message::consumer)
                .add();
    }

    public static boolean serverHasRuneAddon() {
        if (net.minecraftforge.fml.loading.FMLEnvironment.dist == net.minecraftforge.api.distmarker.Dist.CLIENT) {
            return Minecraft.getInstance().isLocalServer() || serverHasRuneAddon;
        }
        return true; // Always enabled on the server if the mod is loaded
    }

    public static boolean playerHasRuneAddon(UUID uuid) {
        return DistExecutor.unsafeRunForDist(
            () -> () -> Minecraft.getInstance().isLocalServer(),
            () -> () -> PLAYERS_WITH_RUNE_ADDON.contains(uuid)
        );
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void onClientConnect(ClientPlayerNetworkEvent.LoggedInEvent event) {
        CHANNEL.sendToServer(new ModPresence.Message(VaultFiltersRuneAddon.MOD_VERSION));
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.getCurrentServer() == null || mc.player == null || serverHasRuneAddon || event.phase != TickEvent.Phase.END || clientLoginTicks == RECEIVE_MESSAGE_TIMEOUT)
            return;

        clientLoginTicks++;
        if (clientLoginTicks == RECEIVE_MESSAGE_TIMEOUT) {
            Component msg = new TextComponent("Vault Filters Rune Addon not detected on server, custom rune filter features are disabled.").withStyle(ChatFormatting.RED);
            mc.player.displayClientMessage(msg, false);
        }
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void onClientDisconnect(ClientPlayerNetworkEvent.LoggedOutEvent event) {
        serverHasRuneAddon = false;
        clientLoginTicks = 0;
    }

    @OnlyIn(Dist.DEDICATED_SERVER)
    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getPlayer() instanceof ServerPlayer player) {
            CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new ModPresence.Message(VaultFiltersRuneAddon.MOD_VERSION));
            SERVER_LOGIN_TICKS.put(player.getUUID(), 0);
        }
    }

    @OnlyIn(Dist.DEDICATED_SERVER)
    @SubscribeEvent
    public static void onServerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !(event.player instanceof ServerPlayer player)) return;
        UUID uuid = player.getUUID();
        Integer ticks = SERVER_LOGIN_TICKS.get(uuid);
        if (ticks == null) return;
        if (ticks < RECEIVE_MESSAGE_TIMEOUT) {
            SERVER_LOGIN_TICKS.put(uuid, ticks + 1);
        } else {
            Component msg = new TextComponent("This server has Vault Filters Rune Addon installed, but your client does not. Please install version " + VaultFiltersRuneAddon.MOD_VERSION + " for custom rune filter features.").withStyle(ChatFormatting.RED);
            player.displayClientMessage(msg, false);
            SERVER_LOGIN_TICKS.remove(uuid);
        }
    }

    @OnlyIn(Dist.DEDICATED_SERVER)
    @SubscribeEvent
    public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getPlayer() instanceof ServerPlayer player) {
            PLAYERS_WITH_RUNE_ADDON.remove(player.getUUID());
            SERVER_LOGIN_TICKS.remove(player.getUUID());
        }
    }

    public static class Message {
        protected final String version;

        public Message(String version) {
            this.version = version;
        }

        public void encoder(FriendlyByteBuf buf) {
            buf.writeUtf(this.version);
        }

        public static Message decoder(FriendlyByteBuf buf) {
            return new Message(buf.readUtf());
        }

        public void consumer(Supplier<NetworkEvent.Context> ctx) {
            NetworkEvent.Context context = ctx.get();
            LogicalSide side = context.getDirection().getReceptionSide();

            DistExecutor.unsafeRunForDist(
                () -> () -> {
                    if (side.isClient()) {
                        serverHasRuneAddon = this.version.equals(VaultFiltersRuneAddon.MOD_VERSION);
                        clientLoginTicks = RECEIVE_MESSAGE_TIMEOUT;
                    }
                    return null;
                },
                () -> () -> {
                    if (side.isServer()) {
                        ServerPlayer player = context.getSender();
                        if (player != null) {
                            UUID uuid = player.getUUID();
                            if (this.version.equals(VaultFiltersRuneAddon.MOD_VERSION)) {
                                PLAYERS_WITH_RUNE_ADDON.add(uuid);
                            } else {
                                Component msg = new TextComponent("Vault Filters Rune Addon version mismatch! Please install version " + VaultFiltersRuneAddon.MOD_VERSION + " for custom rune filter features.").withStyle(ChatFormatting.RED);
                                player.displayClientMessage(msg,false);
                            }
                            SERVER_LOGIN_TICKS.remove(uuid);
                        }
                    }
                    return null;
                }
            );
            context.setPacketHandled(true);
        }
    }
}
