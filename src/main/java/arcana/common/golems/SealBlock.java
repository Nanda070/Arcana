package arcana.common.golems;

import arcana.registry.ModItems;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

/**
 * Thin wall-attached seal plate. Job = {@link GolemJob} ordinal.
 */
public class SealBlock extends HorizontalDirectionalBlock {
    public static final IntegerProperty JOB = IntegerProperty.create("job", 0, 7);

    private static final VoxelShape NORTH = Block.box(2, 2, 14, 14, 14, 16);
    private static final VoxelShape SOUTH = Block.box(2, 2, 0, 14, 14, 2);
    private static final VoxelShape WEST = Block.box(14, 2, 2, 16, 14, 14);
    private static final VoxelShape EAST = Block.box(0, 2, 2, 2, 14, 14);

    public SealBlock() {
        super(BlockBehaviour.Properties.of()
                .mapColor(MapColor.WOOL)
                .strength(0.4f)
                .sound(SoundType.WOOL)
                .noOcclusion()
                .noCollission()
                .pushReaction(PushReaction.DESTROY));
        registerDefaultState(stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(JOB, 0));
    }

    public static GolemJob jobOf(BlockState state) {
        return GolemJob.byId(state.getValue(JOB));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, JOB);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction face = context.getClickedFace();
        if (!face.getAxis().isHorizontal()) {
            return null;
        }
        BlockPos attach = context.getClickedPos().relative(face.getOpposite());
        if (!context.getLevel().getBlockState(attach).isFaceSturdy(context.getLevel(), attach, face)) {
            return null;
        }
        return defaultBlockState().setValue(FACING, face);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        Direction facing = state.getValue(FACING);
        BlockPos attach = pos.relative(facing.getOpposite());
        return level.getBlockState(attach).isFaceSturdy(level, attach, facing);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
                                  LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        return direction == state.getValue(FACING).getOpposite() && !state.canSurvive(level, pos)
                ? Blocks.AIR.defaultBlockState()
                : state;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(FACING)) {
            case SOUTH -> SOUTH;
            case WEST -> WEST;
            case EAST -> EAST;
            default -> NORTH;
        };
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        return List.of(switch (jobOf(state)) {
            case GATHER -> new ItemStack(ModItems.SEAL_GATHER.get());
            case GUARD -> new ItemStack(ModItems.SEAL_GUARD.get());
            case FILL -> new ItemStack(ModItems.SEAL_FILL.get());
            case EMPTY -> new ItemStack(ModItems.SEAL_EMPTY.get());
            case HARVEST -> new ItemStack(ModItems.SEAL_HARVEST.get());
            case USE -> new ItemStack(ModItems.SEAL_USE.get());
            case BUTCHER -> new ItemStack(ModItems.SEAL_BUTCHER.get());
            default -> new ItemStack(ModItems.SEAL_BLANK.get());
        });
    }
}
