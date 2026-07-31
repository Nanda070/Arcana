package arcana.common.blockentities;

import arcana.api.aspects.Aspect;
import arcana.api.aspects.AspectHelper;
import arcana.api.aspects.AspectList;
import arcana.api.aura.AuraHelper;
import arcana.common.menu.EssentiaSmelterMenu;
import arcana.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeHooks;
import org.jetbrains.annotations.Nullable;

public class EssentiaSmelterBlockEntity extends BlockEntity implements MenuProvider, Container {
    public static final int SLOT_INPUT = 0;
    public static final int SLOT_FUEL = 1;
    public static final int MAX_VIS = 256;
    public static final int SLOTS = 2;

    private final SimpleContainer items = new SimpleContainer(SLOTS) {
        @Override
        public void setChanged() {
            EssentiaSmelterBlockEntity.this.setChanged();
        }
    };

    private final AspectList aspects = new AspectList();
    private int burnTime;
    private int burnTimeTotal;
    private int cookTime;
    private int cookTimeTotal = 100;
    private int pushCooldown;

    private final ContainerData data = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> burnTime;
                case 1 -> burnTimeTotal;
                case 2 -> cookTime;
                case 3 -> cookTimeTotal;
                case 4 -> aspects.visSize();
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> burnTime = value;
                case 1 -> burnTimeTotal = value;
                case 2 -> cookTime = value;
                case 3 -> cookTimeTotal = value;
                default -> {
                }
            }
        }

        @Override
        public int getCount() {
            return 5;
        }
    };

    public EssentiaSmelterBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ESSENTIA_SMELTER.get(), pos, state);
    }

    public AspectList getAspects() {
        return aspects.copy();
    }

    public ContainerData getData() {
        return data;
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, EssentiaSmelterBlockEntity smelter) {
        boolean lit = smelter.burnTime > 0;
        boolean dirty = false;

        if (smelter.burnTime > 0) {
            smelter.burnTime--;
        }

        if (++smelter.pushCooldown >= 10) {
            smelter.pushCooldown = 0;
            if (smelter.aspects.size() > 0) {
                for (Aspect aspect : smelter.aspects.getAspectsSortedByAmount()) {
                    if (smelter.aspects.getAmount(aspect) > 0
                            && AlembicBlockEntity.processAlembics(level, pos, aspect)) {
                        smelter.aspects.remove(aspect, 1);
                        dirty = true;
                        break;
                    }
                }
            }
        }

        ItemStack fuel = smelter.items.getItem(SLOT_FUEL);
        if (smelter.burnTime == 0 && smelter.canSmelt()) {
            int burn = ForgeHooks.getBurnTime(fuel, RecipeType.SMELTING);
            if (burn > 0) {
                smelter.burnTime = burn;
                smelter.burnTimeTotal = burn;
                fuel.shrink(1);
                if (fuel.isEmpty()) {
                    smelter.items.setItem(SLOT_FUEL, ItemStack.EMPTY);
                }
                dirty = true;
            }
        }

        if (smelter.burnTime > 0 && smelter.canSmelt()) {
            smelter.cookTime++;
            if (smelter.cookTime >= smelter.cookTimeTotal) {
                smelter.cookTime = 0;
                smelter.smeltItem();
                dirty = true;
            }
        } else {
            smelter.cookTime = 0;
        }

        boolean nowLit = smelter.burnTime > 0;
        if (lit != nowLit) {
            level.setBlock(pos, state.setValue(arcana.common.blocks.EssentiaSmelterBlock.LIT, nowLit), 3);
            dirty = true;
        }
        if (dirty) {
            smelter.setChangedAndSync();
        }
    }

    private boolean canSmelt() {
        ItemStack input = items.getItem(SLOT_INPUT);
        if (input.isEmpty()) {
            return false;
        }
        AspectList al = AspectHelper.getObjectAspects(input);
        if (al == null || al.size() == 0) {
            return false;
        }
        int vs = al.visSize();
        if (vs > MAX_VIS - aspects.visSize()) {
            return false;
        }
        cookTimeTotal = Math.max(40, vs * 8);
        return true;
    }

    private void smeltItem() {
        if (!canSmelt()) {
            return;
        }
        ItemStack input = items.getItem(SLOT_INPUT);
        AspectList al = AspectHelper.getObjectAspects(input).copy();
        float efficiency = 0.85f;
        int flux = 0;
        for (Aspect a : al.getAspects()) {
            int amount = al.getAmount(a);
            int kept = 0;
            for (int i = 0; i < amount; i++) {
                if (level != null && level.random.nextFloat() <= efficiency) {
                    kept++;
                } else {
                    flux++;
                }
            }
            if (kept > 0) {
                aspects.add(a, kept);
            }
        }
        if (flux > 0 && level != null) {
            AuraHelper.polluteAura(level, worldPosition, Math.min(flux, 3), true);
        }
        input.shrink(1);
        if (input.isEmpty()) {
            items.setItem(SLOT_INPUT, ItemStack.EMPTY);
        }
    }

    private void setChangedAndSync() {
        setChanged();
        if (level != null && !level.isClientSide()) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.arcana.essentia_smelter");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return new EssentiaSmelterMenu(id, inv, this);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("BurnTime", burnTime);
        tag.putInt("BurnTimeTotal", burnTimeTotal);
        tag.putInt("CookTime", cookTime);
        tag.putInt("CookTimeTotal", cookTimeTotal);
        aspects.writeToNBT(tag);
        CompoundTag itemsTag = new CompoundTag();
        for (int i = 0; i < SLOTS; i++) {
            if (!items.getItem(i).isEmpty()) {
                itemsTag.put("S" + i, items.getItem(i).save(new CompoundTag()));
            }
        }
        tag.put("Items", itemsTag);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        burnTime = tag.getInt("BurnTime");
        burnTimeTotal = tag.getInt("BurnTimeTotal");
        cookTime = tag.getInt("CookTime");
        cookTimeTotal = Math.max(1, tag.getInt("CookTimeTotal"));
        aspects.aspects.clear();
        aspects.readFromNBT(tag);
        CompoundTag itemsTag = tag.getCompound("Items");
        for (int i = 0; i < SLOTS; i++) {
            if (itemsTag.contains("S" + i)) {
                items.setItem(i, ItemStack.of(itemsTag.getCompound("S" + i)));
            } else {
                items.setItem(i, ItemStack.EMPTY);
            }
        }
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

    // Container
    @Override
    public int getContainerSize() {
        return SLOTS;
    }

    @Override
    public boolean isEmpty() {
        return items.isEmpty();
    }

    @Override
    public ItemStack getItem(int slot) {
        return items.getItem(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        return items.removeItem(slot, amount);
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return items.removeItemNoUpdate(slot);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        items.setItem(slot, stack);
    }

    @Override
    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    @Override
    public void clearContent() {
        items.clearContent();
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        if (slot == SLOT_FUEL) {
            return ForgeHooks.getBurnTime(stack, RecipeType.SMELTING) > 0;
        }
        AspectList al = AspectHelper.getObjectAspects(stack);
        return al != null && al.size() > 0;
    }
}
