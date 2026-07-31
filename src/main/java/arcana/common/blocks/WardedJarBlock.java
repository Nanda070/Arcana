package arcana.common.blocks;

import arcana.common.blockentities.WardedJarBlockEntity;
import arcana.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

public class WardedJarBlock extends BaseEntityBlock {
    private static final VoxelShape SHAPE = Block.box(3, 0, 3, 13, 14, 13);

    public WardedJarBlock(Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new WardedJarBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide() ? null : createTickerHelper(type, ModBlockEntities.WARDED_JAR.get(), WardedJarBlockEntity::serverTick);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                 InteractionHand hand, BlockHitResult hit) {
        if (!(level.getBlockEntity(pos) instanceof WardedJarBlockEntity jar)) {
            return InteractionResult.PASS;
        }
        if (!level.isClientSide()) {
            if (player.isShiftKeyDown() && player.getItemInHand(hand).isEmpty()) {
                if (jar.getAspectFilter() != null) {
                    jar.setAspectFilter(null);
                    player.displayClientMessage(Component.translatable("arcana.label.cleared"), true);
                    return InteractionResult.SUCCESS;
                }
                for (Direction dir : Direction.values()) {
                    BlockEntity be = level.getBlockEntity(pos.relative(dir));
                    if (be instanceof arcana.common.blockentities.CrucibleBlockEntity crucible) {
                        int moved = jar.pourInto(crucible);
                        if (moved > 0) {
                            player.displayClientMessage(Component.translatable("arcana.jar.poured", moved), true);
                            return InteractionResult.SUCCESS;
                        }
                    }
                }
            }
            if (player instanceof ServerPlayer serverPlayer) {
                NetworkHooks.openScreen(serverPlayer, jar, pos);
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }
}
