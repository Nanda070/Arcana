package arcana.api.aura;

import arcana.common.blockentities.AuraNodeBlockEntity;
import arcana.common.world.aura.AuraHandler;
import arcana.registry.ModBlocks;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector3f;

/**
 * Public aura API (TC6 AuraHelper).
 */
public final class AuraHelper {
    private AuraHelper() {
    }

    public static float drainVis(Level level, BlockPos pos, float amount, boolean simulate) {
        if (amount <= 0.0f) {
            return 0.0f;
        }
        float remaining = amount;
        float drained = 0.0f;
        // I1: prefer draining nearby node buffers first
        List<AuraNodeBlockEntity> nodes = AuraNodeBlockEntity.findInRadius(level, pos, AuraNodeBlockEntity.DRAIN_RADIUS);
        for (AuraNodeBlockEntity node : nodes) {
            if (remaining <= 0.0f) {
                break;
            }
            float request = remaining;
            // Hungry nodes yield buffer more aggressively
            if (node.getNodeType() == AuraNodeBlockEntity.NodeType.HUNGRY) {
                request = Math.min(remaining * 1.5f, remaining + 2.0f);
            }
            float taken = node.drainBuffer(Math.min(request, remaining), simulate);
            drained += taken;
            remaining -= taken;
        }
        if (remaining > 0.0f) {
            drained += AuraHandler.drainVis(level, pos, remaining, simulate);
        }
        return drained;
    }

    public static float drainFlux(Level level, BlockPos pos, float amount, boolean simulate) {
        return AuraHandler.drainFlux(level, pos, amount, simulate);
    }

    public static void addVis(Level level, BlockPos pos, float amount) {
        AuraHandler.addVis(level, pos, amount);
    }

    public static float getVis(Level level, BlockPos pos) {
        return AuraHandler.getVis(level, pos);
    }

    public static void polluteAura(Level level, BlockPos pos, float amount, boolean showEffect) {
        AuraHandler.addFlux(level, pos, amount);
        if (showEffect && level instanceof ServerLevel server) {
            server.playSound(null, pos, SoundEvents.BREWING_STAND_BREW, SoundSource.BLOCKS, 0.4f, 0.6f);
            DustParticleOptions dust = new DustParticleOptions(new Vector3f(0.5f, 0.0f, 0.6f), 1.0f);
            for (int i = 0; i < 12; i++) {
                double ox = (server.random.nextDouble() - 0.5) * 0.8;
                double oy = server.random.nextDouble() * 0.6;
                double oz = (server.random.nextDouble() - 0.5) * 0.8;
                server.sendParticles(dust, pos.getX() + 0.5 + ox, pos.getY() + 0.5 + oy, pos.getZ() + 0.5 + oz,
                        1, 0.0, 0.02, 0.0, 0.0);
            }
            server.sendParticles(ParticleTypes.SMOKE, pos.getX() + 0.5, pos.getY() + 0.8, pos.getZ() + 0.5,
                    6, 0.2, 0.2, 0.2, 0.01);
        }
        // G22: small chance to seep flux goo onto nearby solid surfaces
        if (level instanceof ServerLevel server && amount > 0.05f
                && server.random.nextFloat() < Math.min(0.25f, 0.06f + amount * 0.04f)) {
            tryPlaceFluxGoo(server, pos);
        }
    }

    private static void tryPlaceFluxGoo(ServerLevel level, BlockPos origin) {
        RandomSource random = level.random;
        BlockState goo = ModBlocks.FLUX_GOO.get().defaultBlockState();
        for (int attempt = 0; attempt < 8; attempt++) {
            int dx = Mth.randomBetweenInclusive(random, -2, 2);
            int dz = Mth.randomBetweenInclusive(random, -2, 2);
            int dy = Mth.randomBetweenInclusive(random, -1, 1);
            BlockPos air = origin.offset(dx, dy, dz);
            BlockPos below = air.below();
            if (!level.getBlockState(air).isAir()) {
                continue;
            }
            BlockState floor = level.getBlockState(below);
            if (!floor.isFaceSturdy(level, below, Direction.UP) || floor.is(ModBlocks.FLUX_GOO.get())) {
                continue;
            }
            level.setBlock(air, goo, 3);
            return;
        }
    }

    public static float getFlux(Level level, BlockPos pos) {
        return AuraHandler.getFlux(level, pos);
    }

    public static int getAuraBase(Level level, BlockPos pos) {
        return AuraHandler.getAuraBase(level, pos);
    }

    public static boolean shouldPreserveAura(Level level, Player player, BlockPos pos) {
        return AuraHandler.shouldPreserveAura(level, player, pos);
    }
}
