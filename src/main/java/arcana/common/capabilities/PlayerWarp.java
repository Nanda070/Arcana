package arcana.common.capabilities;

import arcana.api.capabilities.ArcanaCapabilities;
import arcana.api.capabilities.IPlayerWarp;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

public final class PlayerWarp {

    private PlayerWarp() {
    }

    public static class Impl implements IPlayerWarp {
        private int[] warp = new int[EnumWarpType.values().length];
        private int counter;

        @Override
        public void clear() {
            warp = new int[EnumWarpType.values().length];
            counter = 0;
        }

        @Override
        public int get(@Nonnull EnumWarpType type) {
            return warp[type.ordinal()];
        }

        @Override
        public void set(EnumWarpType type, int amount) {
            warp[type.ordinal()] = Mth.clamp(amount, 0, 500);
        }

        @Override
        public int add(@Nonnull EnumWarpType type, int amount) {
            return warp[type.ordinal()] = Mth.clamp(warp[type.ordinal()] + amount, 0, 500);
        }

        @Override
        public int reduce(@Nonnull EnumWarpType type, int amount) {
            return warp[type.ordinal()] = Mth.clamp(warp[type.ordinal()] - amount, 0, 500);
        }

        @Override
        public void sync(@Nonnull ServerPlayer player) {
            // PacketSyncWarp arrives with network module.
        }

        @Override
        public CompoundTag serializeNBT() {
            CompoundTag properties = new CompoundTag();
            properties.putIntArray("warp", warp);
            properties.putInt("counter", getCounter());
            return properties;
        }

        @Override
        public void deserializeNBT(CompoundTag properties) {
            if (properties == null) {
                return;
            }
            clear();
            int[] ba = properties.getIntArray("warp");
            int l = Math.min(ba.length, EnumWarpType.values().length);
            for (int a = 0; a < l; a++) {
                warp[a] = ba[a];
            }
            setCounter(properties.getInt("counter"));
        }

        @Override
        public int getCounter() {
            return counter;
        }

        @Override
        public void setCounter(int amount) {
            counter = amount;
        }
    }

    public static class Provider implements ICapabilitySerializable<CompoundTag> {
        private final Impl backend = new Impl();
        private final LazyOptional<IPlayerWarp> optional = LazyOptional.of(() -> backend);

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
            return ArcanaCapabilities.WARP.orEmpty(cap, optional);
        }

        @Override
        public CompoundTag serializeNBT() {
            return backend.serializeNBT();
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) {
            backend.deserializeNBT(nbt);
        }

        public void invalidate() {
            optional.invalidate();
        }

        public Impl getBackend() {
            return backend;
        }
    }
}
