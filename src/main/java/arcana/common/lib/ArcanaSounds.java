package arcana.common.lib;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * Central vanilla-backed sound cues (G27 / L2).
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

    public static void researchStudy(Level level, BlockPos pos) {
        if (level.isClientSide) {
            return;
        }
        level.playSound(null, pos, SoundEvents.BOOK_PAGE_TURN, SoundSource.PLAYERS, 0.8f, 1.05f);
    }

    public static void scanSuccess(Level level, BlockPos pos) {
        if (level.isClientSide) {
            return;
        }
        level.playSound(null, pos, SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 0.6f, 1.35f);
    }

    public static void portalTease(Level level, BlockPos pos) {
        if (level.isClientSide) {
            return;
        }
        level.playSound(null, pos, SoundEvents.PORTAL_AMBIENT, SoundSource.BLOCKS, 0.45f, 0.7f);
    }

    public static void cultistAmbient(Level level, BlockPos pos) {
        if (level.isClientSide) {
            return;
        }
        level.playSound(null, pos, SoundEvents.ILLUSIONER_AMBIENT, SoundSource.HOSTILE, 0.55f, 0.85f);
    }
}
