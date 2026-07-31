package arcana.api.casters;

import arcana.common.entities.projectile.FocusProjectile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/**
 * Focus runtime: Touch / Projectile media Ã— Fire / Frost / Shock / Earth / Heal effects.
 */
public final class FocusEngine {
    private static final double TOUCH_RANGE = 5.0;

    private FocusEngine() {
    }

    public static void castFocusPackage(LivingEntity caster, FocusPackage focusPackage) {
        if (caster.level().isClientSide || focusPackage == null) {
            return;
        }
        FocusPackage pack = focusPackage.copy();
        String effect = pack.getEffect();
        if (FocusPackage.EFFECT_FIRE.equals(effect)) {
            caster.level().playSound(null, caster.blockPosition().above(), SoundEvents.FIRECHARGE_USE,
                    SoundSource.PLAYERS, 1.0f, 1.0f + (float) (caster.getRandom().nextGaussian() * 0.05));
        } else if (FocusPackage.EFFECT_FROST.equals(effect)) {
            caster.level().playSound(null, caster.blockPosition().above(), SoundEvents.GLASS_BREAK,
                    SoundSource.PLAYERS, 0.4f, 1.6f);
        } else if (FocusPackage.EFFECT_SHOCK.equals(effect)) {
            caster.level().playSound(null, caster.blockPosition().above(), SoundEvents.LIGHTNING_BOLT_IMPACT,
                    SoundSource.PLAYERS, 0.35f, 1.4f);
            if (caster.level() instanceof ServerLevel server) {
                server.sendParticles(ParticleTypes.EFFECT,
                        caster.getX(), caster.getY() + 1.0, caster.getZ(),
                        10, 0.35, 0.4, 0.35, 0.02);
            }
        } else if (FocusPackage.EFFECT_EARTH.equals(effect)) {
            caster.level().playSound(null, caster.blockPosition().above(), SoundEvents.GRAVEL_BREAK,
                    SoundSource.PLAYERS, 0.6f, 0.8f);
            if (caster.level() instanceof ServerLevel server) {
                server.sendParticles(ParticleTypes.EFFECT,
                        caster.getX(), caster.getY() + 1.0, caster.getZ(),
                        10, 0.35, 0.25, 0.35, 0.02);
            }
        } else if (FocusPackage.EFFECT_HEAL.equals(effect)) {
            caster.level().playSound(null, caster.blockPosition().above(), SoundEvents.AMETHYST_BLOCK_CHIME,
                    SoundSource.PLAYERS, 0.7f, 1.2f);
        }

        if (FocusPackage.MEDIUM_PROJECTILE.equals(pack.getMedium())) {
            executeProjectile(caster, pack);
        } else {
            executeTouch(caster, pack);
        }
    }

    private static void executeProjectile(LivingEntity caster, FocusPackage pack) {
        Level level = caster.level();
        FocusProjectile projectile = new FocusProjectile(level, caster, pack.remainingEffects());
        Vec3 look = caster.getLookAngle();
        projectile.shoot(look.x, look.y, look.z, 1.25f, 0.0f);
        level.addFreshEntity(projectile);
    }

    private static void executeTouch(LivingEntity caster, FocusPackage focusPackage) {
        Level level = caster.level();
        Vec3 eye = caster.getEyePosition();
        Vec3 look = caster.getLookAngle();
        Vec3 end = eye.add(look.scale(TOUCH_RANGE));
        FocusPackage effects = focusPackage.remainingEffects();

        // Heal applies to caster on touch medium regardless of ray hit.
        if (effects.hasNode(FocusPackage.EFFECT_HEAL)) {
            applyHeal(caster);
        }

        EntityHitResult entityHit = findEntity(caster, eye, end);
        if (entityHit != null) {
            spawnCastTrail(level, eye, entityHit.getLocation(), effects.getEffect());
            applyEffects(caster, effects, entityHit.getEntity(), entityHit.getLocation(), null);
            return;
        }

        BlockHitResult blockHit = level.clip(new ClipContext(eye, end, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, caster));
        if (blockHit.getType() == HitResult.Type.BLOCK) {
            spawnCastTrail(level, eye, blockHit.getLocation(), effects.getEffect());
            applyEffects(caster, effects, null, blockHit.getLocation(), blockHit);
        } else {
            spawnCastTrail(level, eye, end, effects.getEffect());
            // Still allow heal-only cast into empty air
            if (!effects.hasNode(FocusPackage.EFFECT_HEAL)) {
                applyEffects(caster, effects, null, end, null);
            }
        }
    }

