package arcana.common.menu;

import arcana.common.blockentities.ArcaneWorkbenchBlockEntity;
import arcana.common.crafting.ArcaneShapedRecipe;
import arcana.registry.ModBlocks;
import arcana.registry.ModMenus;
import arcana.registry.ModRecipes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.SlotItemHandler;

public class ArcaneWorkbenchMenu extends AbstractContainerMenu implements ArcaneShapedRecipe.ArcaneWorkbenchContainer {
    private final ArcaneWorkbenchBlockEntity workbench;
    private final Player player;
    private final ContainerLevelAccess access;
    private final ResultContainer result = new ResultContainer();
    private final DataSlot visSlot = DataSlot.standalone();
    private ArcaneShapedRecipe currentRecipe;

    public ArcaneWorkbenchMenu(int id, Inventory inv, FriendlyByteBuf buf) {
        this(id, inv, getBlockEntity(inv, buf));
    }

    public ArcaneWorkbenchMenu(int id, Inventory inv, ArcaneWorkbenchBlockEntity workbench) {
        super(ModMenus.ARCANE_WORKBENCH.get(), id);
        this.workbench = workbench;
        this.player = inv.player;
        this.access = ContainerLevelAccess.create(workbench.getLevel(), workbench.getBlockPos());

        addSlot(new ResultSlot(0, 142, 35));

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                addSlot(new SlotItemHandler(workbench.getItems(), col + row * 3, 30 + col * 18, 17 + row * 18) {
                    @Override
                    public void setChanged() {
                        super.setChanged();
                        ArcaneWorkbenchMenu.this.slotsChanged(null);
                    }
                });
            }
        }

        int[] cx = {8, 8, 8, 152, 152, 152};
        int[] cy = {17, 35, 53, 17, 35, 53};
        for (int i = 0; i < 6; i++) {
            addSlot(new SlotItemHandler(workbench.getItems(), 9 + i, cx[i], cy[i]) {
                @Override
                public void setChanged() {
                    super.setChanged();
                    ArcaneWorkbenchMenu.this.slotsChanged(null);
                }
            });
        }

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(inv, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(inv, col, 8 + col * 18, 142));
        }

        addDataSlot(visSlot);
        slotsChanged(null);
    }

    private static ArcaneWorkbenchBlockEntity getBlockEntity(Inventory inv, FriendlyByteBuf buf) {
        BlockEntity be = inv.player.level().getBlockEntity(buf.readBlockPos());
        if (be instanceof ArcaneWorkbenchBlockEntity workbench) {
            return workbench;
        }
        throw new IllegalStateException("Arcane workbench missing at menu open");
    }

    @Override
    public void slotsChanged(Container container) {
        if (workbench.getLevel() == null) {
            return;
        }
        visSlot.set(workbench.getAvailableVis());
        currentRecipe = null;
        ItemStack out = ItemStack.EMPTY;
        var optional = workbench.getLevel().getRecipeManager()
                .getRecipeFor(ModRecipes.ARCANE_SHAPED_TYPE.get(), this, workbench.getLevel());
        if (optional.isPresent()) {
            ArcaneShapedRecipe recipe = optional.get();
            if (recipe.canPlayerCraft(player, visSlot.get())) {
                currentRecipe = recipe;
                out = recipe.assemble(this, workbench.getLevel().registryAccess());
            }
        }
        result.setItem(0, out);
        broadcastChanges();
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(access, player, ModBlocks.ARCANE_WORKBENCH.get());
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack copy = ItemStack.EMPTY;
        Slot slot = slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack stack = slot.getItem();
            copy = stack.copy();
            if (index == 0) {
                if (!moveItemStackTo(stack, 16, 52, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (index < 16) {
                if (!moveItemStackTo(stack, 16, 52, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!moveItemStackTo(stack, 1, 16, false)) {
                return ItemStack.EMPTY;
            }
            if (stack.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return copy;
    }

    @Override
    public ItemStack getCraftItem(int index) {
        return workbench.getItems().getStackInSlot(index);
    }

    @Override
    public ItemStack getCrystalItem(int index) {
        return workbench.getItems().getStackInSlot(9 + index);
    }

    @Override
    public int getContainerSize() {
        return ArcaneWorkbenchBlockEntity.SLOTS;
    }

    @Override
    public boolean isEmpty() {
        for (int i = 0; i < getContainerSize(); i++) {
            if (!getItem(i).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getItem(int index) {
        return workbench.getItems().getStackInSlot(index);
    }

    @Override
    public ItemStack removeItem(int index, int count) {
        return workbench.getItems().extractItem(index, count, false);
    }

    @Override
    public ItemStack removeItemNoUpdate(int index) {
        ItemStack stack = workbench.getItems().getStackInSlot(index);
        workbench.getItems().setStackInSlot(index, ItemStack.EMPTY);
        return stack;
    }

    @Override
    public void setItem(int index, ItemStack stack) {
        workbench.getItems().setStackInSlot(index, stack);
    }

    @Override
    public void setChanged() {
        workbench.setChanged();
        slotsChanged(null);
    }

    @Override
    public void clearContent() {
        for (int i = 0; i < getContainerSize(); i++) {
            setItem(i, ItemStack.EMPTY);
        }
    }

    public int getVis() {
        return visSlot.get();
    }

    private class ResultSlot extends Slot {
        public ResultSlot(int index, int x, int y) {
            super(result, index, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return false;
        }

        @Override
        public void onTake(Player player, ItemStack stack) {
            if (currentRecipe != null && !player.level().isClientSide()) {
                currentRecipe.consumeGrid(workbench);
                currentRecipe.consumeCrystals(workbench);
                workbench.spendVis(currentRecipe.getVis());
            }
            slotsChanged(null);
            super.onTake(player, stack);
        }
    }
}
