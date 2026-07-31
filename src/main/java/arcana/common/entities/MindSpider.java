package arcana.common.entities;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.level.Level;

/**
 * Fleeting warp hallucination — fragile, short-lived, no loot.
 */
public class MindSpider extends Spider {
    public static final int LIFESPAN_TICKS = 1200;

    private int lifeTicks;
    private boolean harmless;

    public MindSpider(EntityType<? extends MindSpider> type, Level level) {
        super(type, level);
        this.xpReward = 0;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 1.0)
                .add(Attributes.MOVEMENT_SPEED, 0.3)
                .add(Attributes.ATTACK_DAMAGE, 1.0)
                .add(Attributes.FOLLOW_RANGE, 16.0);
    }

    public void setHarmless(boolean harmless) {
        this.harmless = harmless;
    }

    public boolean isHarmless() {
        return harmless;
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide) {
            lifeTicks++;
            if (lifeTicks >= LIFESPAN_TICKS) {
                discard();
            }
        }
    }

    @Override
    protected void dropFromLootTable(DamageSource source, boolean damagedByPlayer) {
        // no loot
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("LifeTicks", lifeTicks);
        tag.putBoolean("Harmless", harmless);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        lifeTicks = tag.getInt("LifeTicks");
        harmless = tag.getBoolean("Harmless");
    }
}
