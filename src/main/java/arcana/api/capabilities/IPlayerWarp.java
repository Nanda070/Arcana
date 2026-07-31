package arcana.api.capabilities;

import javax.annotation.Nonnull;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.util.INBTSerializable;

public interface IPlayerWarp extends INBTSerializable<CompoundTag> {

    void clear();

    int get(@Nonnull EnumWarpType type);

    void set(@Nonnull EnumWarpType type, int amount);

    int add(@Nonnull EnumWarpType type, int amount);

    int reduce(@Nonnull EnumWarpType type, int amount);

    enum EnumWarpType {
        PERMANENT, NORMAL, TEMPORARY
    }

    /** Network sync stub until packet layer. */
    void sync(ServerPlayer player);

    int getCounter();

    void setCounter(int amount);
}
