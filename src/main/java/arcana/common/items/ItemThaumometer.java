package arcana.common.items;

import arcana.api.aura.AuraHelper;
import arcana.api.research.ScanningManager;
import arcana.client.ClientScanTarget;
import arcana.common.lib.research.ResearchManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;

public class ItemThaumometer extends Item {
    public ItemThaumometer() {
        super(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide) {
            Object target = findScanTarget(player, 8.0);
            ScanningManager.scanTheThing(player, target);
            level.playSound(null, player.blockPosition(), SoundEvents.ENCHANTMENT_TABLE_USE,
                    SoundSource.PLAYERS, 0.5f, 1.2f);

            // High flux while scanning starts FLUX research
            if (AuraHelper.getFlux(level, player.blockPosition()) >= 10.0f
                    && player instanceof ServerPlayer serverPlayer) {
                ResearchManager.startResearchWithPopup(serverPlayer, "FLUX");
            }
        } else {
            level.playLocalSound(player.getX(), player.getY(), player.getZ(),
                    SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS, 0.5f, 1.2f, false);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (!(entity instanceof Player player)) {
            return;
        }
        boolean held = isSelected || ItemStack.isSameItemSameTags(player.getOffhandItem(), stack)
                || player.getOffhandItem() == stack;
        if (!held) {
            return;
        }
        if (level.isClientSide && player.tickCount % 5 == 0) {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                Object target = findScanTarget(player, 8.0);
                if (target != null && ScanningManager.isThingStillScannable(player, target)) {
                    ClientScanTarget.set(target);
                } else {
                    ClientScanTarget.set(null);
                }
            });
        }
    }

    public static Object findScanTarget(Player player, double range) {
        Vec3 eye = player.getEyePosition();
        Vec3 look = player.getLookAngle();
        Vec3 end = eye.add(look.scale(range));

        EntityHitResult entityHit = findEntity(player, eye, end, range);
        if (entityHit != null) {
            return entityHit.getEntity();
        }

        BlockHitResult blockHit = player.level().clip(
                new ClipContext(eye, end, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player));
        if (blockHit.getType() == HitResult.Type.BLOCK) {
            return blockHit.getBlockPos();
        }
        return null;
    }

    private static EntityHitResult findEntity(Player player, Vec3 start, Vec3 end, double range) {
        AABB box = player.getBoundingBox().expandTowards(player.getLookAngle().scale(range)).inflate(1.0);
        EntityHitResult best = null;
        double bestDist = range * range;
        for (Entity entity : player.level().getEntities(player, box, e -> e.isPickable() && e != player)) {
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
}
