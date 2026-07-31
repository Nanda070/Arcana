package arcana.common.blockentities;

import arcana.api.aura.AuraHelper;
import arcana.common.menu.ArcaneWorkbenchMenu;
import arcana.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

public class ArcaneWorkbenchBlockEntity extends BlockEntity implements MenuProvider {
    /** 0-8 craft grid, 9-14 crystals (aer,terra,ignis,aqua,ordo,perditio) */
    public static final int SLOTS = 15;

    private final ItemStackHandler items = new ItemStackHandler(SLOTS) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    private final LazyOptional<IItemHandler> itemHandler = LazyOptional.of(() -> items);

    public ArcaneWorkbenchBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ARCANE_WORKBENCH.get(), pos, state);
    }

    public ItemStackHandler getItems() {
        return items;
    }

    public int getAvailableVis() {
        if (level == null || level.isClientSide()) {
            return 0;
        }
        return (int) AuraHelper.getVis(level, worldPosition);
    }

    public void spendVis(int amount) {
        if (level != null && !level.isClientSide()) {
            AuraHelper.drainVis(level, worldPosition, amount, false);
        }
    }

    public void drops() {
        if (level != null) {
            Containers.dropContents(level, worldPosition, net.minecraft.core.NonNullList.of(
                    net.minecraft.world.item.ItemStack.EMPTY,
                    java.util.stream.IntStream.range(0, items.getSlots())
                            .mapToObj(items::getStackInSlot)
                            .toArray(net.minecraft.world.item.ItemStack[]::new)));
        }
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.arcana.arcane_workbench");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return new ArcaneWorkbenchMenu(id, inv, this);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("Items", items.serializeNBT());
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("Items")) {
            items.deserializeNBT(tag.getCompound("Items"));
        }
    }

    @Override
    public <T> LazyOptional<T> getCapability(net.minecraftforge.common.capabilities.Capability<T> cap,
                                             @Nullable net.minecraft.core.Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return itemHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        itemHandler.invalidate();
    }
}
