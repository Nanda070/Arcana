package arcana.common.network;

import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

public class PacketAuraToClient {
    private final short base;
    private final float vis;
    private final float flux;

    public PacketAuraToClient(short base, float vis, float flux) {
        this.base = base;
        this.vis = vis;
        this.flux = flux;
    }

    public static void encode(PacketAuraToClient msg, FriendlyByteBuf buf) {
        buf.writeShort(msg.base);
        buf.writeFloat(msg.vis);
        buf.writeFloat(msg.flux);
    }

    public static PacketAuraToClient decode(FriendlyByteBuf buf) {
        return new PacketAuraToClient(buf.readShort(), buf.readFloat(), buf.readFloat());
    }

    public static void handle(PacketAuraToClient msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> Client.handle(msg)));
        ctx.get().setPacketHandled(true);
    }

    private static final class Client {
        private Client() {
        }

        private static void handle(PacketAuraToClient msg) {
            if (Minecraft.getInstance().player == null) {
                return;
            }
            arcana.client.ClientAuraCache.update(msg.base, msg.vis, msg.flux);
        }
    }
}
