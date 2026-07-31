package arcana.common.blockentities;

import arcana.api.aspects.Aspect;
import arcana.registry.ModBlockEntities;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Tube that only transfers a single filtered primal aspect (or any when filter is null).
 */
public class EssentiaFilterTubeBlockEntity extends EssentiaTubeBlockEntity {
    private Aspect filter;

    public EssentiaFilterTubeBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ESSENTIA_FILTER_TUBE.get(), pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, EssentiaFilterTubeBlockEntity tube) {
        EssentiaTubeBlockEntity.serverTick(level, pos, state, tube);
    }

    public Aspect getFilter() {
        return filter;
    }

    public void cycleFilter(Player player) {
        List<Aspect> primals = Aspect.getPrimalAspects();
        if (filter == null) {
            filter = primals.isEmpty() ? Aspect.AIR : primals.get(0);
        } else {
            int idx = primals.indexOf(filter);
            if (idx < 0 || idx + 1 >= primals.size()) {
                filter = null;
            } else {
                filter = primals.get(idx + 1);
            }
        }
        setChangedAndSync();
        if (player != null) {
            String label = filter == null ? "any" : filter.getTag();
            player.displayClientMessage(Component.translatable("arcana.filter_tube.set", label), true);
        }
    }

    private boolean accepts(Aspect aspect) {
        return filter == null || aspect == filter;
    }

    @Override
    public boolean canInputFrom(Direction face) {
        return true;
    }

    @Override
    public int addEssentia(Aspect aspect, int amount, Direction face) {
        if (!accepts(aspect)) {
            return 0;
        }
        return super.addEssentia(aspect, amount, face);
    }

    @Override
    public Aspect getSuctionType(Direction face) {
        return filter != null ? filter : super.getSuctionType(face);
    }

    @Override
    protected Aspect filterPullType(Aspect candidate) {
        return filter != null ? filter : candidate;
    }

    @Override
    protected boolean allowsAspect(Aspect aspect) {
        return accepts(aspect);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (filter != null) {
            tag.putString("filter", filter.getTag());
        }
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        filter = tag.contains("filter") ? Aspect.getAspect(tag.getString("filter")) : null;
    }
}
