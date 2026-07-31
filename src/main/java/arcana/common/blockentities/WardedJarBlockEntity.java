package arcana.common.blockentities;

import arcana.api.aspects.Aspect;
import arcana.api.aspects.AspectList;
import arcana.api.aspects.IAspectSource;
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

public class WardedJarBlockEntity extends BlockEntity implements IAspectSource, IEssentiaTransport {
    public static final int CAPACITY = 250;

    private Aspect aspect;
    private Aspect aspectFilter;
    private int amount;
    private int tickCounter;

    public WardedJarBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.WARDED_JAR.get(), pos, state);
    }

    public Aspect getAspect() {
        return aspect;
    }

    public int getAmount() {
        return amount;
    }

    public Aspect getAspectFilter() {
        return aspectFilter;
    }

    public void setAspectFilter(Aspect filter) {
        this.aspectFilter = filter;
        setChangedAndSync();
    }

    /** Empty jar contents into a crucible aspect list. Returns units moved. */
    public int pourInto(CrucibleBlockEntity crucible) {
        if (aspect == null || amount <= 0) {
            return 0;
        }
        int before = amount;
        crucible.addToContainer(aspect, amount);
        // crucible accepts all in current impl
        amount = 0;
        aspect = null;
        setChangedAndSync();
        return before;
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, WardedJarBlockEntity jar) {
        if (++jar.tickCounter % 5 != 0 || jar.amount >= CAPACITY) {
            return;
        }
        jar.fillFromAbove();
    }

    private void fillFromAbove() {
        if (level == null) {
            return;
        }
        BlockEntity above = level.getBlockEntity(worldPosition.above());
        if (!(above instanceof IEssentiaTransport transport) || !transport.canOutputTo(Direction.DOWN)) {
            return;
        }
        Aspect want = aspect;
        if (want == null) {
            if (transport.getEssentiaAmount(Direction.DOWN) <= 0) {
                return;
            }
            if (transport.getSuctionAmount(Direction.DOWN) >= getSuctionAmount(Direction.UP)) {
                return;
            }
            want = transport.getEssentiaType(Direction.DOWN);
        }
        if (want == null) {
            return;
        }
        if (transport.getSuctionAmount(Direction.DOWN) < getSuctionAmount(Direction.UP)
                && getSuctionAmount(Direction.UP) >= transport.getMinimumSuction()) {
            int taken = transport.takeEssentia(want, 1, Direction.DOWN);
            if (taken > 0) {
                addToContainer(want, taken);
            }
        }
    }

    @Override
    public AspectList getAspects() {
        AspectList list = new AspectList();
        if (aspect != null && amount > 0) {
            list.add(aspect, amount);
        }
        return list;
    }

    @Override
    public void setAspects(AspectList aspects) {
        if (aspects != null && aspects.size() > 0) {
            Aspect first = aspects.getAspectsSortedByAmount()[0];
            aspect = first;
            amount = aspects.getAmount(first);
            setChangedAndSync();
        }
    }

    @Override
    public boolean doesContainerAccept(Aspect tag) {
        if (aspectFilter != null && tag != aspectFilter) {
            return false;
        }
        return aspect == null || aspect == tag;
    }

    @Override
    public int addToContainer(Aspect tag, int am) {
        if (am <= 0 || !doesContainerAccept(tag)) {
            return am;
        }
        if ((amount < CAPACITY && tag == aspect) || amount == 0) {
            aspect = tag;
            int added = Math.min(am, CAPACITY - amount);
            amount += added;
            am -= added;
            setChangedAndSync();
        }
        return am;
    }

    @Override
    public boolean takeFromContainer(Aspect tag, int am) {
        if (amount >= am && tag == aspect) {
            amount -= am;
            if (amount <= 0) {
                aspect = null;
                amount = 0;
            }
            setChangedAndSync();
            return true;
        }
        return false;
    }

    @Override
    public boolean doesContainerContainAmount(Aspect tag, int am) {
        return amount >= am && tag == aspect;
    }

    @Override
    public int containerContains(Aspect tag) {
        return tag == aspect ? amount : 0;
    }

    @Override
    public boolean isBlocked() {
        return false;
    }

    @Override
    public boolean isConnectable(Direction face) {
        return face == Direction.UP;
    }

    @Override
    public boolean canInputFrom(Direction face) {
        return face == Direction.UP;
    }

    @Override
    public boolean canOutputTo(Direction face) {
        return face == Direction.UP;
    }

    @Override
    public void setSuction(Aspect aspect, int amount) {
    }

    @Override
    public int getMinimumSuction() {
        return aspectFilter != null ? 48 : 32;
    }

    @Override
    public Aspect getSuctionType(Direction face) {
        return aspectFilter != null ? aspectFilter : aspect;
    }

    @Override
    public int getSuctionAmount(Direction face) {
        return amount >= CAPACITY ? 0 : (aspectFilter != null ? 64 : 32);
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
    public int takeEssentia(Aspect aspect, int amount, Direction face) {
        return (canOutputTo(face) && takeFromContainer(aspect, amount)) ? amount : 0;
    }

    @Override
    public int addEssentia(Aspect aspect, int amount, Direction face) {
        return canInputFrom(face) ? amount - addToContainer(aspect, amount) : 0;
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
        if (aspectFilter != null) {
            tag.putString("AspectFilter", aspectFilter.getTag());
        }
        tag.putInt("Amount", amount);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        aspect = Aspect.getAspect(tag.getString("Aspect"));
        aspectFilter = tag.contains("AspectFilter") ? Aspect.getAspect(tag.getString("AspectFilter")) : null;
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
