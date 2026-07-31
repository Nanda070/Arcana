package arcana.common.entities;

import arcana.api.aspects.Aspect;
import arcana.api.aura.AuraHelper;
import arcana.registry.ModItems;
import java.util.List;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.FlyingMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * Flying aura parasite: drains vis near players and pollutes flux.
 */
public class Wisp extends FlyingMob implements Enemy {
    private static final EntityDataAccessor<String> ASPECT =
            SynchedEntityData.defineId(Wisp.class, EntityDataSerializers.STRING);

    private int attackCooldown;

    public Wisp(EntityType<? extends Wisp> type, Level level) {
        super(type, level);
        this.moveControl = new FlyingMoveControl(this, 20, true);
        this.xpReward = 5;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 18.0)
                .add(Attributes.FOLLOW_RANGE, 20.0)
                .add(Attributes.ATTACK_DAMAGE, 3.0)
                .add(Attributes.FLYING_SPEED, 0.4)
                .add(Attributes.MOVEMENT_SPEED, 0.25);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ASPECT, Aspect.AIR.getTag());
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new WispChargeGoal(this));
        this.goalSelector.addGoal(2, new WispWanderGoal(this));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 12.0f));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        FlyingPathNavigation nav = new FlyingPathNavigation(this, level);
        nav.setCanOpenDoors(false);
        nav.setCanFloat(true);
        nav.setCanPassDoors(true);
        return nav;
    }

    public String getAspectTag() {
        return this.entityData.get(ASPECT);
    }

    public void setAspectTag(String tag) {
        this.entityData.set(ASPECT, tag == null ? Aspect.AIR.getTag() : tag);
    }

    public Aspect getAspect() {
        Aspect a = Aspect.getAspect(getAspectTag());
        return a != null ? a : Aspect.AIR;
    }

    public void randomizeAspect(RandomSource random) {
        List<Aspect> primals = Aspect.getPrimalAspects();
        if (random.nextInt(8) == 0) {
            List<Aspect> compounds = Aspect.getCompoundAspects();
            if (!compounds.isEmpty()) {
                setAspectTag(compounds.get(random.nextInt(compounds.size())).getTag());
                return;
            }
        }
        setAspectTag(primals.get(random.nextInt(primals.size())).getTag());
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide) {
            Aspect aspect = getAspect();
            int color = aspect.getColor();
            float r = ((color >> 16) & 255) / 255.0f;
            float g = ((color >> 8) & 255) / 255.0f;
            float b = (color & 255) / 255.0f;
            if (this.random.nextBoolean()) {
                this.level().addParticle(new net.minecraft.core.particles.DustParticleOptions(
                                new org.joml.Vector3f(r, g, b), 1.0f),
                        getRandomX(0.4), getY(0.5) + (this.random.nextDouble() - 0.5) * 0.3, getRandomZ(0.4),
                        0.0, 0.02, 0.0);
            }
            return;
        }

        if (getAspectTag().isEmpty() || Aspect.getAspect(getAspectTag()) == null) {
            randomizeAspect(this.random);
        }

        if (this.tickCount % 40 == 0) {
            Player nearest = this.level().getNearestPlayer(this, 8.0);
            if (nearest != null) {
                AuraHelper.drainVis(this.level(), this.blockPosition(), 0.5f, false);
                AuraHelper.polluteAura(this.level(), this.blockPosition(), 0.25f, false);
            }
        }

        if (this.attackCooldown > 0) {
            this.attackCooldown--;
        }
        LivingEntity target = getTarget();
        if (target != null && this.attackCooldown <= 0 && distanceToSqr(target) < 2.5) {
            this.doHurtTarget(target);
            this.attackCooldown = 30;
        }
    }

    @Override
    protected void dropCustomDeathLoot(DamageSource source, int looting, boolean recentlyHit) {
        super.dropCustomDeathLoot(source, looting, recentlyHit);
        Aspect aspect = getAspect();
        if (Aspect.getPrimalAspects().contains(aspect) && this.random.nextInt(Math.max(1, 4 - looting)) == 0) {
            this.spawnAtLocation(new ItemStack(ModItems.crystalFor(aspect).get()));
        }
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, DamageSource source) {
        return false;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ALLAY_AMBIENT_WITHOUT_ITEM;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ALLAY_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ALLAY_DEATH;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putString("Aspect", getAspectTag());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("Aspect")) {
            setAspectTag(tag.getString("Aspect"));
        }
    }

    private static class WispWanderGoal extends Goal {
        private final Wisp wisp;
        private int cooldown;

        WispWanderGoal(Wisp wisp) {
            this.wisp = wisp;
        }

        @Override
        public boolean canUse() {
            return wisp.getTarget() == null && --cooldown <= 0;
        }

        @Override
        public void start() {
            cooldown = 20 + wisp.getRandom().nextInt(40);
            RandomSource random = wisp.getRandom();
            double x = wisp.getX() + (random.nextDouble() * 2.0 - 1.0) * 8.0;
            double y = Mth.clamp(wisp.getY() + (random.nextDouble() * 2.0 - 1.0) * 4.0, 4.0, 120.0);
            double z = wisp.getZ() + (random.nextDouble() * 2.0 - 1.0) * 8.0;
            wisp.getMoveControl().setWantedPosition(x, y, z, 1.0);
        }
    }

    private static class WispChargeGoal extends Goal {
        private final Wisp wisp;

        WispChargeGoal(Wisp wisp) {
            this.wisp = wisp;
        }

        @Override
        public boolean canUse() {
            return wisp.getTarget() != null && wisp.getTarget().isAlive();
        }

        @Override
        public void tick() {
            LivingEntity target = wisp.getTarget();
            if (target == null) {
                return;
            }
            wisp.getLookControl().setLookAt(target, 30.0f, 30.0f);
            Vec3 dest = target.position().add(0.0, target.getBbHeight() * 0.5, 0.0);
            wisp.getMoveControl().setWantedPosition(dest.x, dest.y, dest.z, 1.2);
        }
    }
}
