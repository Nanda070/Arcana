package arcana.common.menu;

import arcana.api.capabilities.ArcanaCapabilities;
import arcana.api.capabilities.IPlayerKnowledge;
import arcana.api.research.ResearchCategories;
import arcana.api.research.ResearchCategory;
import arcana.api.research.theorycraft.ResearchTableData;
import arcana.api.research.theorycraft.TheorycraftCard;
import arcana.common.blockentities.ResearchTableBlockEntity;
import arcana.common.lib.research.ResearchManager;
import arcana.common.network.PacketHandler;
import arcana.registry.ModBlocks;
import arcana.registry.ModItems;
import arcana.registry.ModMenus;
import java.util.HashMap;
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
    /** Secondary: consume note for observation (legacy). */
    public static final int BTN_NOTE = 1;
    /** Draw 2 cards (3 if bonusDraws available — pass as 3). */
    public static final int BTN_DRAW = 2;
    public static final int BTN_DRAW_BONUS = 3;
    public static final int BTN_CARD_0 = 4;
    public static final int BTN_CARD_1 = 5;
    public static final int BTN_CARD_2 = 6;
    public static final int BTN_COMPLETE = 7;
    public static final int BTN_ABORT = 9;
    public static final int BTN_START = 10;

    private final ResearchTableBlockEntity table;
    private final Player player;
    private final ContainerLevelAccess access;
    private final DataSlot inspirationSlot = DataSlot.standalone();
    private final DataSlot inspirationStartSlot = DataSlot.standalone();
    private final DataSlot bonusDrawsSlot = DataSlot.standalone();
    private final DataSlot cardCountSlot = DataSlot.standalone();
    private final DataSlot hasSessionSlot = DataSlot.standalone();
    private final DataSlot completeSlot = DataSlot.standalone();

    private static final HashMap<Integer, Long> ANTI_SPAM = new HashMap<>();

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
                addSlot(new Slot(inv, col + row * 9 + 9, 8 + col * 18, 104 + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(inv, col, 8 + col * 18, 162));
        }

        addDataSlot(inspirationSlot);
        addDataSlot(inspirationStartSlot);
        addDataSlot(bonusDrawsSlot);
        addDataSlot(cardCountSlot);
        addDataSlot(hasSessionSlot);
        addDataSlot(completeSlot);
        syncData();
    }

    private static ResearchTableBlockEntity getBlockEntity(Inventory inv, FriendlyByteBuf buf) {
        BlockEntity be = inv.player.level().getBlockEntity(buf.readBlockPos());
        if (be instanceof ResearchTableBlockEntity table) {
            return table;
        }
        throw new IllegalStateException("Research table missing at menu open");
    }

    public ResearchTableBlockEntity getTable() {
        return table;
    }

    private void syncData() {
        ResearchTableData d = table.data;
        hasSessionSlot.set(d != null ? 1 : 0);
        if (d != null) {
            inspirationSlot.set(d.inspiration);
            inspirationStartSlot.set(d.inspirationStart);
            bonusDrawsSlot.set(d.bonusDraws);
            cardCountSlot.set(d.cardChoices.size());
            completeSlot.set(d.isComplete() ? 1 : 0);
        } else {
            inspirationSlot.set(0);
            inspirationStartSlot.set(0);
            bonusDrawsSlot.set(0);
            cardCountSlot.set(0);
            completeSlot.set(0);
        }
    }

    @Override
    public void broadcastChanges() {
        syncData();
        super.broadcastChanges();
    }

    public int getInspiration() {
        return inspirationSlot.get();
    }

    public int getInspirationStart() {
        return inspirationStartSlot.get();
    }

    public int getBonusDraws() {
        return bonusDrawsSlot.get();
    }

    public int getCardCount() {
        return cardCountSlot.get();
    }

    public boolean hasSession() {
        return hasSessionSlot.get() != 0;
    }

    public boolean isTheoryComplete() {
        return completeSlot.get() != 0;
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (player.level().isClientSide()) {
            return true;
        }

        if (id == BTN_NOTE) {
            return handleNote(player);
        }

        if (id == BTN_START) {
            if (table.data == null) {
                table.startNewTheory(player, table.checkSurroundingAids());
                syncAndNotify(player);
            }
            return true;
        }

        if (id == BTN_ABORT) {
            if (table.data != null && !table.data.isComplete()) {
                table.data = null;
                table.setChanged();
                table.syncToClient();
                syncAndNotify(player);
            }
            return true;
        }

        if (id == BTN_COMPLETE) {
            if (table.data != null && table.data.isComplete()) {
                table.finishTheory(player);
                syncAndNotify(player);
                player.displayClientMessage(Component.translatable("arcana.research_table.theory_complete"), true);
            }
            return true;
        }

        if (id == BTN_DRAW || id == BTN_DRAW_BONUS) {
            if (table.data == null) {
                table.startNewTheory(player, table.checkSurroundingAids());
            }
            if (table.data != null && !table.data.isComplete()) {
                int draw = id == BTN_DRAW_BONUS ? 3 : 2;
                if (id == BTN_DRAW && table.data.bonusDraws > 0) {
                    draw = 3;
                }
                table.data.drawCards(draw, player);
                table.setChanged();
                table.syncToClient();
                syncAndNotify(player);
            }
            return true;
        }

        if (id >= BTN_CARD_0 && id <= BTN_CARD_2) {
            return playCard(player, id - BTN_CARD_0);
        }

        return true;
    }

    private boolean playCard(Player player, int index) {
        long now = System.currentTimeMillis();
        long prev = ANTI_SPAM.getOrDefault(player.getId(), 0L);
        if (now - prev < 200L) {
            return false;
        }
        ANTI_SPAM.put(player.getId(), now);

        if (table.data == null || index < 0 || index >= table.data.cardChoices.size()) {
            return false;
        }
        try {
            TheorycraftCard card = table.data.cardChoices.get(index).card;
            if (card.getRequiredItems() != null) {
                for (ItemStack stack : card.getRequiredItems()) {
                    if (stack != null && !stack.isEmpty() && !playerHas(player, stack)) {
                        player.displayClientMessage(Component.translatable("arcana.research_table.need_items"), true);
                        return false;
                    }
                }
                boolean[] consumed = card.getRequiredItemsConsumed();
                if (consumed != null && consumed.length == card.getRequiredItems().length) {
                    for (int a = 0; a < card.getRequiredItems().length; a++) {
                        if (consumed[a] && card.getRequiredItems()[a] != null && !card.getRequiredItems()[a].isEmpty()) {
                            consumeItem(player, card.getRequiredItems()[a]);
                        }
                    }
                }
            }
            if (card.activate(player, table.data)) {
                table.data.cardChoices.get(index).selected = true;
                table.data.lastDraw = table.data.cardChoices.get(index);
                table.data.addInspiration(-card.getInspirationCost());
                table.data.placedCards++;
                table.data.cardChoices.clear();
                table.setChanged();
                table.syncToClient();
                syncAndNotify(player);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean handleNote(Player player) {
        IPlayerKnowledge knowledge = ArcanaCapabilities.getKnowledge(player);
        ResearchCategory cat = ResearchCategories.getResearchCategory("BASICS");
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
        syncAndNotify(player);
        return true;
    }

    private void syncAndNotify(Player player) {
        syncData();
        if (player instanceof ServerPlayer serverPlayer) {
            PacketHandler.syncKnowledge(serverPlayer);
            broadcastChanges();
        }
    }

    private static boolean playerHas(Player player, ItemStack needed) {
        int remaining = needed.getCount();
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (ItemStack.isSameItemSameTags(stack, needed)) {
                remaining -= stack.getCount();
                if (remaining <= 0) {
                    return true;
                }
            }
        }
        return player.getAbilities().instabuild;
    }

    private static void consumeItem(Player player, ItemStack needed) {
        if (player.getAbilities().instabuild) {
            return;
        }
        int remaining = needed.getCount();
        for (int i = 0; i < player.getInventory().getContainerSize() && remaining > 0; i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (ItemStack.isSameItemSameTags(stack, needed)) {
                int take = Math.min(remaining, stack.getCount());
                stack.shrink(take);
                remaining -= take;
            }
        }
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
