package arcana.common.blockentities;

import arcana.api.aura.AuraHelper;
import arcana.api.capabilities.IPlayerWarp;
import arcana.common.crafting.InfusionRecipe;
import arcana.common.lib.ArcanaSounds;
import arcana.common.lib.essentia.LocalEssentiaDrain;
import arcana.common.lib.events.WarpHelper;
import arcana.config.ArcanaConfig;
import arcana.registry.ModBlockEntities;
import arcana.registry.ModBlocks;
import arcana.registry.ModRecipes;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class InfusionMatrixBlockEntity extends BlockEntity {
    private static final int ESSENTIA_RANGE = 8;
    private static final int CRAFT_DURATION = 100;
    private static final int INSTABILITY_INTERVAL = 20;
    private static final int FX_INTERVAL = 10;
    private static final float STABILITY_CAP = 25.0f;
    private static final BlockPos[] COMPONENT_OFFSETS = {
            new BlockPos(2, -1, 0),
            new BlockPos(-2, -1, 0),
            new BlockPos(0, -1, 2),
            new BlockPos(0, -1, -2),
            new BlockPos(2, -1, 2),
            new BlockPos(2, -1, -2),
            new BlockPos(-2, -1, 2),
            new BlockPos(-2, -1, -2)
    };
    /** TC6 pillar corners relative to matrix: (±1, -2, ±1). */
    private static final BlockPos[] PILLAR_OFFSETS = {
            new BlockPos(-1, -2, -1),
            new BlockPos(1, -2, -1),
            new BlockPos(1, -2, 1),
            new BlockPos(-1, -2, 1)
    };

    private boolean crafting;
    private int craftTicks;
    private String recipeId = "";
    private int instabilityAccum;
    private float stability;
    private boolean pillarsStable;
    private UUID craftingPlayer;
    private int requiredPedestals;

    public InfusionMatrixBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.INFUSION_MATRIX.get(), pos, state);
        this.stability = STABILITY_CAP;
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, InfusionMatrixBlockEntity be) {
        if (!be.crafting) {
            return;
        }
        be.craftTicks++;
        if (be.craftTicks % FX_INTERVAL == 0 && level instanceof ServerLevel server) {
            server.sendParticles(ParticleTypes.ENCHANT,
                    pos.getX() + 0.5, pos.getY() + 0.6, pos.getZ() + 0.5,
                    8, 0.35, 0.4, 0.35, 0.5);
            server.sendParticles(ParticleTypes.PORTAL,
                    pos.getX() + 0.5, pos.getY() + 0.4, pos.getZ() + 0.5,
                    4, 0.25, 0.3, 0.25, 0.15);
            for (BlockPos offset : COMPONENT_OFFSETS) {
                BlockPos p = pos.offset(offset);
                if (level.getBlockEntity(p) instanceof PedestalBlockEntity ped && !ped.getItem().isEmpty()) {
                    server.sendParticles(ParticleTypes.WITCH,
                            p.getX() + 0.5, p.getY() + 1.2, p.getZ() + 0.5,
                            3, 0.2, 0.15, 0.2, 0.01);
                    server.sendParticles(ParticleTypes.ENCHANT,
                            p.getX() + 0.5, p.getY() + 1.1, p.getZ() + 0.5,
                            4, 0.2, 0.2, 0.2, 0.4);
                }
            }
        }
        if (be.craftTicks % INSTABILITY_INTERVAL == 0) {
            if (!be.validateLayout(level)) {
                AuraHelper.polluteAura(level, pos, 1.5f, true);
                be.messagePlayer(level, "Infusion layout broken! Craft aborted");
                be.clearCrafting();
                return;
            }
            be.drainStability(level);
            if (be.rollMidCraftInstability(level)) {
                return;
            }
        }
        if (be.craftTicks >= CRAFT_DURATION) {
            be.completeCraft(level);
        }
        be.setChanged();
    }

    public boolean tryCraft(Player player) {
        Level level = this.level;
        if (level == null || level.isClientSide) {
            return false;
        }
        if (crafting) {
            if (player.isShiftKeyDown()) {
                player.displayClientMessage(Component.literal(
                        "Infusion progress: " + craftTicks + "/" + CRAFT_DURATION
                                + "  stability " + String.format("%.1f", stability)
                                + (pillarsStable ? " [stable]" : " [unstable×2]")), true);
            } else {
                player.displayClientMessage(Component.literal(
                        "Infusion already in progress (" + craftTicks + "/" + CRAFT_DURATION + ")"), true);
            }
            return false;
        }

        CraftContext ctx = validateCraft(level, player);
        if (ctx == null) {
            return false;
        }

        crafting = true;
        craftTicks = 0;
        recipeId = ctx.recipe.getId().toString();
        pillarsStable = countPillars(level) >= 4;
        // Without 4 pillars, instability risk is doubled
        instabilityAccum = pillarsStable ? ctx.risk : ctx.risk * 2;
        stability = STABILITY_CAP;
        craftingPlayer = player.getUUID();
        requiredPedestals = ctx.recipe.getComponentCount();
        setChanged();
        String pillarMsg = pillarsStable ? " (pillars stable)" : " (no pillars — instability ×2)";
        player.displayClientMessage(Component.literal("Infusion started..." + pillarMsg), true);
        level.playSound(null, worldPosition, SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.BLOCKS, 0.8f, 1.1f);
        return true;
    }

    public int getPedestalCount() {
        if (level == null) {
            return 0;
        }
        int count = 0;
        for (BlockPos offset : COMPONENT_OFFSETS) {
            if (level.getBlockEntity(worldPosition.offset(offset)) instanceof PedestalBlockEntity ped
                    && !ped.getItem().isEmpty()) {
                count++;
            }
        }
        return count;
    }

    public int countPillars(Level level) {
        int count = 0;
        for (BlockPos offset : PILLAR_OFFSETS) {
            if (level.getBlockState(worldPosition.offset(offset)).is(ModBlocks.ARCANE_PILLAR.get())) {
                count++;
            }
        }
        return count;
    }

    private boolean validateLayout(Level level) {
        BlockPos centerPos = worldPosition.below();
        if (!(level.getBlockEntity(centerPos) instanceof PedestalBlockEntity center)
                || center.getItem().isEmpty()) {
            return false;
        }
        return getPedestalCount() >= Math.max(1, requiredPedestals);
    }

    /** Mid-craft stability drain scaled by recipe instability (and ×2 without pillars). */
    private void drainStability(Level level) {
        float loss = Math.max(0.15f, instabilityAccum / 20.0f);
        if (!pillarsStable) {
            loss *= 2.0f;
        }
        loss *= (float) ArcanaConfig.COMMON.infusionStabilityMultiplier.get().doubleValue();
        // Refresh pillar status each cycle
        pillarsStable = countPillars(level) >= 4;
        stability -= loss * (0.5f + level.random.nextFloat());
        if (stability < -100.0f) {
            stability = -100.0f;
        }
        if (stability > STABILITY_CAP) {
            stability = STABILITY_CAP;
        }
    }

    private boolean rollMidCraftInstability(Level level) {
        int risk = Math.max(1, instabilityAccum);
        // Also fire TC6-style events when stability is negative
        if (stability < 0.0f && level.random.nextInt(1500) <= Math.abs((int) stability)) {
            fireStabilityEvent(level);
            stability += 5.0f + level.random.nextFloat() * 5.0f;
            return false;
        }
        if (level.random.nextInt(100) >= risk) {
            return false;
        }
        fireStabilityEvent(level);
        AuraHelper.polluteAura(level, worldPosition, risk * 0.35f, true);
        level.playSound(null, worldPosition, SoundEvents.GENERIC_EXPLODE, SoundSource.BLOCKS, 0.6f, 1.0f);
        if (level instanceof ServerLevel server) {
            server.sendParticles(ParticleTypes.EXPLOSION,
                    worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5,
                    2, 0.2, 0.2, 0.2, 0.0);
        }
        if (stability < -40.0f || level.random.nextInt(3) == 0) {
            messagePlayer(level, "Infusion instability! Craft aborted");
            clearCrafting();
            return true;
        }
        messagePlayer(level, "Infusion surges with instability...");
        return false;
    }

    /** TC6 inEv* stubs: eject, zap, warp, flux pollute. */
    private void fireStabilityEvent(Level level) {
        switch (level.random.nextInt(8)) {
            case 0, 1 -> inEvEjectItem(level);
            case 2, 3 -> inEvZap(level);
            case 4, 5 -> inEvWarp(level);
            case 6 -> inEvFluxPollute(level);
            default -> inEvHarm(level);
        }
    }

    private void inEvEjectItem(Level level) {
        List<PedestalBlockEntity> filled = new ArrayList<>();
        for (BlockPos offset : COMPONENT_OFFSETS) {
            if (level.getBlockEntity(worldPosition.offset(offset)) instanceof PedestalBlockEntity ped
                    && !ped.getItem().isEmpty()) {
                filled.add(ped);
            }
        }
        if (filled.isEmpty()) {
            return;
        }
        PedestalBlockEntity ped = filled.get(level.random.nextInt(filled.size()));
        ItemStack stack = ped.getItem().copy();
        ped.setItem(ItemStack.EMPTY);
        BlockPos p = ped.getBlockPos();
        ItemEntity entity = new ItemEntity(level, p.getX() + 0.5, p.getY() + 1.2, p.getZ() + 0.5, stack);
        entity.setDeltaMovement(
                (level.random.nextDouble() - 0.5) * 0.4,
                0.35,
                (level.random.nextDouble() - 0.5) * 0.4);
        level.addFreshEntity(entity);
        level.playSound(null, p, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 0.5f, 0.6f);
        messagePlayer(level, "A component is ejected!");
    }

    private void inEvZap(Level level) {
        AABB box = new AABB(worldPosition).inflate(10.0);
        List<LivingEntity> targets = level.getEntitiesOfClass(LivingEntity.class, box);
        if (targets.isEmpty()) {
            return;
        }
        LivingEntity target = targets.get(level.random.nextInt(targets.size()));
        target.hurt(level.damageSources().magic(), 4.0f + level.random.nextInt(4));
        if (level instanceof ServerLevel server) {
            server.sendParticles(ParticleTypes.ELECTRIC_SPARK,
                    target.getX(), target.getY() + 1.0, target.getZ(),
                    12, 0.3, 0.4, 0.3, 0.05);
        }
        level.playSound(null, target.blockPosition(), SoundEvents.LIGHTNING_BOLT_IMPACT, SoundSource.BLOCKS, 0.4f, 1.4f);
    }

    private void inEvWarp(Level level) {
        AABB box = new AABB(worldPosition).inflate(10.0);
        List<Player> targets = level.getEntitiesOfClass(Player.class, box);
        if (targets.isEmpty()) {
            return;
        }
        Player target = targets.get(level.random.nextInt(targets.size()));
        if (level.random.nextFloat() < 0.25f) {
            WarpHelper.addWarpToPlayer(target, 1, IPlayerWarp.EnumWarpType.NORMAL);
        } else {
            WarpHelper.addWarpToPlayer(target, 2 + level.random.nextInt(4), IPlayerWarp.EnumWarpType.TEMPORARY);
        }
        target.displayClientMessage(Component.literal("Warp coils from the matrix..."), true);
    }

    private void inEvFluxPollute(Level level) {
        float amount = 5.0f + level.random.nextInt(5);
        AuraHelper.polluteAura(level, worldPosition, amount, true);
        level.playSound(null, worldPosition, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 0.4f, 0.8f);
        messagePlayer(level, "Flux bleeds into the aura");
    }

    private void inEvHarm(Level level) {
        AABB box = new AABB(worldPosition).inflate(10.0);
        List<LivingEntity> targets = level.getEntitiesOfClass(LivingEntity.class, box);
        if (targets.isEmpty()) {
            return;
        }
        LivingEntity target = targets.get(level.random.nextInt(targets.size()));
        if (level.random.nextBoolean()) {
            target.addEffect(new MobEffectInstance(MobEffects.POISON, 120, 0));
        } else {
            target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 200, 0));
            target.addEffect(new MobEffectInstance(MobEffects.HUNGER, 200, 0));
        }
    }

    private void completeCraft(Level level) {
        InfusionRecipe recipe = resolveRecipe(level);
        if (recipe == null) {
            messagePlayer(level, "Infusion failed: recipe missing");
            clearCrafting();
            return;
        }

        BlockPos centerPos = worldPosition.below();
        if (!(level.getBlockEntity(centerPos) instanceof PedestalBlockEntity centerPedestal)) {
            messagePlayer(level, "Infusion failed: center pedestal missing");
            clearCrafting();
            return;
        }

        List<PedestalBlockEntity> componentPedestals = collectComponentPedestals(level);
        if (!LocalEssentiaDrain.hasEnough(level, worldPosition, recipe.getAspects(), ESSENTIA_RANGE)) {
            messagePlayer(level, "Infusion failed: not enough essentia");
            clearCrafting();
            return;
        }
        float vis = recipe.getVis();
        if (vis > 0 && AuraHelper.getVis(level, worldPosition) < vis) {
            messagePlayer(level, "Infusion failed: not enough vis");
            clearCrafting();
            return;
        }
        if (!LocalEssentiaDrain.drain(level, worldPosition, recipe.getAspects(), ESSENTIA_RANGE)) {
            messagePlayer(level, "Infusion failed: essentia drain failed");
            clearCrafting();
            return;
        }
        if (vis > 0) {
            AuraHelper.drainVis(level, worldPosition, vis, false);
        }
        for (PedestalBlockEntity ped : componentPedestals) {
            if (!ped.getItem().isEmpty()) {
                ped.setItem(ItemStack.EMPTY);
            }
        }
        centerPedestal.setItem(recipe.getResultItem(level.registryAccess()).copy());
        if (instabilityAccum > 0) {
            messagePlayer(level, "Infusion complete (instability avoided)");
        } else {
            messagePlayer(level, "Infusion complete");
        }
        ArcanaSounds.infusionComplete(level, worldPosition);
        if (level instanceof ServerLevel server) {
            double cx = worldPosition.getX() + 0.5;
            double cy = worldPosition.getY() + 0.8;
            double cz = worldPosition.getZ() + 0.5;
            server.sendParticles(ParticleTypes.EXPLOSION, cx, cy, cz, 2, 0.15, 0.15, 0.15, 0.0);
            server.sendParticles(ParticleTypes.ENCHANT, cx, cy - 0.2, cz, 24, 0.5, 0.5, 0.5, 0.8);
            for (int i = 0; i < 16; i++) {
                double angle = (Math.PI * 2.0 * i) / 16.0;
                double ox = Math.cos(angle) * 1.2;
                double oz = Math.sin(angle) * 1.2;
                server.sendParticles(ParticleTypes.END_ROD, cx + ox, cy, cz + oz, 1, 0.0, 0.02, 0.0, 0.0);
            }
        }
        clearCrafting();
    }

    private CraftContext validateCraft(Level level, Player player) {
        BlockPos centerPos = worldPosition.below();
        if (!(level.getBlockEntity(centerPos) instanceof PedestalBlockEntity centerPedestal)) {
            player.displayClientMessage(Component.literal("Need pedestal under matrix"), true);
            return null;
        }
        ItemStack center = centerPedestal.getItem();
        if (center.isEmpty()) {
            player.displayClientMessage(Component.literal("Center pedestal empty"), true);
            return null;
        }

        List<PedestalBlockEntity> componentPedestals = new ArrayList<>();
        List<ItemStack> components = new ArrayList<>();
        for (BlockPos offset : COMPONENT_OFFSETS) {
            BlockPos p = worldPosition.offset(offset);
            if (level.getBlockEntity(p) instanceof PedestalBlockEntity ped) {
                componentPedestals.add(ped);
                if (!ped.getItem().isEmpty()) {
                    components.add(ped.getItem());
                }
            }
        }

        InfusionRecipe recipe = null;
        for (InfusionRecipe candidate : level.getRecipeManager().getAllRecipesFor(ModRecipes.INFUSION_TYPE.get())) {
            if (candidate.matchesInfusion(center, components, player)) {
                recipe = candidate;
                break;
            }
        }
        if (recipe == null) {
            player.displayClientMessage(Component.literal("No matching infusion recipe"), true);
            return null;
        }

        if (getPedestalCount() < recipe.getComponentCount()) {
            player.displayClientMessage(Component.literal(
                    "Need at least " + recipe.getComponentCount() + " component pedestals with items"), true);
            return null;
        }

        int risk = recipe.getInstability() + (int) (AuraHelper.getFlux(level, worldPosition) / 5.0f);
        if (!LocalEssentiaDrain.hasEnough(level, worldPosition, recipe.getAspects(), ESSENTIA_RANGE)) {
            player.displayClientMessage(Component.literal("Not enough essentia nearby"), true);
            return null;
        }
        float vis = recipe.getVis();
        if (vis > 0 && AuraHelper.getVis(level, worldPosition) < vis) {
            player.displayClientMessage(Component.literal("Not enough vis"), true);
            return null;
        }
        int pillars = countPillars(level);
        if (pillars < 4) {
            player.displayClientMessage(Component.literal(
                    "Warning: " + pillars + "/4 arcane pillars — craft unstable (×2)"), true);
        }
        return new CraftContext(recipe, risk, componentPedestals);
    }

    private List<PedestalBlockEntity> collectComponentPedestals(Level level) {
        List<PedestalBlockEntity> list = new ArrayList<>();
        for (BlockPos offset : COMPONENT_OFFSETS) {
            if (level.getBlockEntity(worldPosition.offset(offset)) instanceof PedestalBlockEntity ped) {
                list.add(ped);
            }
        }
        return list;
    }

    private InfusionRecipe resolveRecipe(Level level) {
        if (recipeId == null || recipeId.isEmpty()) {
            return null;
        }
        Optional<? extends Recipe<?>> opt = level.getRecipeManager().byKey(new ResourceLocation(recipeId));
        if (opt.isPresent() && opt.get() instanceof InfusionRecipe infusion) {
            return infusion;
        }
        return null;
    }

    private void clearCrafting() {
        crafting = false;
        craftTicks = 0;
        recipeId = "";
        instabilityAccum = 0;
        stability = STABILITY_CAP;
        pillarsStable = false;
        craftingPlayer = null;
        requiredPedestals = 0;
        setChanged();
    }

    private void messagePlayer(Level level, String message) {
        if (craftingPlayer == null) {
            return;
        }
        Player player = level.getPlayerByUUID(craftingPlayer);
        if (player != null) {
            player.displayClientMessage(Component.literal(message), true);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putBoolean("crafting", crafting);
        tag.putInt("craftTicks", craftTicks);
        tag.putString("recipeId", recipeId == null ? "" : recipeId);
        tag.putInt("instabilityAccum", instabilityAccum);
        tag.putFloat("stability", stability);
        tag.putBoolean("pillarsStable", pillarsStable);
        tag.putInt("requiredPedestals", requiredPedestals);
        if (craftingPlayer != null) {
            tag.putUUID("craftingPlayer", craftingPlayer);
        }
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        crafting = tag.getBoolean("crafting");
        craftTicks = tag.getInt("craftTicks");
        recipeId = tag.contains("recipeId") ? tag.getString("recipeId") : "";
        instabilityAccum = tag.getInt("instabilityAccum");
        stability = tag.contains("stability") ? tag.getFloat("stability") : STABILITY_CAP;
        pillarsStable = tag.getBoolean("pillarsStable");
        requiredPedestals = tag.getInt("requiredPedestals");
        craftingPlayer = tag.hasUUID("craftingPlayer") ? tag.getUUID("craftingPlayer") : null;
    }

    private record CraftContext(InfusionRecipe recipe, int risk, List<PedestalBlockEntity> componentPedestals) {
    }
}
