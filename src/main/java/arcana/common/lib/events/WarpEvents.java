package arcana.common.lib.events;

import arcana.api.aura.AuraHelper;
import arcana.api.capabilities.ArcanaCapabilities;
import arcana.api.capabilities.IPlayerWarp;
import arcana.common.lib.ArcanaSounds;
import arcana.registry.ModEntities;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * Periodic warp side-effects (trimmed TC6 WarpEvents ladder).
 */
public final class WarpEvents {
    private WarpEvents() {
    }

    public static void checkWarpEvent(Player player) {
        if (player.level().isClientSide) {
            return;
        }
        IPlayerWarp wc = ArcanaCapabilities.getWarp(player);

        int tw = wc.get(IPlayerWarp.EnumWarpType.TEMPORARY);
        int nw = wc.get(IPlayerWarp.EnumWarpType.NORMAL);
        int pw = wc.get(IPlayerWarp.EnumWarpType.PERMANENT);
        int actualWarp = pw + nw;
        int gearWarp = WarpHelper.getGearWarp(player);
        int warp = tw + nw + pw + gearWarp;
        int warpCounter = wc.getCounter();
        RandomSource random = player.getRandom();

        if (warpCounter <= 0 || warp <= 0) {
            return;
        }
        if (random.nextInt(100) > Math.sqrt(warpCounter)) {
            return;
        }

        warp = Math.min(100, (warp + warp + warpCounter) / 3);
        warpCounter -= (int) Math.max(5.0, Math.sqrt(warpCounter) * 2.0 - gearWarp * 2.0);
        wc.setCounter(Math.max(0, warpCounter));

        int eff = random.nextInt(Math.max(1, warp)) + gearWarp;
        if (eff <= 0) {
            return;
        }
        applyEffect(player, eff, warp, tw, nw, pw, actualWarp);
    }

