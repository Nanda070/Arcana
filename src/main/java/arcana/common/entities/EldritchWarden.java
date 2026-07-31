package arcana.common.entities;

import arcana.common.lib.events.MindSpiderSpawnHelper;
import arcana.registry.ModEntities;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.UUID;

/**
 * Eldritch Warden with phased fight: rage at &lt;50%, summon spiders at &lt;25%.
 */
public class EldritchWarden extends EldritchGuardian {
    private static final UUID RAGE_SPEED_UUID = UUID.fromString("a7c3e1b0-4d2f-4a8e-9c1b-2e5f6a7b8c9d");
    private static final AttributeModifier RAGE_SPEED = new AttributeModifier(
            RAGE_SPEED_UUID, "Warden rage speed", 0.35, AttributeModifier.Operation.MULTIPLY_TOTAL);

    /** 0 = calm, 1 = rage (&lt;50%), 2 = swarm (&lt;25%). */
    private int phase;
    private boolean healedOnce;

    public EldritchWarden(EntityType<? extends EldritchGuardian> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return EldritchGuardian.createAttributes()
                .add(Attributes.MAX_HEALTH, 80.0)
                .add(Attributes.ATTACK_DAMAGE, 10.0)
                .add(Attributes.ARMOR, 6.0)
                .add(Attributes.FOLLOW_RANGE, 28.0);
    }

    public int getPhase() {
        return phase;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (level().isClientSide) {
            return;
        }
        float pct = getHealth() / getMaxHealth();
        if (pct < 0.5f && phase < 1) {
            enterRage();
        }
        if (pct < 0.25f && phase < 2) {
            enterSwarm();
        }
    }

    private void enterRage() {
        phase = 1;
        if (!healedOnce) {
            healedOnce = true;
            setHealth(Math.min(getMaxHealth(), getHealth() + getMaxHealth() * 0.25f));
        }
        AttributeInstance speed = getAttribute(Attributes.MOVEMENT_SPEED);
        if (speed != null && !speed.hasModifier(RAGE_SPEED)) {
            speed.addPermanentModifier(RAGE_SPEED);
        }
        addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 200, 0, false, false));
    }

    private void enterSwarm() {
        phase = 2;
        if (getTarget() instanceof Player player) {
            MindSpiderSpawnHelper.spawnNear(player, 2);
        } else if (level() instanceof ServerLevel server) {
            for (int i = 0; i < 2; i++) {
                MindSpider spider = ModEntities.MIND_SPIDER.get().create(server);
                if (spider == null) {
                    break;
                }
                spider.moveTo(getX() + (random.nextDouble() - 0.5) * 4.0, getY(),
                        getZ() + (random.nextDouble() - 0.5) * 4.0, random.nextFloat() * 360.0f, 0.0f);
                spider.setTarget(getTarget());
                server.addFreshEntity(spider);
            }
        }
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        boolean hurt = super.doHurtTarget(target);
        if (hurt && phase >= 1 && target instanceof Player player && !player.level().isClientSide) {
            player.addEffect(new MobEffectInstance(MobEffects.WITHER, 80 + phase * 40, 0));
        }
        return hurt;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("phase", phase);
        tag.putBoolean("HealedOnce", healedOnce);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        phase = tag.getInt("phase");
        healedOnce = tag.getBoolean("HealedOnce");
        if (phase >= 1) {
            AttributeInstance speed = getAttribute(Attributes.MOVEMENT_SPEED);
            if (speed != null && !speed.hasModifier(RAGE_SPEED)) {
                speed.addPermanentModifier(RAGE_SPEED);
            }
        }
    }

    @Override
    protected void dropCustomDeathLoot(DamageSource source, int looting, boolean recentlyHit) {
        super.dropCustomDeathLoot(source, looting, recentlyHit);
    }
}
