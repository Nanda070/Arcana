package arcana.common.menu;

import arcana.api.capabilities.ArcanaCapabilities;
import arcana.api.capabilities.IPlayerKnowledge;
import arcana.api.research.ResearchCategories;
import arcana.api.research.ResearchCategory;
import arcana.common.blockentities.ResearchTableBlockEntity;
import arcana.common.lib.research.ResearchManager;
import arcana.common.network.PacketHandler;
import arcana.registry.ModBlocks;
import arcana.registry.ModItems;
import arcana.registry.ModMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

public class ResearchTableMenu extends AbstractContainerMenu {
    public static final int BTN_STUDY = 0;
    public static final int BTN_NOTE = 1;

    private final ResearchTableBlockEntity table;
    private final Player player;
    private final ContainerLevelAccess access;
    private final DataSlot observationSlot = DataSlot.standalone();
    private final DataSlot theorySlot = DataSlot.standalone();

    public ResearchTableMenu(int id, Inventory inv, FriendlyByteBuf buf) {
        this(id, inv, getBlockEntity(inv, buf));
    }

    public ResearchTableMenu(int id, Inventory inv, ResearchTableBlockEntity table) {
        super(ModMenus.RESEARCH_TABLE.get(), id);
        this.table = table;
        this.player = inv.player;
        this.access = ContainerLevelAccess.create(table.getLevel(), table.getBlockPos());

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(inv, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(inv, col, 8 + col * 18, 142));
        }

        addDataSlot(observationSlot);
        addDataSlot(theorySlot);
        syncKnowledge();
    }

    private static ResearchTableBlockEntity getBlockEntity(Inventory inv, FriendlyByteBuf buf) {
        BlockEntity be = inv.player.level().getBlockEntity(buf.readBlockPos());
        if (be instanceof ResearchTableBlockEntity table) {
            return table;
        }
        throw new IllegalStateException("Research table missing at menu open");
    }

    private ResearchCategory basics() {
        return ResearchCategories.getResearchCategory("BASICS");
    }

    private void syncKnowledge() {
        IPlayerKnowledge knowledge = ArcanaCapabilities.getKnowledge(player);
        ResearchCategory cat = basics();
        observationSlot.set(knowledge.getKnowledgeRaw(IPlayerKnowledge.EnumKnowledgeType.OBSERVATION, cat));
        theorySlot.set(knowledge.getKnowledgeRaw(IPlayerKnowledge.EnumKnowledgeType.THEORY, cat));
    }

    @Override
    public void broadcastChanges() {
        syncKnowledge();
        super.broadcastChanges();
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (player.level().isClientSide()) {
            return true;
        }
        IPlayerKnowledge knowledge = ArcanaCapabilities.getKnowledge(player);
        ResearchCategory cat = basics();
        boolean changed = false;
        if (id == BTN_STUDY) {
            if (knowledge.getKnowledgeRaw(IPlayerKnowledge.EnumKnowledgeType.OBSERVATION, cat) < 16) {
                player.displayClientMessage(Component.translatable("arcana.research_table.need_observation"), true);
                return true;
            }
            if (!knowledge.addKnowledge(IPlayerKnowledge.EnumKnowledgeType.OBSERVATION, cat, -16)) {
                return true;
            }
            if (!knowledge.isResearchComplete("THEORYRESEARCH")) {
                ResearchManager.progressResearch(player, "THEORYRESEARCH", true);
                player.displayClientMessage(Component.translatable("arcana.research_table.theory_progress"), true);
            } else {
                knowledge.addKnowledge(IPlayerKnowledge.EnumKnowledgeType.THEORY, cat, 16);
                player.displayClientMessage(Component.translatable("arcana.research_table.theory_gained"), true);
            }
            changed = true;
        } else if (id == BTN_NOTE) {
            if (!consumeArcaneNote(player)) {
                player.displayClientMessage(Component.translatable("arcana.research_table.need_note"), true);
                return true;
            }
            boolean granted = knowledge.addKnowledge(IPlayerKnowledge.EnumKnowledgeType.OBSERVATION, cat, 16);
            if (!granted) {
                ResearchManager.progressResearch(player, "THEORYRESEARCH", true);
            } else {
                player.displayClientMessage(Component.translatable("arcana.note.observation"), true);
            }
            changed = true;
        }
        if (changed && player instanceof ServerPlayer serverPlayer) {
            PacketHandler.syncKnowledge(serverPlayer);
            syncKnowledge();
            broadcastChanges();
        }
        return true;
    }

    private static boolean consumeArcaneNote(Player player) {
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.is(ModItems.ARCANE_NOTE.get())) {
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
                return true;
            }
        }
        return player.getAbilities().instabuild;
    }

    public int getObservationPoints() {
        return observationSlot.get();
    }

    public int getTheoryPoints() {
        return theorySlot.get();
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(access, player, ModBlocks.RESEARCH_TABLE.get());
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack copy = ItemStack.EMPTY;
        Slot slot = slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack stack = slot.getItem();
            copy = stack.copy();
            if (index < 27) {
                if (!moveItemStackTo(stack, 27, 36, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!moveItemStackTo(stack, 0, 27, false)) {
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
}
