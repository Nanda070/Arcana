package arcana.common.lib.research;

import arcana.Arcana;
import arcana.api.aspects.Aspect;
import arcana.api.aspects.AspectList;
import arcana.api.capabilities.ArcanaCapabilities;
import arcana.api.capabilities.IPlayerKnowledge;
import arcana.api.research.ResearchCategories;
import arcana.api.research.ResearchCategory;
import arcana.api.research.ResearchEntry;
import arcana.api.research.ResearchStage;
import arcana.api.research.ScanningManager;
import arcana.api.research.theorycraft.AidBookshelf;
import arcana.api.research.theorycraft.CardAnalyze;
import arcana.api.research.theorycraft.CardBalance;
import arcana.api.research.theorycraft.CardExperimentation;
import arcana.api.research.theorycraft.CardInspired;
import arcana.api.research.theorycraft.CardNotation;
import arcana.api.research.theorycraft.CardPonder;
import arcana.api.research.theorycraft.CardReject;
import arcana.api.research.theorycraft.CardRethink;
import arcana.api.research.theorycraft.CardStudy;
import arcana.api.research.theorycraft.TheorycraftManager;
import arcana.common.config.ConfigAspects;
import arcana.common.lib.research.theorycraft.CardAwareness;
import arcana.common.lib.research.theorycraft.CardBeacon;
import arcana.common.lib.research.theorycraft.CardCalibrate;
import arcana.common.lib.research.theorycraft.CardCelestial;
import arcana.common.lib.research.theorycraft.CardChannel;
import arcana.common.lib.research.theorycraft.CardConcentrate;
import arcana.common.lib.research.theorycraft.CardCurio;
import arcana.common.lib.research.theorycraft.CardDarkWhispers;
import arcana.common.lib.research.theorycraft.CardEnchantment;
import arcana.common.lib.research.theorycraft.CardFocus;
import arcana.common.lib.research.theorycraft.CardGlyphs;
import arcana.common.lib.research.theorycraft.CardInfuse;
import arcana.common.lib.research.theorycraft.CardMeasure;
import arcana.common.lib.research.theorycraft.CardMindOverMatter;
import arcana.common.lib.research.theorycraft.CardPortal;
import arcana.common.lib.research.theorycraft.CardReactions;
import arcana.common.lib.research.theorycraft.CardRealization;
import arcana.common.lib.research.theorycraft.CardRevelation;
import arcana.common.lib.research.theorycraft.CardScripting;
import arcana.common.lib.research.theorycraft.CardSculpting;
import arcana.common.lib.research.theorycraft.CardSpellbinding;
import arcana.common.lib.research.theorycraft.CardSynergy;
import arcana.common.lib.research.theorycraft.CardSynthesis;
import arcana.common.lib.research.theorycraft.CardTinker;
import arcana.common.network.PacketHandler;
import arcana.common.lib.research.ScanAspect;
import arcana.common.lib.research.ScanGeneric;
import arcana.common.lib.research.ScanResearchUnlock;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public final class ResearchManager {
    private ResearchManager() {
    }

    public static void bootstrap() {
        ResearchCategories.researchCategories.clear();
        ResourceLocation icon = new ResourceLocation(Arcana.MODID, "textures/item/thaumonomicon.png");
        ResourceLocation back = new ResourceLocation(Arcana.MODID, "textures/gui/gui_research_back_1.jpg");
        AspectList formula = new AspectList()
                .add(Aspect.PLANT, 5).add(Aspect.ORDER, 5).add(Aspect.ENTROPY, 5)
                .add(Aspect.AIR, 5).add(Aspect.FIRE, 5).add(Aspect.EARTH, 3).add(Aspect.WATER, 5);
        ResearchCategories.registerCategory("BASICS", null, formula, icon, back);
        ResearchCategories.registerCategory("AUROMANCY", "UNLOCKAUROMANCY", formula, icon, back);
        ResearchCategories.registerCategory("ALCHEMY", "UNLOCKALCHEMY", formula, icon, back);
        ResearchCategories.registerCategory("ARTIFICE", "UNLOCKARTIFICE", formula, icon, back);
        ResearchCategories.registerCategory("INFUSION", "INFUSION", formula, icon, back);
        ResearchCategories.registerCategory("GOLEMANCY", "GOLEMBASIC", formula, icon, back);
        ResearchCategories.registerCategory("ELDRITCH", "ELDRITCH", formula, icon, back);
        parseResearchResource("/assets/arcana/research/basics.json");
        parseResearchResource("/assets/arcana/research/auromancy.json");
        parseResearchResource("/assets/arcana/research/alchemy.json");
        parseResearchResource("/assets/arcana/research/artifice.json");
        parseResearchResource("/assets/arcana/research/infusion.json");
        parseResearchResource("/assets/arcana/research/golemancy.json");
        parseResearchResource("/assets/arcana/research/eldritch.json");
        parseResearchResource("/assets/arcana/research/scans.json");

        ConfigAspects.register();
        ScanningManager.clear();
        ScanningManager.addScannableThing(new ScanGeneric());
        ScanResearchUnlock.registerAll();
        for (Aspect aspect : Aspect.aspects.values()) {
            ScanningManager.addScannableThing(new ScanAspect(aspect));
        }

        initTheorycraft();

        Arcana.LOGGER.info("Research bootstrap complete — {} categories, {} theorycraft cards, scanners ready",
                ResearchCategories.researchCategories.size(), TheorycraftManager.cards.size());
    }

    private static void initTheorycraft() {
        TheorycraftManager.aids.clear();
        TheorycraftManager.cards.clear();
        TheorycraftManager.registerAid(new AidBookshelf());
        TheorycraftManager.registerCard(CardStudy.class);
        TheorycraftManager.registerCard(CardAnalyze.class);
        TheorycraftManager.registerCard(CardBalance.class);
        TheorycraftManager.registerCard(CardNotation.class);
        TheorycraftManager.registerCard(CardPonder.class);
        TheorycraftManager.registerCard(CardRethink.class);
        TheorycraftManager.registerCard(CardReject.class);
        TheorycraftManager.registerCard(CardExperimentation.class);
        TheorycraftManager.registerCard(CardCurio.class);
        TheorycraftManager.registerCard(CardInspired.class);
        TheorycraftManager.registerCard(CardConcentrate.class);
        TheorycraftManager.registerCard(CardTinker.class);
        TheorycraftManager.registerCard(CardMeasure.class);
        TheorycraftManager.registerCard(CardChannel.class);
        TheorycraftManager.registerCard(CardInfuse.class);
        TheorycraftManager.registerCard(CardFocus.class);
        TheorycraftManager.registerCard(CardDarkWhispers.class);
        TheorycraftManager.registerCard(CardSynthesis.class);
        TheorycraftManager.registerCard(CardReactions.class);
        TheorycraftManager.registerCard(CardCalibrate.class);
        TheorycraftManager.registerCard(CardMindOverMatter.class);
        TheorycraftManager.registerCard(CardSpellbinding.class);
        TheorycraftManager.registerCard(CardSculpting.class);
        TheorycraftManager.registerCard(CardScripting.class);
        TheorycraftManager.registerCard(CardSynergy.class);
        TheorycraftManager.registerCard(CardGlyphs.class);
        TheorycraftManager.registerCard(CardPortal.class);
        TheorycraftManager.registerCard(CardRevelation.class);
        TheorycraftManager.registerCard(CardRealization.class);
        TheorycraftManager.registerCard(CardAwareness.class);
        TheorycraftManager.registerCard(CardCelestial.class);
        TheorycraftManager.registerCard(CardBeacon.class);
        TheorycraftManager.registerCard(CardEnchantment.class);
    }

    /** I6: grant +4 OBSERVATION once per successful scan of something with aspects. */
    public static void grantScanObservation(Player player) {
        IPlayerKnowledge knowledge = ArcanaCapabilities.getKnowledge(player);
        ResearchCategory cat = ResearchCategories.getResearchCategory("BASICS");
        knowledge.addKnowledge(IPlayerKnowledge.EnumKnowledgeType.OBSERVATION, cat, 4);
        if (player instanceof ServerPlayer serverPlayer) {
            PacketHandler.syncKnowledge(serverPlayer);
        }
    }

    public static boolean completeResearch(Player player, String researchkey) {
        boolean b = false;
        while (progressResearch(player, researchkey, true)) {
            b = true;
        }
        return b;
    }

    public static boolean startResearchWithPopup(Player player, String researchkey) {
        boolean b = progressResearch(player, researchkey, true);
        if (b) {
            IPlayerKnowledge knowledge = ArcanaCapabilities.getKnowledge(player);
            knowledge.setResearchFlag(researchkey, IPlayerKnowledge.EnumResearchFlag.POPUP);
            knowledge.setResearchFlag(researchkey, IPlayerKnowledge.EnumResearchFlag.RESEARCH);
        }
        return b;
    }

    public static boolean progressResearch(Player player, String researchkey, boolean sync) {
        IPlayerKnowledge knowledge = ArcanaCapabilities.getKnowledge(player);
        if (knowledge.isResearchComplete(researchkey) || !doesPlayerHaveRequisites(player, researchkey)) {
            return false;
        }
        if (!knowledge.isResearchKnown(researchkey)) {
            knowledge.addResearch(researchkey);
        }
        ResearchEntry re = ResearchCategories.getResearch(researchkey);
        if (re != null && re.getStages() != null && re.getStages().length > 0) {
            int cs = knowledge.getResearchStage(researchkey);
            if (cs < 0) {
                cs = 0;
            }
            // M5: skip craft/obtain gates — advance one stage per call.
            int next = Math.min(re.getStages().length + 1, cs + 1);
            if (next == cs) {
                return false;
            }
            knowledge.setResearchStage(researchkey, next);
        } else {
            // Flag-style research with no stages → complete immediately.
            if (knowledge.getResearchStage(researchkey) <= 0) {
                knowledge.setResearchStage(researchkey, 1);
            } else {
                return false;
            }
        }

        if (re != null && knowledge.isResearchComplete(researchkey) && re.getSiblings() != null) {
            for (String sib : re.getSiblings()) {
                String clean = sib.startsWith("!") ? sib.substring(1) : sib;
                if (!knowledge.isResearchComplete(clean)) {
                    completeResearch(player, clean);
                }
            }
        }

        if (sync && player instanceof ServerPlayer serverPlayer) {
            PacketHandler.syncKnowledge(serverPlayer);
        }
        return true;
    }

    public static boolean doesPlayerHaveRequisites(Player player, String researchkey) {
        ResearchEntry re = ResearchCategories.getResearch(researchkey);
        if (re == null) {
            // Allow freeform flag keys (e.g. gotthaumonomicon) with no entry.
            return true;
        }
        String[] parents = re.getParents();
        if (parents == null || parents.length == 0) {
            return true;
        }
        IPlayerKnowledge knowledge = ArcanaCapabilities.getKnowledge(player);
        for (String parent : parents) {
            boolean soft = parent.startsWith("~");
            String req = soft ? parent.substring(1) : parent;
            if (req.startsWith("!")) {
                req = req.substring(1);
            }
            // Unknown parent entries soft-fail (adapted TC6 trees may reference unfinished keys).
            if (ResearchCategories.getResearch(req) == null) {
                continue;
            }
            if (!knowledge.isResearchKnown(req) && !soft) {
                return false;
            }
        }
        return true;
    }

    public static void parseResearchResource(String classpath) {
        InputStream stream = ResearchManager.class.getResourceAsStream(classpath);
        if (stream == null) {
            Arcana.LOGGER.warn("Research file not found: {}", classpath);
            return;
        }
        try (InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
            JsonObject obj = JsonParser.parseReader(reader).getAsJsonObject();
            JsonArray entries = obj.getAsJsonArray("entries");
            int loaded = 0;
            for (JsonElement element : entries) {
                try {
                    ResearchEntry entry = parseResearchJson(element.getAsJsonObject());
                    addResearchToCategory(entry);
                    loaded++;
                } catch (Exception e) {
                    Arcana.LOGGER.warn("Invalid research entry in {}: {}", classpath, e.getMessage());
                }
            }
            Arcana.LOGGER.info("Loaded {} research entries from {}", loaded, classpath);
        } catch (Exception e) {
            Arcana.LOGGER.warn("Invalid research file {}: {}", classpath, e.getMessage());
        }
    }

    private static ResearchEntry parseResearchJson(JsonObject obj) {
        ResearchEntry entry = new ResearchEntry();
        entry.setKey(obj.getAsJsonPrimitive("key").getAsString());
        entry.setName(obj.getAsJsonPrimitive("name").getAsString());
        entry.setCategory(obj.getAsJsonPrimitive("category").getAsString());
        if (obj.has("parents")) {
            entry.setParents(arrayToString(obj.getAsJsonArray("parents")));
        }
        if (obj.has("siblings")) {
            entry.setSiblings(arrayToString(obj.getAsJsonArray("siblings")));
        }
        if (obj.has("meta")) {
            String[] meta = arrayToString(obj.getAsJsonArray("meta"));
            ArrayList<ResearchEntry.EnumResearchMeta> metas = new ArrayList<>();
            for (String s : meta) {
                metas.add(ResearchEntry.EnumResearchMeta.valueOf(s.toUpperCase()));
            }
            entry.setMeta(metas.toArray(new ResearchEntry.EnumResearchMeta[0]));
        }
        if (obj.has("location")) {
            JsonArray loc = obj.getAsJsonArray("location");
            entry.setDisplayColumn(loc.get(0).getAsInt());
            entry.setDisplayRow(loc.get(1).getAsInt());
        }
        if (obj.has("stages")) {
            ArrayList<ResearchStage> stages = new ArrayList<>();
            for (JsonElement el : obj.getAsJsonArray("stages")) {
                JsonObject stageObj = el.getAsJsonObject();
                ResearchStage stage = new ResearchStage();
                if (stageObj.has("text")) {
                    stage.setText(stageObj.getAsJsonPrimitive("text").getAsString());
                }
                stages.add(stage);
            }
            entry.setStages(stages.toArray(new ResearchStage[0]));
        }
        return entry;
    }

    private static String[] arrayToString(JsonArray array) {
        String[] out = new String[array.size()];
        for (int i = 0; i < array.size(); i++) {
            out[i] = array.get(i).getAsString();
        }
        return out;
    }

    private static void addResearchToCategory(ResearchEntry ri) {
        ResearchCategory rl = ResearchCategories.getResearchCategory(ri.getCategory());
        if (rl == null || rl.research.containsKey(ri.getKey())) {
            Arcana.LOGGER.warn("Could not add research entry {}", ri.getKey());
            return;
        }
        rl.research.put(ri.getKey(), ri);
        if (ri.getDisplayColumn() < rl.minDisplayColumn) {
            rl.minDisplayColumn = ri.getDisplayColumn();
        }
        if (ri.getDisplayRow() < rl.minDisplayRow) {
            rl.minDisplayRow = ri.getDisplayRow();
        }
        if (ri.getDisplayColumn() > rl.maxDisplayColumn) {
            rl.maxDisplayColumn = ri.getDisplayColumn();
        }
        if (ri.getDisplayRow() > rl.maxDisplayRow) {
            rl.maxDisplayRow = ri.getDisplayRow();
        }
    }
}
