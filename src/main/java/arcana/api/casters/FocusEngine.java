package arcana.api.casters;

import arcana.api.aura.AuraHelper;
import arcana.common.entities.projectile.FocusProjectile;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
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
 * Focus runtime: Touch / Projectile / Bolt / Cloud × TC6 effect set.
 * SHOCK remains as lightning alias; AIR is knockback blast.
 */
public final class FocusEngine {
    private static final double TOUCH_RANGE = 5.0;
    private static final double BOLT_RANGE = 16.0;
    private static final double CLOUD_RADIUS = 2.5;
    private static final int CLOUD_DURATION_TICKS = 60;

    private FocusEngine() {
    }

    public static void castFocusPackage(LivingEntity caster, FocusPackage focusPackage) {
        if (caster.level().isClientSide || focusPackage == null) {
            return;
        }
        FocusPackage pack = focusPackage.copy();
        playCastSound(caster, pack.getEffect());

        if (pack.hasNode(FocusPackage.MOD_SCATTER)) {
            castScatter(caster, pack);
            return;
        }

        String medium = pack.getMedium();
        if (FocusPackage.MEDIUM_PROJECTILE.equals(medium)) {
            executeProjectile(caster, pack);
        } else if (FocusPackage.MEDIUM_BOLT.equals(medium)) {
            executeBolt(caster, pack);
        } else if (FocusPackage.MEDIUM_CLOUD.equals(medium)) {
            executeCloud(caster, pack);
        } else {
            // MEDIUM_MINE stub falls through to touch until mine beam is implemented
            executeTouch(caster, pack);
        }
    }

    /** Scatter: cast the package effect path 3 times with slight look-angle offsets. */
    private static void castScatter(LivingEntity caster, FocusPackage pack) {
        FocusPackage base = pack.copy();
        // Strip scatter so recursive casts don't re-scatter.
        List<String> nodes = new ArrayList<>(base.getNodes());
        nodes.removeIf(FocusPackage.MOD_SCATTER::equals);
        FocusPackage once = new FocusPackage(nodes);

        float[] yaws = { -12.0f, 0.0f, 12.0f };
        float savedYaw = caster.getYRot();
        float savedPitch = caster.getXRot();
        for (float yawOff : yaws) {
            caster.setYRot(savedYaw + yawOff);
            caster.setXRot(savedPitch);
            String medium = once.getMedium();
            if (FocusPackage.MEDIUM_PROJECTILE.equals(medium)) {
                executeProjectile(caster, once);
            } else if (FocusPackage.MEDIUM_BOLT.equals(medium)) {
                executeBolt(caster, once);
            } else if (FocusPackage.MEDIUM_CLOUD.equals(medium)) {
                executeCloud(caster, once);
            } else {
                executeTouch(caster, once);
            }
        }
        caster.setYRot(savedYaw);
        caster.setXRot(savedPitch);
    }

    private static void playCastSound(LivingEntity caster, String effect) {
        Level level = caster.level();
        BlockPos above = caster.blockPosition().above();
        if (FocusPackage.EFFECT_FIRE.equals(effect)) {
            level.playSound(null, above, SoundEvents.FIRECHARGE_USE, SoundSource.PLAYERS, 1.0f,
                    1.0f + (float) (caster.getRandom().nextGaussian() * 0.05));
        } else if (FocusPackage.EFFECT_FROST.equals(effect)) {
            level.playSound(null, above, SoundEvents.GLASS_BREAK, SoundSource.PLAYERS, 0.4f, 1.6f);
        } else if (FocusPackage.EFFECT_SHOCK.equals(effect) || FocusPackage.EFFECT_AIR.equals(effect)) {
            level.playSound(null, above, SoundEvents.LIGHTNING_BOLT_IMPACT, SoundSource.PLAYERS, 0.35f, 1.4f);
            if (level instanceof ServerLevel server) {
                server.sendParticles(ParticleTypes.EFFECT,
                        caster.getX(), caster.getY() + 1.0, caster.getZ(),
                        10, 0.35, 0.4, 0.35, 0.02);
            }
        } else if (FocusPackage.EFFECT_EARTH.equals(effect) || FocusPackage.EFFECT_BREAK.equals(effect)) {
            level.playSound(null, above, SoundEvents.GRAVEL_BREAK, SoundSource.PLAYERS, 0.6f, 0.8f);
        } else if (FocusPackage.EFFECT_HEAL.equals(effect)) {
            level.playSound(null, above, SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 0.7f, 1.2f);
        } else if (FocusPackage.EFFECT_FLUX.equals(effect) || FocusPackage.EFFECT_CURSE.equals(effect)) {
            level.playSound(null, above, SoundEvents.BREWING_STAND_BREW, SoundSource.PLAYERS, 0.5f, 0.7f);
        } else if (FocusPackage.EFFECT_RIFT.equals(effect) || FocusPackage.EFFECT_EXCHANGE.equals(effect)) {
            level.playSound(null, above, SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 0.45f, 1.3f);
        }
    }

