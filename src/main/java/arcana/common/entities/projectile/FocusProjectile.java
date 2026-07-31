package arcana.common.entities.projectile;

import arcana.api.casters.FocusEngine;
import arcana.api.casters.FocusPackage;
import arcana.registry.ModEntities;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.network.NetworkHooks;

public class FocusProjectile extends ThrowableProjectile {
    private FocusPackage remaining = FocusPackage.touchFire().remainingEffects();

    public FocusProjectile(EntityType<? extends FocusProjectile> type, Level level) {
        super(type, level);
    }

    public FocusProjectile(Level level, LivingEntity owner, FocusPackage remainingEffects) {
        super(ModEntities.FOCUS_PROJECTILE.get(), owner, level);
        this.remaining = remainingEffects == null ? FocusPackage.touchFire().remainingEffects() : remainingEffects.copy();
    }

    public FocusPackage getRemaining() {
        return remaining;
    }

    @Override
    protected void defineSynchedData() {
    }

    @Override
    protected float getGravity() {
        return 0.03f;
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide) {
            String effect = remaining.getEffect();
            if (FocusPackage.EFFECT_FROST.equals(effect)) {
                level().addParticle(net.minecraft.core.particles.ParticleTypes.SNOWFLAKE,
                        getX(), getY(), getZ(), 0, 0, 0);
            } else if (FocusPackage.EFFECT_SHOCK.equals(effect)) {
                level().addParticle(net.minecraft.core.particles.ParticleTypes.ELECTRIC_SPARK,
                        getX(), getY(), getZ(), 0, 0, 0);
                level().addParticle(net.minecraft.core.particles.ParticleTypes.EFFECT,
                        getX(), getY(), getZ(), 0, 0, 0);
            } else if (FocusPackage.EFFECT_EARTH.equals(effect)) {
                level().addParticle(net.minecraft.core.particles.ParticleTypes.CLOUD,
                        getX(), getY(), getZ(), 0, 0, 0);
                level().addParticle(net.minecraft.core.particles.ParticleTypes.EFFECT,
                        getX(), getY(), getZ(), 0, 0, 0);
            } else if (FocusPackage.EFFECT_HEAL.equals(effect)) {
                level().addParticle(net.minecraft.core.particles.ParticleTypes.HEART,
                        getX(), getY(), getZ(), 0, 0, 0);
            } else {
                level().addParticle(net.minecraft.core.particles.ParticleTypes.FLAME,
                        getX(), getY(), getZ(), 0, 0, 0);
            }
        }
        if (tickCount > 80) {
            discard();
        }
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (!level().isClientSide) {
            LivingEntity caster = getOwner() instanceof LivingEntity living ? living : null;
            if (result instanceof EntityHitResult entityHit) {
                FocusEngine.applyEffects(caster, remaining, entityHit.getEntity(), entityHit.getLocation(), null);
            } else if (result instanceof BlockHitResult blockHit) {
                FocusEngine.applyEffects(caster, remaining, null, blockHit.getLocation(), blockHit);
            }
            discard();
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.put("FocusPack", remaining.serialize());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("FocusPack")) {
            remaining = new FocusPackage();
            remaining.deserialize(tag.getCompound("FocusPack"));
        }
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
