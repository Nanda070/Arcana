package arcana.common.menu;

import arcana.api.casters.FocusPackage;
import arcana.common.blockentities.FocalManipulatorBlockEntity;
import arcana.common.items.casters.ItemFocus;
import arcana.registry.ModMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

public class FocalManipulatorMenu extends AbstractContainerMenu {
    public static final int BTN_TOUCH = 0;
    public static final int BTN_PROJECTILE = 1;
    public static final int BTN_FIRE = 2;
    public static final int BTN_FROST = 3;
    public static final int BTN_COMPOSE = 4;
    public static final int BTN_SHOCK = 5;
    public static final int BTN_EARTH = 6;
    public static final int BTN_HEAL = 7;
    public static final int BTN_BOLT = 8;
    public static final int BTN_CLOUD = 9;
    public static final int BTN_AIR = 10;
    public static final int BTN_BREAK = 11;
    public static final int BTN_CURSE = 12;
    public static final int BTN_EXCHANGE = 13;
    public static final int BTN_FLUX = 14;
    public static final int BTN_RIFT = 15;
    public static final int BTN_SCATTER = 16;

    private final FocalManipulatorBlockEntity be;
    private final DataSlot complexityUsed = DataSlot.standalone();
    private final DataSlot complexityMax = DataSlot.standalone();

    public FocalManipulatorMenu(int id, Inventory inv, FriendlyByteBuf buf) {
        this(id, inv, getBe(inv, buf));
    }

    public FocalManipulatorMenu(int id, Inventory inv, FocalManipulatorBlockEntity be) {
        super(ModMenus.FOCAL_MANIPULATOR.get(), id);
        this.be = be;
        addSlot(new Slot(be, FocalManipulatorBlockEntity.SLOT_FOCUS, 80, 20) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.getItem() instanceof ItemFocus;
            }

            @Override
            public void setChanged() {
                super.setChanged();
                syncComplexity();
            }
        });
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(inv, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(inv, col, 8 + col * 18, 142));
        }
        addDataSlot(complexityUsed);
        addDataSlot(complexityMax);
        syncComplexity();
    }

    private static FocalManipulatorBlockEntity getBe(Inventory inv, FriendlyByteBuf buf) {
        BlockEntity blockEntity = inv.player.level().getBlockEntity(buf.readBlockPos());
        if (blockEntity instanceof FocalManipulatorBlockEntity manipulator) {
            return manipulator;
        }
        throw new IllegalStateException("Focal manipulator missing");
    }

    private void syncComplexity() {
        FocusPackage pending = be.pendingPackage();
        complexityUsed.set(pending.getComplexity());
        ItemStack focus = be.getItem(FocalManipulatorBlockEntity.SLOT_FOCUS);
        if (!focus.isEmpty() && focus.getItem() instanceof ItemFocus itemFocus) {
            complexityMax.set(itemFocus.getMaxComplexity());
        } else {
            complexityMax.set(0);
        }
    }

    @Override
    public void broadcastChanges() {
        syncComplexity();
        super.broadcastChanges();
    }

    public FocalManipulatorBlockEntity getBlockEntity() {
        return be;
    }

    public int getComplexityUsed() {
        return complexityUsed.get();
    }

    public int getComplexityMax() {
        return complexityMax.get();
    }

    public FocusPackage getFocusPackage() {
        ItemStack focus = be.getItem(FocalManipulatorBlockEntity.SLOT_FOCUS);
        if (focus.isEmpty() || !(focus.getItem() instanceof ItemFocus)) {
            return null;
        }
        return ItemFocus.getPackage(focus);
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        boolean ok = switch (id) {
            case BTN_TOUCH -> {
                be.setMedium(FocusPackage.MEDIUM_TOUCH);
                yield true;
            }
            case BTN_PROJECTILE -> {
                be.setMedium(FocusPackage.MEDIUM_PROJECTILE);
                yield true;
            }
            case BTN_BOLT -> {
                be.setMedium(FocusPackage.MEDIUM_BOLT);
                yield true;
            }
            case BTN_CLOUD -> {
                be.setMedium(FocusPackage.MEDIUM_CLOUD);
                yield true;
            }
            case BTN_FIRE -> {
                be.toggleEffect(FocusPackage.EFFECT_FIRE);
                yield true;
            }
            case BTN_FROST -> {
                be.toggleEffect(FocusPackage.EFFECT_FROST);
                yield true;
            }
            case BTN_SHOCK -> {
                be.toggleEffect(FocusPackage.EFFECT_SHOCK);
                yield true;
            }
            case BTN_EARTH -> {
                be.toggleEffect(FocusPackage.EFFECT_EARTH);
                yield true;
            }
            case BTN_HEAL -> {
                be.toggleEffect(FocusPackage.EFFECT_HEAL);
                yield true;
            }
            case BTN_AIR -> {
                be.toggleEffect(FocusPackage.EFFECT_AIR);
                yield true;
            }
            case BTN_BREAK -> {
                be.toggleEffect(FocusPackage.EFFECT_BREAK);
                yield true;
            }
            case BTN_CURSE -> {
                be.toggleEffect(FocusPackage.EFFECT_CURSE);
                yield true;
            }
            case BTN_EXCHANGE -> {
                be.toggleEffect(FocusPackage.EFFECT_EXCHANGE);
                yield true;
            }
            case BTN_FLUX -> {
                be.toggleEffect(FocusPackage.EFFECT_FLUX);
                yield true;
            }
            case BTN_RIFT -> {
                be.toggleEffect(FocusPackage.EFFECT_RIFT);
                yield true;
            }
            case BTN_SCATTER -> {
                be.toggleScatter();
                yield true;
            }
            case BTN_COMPOSE -> be.applyCompose(player);
            default -> false;
        };
        if (ok) {
            syncComplexity();
            broadcastChanges();
        }
        return ok;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack stack = slot.getItem();
            result = stack.copy();
            if (index == 0) {
                if (!moveItemStackTo(stack, 1, slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!moveItemStackTo(stack, 0, 1, false)) {
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
        return be.stillValid(player);
    }
}
