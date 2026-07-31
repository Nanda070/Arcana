package arcana.common.blockentities;

import arcana.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class ArcaneLevitatorBlockEntity extends BlockEntity {
    public ArcaneLevitatorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ARCANE_LEVITATOR.get(), pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, ArcaneLevitatorBlockEntity be) {
        AABB column = new AABB(pos.getX(), pos.getY() + 1, pos.getZ(),
                pos.getX() + 1, pos.getY() + 4, pos.getZ() + 1);
        for (Entity entity : level.getEntities(null, column)) {
            if (!entity.isAlive() || entity.isShiftKeyDown()) {
                continue;
            }
            entity.setDeltaMovement(entity.getDeltaMovement().x, Math.max(0.18, entity.getDeltaMovement().y + 0.08),
                    entity.getDeltaMovement().z);
            entity.fallDistance = 0;
            entity.hurtMarked = true;
        }
    }
}
