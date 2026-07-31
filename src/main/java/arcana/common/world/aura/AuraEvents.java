package arcana.common.world.aura;

import arcana.Arcana;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.event.level.ChunkDataEvent;
import net.minecraftforge.event.level.ChunkEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Arcana.MODID)
public final class AuraEvents {
    private AuraEvents() {
    }

    @SubscribeEvent
    public static void onLevelLoad(LevelEvent.Load event) {
        if (event.getLevel() instanceof ServerLevel level) {
            AuraHandler.addAuraWorld(AuraHandler.dimKey(level));
        }
    }

    @SubscribeEvent
    public static void onLevelUnload(LevelEvent.Unload event) {
        if (event.getLevel() instanceof ServerLevel level) {
            AuraHandler.removeAuraWorld(AuraHandler.dimKey(level));
        }
    }

    @SubscribeEvent
    public static void onChunkLoad(ChunkDataEvent.Load event) {
        if (!(event.getLevel() instanceof ServerLevel level)) {
            return;
        }
        if (!(event.getChunk() instanceof LevelChunk chunk)) {
            return;
        }
        CompoundTag root = event.getData();
        CompoundTag nbt = root.getCompound(AuraHandler.CHUNK_NBT);
        String dim = AuraHandler.dimKey(level);
        if (nbt.contains("base")) {
            short base = nbt.getShort("base");
            float flux = nbt.getFloat("flux");
            float vis = nbt.getFloat("vis");
            AuraHandler.addAuraChunk(dim, chunk, base, vis, flux);
        } else {
            AuraHandler.generateAura(chunk, level.random);
        }
    }

    @SubscribeEvent
    public static void onChunkSave(ChunkDataEvent.Save event) {
        if (!(event.getLevel() instanceof ServerLevel level)) {
            return;
        }
        if (!(event.getChunk() instanceof LevelChunk chunk)) {
            return;
        }
        ChunkPos loc = chunk.getPos();
        AuraChunk ac = AuraHandler.getAuraChunk(AuraHandler.dimKey(level), loc.x, loc.z);
        if (ac == null) {
            return;
        }
        CompoundTag nbt = new CompoundTag();
        nbt.putShort("base", ac.getBase());
        nbt.putFloat("flux", ac.getFlux());
        nbt.putFloat("vis", ac.getVis());
        event.getData().put(AuraHandler.CHUNK_NBT, nbt);
    }

    @SubscribeEvent
    public static void onChunkUnload(ChunkEvent.Unload event) {
        if (!(event.getLevel() instanceof ServerLevel level)) {
            return;
        }
        if (!(event.getChunk() instanceof LevelChunk chunk)) {
            return;
        }
        ChunkPos loc = chunk.getPos();
        AuraHandler.removeAuraChunk(AuraHandler.dimKey(level), loc.x, loc.z);
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.LevelTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.level.isClientSide()) {
            return;
        }
        if (!(event.level instanceof ServerLevel level)) {
            return;
        }
        // Once per second
        if (level.getGameTime() % 20L == 0L) {
            AuraHandler.tickRegen(level);
        }
    }
}