    private static void executeProjectile(LivingEntity caster, FocusPackage pack) {
        Level level = caster.level();
        FocusProjectile projectile = new FocusProjectile(level, caster, pack.remainingEffects());
        Vec3 look = caster.getLookAngle();
        projectile.shoot(look.x, look.y, look.z, 1.25f, 0.0f);
        level.addFreshEntity(projectile);
    }

    private static void executeBolt(LivingEntity caster, FocusPackage pack) {
        Level level = caster.level();
        Vec3 eye = caster.getEyePosition();
        Vec3 look = caster.getLookAngle();
        Vec3 end = eye.add(look.scale(BOLT_RANGE));
        FocusPackage effects = pack.remainingEffects();

        EntityHitResult entityHit = findEntity(caster, eye, end, BOLT_RANGE);
        if (entityHit != null) {
            spawnCastTrail(level, eye, entityHit.getLocation(), effects.getEffect());
            applyEffects(caster, effects, entityHit.getEntity(), entityHit.getLocation(), null);
            return;
        }
        BlockHitResult blockHit = level.clip(new ClipContext(eye, end, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, caster));
        Vec3 hit = blockHit.getType() == HitResult.Type.BLOCK ? blockHit.getLocation() : end;
        spawnCastTrail(level, eye, hit, effects.getEffect());
        if (blockHit.getType() == HitResult.Type.BLOCK) {
            applyEffects(caster, effects, null, hit, blockHit);
        } else {
            applyEffects(caster, effects, null, hit, null);
        }
    }

    /** AoE linger stub: apply effects once now and again after a short delay in a radius. */
    private static void executeCloud(LivingEntity caster, FocusPackage pack) {
        Level level = caster.level();
        Vec3 eye = caster.getEyePosition();
        Vec3 look = caster.getLookAngle();
        Vec3 aim = eye.add(look.scale(TOUCH_RANGE));
        BlockHitResult blockHit = level.clip(new ClipContext(eye, aim, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, caster));
        Vec3 center = blockHit.getType() == HitResult.Type.BLOCK ? blockHit.getLocation() : aim;
        FocusPackage effects = pack.remainingEffects();

        if (level instanceof ServerLevel server) {
            server.sendParticles(ParticleTypes.CLOUD, center.x, center.y + 0.5, center.z,
                    24, CLOUD_RADIUS * 0.4, 0.4, CLOUD_RADIUS * 0.4, 0.02);
        }
        level.playSound(null, BlockPos.containing(center), SoundEvents.FIRE_EXTINGUISH,
                SoundSource.PLAYERS, 0.5f, 0.8f);
        applyCloudPulse(caster, effects, center);

        if (level instanceof ServerLevel server) {
            // Linger stub: second pulse mid-duration
            server.getServer().tell(new net.minecraft.server.TickTask(
                    server.getServer().getTickCount() + CLOUD_DURATION_TICKS / 2,
                    () -> {
                        if (!caster.isRemoved()) {
                            applyCloudPulse(caster, effects, center);
                        }
                    }));
        }
    }

    private static void applyCloudPulse(LivingEntity caster, FocusPackage effects, Vec3 center) {
        Level level = caster.level();
        AABB box = new AABB(center, center).inflate(CLOUD_RADIUS);
        for (LivingEntity living : level.getEntitiesOfClass(LivingEntity.class, box)) {
            applyEffects(caster, effects, living, living.position(), null);
        }
        if (level instanceof ServerLevel server) {
            server.sendParticles(ParticleTypes.CLOUD, center.x, center.y + 0.4, center.z,
                    12, CLOUD_RADIUS * 0.3, 0.25, CLOUD_RADIUS * 0.3, 0.01);
        }
    }

