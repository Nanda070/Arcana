package arcana.common.lib;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * Central vanilla-backed sound cues (G27).
 */
public final class ArcanaSounds {
    private ArcanaSounds() {
    }

    public static void infusionComplete(Level level, BlockPos pos) {
        if (level.isClientSide) {
            return;
        }
        level.playSound(null, pos, SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.BLOCKS, 1.0f, 1.15f);
    }

    public static void cast(Level level, Player player) {
        if (level.isClientSide) {
            return;
        }
        level.playSound(null, player.blockPosition(), SoundEvents.EVOKER_CAST_SPELL, SoundSource.PLAYERS, 0.55f, 1.1f);
    }

    public static void nodeHum(Level level, BlockPos pos) {
        if (level.isClientSide) {
            return;
        }
        level.playSound(null, pos, SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.AMBIENT, 0.25f, 0.75f);
    }

    public static void warpWhisper(Level level, BlockPos pos) {
        if (level.isClientSide) {
            return;
        }
        level.playSound(null, pos, SoundEvents.GHAST_AMBIENT, SoundSource.AMBIENT, 0.35f, 0.45f);
    }
}
