package arcana.common.network;

import arcana.common.lib.research.ResearchManager;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

public class PacketProgressResearch {
    private final String key;

    public PacketProgressResearch(String key) {
        this.key = key;
    }

    public static void encode(PacketProgressResearch msg, FriendlyByteBuf buf) {
        buf.writeUtf(msg.key);
    }

    public static PacketProgressResearch decode(FriendlyByteBuf buf) {
        return new PacketProgressResearch(buf.readUtf());
    }

    public static void handle(PacketProgressResearch msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null || msg.key == null || msg.key.isEmpty()) {
                return;
            }
            if (ResearchManager.progressResearch(player, msg.key, true)) {
                PacketHandler.syncKnowledge(player);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
