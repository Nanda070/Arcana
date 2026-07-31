package arcana.common.capabilities;

import arcana.api.capabilities.IPlayerKnowledge;
import arcana.api.research.ResearchCategory;
import arcana.api.research.ResearchCategories;
import arcana.api.research.ResearchEntry;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import arcana.api.capabilities.ArcanaCapabilities;

public final class PlayerKnowledge {

    private PlayerKnowledge() {
    }

    public static class Impl implements IPlayerKnowledge {
        private final HashSet<String> research = new HashSet<>();
        private final Map<String, Integer> stages = new HashMap<>();
        private final Map<String, HashSet<EnumResearchFlag>> flags = new HashMap<>();
        private final Map<String, Integer> knowledge = new HashMap<>();

        @Override
        public void clear() {
            research.clear();
            flags.clear();
            stages.clear();
            knowledge.clear();
        }

        @Override
        public EnumResearchStatus getResearchStatus(@Nonnull String res) {
            if (!isResearchKnown(res)) {
                return EnumResearchStatus.UNKNOWN;
            }
            ResearchEntry entry = ResearchCategories.getResearch(res);
            if (entry == null || entry.getStages() == null
                    || getResearchStage(res) > entry.getStages().length) {
                return EnumResearchStatus.COMPLETE;
            }
            return EnumResearchStatus.IN_PROGRESS;
        }

        @Override
        public boolean isResearchKnown(String res) {
            if (res == null) {
                return false;
            }
            if (res.isEmpty()) {
                return true;
            }
            String[] ss = res.split("@");
            if (ss.length > 1) {
                int required;
                try {
                    required = Integer.parseInt(ss[1]);
                } catch (NumberFormatException e) {
                    required = 0;
                }
                return research.contains(ss[0]) && getResearchStage(ss[0]) >= required;
            }
            return research.contains(ss[0]);
        }

        @Override
        public boolean isResearchComplete(String res) {
            return getResearchStatus(res) == EnumResearchStatus.COMPLETE;
        }

        @Override
        public int getResearchStage(String res) {
            if (res == null || !research.contains(res)) {
                return -1;
            }
            Integer stage = stages.get(res);
            return stage == null ? 0 : stage;
        }

        @Override
        public boolean setResearchStage(String res, int stage) {
            if (res == null || !research.contains(res) || stage <= 0) {
                return false;
            }
            stages.put(res, stage);
            return true;
        }

        @Override
        public boolean addResearch(@Nonnull String res) {
            if (!isResearchKnown(res)) {
                research.add(res);
                return true;
            }
            return false;
        }

        @Override
        public boolean removeResearch(@Nonnull String res) {
            if (isResearchKnown(res)) {
                research.remove(res);
                stages.remove(res);
                flags.remove(res);
                return true;
            }
            return false;
        }

        @Nonnull
        @Override
        public Set<String> getResearchList() {
            return Collections.unmodifiableSet(research);
        }

        @Override
        public boolean setResearchFlag(@Nonnull String res, @Nonnull EnumResearchFlag flag) {
            HashSet<EnumResearchFlag> list = flags.computeIfAbsent(res, k -> new HashSet<>());
            return list.add(flag);
        }

        @Override
        public boolean clearResearchFlag(@Nonnull String res, @Nonnull EnumResearchFlag flag) {
            HashSet<EnumResearchFlag> list = flags.get(res);
            if (list == null) {
                return false;
            }
            boolean removed = list.remove(flag);
            if (list.isEmpty()) {
                flags.remove(res);
            }
            return removed;
        }

        @Override
        public boolean hasResearchFlag(@Nonnull String res, @Nonnull EnumResearchFlag flag) {
            HashSet<EnumResearchFlag> list = flags.get(res);
            return list != null && list.contains(flag);
        }

        private static String getKey(EnumKnowledgeType type, ResearchCategory category) {
            return type.getAbbreviation() + "_" + (category == null ? "" : category.key);
        }

