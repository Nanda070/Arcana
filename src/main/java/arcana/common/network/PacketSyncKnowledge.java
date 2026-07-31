package arcana.common.network;

import arcana.api.capabilities.ArcanaCapabilities;
import arcana.api.capabilities.IPlayerKnowledge;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

public class PacketSyncKnowledge {
    private final CompoundTag data;

    public PacketSyncKnowledge(ServerPlayer player) {
        this.data = ArcanaCapabilities.getKnowledge(player).serializeNBT();
    }

    public PacketSyncKnowledge(CompoundTag data) {
        this.data = data;
    }

    public static void encode(PacketSyncKnowledge msg, FriendlyByteBuf buf) {
        buf.writeNbt(msg.data);
    }

    public static PacketSyncKnowledge decode(FriendlyByteBuf buf) {
        return new PacketSyncKnowledge(buf.readNbt());
    }

    public static void handle(PacketSyncKnowledge msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> Client.handle(msg)));
        ctx.get().setPacketHandled(true);
    }

    private static final class Client {
        private Client() {
        }

        private static void handle(PacketSyncKnowledge msg) {
            Player player = Minecraft.getInstance().player;
            if (player == null || msg.data == null) {
                return;
            }
            IPlayerKnowledge knowledge = ArcanaCapabilities.getKnowledge(player);
            knowledge.deserializeNBT(msg.data);
        }
    }
}
