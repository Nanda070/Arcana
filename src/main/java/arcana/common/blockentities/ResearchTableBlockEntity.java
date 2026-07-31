package arcana.common.blockentities;

import arcana.api.capabilities.IPlayerKnowledge;
import arcana.api.research.ResearchCategories;
import arcana.api.research.ResearchCategory;
import arcana.api.research.theorycraft.ITheorycraftAid;
import arcana.api.research.theorycraft.ResearchTableData;
import arcana.api.research.theorycraft.TheorycraftManager;
import arcana.common.menu.ResearchTableMenu;
import arcana.registry.ModBlockEntities;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class ResearchTableBlockEntity extends BlockEntity implements MenuProvider {

    public ResearchTableData data;

    public ResearchTableBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.RESEARCH_TABLE.get(), pos, state);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.arcana.research_table");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return new ResearchTableMenu(id, inv, this);
    }

    public void startNewTheory(Player player, Set<String> mutators) {
        data = new ResearchTableData(player, this);
        data.initialize(player, mutators);
        setChanged();
        syncToClient();
    }

    public void finishTheory(Player player) {
        if (data == null) {
            return;
        }
        Comparator<Map.Entry<String, Integer>> valueComparator =
                (e1, e2) -> e2.getValue().compareTo(e1.getValue());
        Map<String, Integer> sortedMap = data.categoryTotals.entrySet().stream()
                .sorted(valueComparator)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
        int i = 0;
        var knowledge = arcana.api.capabilities.ArcanaCapabilities.getKnowledge(player);
        for (String cat : sortedMap.keySet()) {
            int tot = Math.round(sortedMap.get(cat) / 100.0f
                    * IPlayerKnowledge.EnumKnowledgeType.THEORY.getProgression());
            if (i > data.penaltyStart) {
                tot = (int) Math.max(1.0, tot * 0.666666667);
            }
            ResearchCategory rc = ResearchCategories.getResearchCategory(cat);
            if (rc != null && tot > 0) {
                knowledge.addKnowledge(IPlayerKnowledge.EnumKnowledgeType.THEORY, rc, tot);
            }
            // Progress flag research tied to this category when present.
            if ("BASICS".equals(cat)) {
                arcana.common.lib.research.ResearchManager.progressResearch(player, "THEORYRESEARCH", true);
            }
            i++;
        }
        data = null;
        setChanged();
        syncToClient();
    }

    /**
     * Stub: bookshelves (and other registered aids) within 4 blocks horizontally, ±1 vertically.
     */
    public Set<String> checkSurroundingAids() {
        HashMap<String, ITheorycraftAid> mutators = new HashMap<>();
        if (level == null) {
            return mutators.keySet();
        }
        for (int y = -1; y <= 1; y++) {
            for (int x = -4; x <= 4; x++) {
                for (int z = -4; z <= 4; z++) {
                    BlockPos check = worldPosition.offset(x, y, z);
                    BlockState state = level.getBlockState(check);
                    for (Map.Entry<String, ITheorycraftAid> entry : TheorycraftManager.aids.entrySet()) {
                        ITheorycraftAid mu = entry.getValue();
                        Object aid = mu.getAidObject();
                        if (aid instanceof Block block) {
                            if (state.is(block)) {
                                mutators.put(entry.getKey(), mu);
                            }
                        } else if (aid instanceof ItemStack stack) {
                            ItemStack drop = state.getBlock().asItem().getDefaultInstance();
                            if (!drop.isEmpty() && ItemStack.isSameItem(drop, stack)) {
                                mutators.put(entry.getKey(), mu);
                            }
                        }
                    }
                }
            }
        }
        return mutators.keySet();
    }

    public void syncToClient() {
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (data != null) {
            tag.put("note", data.serialize());
        }
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("note")) {
            data = new ResearchTableData(this);
            data.deserialize(tag.getCompound("note"));
        } else {
            data = null;
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
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        load(tag);
    }

    @Override
    public void onDataPacket(net.minecraft.network.Connection net, ClientboundBlockEntityDataPacket pkt) {
        CompoundTag tag = pkt.getTag();
        if (tag != null) {
            handleUpdateTag(tag);
        }
    }
}
