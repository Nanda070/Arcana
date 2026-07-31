package arcana.common.golems;

import arcana.registry.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

public class GolemPlacerItem extends Item {
    public GolemPlacerItem() {
        super(new Item.Properties().stacksTo(16).rarity(Rarity.UNCOMMON));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        BlockPos clicked = context.getClickedPos();
        Direction face = context.getClickedFace();
        BlockState state = level.getBlockState(clicked);
        if (!state.isFaceSturdy(level, clicked, face)) {
            return InteractionResult.FAIL;
        }

        BlockPos spawnPos = clicked.relative(face);
        Player player = context.getPlayer();
        ItemStack stack = context.getItemInHand();
        if (player != null && !player.mayUseItemAt(spawnPos, face, stack)) {
            return InteractionResult.FAIL;
        }
        if (!level.getBlockState(spawnPos).getCollisionShape(level, spawnPos).isEmpty()) {
            return InteractionResult.FAIL;
        }

        ArcanaGolem golem = ModEntities.GOLEM.get().create(level);
        if (golem == null) {
            return InteractionResult.FAIL;
        }
        golem.moveTo(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5, 0.0f, 0.0f);
        if (player != null) {
            golem.tame(player);
            golem.setOwnerUUID(player.getUUID());
        }
        golem.setFollowing(true);
        if (level instanceof ServerLevelAccessor accessor) {
            golem.finalizeSpawn(accessor, level.getCurrentDifficultyAt(spawnPos), MobSpawnType.SPAWN_EGG, null, null);
        }
        level.addFreshEntity(golem);

        if (player == null || !player.getAbilities().instabuild) {
            stack.shrink(1);
        }
        return InteractionResult.CONSUME;
    }
}