    private static void spawnCastTrail(Level level, Vec3 from, Vec3 to, String effect) {
        if (!(level instanceof ServerLevel server)) {
            return;
        }
        Vec3 delta = to.subtract(from);
        int steps = 8;
        for (int i = 0; i <= steps; i++) {
            double t = i / (double) steps;
            Vec3 p = from.add(delta.scale(t));
            if (FocusPackage.EFFECT_FROST.equals(effect)) {
                server.sendParticles(ParticleTypes.SNOWFLAKE, p.x, p.y, p.z, 1, 0.02, 0.02, 0.02, 0.0);
            } else if (FocusPackage.EFFECT_SHOCK.equals(effect)) {
                server.sendParticles(ParticleTypes.ELECTRIC_SPARK, p.x, p.y, p.z, 1, 0.02, 0.02, 0.02, 0.0);
                server.sendParticles(ParticleTypes.EFFECT, p.x, p.y, p.z, 1, 0.02, 0.02, 0.02, 0.0);
            } else if (FocusPackage.EFFECT_EARTH.equals(effect)) {
                server.sendParticles(ParticleTypes.CLOUD, p.x, p.y, p.z, 1, 0.02, 0.02, 0.02, 0.0);
                server.sendParticles(ParticleTypes.EFFECT, p.x, p.y, p.z, 1, 0.02, 0.02, 0.02, 0.0);
            } else if (FocusPackage.EFFECT_HEAL.equals(effect)) {
                server.sendParticles(ParticleTypes.HEART, p.x, p.y, p.z, 1, 0.02, 0.02, 0.02, 0.0);
            } else {
                server.sendParticles(ParticleTypes.FLAME, p.x, p.y, p.z, 1, 0.02, 0.02, 0.02, 0.0);
            }
        }
    }

    private static EntityHitResult findEntity(LivingEntity caster, Vec3 start, Vec3 end) {
        AABB box = caster.getBoundingBox().expandTowards(caster.getLookAngle().scale(TOUCH_RANGE)).inflate(1.0);
        EntityHitResult best = null;
        double bestDist = TOUCH_RANGE * TOUCH_RANGE;
        for (Entity entity : caster.level().getEntities(caster, box, e -> e.isPickable() && e != caster)) {
            AABB aabb = entity.getBoundingBox().inflate(0.3);
            var optional = aabb.clip(start, end);
            if (optional.isPresent()) {
                double dist = start.distanceToSqr(optional.get());
                if (dist < bestDist) {
                    bestDist = dist;
                    best = new EntityHitResult(entity, optional.get());
                }
            }
        }
        return best;
    }

    public static void applyEffects(LivingEntity caster, FocusPackage effects, Entity targetEntity,
                                    Vec3 hit, BlockHitResult blockHit) {
        if (effects == null) {
            return;
        }
        for (String node : effects.getNodes()) {
            if (FocusPackage.EFFECT_FIRE.equals(node)) {
                applyFire(caster, targetEntity, blockHit);
            } else if (FocusPackage.EFFECT_FROST.equals(node)) {
                applyFrost(caster, targetEntity, hit, blockHit);
            } else if (FocusPackage.EFFECT_SHOCK.equals(node)) {
                applyShock(caster, targetEntity);
            } else if (FocusPackage.EFFECT_EARTH.equals(node)) {
                applyEarth(caster, targetEntity, hit, blockHit);
            }
            // EFFECT_HEAL: touch-only, applied in executeTouch â€” skipped on projectile
        }
    }

    private static void applyFire(LivingEntity caster, Entity targetEntity, BlockHitResult blockHit) {
        Level level = caster != null ? caster.level() : (targetEntity != null ? targetEntity.level() : null);
        if (level == null) {
            return;
        }
        float damage = 4.0f;
        if (targetEntity != null) {
            if (targetEntity.fireImmune()) {
                return;
            }
            if (caster != null) {
                targetEntity.hurt(level.damageSources().indirectMagic(caster, caster), damage);
            } else {
                targetEntity.hurt(level.damageSources().magic(), damage);
            }
            targetEntity.setSecondsOnFire(3);
            return;
        }
        if (blockHit != null) {
            BlockPos place = blockHit.getBlockPos().relative(blockHit.getDirection());
            if (level.isEmptyBlock(place)) {
                level.playSound(null, place, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS,
                        1.0f, level.getRandom().nextFloat() * 0.4f + 0.8f);
                level.setBlock(place, Blocks.FIRE.defaultBlockState(), 11);
            }
        }
    }

