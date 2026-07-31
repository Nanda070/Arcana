package arcana.common.golems;

import arcana.common.menu.GolemJobMenu;
import arcana.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

public class GolemSealItem extends Item {
    private final GolemJob job;

    public GolemSealItem(GolemJob job) {
        super(new Item.Properties().stacksTo(16));
        this.job = job;
    }

    public GolemJob getJob() {
        return job;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        Direction face = context.getClickedFace();
        if (!face.getAxis().isHorizontal()) {
            return InteractionResult.FAIL;
        }
        BlockPos attach = context.getClickedPos();
        BlockPos place = attach.relative(face);
        if (!level.getBlockState(attach).isFaceSturdy(level, attach, face)) {
            return InteractionResult.FAIL;
        }
        BlockState existing = level.getBlockState(place);
        if (!existing.canBeReplaced()) {
            return InteractionResult.FAIL;
        }
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        BlockState state = ModBlocks.GOLEM_SEAL.get().defaultBlockState()
                .setValue(SealBlock.FACING, face)
                .setValue(SealBlock.JOB, job.ordinal());
        if (!state.canSurvive(level, place)) {
            return InteractionResult.FAIL;
        }
        level.setBlock(place, state, 3);
        level.playSound(null, place, SoundEvents.WOOL_PLACE, SoundSource.BLOCKS, 0.8f, 1.0f);
        Player player = context.getPlayer();
        if (player != null && !player.getAbilities().instabuild) {
            context.getItemInHand().shrink(1);
        }
        return InteractionResult.CONSUME;
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
        if (!(target instanceof ArcanaGolem golem)) {
            return InteractionResult.PASS;
        }
        if (golem.getOwnerUUID() == null || !player.getUUID().equals(golem.getOwnerUUID())) {
            if (!player.level().isClientSide) {
                player.displayClientMessage(Component.translatable("arcana.golem.seal.not_owner"), true);
            }
            return InteractionResult.FAIL;
        }
        // G17: sneak opens job GUI
        if (player.isShiftKeyDown()) {
            if (!player.level().isClientSide && player instanceof ServerPlayer serverPlayer) {
                openJobScreen(serverPlayer, golem);
            }
            return InteractionResult.sidedSuccess(player.level().isClientSide);
        }
        if (player.level().isClientSide) {
            return InteractionResult.SUCCESS;
        }
        golem.setJob(job);
        player.displayClientMessage(Component.translatable("arcana.golem.job." + job.name().toLowerCase()), true);
        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }
        return InteractionResult.CONSUME;
    }

    public static void openJobScreen(ServerPlayer player, ArcanaGolem golem) {
        MenuProvider provider = new MenuProvider() {
            @Override
            public Component getDisplayName() {
                return Component.translatable("gui.arcana.golem_job");
            }

            @Nullable
            @Override
            public AbstractContainerMenu createMenu(int id, Inventory inv, Player p) {
                return new GolemJobMenu(id, inv, golem.getId());
            }
        };
        NetworkHooks.openScreen(player, provider, buf -> buf.writeVarInt(golem.getId()));
    }
}
