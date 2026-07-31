package arcana.common.world.aura;

import arcana.Arcana;
import arcana.api.capabilities.ArcanaCapabilities;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.ChunkPos;

public final class AuraHandler {
    public static final int AURA_CEILING = 500;
    public static final String CHUNK_NBT = "Arcana";

    private static final ConcurrentHashMap<String, AuraWorld> AURAS = new ConcurrentHashMap<>();

    private AuraHandler() {
    }

    public static String dimKey(Level level) {
        return level.dimension().location().toString();
    }

    public static AuraWorld getAuraWorld(String dim) {
        return AURAS.get(dim);
    }

    public static AuraChunk getAuraChunk(String dim, int chunkX, int chunkZ) {
        AuraWorld world = AURAS.get(dim);
        if (world == null) {
            addAuraWorld(dim);
            world = AURAS.get(dim);
        }
        return world == null ? null : world.getAuraChunkAt(chunkX, chunkZ);
    }

    public static AuraChunk getAuraChunk(Level level, BlockPos pos) {
        return getAuraChunk(dimKey(level), pos.getX() >> 4, pos.getZ() >> 4);
    }

    public static void addAuraWorld(String dim) {
        AURAS.computeIfAbsent(dim, key -> {
            Arcana.LOGGER.info("Creating aura cache for {}", key);
            return new AuraWorld(key);
        });
    }

    public static void removeAuraWorld(String dim) {
        if (AURAS.remove(dim) != null) {
            Arcana.LOGGER.info("Removing aura cache for {}", dim);
        }
    }

    public static void addAuraChunk(String dim, LevelChunk chunk, short base, float vis, float flux) {
        AuraWorld aw = AURAS.computeIfAbsent(dim, AuraWorld::new);
        ChunkPos pos = chunk.getPos();
        aw.getAuraChunks().put(ChunkPos.asLong(pos.x, pos.z), new AuraChunk(chunk, base, vis, flux));
    }

    public static void removeAuraChunk(String dim, int chunkX, int chunkZ) {
        AuraWorld aw = AURAS.get(dim);
        if (aw != null) {
            aw.getAuraChunks().remove(ChunkPos.asLong(chunkX, chunkZ));
        }
    }

    public static float getVis(Level level, BlockPos pos) {
        AuraChunk ac = getAuraChunk(level, pos);
        return ac != null ? ac.getVis() : 0.0f;
    }

    public static float getFlux(Level level, BlockPos pos) {
        AuraChunk ac = getAuraChunk(level, pos);
        return ac != null ? ac.getFlux() : 0.0f;
    }

    public static int getAuraBase(Level level, BlockPos pos) {
        AuraChunk ac = getAuraChunk(level, pos);
        return ac != null ? ac.getBase() : 0;
    }

    public static float getTotalAura(Level level, BlockPos pos) {
        AuraChunk ac = getAuraChunk(level, pos);
        return ac != null ? ac.getVis() + ac.getFlux() : 0.0f;
    }

    public static float getFluxSaturation(Level level, BlockPos pos) {
        AuraChunk ac = getAuraChunk(level, pos);
        return ac != null && ac.getBase() > 0 ? ac.getFlux() / ac.getBase() : 0.0f;
    }

    public static boolean shouldPreserveAura(Level level, Player player, BlockPos pos) {
        int base = getAuraBase(level, pos);
        if (base <= 0) {
            return false;
        }
        boolean researched = player == null
                || ArcanaCapabilities.getKnowledge(player).isResearchComplete("AURAPRESERVE");
        return researched && getVis(level, pos) / base < 0.1f;
    }

    public static void addVis(Level level, BlockPos pos, float amount) {
        if (amount < 0.0f) {
            return;
        }
        modifyVisInChunk(getAuraChunk(level, pos), amount, true);
    }

