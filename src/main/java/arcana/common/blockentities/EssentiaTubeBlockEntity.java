package arcana.common.blockentities;

import arcana.api.aspects.Aspect;
import arcana.api.aspects.IEssentiaTransport;
import arcana.registry.ModBlockEntities;
import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * Essentia tube: holds 1 unit, relays suction (BFS depth 8), pulls/pushes between neighbors.
 */
public class EssentiaTubeBlockEntity extends BlockEntity implements IEssentiaTransport {
    private static final int SUCTION_DEPTH = 8;

    private Aspect essentiaType;
    private int essentiaAmount;
    private Aspect suctionType;
    private int suction;
    private int tickCounter;

    public EssentiaTubeBlockEntity(BlockPos pos, BlockState state) {
        this(ModBlockEntities.ESSENTIA_TUBE.get(), pos, state);
    }

    protected EssentiaTubeBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, EssentiaTubeBlockEntity tube) {
        // Active tubes (holding essentia): every 5t; idle empty tubes: every 10t
        int interval = tube.essentiaAmount > 0 ? 5 : 10;
        if (++tube.tickCounter % interval != 0) {
            return;
        }
        tube.calculateSuction();
        tube.processEssentia();
    }

    /**
     * BFS through connected transports up to depth 8; take max(suction − distance).
     * Closed valves (non-connectable) are skipped.
     */
    protected void calculateSuction() {
        if (level == null) {
            return;
        }
        int best = 0;
        Aspect bestType = null;
        Queue<long[]> queue = new ArrayDeque<>();
        Set<BlockPos> visited = new HashSet<>();
        queue.add(new long[]{worldPosition.asLong(), 0});
        visited.add(worldPosition);

        while (!queue.isEmpty()) {
            long[] node = queue.poll();
            BlockPos cur = BlockPos.of(node[0]);
            int depth = (int) node[1];
            BlockEntity curBe = level.getBlockEntity(cur);
            if (!(curBe instanceof IEssentiaTransport curTransport)) {
                continue;
            }
            if (depth > 0) {
                // Sample suction from this transport toward the path we arrived from is approximated
                // by taking the max face suction (tubes report the same on all faces).
                for (Direction face : Direction.values()) {
                    if (!curTransport.isConnectable(face)) {
                        continue;
                    }
                    int amount = curTransport.getSuctionAmount(face);
                    int effective = amount - depth;
                    if (effective > best) {
                        best = effective;
                        bestType = curTransport.getSuctionType(face);
                    }
                }
            }
            if (depth >= SUCTION_DEPTH) {
                continue;
            }
            for (Direction dir : Direction.values()) {
                if (!curTransport.isConnectable(dir)) {
                    continue;
                }
                BlockPos next = cur.relative(dir);
                if (visited.contains(next)) {
                    continue;
                }
                BlockEntity nextBe = level.getBlockEntity(next);
                if (!(nextBe instanceof IEssentiaTransport nextTransport)
                        || !nextTransport.isConnectable(dir.getOpposite())) {
                    continue;
                }
                visited.add(next);
                queue.add(new long[]{next.asLong(), depth + 1});
            }
        }

        suction = Math.max(0, best);
        if (filterPullType(null) != null && bestType == null) {
            suctionType = filterPullType(null);
        } else {
            Aspect filtered = filterPullType(bestType);
            suctionType = filtered != null ? filtered : bestType;
        }
    }

    protected void processEssentia() {
        if (level == null) {
            return;
        }
        if (essentiaAmount > 0 && essentiaType != null) {
            if (!allowsAspect(essentiaType)) {
                return;
            }
            Direction bestDir = null;
            int bestSuction = suction;
            for (Direction dir : Direction.values()) {
                BlockEntity be = level.getBlockEntity(worldPosition.relative(dir));
                if (!(be instanceof IEssentiaTransport other) || !canOutputTo(dir) || !other.canInputFrom(dir.getOpposite())) {
                    continue;
                }
                int s = other.getSuctionAmount(dir.getOpposite());
                Aspect st = other.getSuctionType(dir.getOpposite());
                if (s > bestSuction && (st == null || st == essentiaType)) {
                    bestSuction = s;
                    bestDir = dir;
                }
            }
            if (bestDir != null) {
                IEssentiaTransport other = (IEssentiaTransport) level.getBlockEntity(worldPosition.relative(bestDir));
                if (other != null) {
                    int added = other.addEssentia(essentiaType, 1, bestDir.getOpposite());
                    if (added > 0) {
                        essentiaAmount -= added;
                        if (essentiaAmount <= 0) {
                            essentiaAmount = 0;
                            essentiaType = null;
                        }
                        setChangedAndSync();
                    }
                }
            }
        } else {
            Direction bestDir = null;
            int worstSuction = Integer.MAX_VALUE;
            Aspect pullType = filterPullType(suctionType);
            for (Direction dir : Direction.values()) {
                BlockEntity be = level.getBlockEntity(worldPosition.relative(dir));
                if (!(be instanceof IEssentiaTransport other) || !canInputFrom(dir) || !other.canOutputTo(dir.getOpposite())) {
                    continue;
                }
                int s = other.getSuctionAmount(dir.getOpposite());
                Aspect type = other.getEssentiaType(dir.getOpposite());
                int amt = other.getEssentiaAmount(dir.getOpposite());
                if (amt <= 0 || type == null || !allowsAspect(type)) {
                    continue;
                }
                if (pullType != null && type != pullType) {
                    continue;
                }
                if (s < suction && s < worstSuction && suction >= other.getMinimumSuction()) {
                    worstSuction = s;
                    bestDir = dir;
                    pullType = type;
                }
            }
            if (bestDir != null && pullType != null) {
                IEssentiaTransport other = (IEssentiaTransport) level.getBlockEntity(worldPosition.relative(bestDir));
                if (other != null) {
                    int taken = other.takeEssentia(pullType, 1, bestDir.getOpposite());
                    if (taken > 0) {
                        essentiaType = pullType;
                        essentiaAmount = taken;
                        setChangedAndSync();
                    }
                }
            }
        }
    }

    /** Override in filter tubes to force a preferred pull type. */
    protected Aspect filterPullType(@Nullable Aspect candidate) {
        return candidate;
    }

    protected boolean allowsAspect(Aspect aspect) {
        return true;
    }

    /** Override for buffer tubes. */
    protected int getCapacity() {
        return 1;
    }

    /** Override for restrict tubes (lower relayed suction). */
    protected int modifySuction(int raw) {
        return raw;
    }

    @Override
    public boolean isConnectable(Direction face) {
        return true;
    }

    @Override
    public boolean canInputFrom(Direction face) {
        return true;
    }

    @Override
    public boolean canOutputTo(Direction face) {
        return true;
    }

    @Override
    public void setSuction(Aspect aspect, int amount) {
        suctionType = aspect;
        suction = amount;
    }

    @Override
    public Aspect getSuctionType(Direction face) {
        return suctionType;
    }

    @Override
    public int getSuctionAmount(Direction face) {
        return modifySuction(suction);
    }

    @Override
    public int takeEssentia(Aspect aspect, int amount, Direction face) {
        if (!canOutputTo(face) || essentiaType != aspect || essentiaAmount < amount) {
            return 0;
        }
        essentiaAmount -= amount;
        if (essentiaAmount <= 0) {
            essentiaAmount = 0;
            essentiaType = null;
        }
        setChangedAndSync();
        return amount;
    }

    @Override
    public int addEssentia(Aspect aspect, int amount, Direction face) {
        if (!canInputFrom(face) || amount <= 0 || !allowsAspect(aspect)) {
            return 0;
        }
        if (essentiaAmount > 0 && essentiaType != aspect) {
            return 0;
        }
        int capacity = getCapacity();
        if (essentiaAmount >= capacity) {
            return 0;
        }
        int space = capacity - essentiaAmount;
        int added = Math.min(amount, space);
        essentiaType = aspect;
        essentiaAmount += added;
        setChangedAndSync();
        return added;
    }

    @Override
    public Aspect getEssentiaType(Direction face) {
        return essentiaType;
    }

    @Override
    public int getEssentiaAmount(Direction face) {
        return essentiaAmount;
    }

    @Override
    public int getMinimumSuction() {
        return 1;
    }

    protected void setChangedAndSync() {
        setChanged();
        if (level != null && !level.isClientSide()) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (essentiaType != null) {
            tag.putString("type", essentiaType.getTag());
        }
        tag.putInt("amount", essentiaAmount);
        if (suctionType != null) {
            tag.putString("stype", suctionType.getTag());
        }
        tag.putInt("samount", suction);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        essentiaType = Aspect.getAspect(tag.getString("type"));
        essentiaAmount = tag.getInt("amount");
        suctionType = Aspect.getAspect(tag.getString("stype"));
        suction = tag.getInt("samount");
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        saveAdditional(tag);
        return tag;
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
