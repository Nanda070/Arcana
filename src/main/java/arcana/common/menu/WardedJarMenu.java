package arcana.common.menu;

import arcana.common.blockentities.WardedJarBlockEntity;
import arcana.registry.ModBlocks;
import arcana.registry.ModMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

public class WardedJarMenu extends AbstractContainerMenu {
    private final WardedJarBlockEntity jar;
    private final ContainerData data;
    private final ContainerLevelAccess access;

    public WardedJarMenu(int id, Inventory inv, FriendlyByteBuf buf) {
        this(id, inv, getJar(inv, buf));
    }

    public WardedJarMenu(int id, Inventory inv, WardedJarBlockEntity jar) {
        this(id, inv, jar, jar.getData());
    }

    public WardedJarMenu(int id, Inventory inv, WardedJarBlockEntity jar, ContainerData data) {
        super(ModMenus.WARDED_JAR.get(), id);
        this.jar = jar;
        this.data = data;
        this.access = ContainerLevelAccess.create(jar.getLevel(), jar.getBlockPos());
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new net.minecraft.world.inventory.Slot(inv, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            addSlot(new net.minecraft.world.inventory.Slot(inv, col, 8 + col * 18, 142));
        }
        addDataSlots(data);
    }

    private static WardedJarBlockEntity getJar(Inventory inv, FriendlyByteBuf buf) {
        BlockEntity be = inv.player.level().getBlockEntity(buf.readBlockPos());
        if (be instanceof WardedJarBlockEntity jar) {
            return jar;
        }
        throw new IllegalStateException("Warded jar missing at pos");
    }

    public WardedJarBlockEntity getJar() {
        return jar;
    }

    public int getAmount() {
        return data.get(0);
    }

    public int getCapacity() {
        return Math.max(1, data.get(1));
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(access, player, ModBlocks.WARDED_JAR.get());
    }
}