    private static void applyFrost(LivingEntity caster, Entity targetEntity, Vec3 hit, BlockHitResult blockHit) {
        Level level = caster != null ? caster.level() : (targetEntity != null ? targetEntity.level() : null);
        if (level == null) {
            return;
        }
        if (targetEntity instanceof LivingEntity living) {
            float damage = 3.0f;
            if (caster != null) {
                living.hurt(level.damageSources().indirectMagic(caster, caster), damage);
            } else {
                living.hurt(level.damageSources().magic(), damage);
            }
            living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 1));
            if (level instanceof ServerLevel server) {
                server.sendParticles(ParticleTypes.SNOWFLAKE, living.getX(), living.getY() + 1, living.getZ(),
                        12, 0.3, 0.4, 0.3, 0.02);
            }
            return;
        }
        BlockPos pos = blockHit != null ? blockHit.getBlockPos() : BlockPos.containing(hit);
        BlockState state = level.getBlockState(pos);
        if (state.getBlock() == Blocks.WATER || (state.getBlock() instanceof LiquidBlock
                && state.getFluidState().isSource() && state.getFluidState().is(net.minecraft.tags.FluidTags.WATER))) {
            level.setBlock(pos, Blocks.FROSTED_ICE.defaultBlockState(), 3);
        } else {
            BlockPos above = pos.relative(blockHit != null ? blockHit.getDirection() : net.minecraft.core.Direction.UP);
            if (level.isEmptyBlock(above) && Blocks.SNOW.defaultBlockState().canSurvive(level, above)) {
                level.setBlock(above, Blocks.SNOW.defaultBlockState(), 3);
            }
        }
        if (level instanceof ServerLevel server) {
            server.sendParticles(ParticleTypes.SNOWFLAKE, pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5,
                    10, 0.25, 0.2, 0.25, 0.01);
        }
    }

    private static void applyShock(LivingEntity caster, Entity targetEntity) {
        Level level = caster != null ? caster.level() : (targetEntity != null ? targetEntity.level() : null);
        if (level == null || !(targetEntity instanceof LivingEntity living)) {
            return;
        }
        float damage = 5.0f;
        if (caster != null) {
            living.hurt(level.damageSources().indirectMagic(caster, caster), damage);
        } else {
            living.hurt(level.damageSources().magic(), damage);
        }
        living.addEffect(new MobEffectInstance(MobEffects.GLOWING, 40, 0));
        living.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 40, 0));
        if (level instanceof ServerLevel server) {
            server.sendParticles(ParticleTypes.ELECTRIC_SPARK, living.getX(), living.getY() + 1, living.getZ(),
                    16, 0.35, 0.5, 0.35, 0.05);
            server.sendParticles(ParticleTypes.EFFECT, living.getX(), living.getY() + 1, living.getZ(),
                    8, 0.3, 0.4, 0.3, 0.02);
        }
    }

    private static void applyEarth(LivingEntity caster, Entity targetEntity, Vec3 hit, BlockHitResult blockHit) {
        Level level = caster != null ? caster.level() : (targetEntity != null ? targetEntity.level() : null);
        if (level == null) {
            return;
        }
        if (targetEntity instanceof LivingEntity living) {
            float damage = 3.5f;
            if (caster != null) {
                living.hurt(level.damageSources().indirectMagic(caster, caster), damage);
            } else {
                living.hurt(level.damageSources().magic(), damage);
            }
            living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 80, 1));
            if (level instanceof ServerLevel server) {
                server.sendParticles(ParticleTypes.CLOUD, living.getX(), living.getY() + 0.2, living.getZ(),
                        10, 0.3, 0.1, 0.3, 0.02);
                server.sendParticles(ParticleTypes.EFFECT, living.getX(), living.getY() + 0.5, living.getZ(),
                        6, 0.25, 0.2, 0.25, 0.01);
            }
            return;
        }
        // Place temporary cobble under / at hit
        BlockPos pos;
        if (targetEntity != null) {
            pos = targetEntity.blockPosition().below();
        } else if (blockHit != null) {
            pos = blockHit.getBlockPos().relative(blockHit.getDirection());
        } else if (hit != null) {
            pos = BlockPos.containing(hit);
        } else {
            return;
        }
        if (level.isEmptyBlock(pos)) {
            level.setBlock(pos, Blocks.COBBLESTONE.defaultBlockState(), 3);
        }
    }

    private static void applyHeal(LivingEntity caster) {
        if (caster == null) {
            return;
        }
        caster.heal(4.0f); // 2 hearts
        Level level = caster.level();
        if (level instanceof ServerLevel server) {
            server.sendParticles(ParticleTypes.HEART, caster.getX(), caster.getY() + 1.2, caster.getZ(),
                    6, 0.3, 0.3, 0.3, 0.02);
        }
    }

    public static void castTouchFire(Player player) {
        castFocusPackage(player, FocusPackage.touchFire());
    }

    public static void castPreset(Player player, String preset) {
        castFocusPackage(player, FocusPackage.fromPreset(preset));
    }
}
