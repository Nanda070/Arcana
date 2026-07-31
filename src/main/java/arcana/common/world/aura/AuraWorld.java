package arcana.common.world.aura;

import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.world.level.ChunkPos;

public class AuraWorld {
    private final String dimensionKey;
    final ConcurrentHashMap<Long, AuraChunk> auraChunks = new ConcurrentHashMap<>();

    public AuraWorld(String dimensionKey) {
        this.dimensionKey = dimensionKey;
    }

    public String getDimensionKey() {
        return dimensionKey;
    }

    public ConcurrentHashMap<Long, AuraChunk> getAuraChunks() {
        return auraChunks;
    }

    public AuraChunk getAuraChunkAt(int chunkX, int chunkZ) {
        return auraChunks.get(ChunkPos.asLong(chunkX, chunkZ));
    }
}
