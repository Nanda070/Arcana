package arcana.config;

import net.minecraftforge.common.ForgeConfigSpec;

/**
 * Common Forge config (L5 + N10): aura, warp, worldgen, infusion, foci, golems, theorycraft.
 */
public final class ArcanaConfig {
    public static final ForgeConfigSpec COMMON_SPEC;
    public static final Common COMMON;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        COMMON = new Common(builder);
        COMMON_SPEC = builder.build();
    }

    private ArcanaConfig() {
    }

    public static final class Common {
        public final ForgeConfigSpec.DoubleValue auraNodeRegenMultiplier;
        public final ForgeConfigSpec.DoubleValue warpEventChanceMultiplier;
        public final ForgeConfigSpec.DoubleValue worldgenStructureRarityMultiplier;
        public final ForgeConfigSpec.IntValue stickyWarpDecayTicks;
        public final ForgeConfigSpec.IntValue tempWarpDecayTicks;
        public final ForgeConfigSpec.DoubleValue infusionStabilityMultiplier;
        public final ForgeConfigSpec.DoubleValue focusVisCostMultiplier;
        public final ForgeConfigSpec.IntValue golemWorkIntervalTicks;
        public final ForgeConfigSpec.IntValue theorycraftInspirationBase;

        private Common(ForgeConfigSpec.Builder builder) {
            builder.comment("Arcana common settings").push("general");

            auraNodeRegenMultiplier = builder
                    .comment("Multiplier for aura node vis regeneration (default 1.0).")
                    .defineInRange("auraNodeRegenMultiplier", 1.0D, 0.0D, 10.0D);

            warpEventChanceMultiplier = builder
                    .comment("Multiplier for periodic warp event chance (default 1.0).")
                    .defineInRange("warpEventChanceMultiplier", 1.0D, 0.0D, 10.0D);

            worldgenStructureRarityMultiplier = builder
                    .comment("Higher = rarer Eldritch Ring / Flux Patch placements via Feature.place skip (default 1.0).",
                            "Values below 1.0 cannot make features more common than JSON rarity_filter.")
                    .defineInRange("worldgenStructureRarityMultiplier", 1.0D, 0.1D, 20.0D);

            stickyWarpDecayTicks = builder
                    .comment("Ticks between sticky (NORMAL) warp −1 decay (default 6000 = 5 min).")
                    .defineInRange("stickyWarpDecayTicks", 6000, 20, 72000);

            tempWarpDecayTicks = builder
                    .comment("Ticks between temporary warp −1 decay (default 600 = 30s).")
                    .defineInRange("tempWarpDecayTicks", 600, 20, 72000);

            infusionStabilityMultiplier = builder
                    .comment("Multiplier for infusion mid-craft stability drain (default 1.0; lower = more stable).")
                    .defineInRange("infusionStabilityMultiplier", 1.0D, 0.0D, 10.0D);

            focusVisCostMultiplier = builder
                    .comment("Multiplier for caster focus vis cost (default 1.0).")
                    .defineInRange("focusVisCostMultiplier", 1.0D, 0.0D, 10.0D);

            golemWorkIntervalTicks = builder
                    .comment("Base tick interval for golem gather work (default 20). Harvest/breaker scale from this.")
                    .defineInRange("golemWorkIntervalTicks", 20, 5, 200);

            theorycraftInspirationBase = builder
                    .comment("Base inspiration at research table before SPIKY/HIDDEN bonuses (default 5).")
                    .defineInRange("theorycraftInspirationBase", 5, 1, 15);

            builder.pop();
        }
    }
}
