package arcana.common.blockentities;

import arcana.api.aura.AuraHelper;
import arcana.api.capabilities.ArcanaCapabilities;
import arcana.api.casters.FocusPackage;
import arcana.common.items.casters.ItemFocus;
import arcana.common.menu.FocalManipulatorMenu;
import arcana.registry.ModBlockEntities;
import arcana.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class FocalManipulatorBlockEntity extends BlockEntity implements MenuProvider, Container {
    public static final int SLOT_FOCUS = 0;
    private final NonNullList<ItemStack> items = NonNullList.withSize(1, ItemStack.EMPTY);

    private String selectedMedium = FocusPackage.MEDIUM_TOUCH;
    private String selectedEffect = FocusPackage.EFFECT_FIRE;

    public FocalManipulatorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.FOCAL_MANIPULATOR.get(), pos, state);
    }

    public String getSelectedMedium() {
        return selectedMedium;
    }

    public String getSelectedEffect() {
        return selectedEffect;
    }

    public void setMedium(String medium) {
        this.selectedMedium = medium == null ? FocusPackage.MEDIUM_TOUCH : medium;
        setChanged();
    }

    public void setEffect(String effect) {
        this.selectedEffect = effect == null ? FocusPackage.EFFECT_FIRE : effect;
        setChanged();
    }

    public String selectionLabel() {
        String medium = FocusPackage.MEDIUM_PROJECTILE.equals(selectedMedium) ? "Projectile" : "Touch";
        String effect = switch (selectedEffect) {
            case FocusPackage.EFFECT_FROST -> "Frost";
            case FocusPackage.EFFECT_SHOCK -> "Shock";
            case FocusPackage.EFFECT_EARTH -> "Earth";
            case FocusPackage.EFFECT_HEAL -> "Heal";
            default -> "Fire";
        };
        return medium + " + " + effect;
    }

    public boolean applyCompose(Player player) {
        return applyPackage(player, FocusPackage.of(FocusPackage.ROOT, selectedMedium, selectedEffect));
    }

    public boolean applyPreset(Player player, String preset) {
        return applyPackage(player, FocusPackage.fromPreset(preset));
    }

    private boolean applyPackage(Player player, FocusPackage pkg) {
        if (level == null || level.isClientSide) {
            return false;
        }
        ItemStack focus = items.get(SLOT_FOCUS);
        if (focus.isEmpty() || !(focus.getItem() instanceof ItemFocus itemFocus)) {
            player.displayClientMessage(Component.literal("Insert a focus"), true);
            return false;
        }
        if (pkg.getComplexity() > itemFocus.getMaxComplexity()) {
            player.displayClientMessage(Component.literal("Complexity "
                    + pkg.getComplexity() + "/" + itemFocus.getMaxComplexity()), true);
            return false;
        }
        var knowledge = ArcanaCapabilities.getKnowledge(player);
        for (String node : pkg.getNodes()) {
            String req = FocusPackage.researchForNode(node);
            if (req == null || req.isEmpty()) {
                continue;
            }
            boolean ok = knowledge.isResearchKnown(req) || knowledge.isResearchComplete(req)
                    || ("BASEAUROMANCY".equals(req) && knowledge.isResearchComplete("FIRSTSTEPS"));
            if (!ok) {
                player.displayClientMessage(Component.translatable("arcana.cast.node_locked", req), true);
                return false;
            }
        }
        float cost = Math.max(5.0f, pkg.getVisCost());
        if (AuraHelper.getVis(level, worldPosition) < cost) {
            player.displayClientMessage(Component.literal("Not enough vis"), true);
            return false;
        }
        AuraHelper.drainVis(level, worldPosition, cost, false);
        ItemFocus.setPackage(focus, pkg);
        setChanged();
        player.displayClientMessage(Component.literal("Focus programmed: " + pkg.describe()), true);
        return true;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.arcana.focal_manipulator");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return new FocalManipulatorMenu(id, inv, this);
    }

    public void dropContents() {
        if (level != null) {
            Containers.dropContents(level, worldPosition, items);
        }
    }

    @Override
    public int getContainerSize() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return items.get(0).isEmpty();
    }

    @Override
    public ItemStack getItem(int slot) {
        return items.get(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        ItemStack stack = ContainerHelper.removeItem(items, slot, amount);
        if (!stack.isEmpty()) {
            setChanged();
        }
        return stack;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return ContainerHelper.takeItem(items, slot);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        items.set(slot, stack);
        if (stack.getCount() > getMaxStackSize()) {
            stack.setCount(getMaxStackSize());
        }
        setChanged();
    }

    @Override
    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    @Override
    public void clearContent() {
        items.clear();
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        ContainerHelper.saveAllItems(tag, items);
        tag.putString("SelectedMedium", selectedMedium);
        tag.putString("SelectedEffect", selectedEffect);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        items.clear();
        ContainerHelper.loadAllItems(tag, items);
        if (tag.contains("SelectedMedium")) {
            selectedMedium = tag.getString("SelectedMedium");
        }
        if (tag.contains("SelectedEffect")) {
            selectedEffect = tag.getString("SelectedEffect");
        }
    }

    public boolean isInRange(Player player) {
        return stillValid(player) && level != null
                && level.getBlockState(worldPosition).is(ModBlocks.FOCAL_MANIPULATOR.get());
    }
}
