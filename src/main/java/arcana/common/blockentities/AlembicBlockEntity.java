package arcana.common.blockentities;

import arcana.api.aspects.Aspect;
import arcana.api.aspects.AspectList;
import arcana.api.aspects.IAspectContainer;
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

public class AlembicBlockEntity extends BlockEntity implements IAspectContainer, IEssentiaTransport {
    public static final int CAPACITY = 128;

    private Aspect aspect;
    private Aspect aspectFilter;
    private int amount;

    public AlembicBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ALEMBIC.get(), pos, state);
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

    /** Push 1 unit of aspect into the alembic stack above the given smelter pos. */
    public static boolean processAlembics(Level level, BlockPos smelterPos, Aspect aspect) {
        int deep = 1;
        while (true) {
            BlockEntity te = level.getBlockEntity(smelterPos.above(deep));
            if (!(te instanceof AlembicBlockEntity alembic)) {
                break;
            }
            if (alembic.amount > 0 && alembic.aspect == aspect && alembic.addToContainer(aspect, 1) == 0) {
                return true;
            }
            deep++;
        }
        deep = 1;
        while (true) {
            BlockEntity te = level.getBlockEntity(smelterPos.above(deep));
            if (!(te instanceof AlembicBlockEntity alembic)) {
                return false;
            }
            if ((alembic.aspectFilter == null || alembic.aspectFilter == aspect)
                    && alembic.addToContainer(aspect, 1) == 0) {
                return true;
            }
            deep++;
        }
    }

    @Override
    public AspectList getAspects() {
        return aspect != null ? new AspectList().add(aspect, amount) : new AspectList();
    }

    @Override
    public void setAspects(AspectList aspects) {
    }

    @Override
    public boolean doesContainerAccept(Aspect tag) {
        return aspectFilter == null || aspectFilter == tag;
    }

    @Override
    public int addToContainer(Aspect tt, int am) {
        if (aspectFilter != null && tt != aspectFilter) {
            return am;
        }
        if ((amount < CAPACITY && tt == aspect) || amount == 0) {
            aspect = tt;
            int added = Math.min(am, CAPACITY - amount);
            amount += added;
            am -= added;
            setChangedAndSync();
        }
        return am;
    }

    @Override
    public boolean takeFromContainer(Aspect tt, int am) {
        if (aspect != null && amount >= am && tt == aspect) {
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
    public boolean doesContainerContainAmount(Aspect tt, int am) {
        return amount >= am && tt == aspect;
    }

    @Override
    public int containerContains(Aspect tt) {
        return tt == aspect ? amount : 0;
    }

    @Override
    public boolean isConnectable(Direction face) {
        return face != Direction.DOWN;
    }

    @Override
    public boolean canInputFrom(Direction face) {
        return false;
    }

    @Override
    public boolean canOutputTo(Direction face) {
        return face != Direction.DOWN;
    }

    @Override
    public void setSuction(Aspect aspect, int amount) {
    }

    @Override
    public Aspect getSuctionType(Direction face) {
        return null;
    }

    @Override
    public int getSuctionAmount(Direction face) {
        return 0;
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
        return 0;
    }

    @Override
    public int getMinimumSuction() {
        return 0;
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
