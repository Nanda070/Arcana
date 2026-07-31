package arcana.common.blockentities;

import arcana.api.aspects.Aspect;
import arcana.api.aspects.IEssentiaTransport;
import arcana.common.blocks.EssentiaValveBlock;
import arcana.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * H2: Tube-like relay that stops transferring while the valve block is redstone-powered.
 */
public class EssentiaValveBlockEntity extends BlockEntity implements IEssentiaTransport {
    private Aspect essentiaType;
    private int essentiaAmount;
    private Aspect suctionType;
    private int suction;
    private int tickCounter;

    public EssentiaValveBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ESSENTIA_VALVE.get(), pos, state);
    }

    private boolean isOpen() {
        return EssentiaValveBlock.isOpen(getBlockState());
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, EssentiaValveBlockEntity valve) {
        if (++valve.tickCounter % 5 != 0) {
            return;
        }
        if (!valve.isOpen()) {
            valve.suction = 0;
            valve.suctionType = null;
            return;
        }
        valve.calculateSuction();
        valve.processEssentia();
    }

    private void calculateSuction() {
        if (level == null) {
            return;
        }
        int best = 0;
        Aspect bestType = null;
        for (Direction dir : Direction.values()) {
            BlockEntity be = level.getBlockEntity(worldPosition.relative(dir));
            if (!(be instanceof IEssentiaTransport other) || !other.isConnectable(dir.getOpposite())) {
                continue;
            }
            if (!isConnectable(dir)) {
                continue;
            }
            int amount = other.getSuctionAmount(dir.getOpposite());
            if (amount > best + 1) {
                best = amount - 1;
                bestType = other.getSuctionType(dir.getOpposite());
            }
        }
        suction = Math.max(0, best);
        suctionType = bestType;
    }

    private void processEssentia() {
        if (level == null) {
            return;
        }
        if (essentiaAmount > 0 && essentiaType != null) {
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
                        essentiaAmount = 0;
                        essentiaType = null;
                        setChangedAndSync();
                    }
                }
            }
        } else {
            Direction bestDir = null;
            int worstSuction = Integer.MAX_VALUE;
            Aspect pullType = suctionType;
            for (Direction dir : Direction.values()) {
                BlockEntity be = level.getBlockEntity(worldPosition.relative(dir));
                if (!(be instanceof IEssentiaTransport other) || !canInputFrom(dir) || !other.canOutputTo(dir.getOpposite())) {
                    continue;
                }
                int s = other.getSuctionAmount(dir.getOpposite());
                Aspect type = other.getEssentiaType(dir.getOpposite());
                int amt = other.getEssentiaAmount(dir.getOpposite());
                if (amt <= 0 || type == null) {
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

    @Override
    public boolean isConnectable(Direction face) {
        return isOpen();
    }

    @Override
    public boolean canInputFrom(Direction face) {
        return isOpen();
    }

    @Override
    public boolean canOutputTo(Direction face) {
        return isOpen();
    }

    @Override
    public void setSuction(Aspect aspect, int amount) {
        suctionType = aspect;
        suction = amount;
    }

    @Override
    public Aspect getSuctionType(Direction face) {
        return isOpen() ? suctionType : null;
    }

    @Override
    public int getSuctionAmount(Direction face) {
        return isOpen() ? suction : 0;
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
        if (!canInputFrom(face) || amount <= 0) {
            return 0;
        }
        if (essentiaAmount > 0 && essentiaType != aspect) {
            return 0;
        }
        if (essentiaAmount >= 1) {
            return 0;
        }
        essentiaType = aspect;
        essentiaAmount = 1;
        setChangedAndSync();
        return 1;
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

    private void setChangedAndSync() {
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
