package arcana.common.network;

import arcana.Arcana;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public final class PacketHandler {
    private static final String PROTOCOL = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(Arcana.MODID, "main"),
            () -> PROTOCOL,
            PROTOCOL::equals,
            PROTOCOL::equals);

    private static int id;

    private PacketHandler() {
    }

    public static void register() {
        id = 0;
        CHANNEL.registerMessage(id++, PacketSyncKnowledge.class,
                PacketSyncKnowledge::encode,
                PacketSyncKnowledge::decode,
                PacketSyncKnowledge::handle);
        CHANNEL.registerMessage(id++, PacketAuraToClient.class,
                PacketAuraToClient::encode,
                PacketAuraToClient::decode,
                PacketAuraToClient::handle);
        CHANNEL.registerMessage(id++, PacketProgressResearch.class,
                PacketProgressResearch::encode,
                PacketProgressResearch::decode,
                PacketProgressResearch::handle);
    }

    public static void syncKnowledge(ServerPlayer player) {
        CHANNEL.sendTo(new PacketSyncKnowledge(player), player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
    }

    public static void syncAura(ServerPlayer player, short base, float vis, float flux) {
        CHANNEL.sendTo(new PacketAuraToClient(base, vis, flux), player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
    }
}
