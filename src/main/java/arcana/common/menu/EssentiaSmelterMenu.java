package arcana.common.menu;

import arcana.common.blockentities.EssentiaSmelterBlockEntity;
import arcana.registry.ModMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

public class EssentiaSmelterMenu extends AbstractContainerMenu {
    private final EssentiaSmelterBlockEntity smelter;
    private final ContainerData data;

    public EssentiaSmelterMenu(int id, Inventory inv, FriendlyByteBuf buf) {
        this(id, inv, getBlockEntity(inv, buf));
    }

    public EssentiaSmelterMenu(int id, Inventory inv, EssentiaSmelterBlockEntity smelter) {
        this(id, inv, smelter, smelter.getData());
    }

    public EssentiaSmelterMenu(int id, Inventory inv, EssentiaSmelterBlockEntity smelter, ContainerData data) {
        super(ModMenus.ESSENTIA_SMELTER.get(), id);
        this.smelter = smelter;
        this.data = data;
        checkContainerSize(smelter, EssentiaSmelterBlockEntity.SLOTS);
        addSlot(new Slot(smelter, EssentiaSmelterBlockEntity.SLOT_INPUT, 56, 17));
        addSlot(new Slot(smelter, EssentiaSmelterBlockEntity.SLOT_FUEL, 56, 53));
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(inv, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(inv, col, 8 + col * 18, 142));
        }
        addDataSlots(data);
    }

    private static EssentiaSmelterBlockEntity getBlockEntity(Inventory inv, FriendlyByteBuf buf) {
        BlockEntity be = inv.player.level().getBlockEntity(buf.readBlockPos());
        if (be instanceof EssentiaSmelterBlockEntity smelter) {
            return smelter;
        }
        throw new IllegalStateException("Essentia smelter missing at pos");
    }

    public int getBurnTime() {
        return data.get(0);
    }

    public int getBurnTimeTotal() {
        return Math.max(1, data.get(1));
    }

    public int getCookTime() {
        return data.get(2);
    }

    public int getCookTimeTotal() {
        return Math.max(1, data.get(3));
    }

    public int getVisStored() {
        return data.get(4);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack stack = slot.getItem();
            result = stack.copy();
            if (index < EssentiaSmelterBlockEntity.SLOTS) {
                if (!moveItemStackTo(stack, EssentiaSmelterBlockEntity.SLOTS, slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!moveItemStackTo(stack, 0, EssentiaSmelterBlockEntity.SLOTS, false)) {
                return ItemStack.EMPTY;
            }
            if (stack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return result;
    }

    @Override
    public boolean stillValid(Player player) {
        return smelter.stillValid(player);
    }
}
