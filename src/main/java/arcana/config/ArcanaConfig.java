package arcana.config;

import net.minecraftforge.common.ForgeConfigSpec;

/**
 * Common Forge config (L5): aura regen, warp intensity, worldgen rarity, warp decay.
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

            builder.pop();
        }
    }
}
