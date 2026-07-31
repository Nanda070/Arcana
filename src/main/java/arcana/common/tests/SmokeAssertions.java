package arcana.common.tests;

import arcana.api.aspects.Aspect;
import arcana.api.research.ResearchCategories;
import arcana.api.research.theorycraft.TheorycraftManager;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;

/**
 * Lightweight registry/smoke checks for {@code /arcana smoke}.
 * No JUnit — results are printed as PASS/FAIL lines.
 */
public final class SmokeAssertions {
    public record Result(String name, boolean pass, String detail) {
        public String line() {
            return (pass ? "PASS" : "FAIL") + " — " + name + (detail == null || detail.isEmpty() ? "" : ": " + detail);
        }
    }

    private SmokeAssertions() {
    }

    public static List<Result> runAll() {
        List<Result> results = new ArrayList<>();
        checkAspects(results);
        checkResearchCategories(results);
        checkInfusionCategory(results);
        checkTheorycraftCards(results);
        checkBlock("research_table", results);
        checkBlock("infusion_matrix", results);
        checkBlock("essentia_filter_tube", results);
        checkBlock("outer_lands_portal", results);
        return results;
    }

    /** @return number of failures */
    public static int runAndReport(Consumer<String> out) {
        List<Result> results = runAll();
        int fail = 0;
        for (Result r : results) {
            out.accept(r.line());
            if (!r.pass) {
                fail++;
            }
        }
        out.accept(fail == 0
                ? "Smoke: ALL PASS (" + results.size() + ")"
                : "Smoke: " + fail + " FAIL / " + results.size());
        return fail;
    }

    private static void checkAspects(List<Result> results) {
        int n = Aspect.aspects == null ? 0 : Aspect.aspects.size();
        results.add(new Result("Aspect.aspects non-empty", n > 0, "size=" + n));
    }

    private static void checkResearchCategories(List<Result> results) {
        int n = ResearchCategories.researchCategories.size();
        results.add(new Result("ResearchCategories size>=6", n >= 6, "size=" + n));
    }

    private static void checkInfusionCategory(List<Result> results) {
        boolean ok = ResearchCategories.getResearchCategory("INFUSION") != null;
        results.add(new Result("INFUSION category exists", ok, ok ? "present" : "missing"));
    }

    private static void checkTheorycraftCards(List<Result> results) {
        int n = TheorycraftManager.cards.size();
        results.add(new Result("TheorycraftManager cards>=20", n >= 20, "size=" + n));
    }

    private static void checkBlock(String path, List<Result> results) {
        ResourceLocation id = new ResourceLocation("arcana", path);
        var block = BuiltInRegistries.BLOCK.get(id);
        boolean ok = block != null && block != Blocks.AIR;
        results.add(new Result("registry block " + id, ok, ok ? "present" : "missing"));
    }
}
