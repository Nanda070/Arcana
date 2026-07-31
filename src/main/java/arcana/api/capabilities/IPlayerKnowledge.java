package arcana.api.capabilities;

import arcana.api.research.ResearchCategory;
import java.util.Set;
import javax.annotation.Nonnull;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.util.INBTSerializable;

/**
 * Raw player research/knowledge data. Prefer helper APIs once InternalMethodHandler lands (M5).
 */
public interface IPlayerKnowledge extends INBTSerializable<CompoundTag> {

    void clear();

    EnumResearchStatus getResearchStatus(@Nonnull String research);

    boolean isResearchComplete(String research);

    /**
     * Empty string always returns true. Supports {@code research@stage} form.
     */
    boolean isResearchKnown(String research);

    enum EnumResearchStatus {
        UNKNOWN, COMPLETE, IN_PROGRESS
    }

    /**
     * @return stage (>=1), 0 if known with no stage, -1 if unknown
     */
    int getResearchStage(@Nonnull String research);

    boolean addResearch(@Nonnull String research);

    boolean setResearchStage(@Nonnull String research, int stage);

    boolean removeResearch(@Nonnull String research);

    @Nonnull
    Set<String> getResearchList();

    boolean setResearchFlag(@Nonnull String research, @Nonnull EnumResearchFlag flag);

    boolean clearResearchFlag(@Nonnull String research, @Nonnull EnumResearchFlag flag);

    boolean hasResearchFlag(@Nonnull String research, @Nonnull EnumResearchFlag flag);

    boolean addKnowledge(@Nonnull EnumKnowledgeType type, ResearchCategory category, int amount);

    int getKnowledge(@Nonnull EnumKnowledgeType type, ResearchCategory category);

    int getKnowledgeRaw(@Nonnull EnumKnowledgeType type, ResearchCategory category);

    /** Network sync stub until packet layer (M4/M5). */
    void sync(ServerPlayer player);

    enum EnumKnowledgeType {
        THEORY(32, true, "T"),
        OBSERVATION(16, true, "O");

        private final short progression;
        private final boolean hasFields;
        private final String abbr;

        EnumKnowledgeType(int progression, boolean hasFields, String abbr) {
            this.progression = (short) progression;
            this.hasFields = hasFields;
            this.abbr = abbr;
        }

        public int getProgression() {
            return progression;
        }

        public boolean hasFields() {
            return hasFields;
        }

        public String getAbbreviation() {
            return abbr;
        }
    }

    enum EnumResearchFlag {
        PAGE,
        RESEARCH,
        POPUP
    }
}
