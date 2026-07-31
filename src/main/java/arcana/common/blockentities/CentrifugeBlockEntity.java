package arcana.common.blockentities;

import arcana.api.aspects.Aspect;
import arcana.api.aspects.IEssentiaTransport;
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
 * Stub centrifuge: every 40 ticks, if holding a compound aspect, convert 1 unit into 2 aer.
 */
public class CentrifugeBlockEntity extends BlockEntity implements IEssentiaTransport {
    public static final int CAPACITY = 16;
    private static final int INTERVAL = 40;

    private Aspect aspect;
    private int amount;
    private int tickCounter;

    public CentrifugeBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CENTRIFUGE.get(), pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, CentrifugeBlockEntity be) {
        if (++be.tickCounter % INTERVAL != 0) {
            return;
        }
        be.spin();
    }

    private void spin() {
        if (aspect == null || amount <= 0 || aspect.isPrimal()) {
            return;
        }
        // Minimal stub: consume 1 compound; when emptied, fill with 2 aer
        amount -= 1;
        if (amount <= 0) {
            aspect = Aspect.AIR;
            amount = Math.min(CAPACITY, 2);
        }
        setChangedAndSync();
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
    }

    @Override
    public Aspect getSuctionType(Direction face) {
        return aspect;
    }

    @Override
    public int getSuctionAmount(Direction face) {
        return amount >= CAPACITY ? 0 : 24;
    }

    @Override
    public int getMinimumSuction() {
        return 1;
    }

    @Override
    public Aspect getEssentiaType(Direction face) {
        return aspect;
    }

    @Override
    public int getEssentiaAmount(Direction face) {
        return amount;
    }

    @Override
    public int takeEssentia(Aspect tag, int am, Direction face) {
        if (!canOutputTo(face) || aspect != tag || amount < am) {
            return 0;
        }
        amount -= am;
        if (amount <= 0) {
            amount = 0;
            aspect = null;
        }
        setChangedAndSync();
        return am;
    }

    @Override
    public int addEssentia(Aspect tag, int am, Direction face) {
        if (!canInputFrom(face) || am <= 0) {
            return 0;
        }
        if (aspect != null && aspect != tag) {
            return 0;
        }
        int space = CAPACITY - amount;
        if (space <= 0) {
            return 0;
        }
        int added = Math.min(am, space);
        aspect = tag;
        amount += added;
        setChangedAndSync();
        return added;
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
        if (aspect != null) {
            tag.putString("Aspect", aspect.getTag());
        }
        tag.putInt("Amount", amount);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        aspect = Aspect.getAspect(tag.getString("Aspect"));
        amount = tag.getInt("Amount");
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
