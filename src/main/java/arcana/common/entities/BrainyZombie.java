package arcana.common.entities;

import arcana.registry.ModItems;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class BrainyZombie extends Zombie {
    public BrainyZombie(EntityType<? extends Zombie> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Zombie.createAttributes()
                .add(Attributes.MAX_HEALTH, 25.0)
                .add(Attributes.ATTACK_DAMAGE, 5.0)
                .add(Attributes.SPAWN_REINFORCEMENTS_CHANCE, 0.0);
    }

    @Override
    public int getArmorValue() {
        return super.getArmorValue() + 1;
    }

    @Override
    protected void dropCustomDeathLoot(DamageSource source, int looting, boolean recentlyHit) {
        super.dropCustomDeathLoot(source, looting, recentlyHit);
        RandomSource random = this.getRandom();
        if (random.nextInt(10) - looting <= 4) {
            this.spawnAtLocation(new ItemStack(ModItems.BRAIN.get()));
        }
    }

    @Override
    protected boolean convertsInWater() {
        return false;
    }
}