        @Override
        public boolean addKnowledge(EnumKnowledgeType type, ResearchCategory category, int amount) {
            String key = getKey(type, category);
            int c = getKnowledgeRaw(type, category);
            if (c + amount < 0) {
                return false;
            }
            knowledge.put(key, c + amount);
            return true;
        }

        @Override
        public int getKnowledge(EnumKnowledgeType type, ResearchCategory category) {
            return (int) Math.floor(getKnowledgeRaw(type, category) / (double) type.getProgression());
        }

        @Override
        public int getKnowledgeRaw(EnumKnowledgeType type, ResearchCategory category) {
            String key = getKey(type, category);
            return knowledge.getOrDefault(key, 0);
        }

        @Override
        public void sync(@Nonnull ServerPlayer player) {
            // PacketSyncKnowledge arrives with network module.
        }

        @Override
        public CompoundTag serializeNBT() {
            CompoundTag rootTag = new CompoundTag();
            ListTag researchList = new ListTag();
            for (String resKey : research) {
                CompoundTag tag = new CompoundTag();
                tag.putString("key", resKey);
                if (stages.containsKey(resKey)) {
                    tag.putInt("stage", stages.get(resKey));
                }
                HashSet<EnumResearchFlag> list = flags.get(resKey);
                if (list != null && !list.isEmpty()) {
                    StringBuilder fs = new StringBuilder();
                    for (EnumResearchFlag flag : list) {
                        if (fs.length() > 0) {
                            fs.append(',');
                        }
                        fs.append(flag.name());
                    }
                    tag.putString("flags", fs.toString());
                }
                researchList.add(tag);
            }
            rootTag.put("research", researchList);

            ListTag knowledgeList = new ListTag();
            for (Map.Entry<String, Integer> entry : knowledge.entrySet()) {
                Integer c = entry.getValue();
                String key = entry.getKey();
                if (c != null && c > 0 && key != null && !key.isEmpty()) {
                    CompoundTag tag = new CompoundTag();
                    tag.putString("key", key);
                    tag.putInt("amount", c);
                    knowledgeList.add(tag);
                }
            }
            rootTag.put("knowledge", knowledgeList);
            return rootTag;
        }

        @Override
        public void deserializeNBT(CompoundTag rootTag) {
            if (rootTag == null) {
                return;
            }
            clear();
            ListTag researchList = rootTag.getList("research", Tag.TAG_COMPOUND);
            for (int i = 0; i < researchList.size(); i++) {
                CompoundTag tag = researchList.getCompound(i);
                String know = tag.getString("key");
                if (know != null && !know.isEmpty() && !research.contains(know)) {
                    research.add(know);
                    int stage = tag.getInt("stage");
                    if (stage > 0) {
                        stages.put(know, stage);
                    }
                    String fs = tag.getString("flags");
                    if (!fs.isEmpty()) {
                        for (String s : fs.split(",")) {
                            try {
                                setResearchFlag(know, EnumResearchFlag.valueOf(s));
                            } catch (IllegalArgumentException ignored) {
                            }
                        }
                    }
                }
            }
            ListTag knowledgeList = rootTag.getList("knowledge", Tag.TAG_COMPOUND);
            for (int j = 0; j < knowledgeList.size(); j++) {
                CompoundTag tag = knowledgeList.getCompound(j);
                knowledge.put(tag.getString("key"), tag.getInt("amount"));
            }
        }
    }

    public static class Provider implements ICapabilitySerializable<CompoundTag> {
        private final Impl backend = new Impl();
        private final LazyOptional<IPlayerKnowledge> optional = LazyOptional.of(() -> backend);

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
            return ArcanaCapabilities.KNOWLEDGE.orEmpty(cap, optional);
        }

        @Override
        public CompoundTag serializeNBT() {
            return backend.serializeNBT();
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) {
            backend.deserializeNBT(nbt);
        }

        public void invalidate() {
            optional.invalidate();
        }

        public Impl getBackend() {
            return backend;
        }
    }
}