    private static void applyEffect(Player player, int eff, int warp, int temporaryWarp, int normalWarp,
                                    int permanentWarp, int actualWarp) {
        Level level = player.level();
        RandomSource random = player.getRandom();

        if (eff <= 4) {
            level.playSound(null, player.blockPosition(), SoundEvents.CREEPER_PRIMED, SoundSource.AMBIENT, 1.0f, 0.5f);
        } else if (eff <= 8) {
            double x = player.getX() + (random.nextFloat() - random.nextFloat()) * 10.0;
            double y = player.getY() + (random.nextFloat() - random.nextFloat()) * 10.0;
            double z = player.getZ() + (random.nextFloat() - random.nextFloat()) * 10.0;
            level.playSound(null, x, y, z, SoundEvents.GENERIC_EXPLODE, SoundSource.AMBIENT, 4.0f,
                    (1.0f + (random.nextFloat() - random.nextFloat()) * 0.2f) * 0.7f);
        } else if (eff <= 12) {
            whisper(player, randomWhisper(random, "warp.text.11", "warp.text.18", "warp.text.19"));
        } else if (eff <= 20) {
            player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 200 + warp * 10, Math.min(2, warp / 20), true, true));
            whisper(player, "warp.text.1");
        } else if (eff <= 28) {
            AuraHelper.polluteAura(level, player.blockPosition(), Math.min(5.0f, 1.0f + warp / 20.0f), true);
            whisper(player, "warp.text.15");
        } else if (eff <= 36) {
            player.addEffect(new MobEffectInstance(MobEffects.HUNGER, 400 + warp * 20, Math.min(2, warp / 15), true, true));
            whisper(player, "warp.text.2");
        } else if (eff <= 44) {
            whisper(player, randomWhisper(random, "warp.text.12", "warp.text.20", "warp.text.21"));
        } else if (eff <= 52) {
            player.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 600 + warp * 10, Math.min(2, warp / 15), true, true));
            whisper(player, "warp.text.9");
        } else if (eff <= 60) {
            player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, Math.min(40 * warp, 6000), 0, true, true));
            whisper(player, "warp.text.10");
        } else if (eff <= 68) {
            int spiders = 3 + random.nextInt(4) + (warp > 70 ? 2 : 0);
            MindSpiderSpawnHelper.spawnNear(player, spiders);
            WispSpawnHelper.spawnNear(player, Math.min(3, 1 + warp / 40));
            whisper(player, "warp.text.6");
        } else if (eff <= 74) {
            int amp = warp >= 60 ? 1 : 0;
            player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, Math.min(260 + warp * 6, 700), amp, true, true));
            if (amp > 0) {
                player.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 160 + warp * 2, 0, true, true));
            }
            whisper(player, "warp.text.13");
        } else if (eff <= 80 && normalWarp > 0) {
            WarpHelper.addWarpToPlayer(player, -1, IPlayerWarp.EnumWarpType.NORMAL);
            whisper(player, "warp.text.14");
        } else if (eff <= 84 && temporaryWarp + normalWarp >= 20) {
            spawnCrimsonCultists(player, 1 + random.nextInt(2), false);
            whisper(player, "warp.text.22");
        } else if (eff <= 86) {
            MindSpiderSpawnHelper.spawnNear(player, 4 + random.nextInt(4));
            whisper(player, "warp.text.16");
        } else if (eff <= 90 || (actualWarp >= 45 && random.nextInt(3) == 0)) {
            int count = 1 + (actualWarp >= 55 && random.nextBoolean() ? 1 : 0);
            spawnEldritchGuardians(player, count);
            whisper(player, "warp.text.eldritch");
        } else if (actualWarp >= 55 && random.nextInt(10) == 0) {
            spawnCrimsonCultists(player, 1, true);
            whisper(player, "warp.text.22");
        } else if (permanentWarp > 50 && random.nextInt(8) == 0) {
            spawnEldritchWarden(player);
            whisper(player, "warp.text.17");
        } else {
            spawnBrainyZombies(player, Math.min(3, 1 + warp / 40));
            whisper(player, "warp.text.7");
        }
    }

    private static String randomWhisper(RandomSource random, String... keys) {
        return keys[random.nextInt(keys.length)];
    }

    private static void whisper(Player player, String key) {
        ArcanaSounds.warpWhisper(player.level(), player.blockPosition());
        player.displayClientMessage(Component.translatable(key).withStyle(ChatFormatting.DARK_PURPLE, ChatFormatting.ITALIC), true);
    }

    private static void spawnEldritchGuardians(Player player, int count) {
        spawnNear(player, count, (level) -> ModEntities.ELDRITCH_GUARDIAN.get().create(level));
    }

    private static void spawnEldritchWarden(Player player) {
        spawnNear(player, 1, (level) -> ModEntities.ELDRITCH_WARDEN.get().create(level));
    }

    private static void spawnBrainyZombies(Player player, int count) {
        spawnNear(player, count, (level) -> ModEntities.BRAINY_ZOMBIE.get().create(level));
    }

    private static void spawnCrimsonCultists(Player player, int count, boolean captain) {
        spawnNear(player, count, (level) -> {
            arcana.common.entities.CrimsonCultist cultist = ModEntities.CRIMSON_CULTIST.get().create(level);
            if (cultist != null && captain) {
                cultist.setCaptain(true);
            }
            return cultist;
        });
    }

    private static void spawnNear(Player player, int count, java.util.function.Function<ServerLevel, Mob> factory) {
        if (!(player.level() instanceof ServerLevel level)) {
            return;
        }
        BlockPos base = player.blockPosition();
        RandomSource random = player.getRandom();
        for (int n = 0; n < count; n++) {
            for (int attempt = 0; attempt < 40; attempt++) {
                int dx = Mth.randomBetweenInclusive(random, 6, 16) * (random.nextBoolean() ? 1 : -1);
                int dz = Mth.randomBetweenInclusive(random, 6, 16) * (random.nextBoolean() ? 1 : -1);
                int dy = Mth.randomBetweenInclusive(random, -2, 2);
                BlockPos pos = base.offset(dx, dy, dz);
                if (!level.getBlockState(pos.below()).isSolidRender(level, pos.below())) {
                    continue;
                }
                if (!level.getBlockState(pos).isAir() || !level.getBlockState(pos.above()).isAir()) {
                    continue;
                }
                Mob mob = factory.apply(level);
                if (mob == null) {
                    return;
                }
                mob.moveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, random.nextFloat() * 360.0f, 0.0f);
                mob.setTarget(player);
                level.addFreshEntity(mob);
                break;
            }
        }
    }
}