    public static void addFlux(Level level, BlockPos pos, float amount) {
        if (amount < 0.0f) {
            return;
        }
        modifyFluxInChunk(getAuraChunk(level, pos), amount, true);
    }

    public static float drainVis(Level level, BlockPos pos, float amount, boolean simulate) {
        AuraChunk ac = getAuraChunk(level, pos);
        if (ac == null) {
            return 0.0f;
        }
        if (amount > ac.getVis()) {
            amount = ac.getVis();
        }
        boolean didit = modifyVisInChunk(ac, -amount, !simulate);
        return didit ? amount : 0.0f;
    }

    public static float drainFlux(Level level, BlockPos pos, float amount, boolean simulate) {
        AuraChunk ac = getAuraChunk(level, pos);
        if (ac == null) {
            return 0.0f;
        }
        if (amount > ac.getFlux()) {
            amount = ac.getFlux();
        }
        boolean didit = modifyFluxInChunk(ac, -amount, !simulate);
        return didit ? amount : 0.0f;
    }

    public static boolean modifyVisInChunk(AuraChunk ac, float amount, boolean doit) {
        if (ac == null) {
            return false;
        }
        if (doit) {
            ac.setVis(Math.max(0.0f, ac.getVis() + amount));
        }
        return true;
    }

    public static boolean modifyFluxInChunk(AuraChunk ac, float amount, boolean doit) {
        if (ac == null) {
            return false;
        }
        if (doit) {
            ac.setFlux(Math.max(0.0f, ac.getFlux() + amount));
        }
        return true;
    }

    /**
     * Simplified generation (full biome blacklist/modifiers arrive with worldgen module).
     * Temperate biomes get more baseline vis.
     */
    public static void generateAura(LevelChunk chunk, RandomSource rand) {
        if (!(chunk.getLevel() instanceof ServerLevel level)) {
            return;
        }
        BlockPos sample = new BlockPos(chunk.getPos().getMinBlockX() + 8, level.getSeaLevel(), chunk.getPos().getMinBlockZ() + 8);
        Biome biome = level.getBiome(sample).value();
        float temp = biome.getBaseTemperature();
        // Peak around temperate (0.5–0.8), lower in extremes.
        float life = 0.55f + 0.45f * (1.0f - Mth.clamp(Math.abs(temp - 0.65f) / 1.2f, 0.0f, 1.0f));
        float noise = (float) (1.0 + rand.nextGaussian() * 0.1);
        short base = (short) Mth.clamp((int) (life * AURA_CEILING * noise), 0, AURA_CEILING);
        addAuraChunk(dimKey(level), chunk, base, base, 0.0f);
    }

    /**
     * Light regen toward base each second — full neighbour equalization comes later.
     * Chunk-map iteration once/sec is fine at current scale; skip micro-opts unless profiling shows heat.
     */
    public static void tickRegen(ServerLevel level) {
        AuraWorld world = AURAS.get(dimKey(level));
        if (world == null) {
            return;
        }
        int moon = level.dimensionType().moonPhase(level.getDayTime());
        float phaseBoost = switch (moon) {
            case 0 -> 0.25f; // full
            case 1, 7 -> 0.15f;
            case 2, 6 -> 0.10f;
            case 3, 5 -> 0.05f;
            default -> 0.0f; // new
        };
        for (AuraChunk ac : world.auraChunks.values()) {
            float base = ac.getBase();
            if (base <= 0) {
                continue;
            }
            float vis = ac.getVis();
            float flux = ac.getFlux();
            float room = Math.max(0.0f, base * (1.0f + phaseBoost * 0.15f) - (vis + flux));
            if (room > 0.0f && vis < base) {
                float regen = Math.min(room, 0.05f + phaseBoost * 0.1f);
                ac.setVis(vis + regen);
            }
            // Mild flux bleed when overloaded
            if (flux > base * 0.75f) {
                ac.setFlux(Math.max(0.0f, flux - 0.02f));
            }
        }
    }
}
