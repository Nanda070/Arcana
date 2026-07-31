package arcana.common.blocks;

import arcana.api.aspects.Aspect;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Primal crystal cluster with AGE 0–3 growth stub. Models are shared across ages.
 */
public class CrystalClusterBlock extends Block {
    public static final IntegerProperty AGE = BlockStateProperties.AGE_3;
    private static final VoxelShape SHAPE = Block.box(3.0, 0.0, 3.0, 13.0, 12.0, 13.0);
    private static final int GROW_LIGHT = 9;

    private final Aspect aspect;

    public CrystalClusterBlock(Aspect aspect) {
        super(BlockBehaviour.Properties.of()
                .mapColor(MapColor.NONE)
                .strength(0.35f)
                .sound(SoundType.GLASS)
                .lightLevel(state -> 6)
                .noOcclusion()
                .randomTicks()
                .pushReaction(PushReaction.DESTROY));
        this.aspect = aspect;
        registerDefaultState(stateDefinition.any().setValue(AGE, 0));
    }

    public Aspect getAspect() {
        return aspect;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AGE);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return touchesSupport(level, pos);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
                                  LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        return !state.canSurvive(level, pos) ? net.minecraft.world.level.block.Blocks.AIR.defaultBlockState() : state;
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return state.getValue(AGE) < 3;
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (state.getValue(AGE) >= 3) {
            return;
        }
        if (level.getMaxLocalRawBrightness(pos) < GROW_LIGHT) {
            return;
        }
        level.setBlock(pos, state.setValue(AGE, state.getValue(AGE) + 1), Block.UPDATE_ALL);
    }

    public static boolean touchesSupport(LevelReader level, BlockPos pos) {
        for (Direction dir : Direction.values()) {
            BlockState neighbor = level.getBlockState(pos.relative(dir));
            if (neighbor.isFaceSturdy(level, pos.relative(dir), dir.getOpposite())) {
                return true;
            }
        }
        return false;
    }
}