    private static void executeTouch(LivingEntity caster, FocusPackage focusPackage) {
        Level level = caster.level();
        Vec3 eye = caster.getEyePosition();
        Vec3 look = caster.getLookAngle();
        Vec3 end = eye.add(look.scale(TOUCH_RANGE));
        FocusPackage effects = focusPackage.remainingEffects();

        if (effects.hasNode(FocusPackage.EFFECT_HEAL)) {
            applyHeal(caster);
        }

        EntityHitResult entityHit = findEntity(caster, eye, end, TOUCH_RANGE);
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
            } else if (FocusPackage.EFFECT_SHOCK.equals(effect) || FocusPackage.EFFECT_AIR.equals(effect)) {
                server.sendParticles(ParticleTypes.ELECTRIC_SPARK, p.x, p.y, p.z, 1, 0.02, 0.02, 0.02, 0.0);
            } else if (FocusPackage.EFFECT_EARTH.equals(effect) || FocusPackage.EFFECT_BREAK.equals(effect)) {
                server.sendParticles(ParticleTypes.CLOUD, p.x, p.y, p.z, 1, 0.02, 0.02, 0.02, 0.0);
            } else if (FocusPackage.EFFECT_HEAL.equals(effect)) {
                server.sendParticles(ParticleTypes.HEART, p.x, p.y, p.z, 1, 0.02, 0.02, 0.02, 0.0);
            } else if (FocusPackage.EFFECT_FLUX.equals(effect) || FocusPackage.EFFECT_CURSE.equals(effect)) {
                server.sendParticles(ParticleTypes.WITCH, p.x, p.y, p.z, 1, 0.02, 0.02, 0.02, 0.0);
            } else if (FocusPackage.EFFECT_RIFT.equals(effect) || FocusPackage.EFFECT_EXCHANGE.equals(effect)) {
                server.sendParticles(ParticleTypes.PORTAL, p.x, p.y, p.z, 1, 0.02, 0.02, 0.02, 0.0);
            } else {
                server.sendParticles(ParticleTypes.FLAME, p.x, p.y, p.z, 1, 0.02, 0.02, 0.02, 0.0);
            }
        }
    }

    private static EntityHitResult findEntity(LivingEntity caster, Vec3 start, Vec3 end) {
        return findEntity(caster, start, end, TOUCH_RANGE);
    }

    private static EntityHitResult findEntity(LivingEntity caster, Vec3 start, Vec3 end, double range) {
        AABB box = caster.getBoundingBox().expandTowards(caster.getLookAngle().scale(range)).inflate(1.0);
        EntityHitResult best = null;
        double bestDist = range * range;
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
            if (FocusPackage.ROOT.equals(node) || FocusPackage.isMedium(node) || FocusPackage.isMod(node)) {
                continue;
            }
            switch (node) {
                case FocusPackage.EFFECT_FIRE -> applyFire(caster, targetEntity, blockHit);
                case FocusPackage.EFFECT_FROST -> applyFrost(caster, targetEntity, hit, blockHit);
                case FocusPackage.EFFECT_SHOCK -> applyShock(caster, targetEntity);
                case FocusPackage.EFFECT_AIR -> applyAir(caster, targetEntity);
                case FocusPackage.EFFECT_EARTH -> applyEarth(caster, targetEntity, hit, blockHit);
                case FocusPackage.EFFECT_BREAK -> applyBreak(caster, blockHit);
                case FocusPackage.EFFECT_CURSE -> applyCurse(caster, targetEntity);
                case FocusPackage.EFFECT_EXCHANGE -> applyExchange(caster, blockHit);
                case FocusPackage.EFFECT_FLUX -> applyFlux(caster, targetEntity, hit, blockHit);
                case FocusPackage.EFFECT_RIFT -> applyRift(caster, targetEntity, hit);
                case FocusPackage.EFFECT_HEAL -> {
                    // Touch applies heal on cast; projectile/bolt/cloud heal hit living targets
                    if (targetEntity instanceof LivingEntity living) {
                        applyHeal(living);
                    }
                }
                default -> {
                }
            }
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
        }
    }

    /** AIR: knockback blast (TC6 Air). SHOCK remains the lightning alias. */
    private static void applyAir(LivingEntity caster, Entity targetEntity) {
        Level level = caster != null ? caster.level() : (targetEntity != null ? targetEntity.level() : null);
        if (level == null || !(targetEntity instanceof LivingEntity living)) {
            return;
        }
        float damage = 2.0f;
        if (caster != null) {
            living.hurt(level.damageSources().indirectMagic(caster, caster), damage);
            Vec3 push = living.position().subtract(caster.position()).normalize().scale(0.8);
            living.push(push.x, 0.35, push.z);
        } else {
            living.hurt(level.damageSources().magic(), damage);
        }
        if (level instanceof ServerLevel server) {
            server.sendParticles(ParticleTypes.CLOUD, living.getX(), living.getY() + 1, living.getZ(),
                    10, 0.3, 0.3, 0.3, 0.05);
        }
        level.playSound(null, living.blockPosition(), SoundEvents.ENDER_DRAGON_FLAP, SoundSource.PLAYERS, 0.4f, 0.9f);
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
            }
            return;
        }
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

    private static void applyBreak(LivingEntity caster, BlockHitResult blockHit) {
        if (caster == null || blockHit == null) {
            return;
        }
        Level level = caster.level();
        BlockPos pos = blockHit.getBlockPos();
        BlockState state = level.getBlockState(pos);
        if (state.isAir() || state.getDestroySpeed(level, pos) < 0) {
            return;
        }
        if (caster instanceof Player player) {
            level.destroyBlock(pos, true, player);
        } else {
            level.destroyBlock(pos, true);
        }
    }

    private static void applyCurse(LivingEntity caster, Entity targetEntity) {
        Level level = caster != null ? caster.level() : (targetEntity != null ? targetEntity.level() : null);
        if (level == null || !(targetEntity instanceof LivingEntity living)) {
            return;
        }
        living.addEffect(new MobEffectInstance(MobEffects.WITHER, 80, 0));
        living.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 100, 0));
        if (caster != null) {
            living.hurt(level.damageSources().indirectMagic(caster, caster), 2.0f);
        }
        if (level instanceof ServerLevel server) {
            server.sendParticles(ParticleTypes.SMOKE, living.getX(), living.getY() + 1, living.getZ(),
                    12, 0.3, 0.4, 0.3, 0.02);
        }
    }

    /** Exchange stub: drops a copy of the hit block as an item (no silk/fortune picker yet). */
    private static void applyExchange(LivingEntity caster, BlockHitResult blockHit) {
        if (caster == null || blockHit == null) {
            return;
        }
        Level level = caster.level();
        BlockPos pos = blockHit.getBlockPos();
        BlockState state = level.getBlockState(pos);
        if (state.isAir() || state.getDestroySpeed(level, pos) < 0) {
            return;
        }
        ItemStack drop = new ItemStack(state.getBlock().asItem());
        if (!drop.isEmpty() && level instanceof ServerLevel server) {
            ItemEntity entity = new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, drop);
            level.addFreshEntity(entity);
            server.sendParticles(ParticleTypes.ENCHANT, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                    16, 0.3, 0.3, 0.3, 0.4);
            level.playSound(null, pos, SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS, 0.4f, 1.5f);
        }
    }

    private static void applyFlux(LivingEntity caster, Entity targetEntity, Vec3 hit, BlockHitResult blockHit) {
        Level level = caster != null ? caster.level() : (targetEntity != null ? targetEntity.level() : null);
        if (level == null) {
            return;
        }
        BlockPos pos = blockHit != null ? blockHit.getBlockPos()
                : (hit != null ? BlockPos.containing(hit)
                : (targetEntity != null ? targetEntity.blockPosition() : caster.blockPosition()));
        AuraHelper.polluteAura(level, pos, 1.5f, true);
        if (targetEntity instanceof LivingEntity living) {
            living.addEffect(new MobEffectInstance(MobEffects.POISON, 60, 0));
            if (caster != null) {
                living.hurt(level.damageSources().indirectMagic(caster, caster), 2.5f);
            }
        }
    }

    private static void applyRift(LivingEntity caster, Entity targetEntity, Vec3 hit) {
        Level level = caster != null ? caster.level() : (targetEntity != null ? targetEntity.level() : null);
        if (level == null) {
            return;
        }
        if (targetEntity instanceof LivingEntity living) {
            if (caster != null) {
                living.hurt(level.damageSources().indirectMagic(caster, caster), 6.0f);
            } else {
                living.hurt(level.damageSources().magic(), 6.0f);
            }
            if (level instanceof ServerLevel server) {
                server.sendParticles(ParticleTypes.PORTAL, living.getX(), living.getY() + 1, living.getZ(),
                        20, 0.4, 0.5, 0.4, 0.1);
            }
            return;
        }
        Vec3 at = hit != null ? hit : (caster != null ? caster.position() : Vec3.ZERO);
        if (level instanceof ServerLevel server) {
            server.sendParticles(ParticleTypes.REVERSE_PORTAL, at.x, at.y, at.z, 16, 0.3, 0.3, 0.3, 0.05);
        }
    }

    private static void applyHeal(LivingEntity target) {
        if (target == null) {
            return;
        }
        target.heal(4.0f);
        Level level = target.level();
        if (level instanceof ServerLevel server) {
            server.sendParticles(ParticleTypes.HEART, target.getX(), target.getY() + 1.2, target.getZ(),
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
