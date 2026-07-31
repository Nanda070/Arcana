package arcana.command;

import arcana.api.aspects.Aspect;
import arcana.api.aura.AuraHelper;
import arcana.api.capabilities.ArcanaCapabilities;
import arcana.api.capabilities.IPlayerKnowledge;
import arcana.api.capabilities.IPlayerWarp;
import arcana.api.casters.FocusEngine;
import arcana.api.casters.FocusPackage;
import arcana.api.research.ResearchCategory;
import arcana.common.blockentities.CrucibleBlockEntity;
import arcana.common.blockentities.WardedJarBlockEntity;
import arcana.common.lib.events.WarpEvents;
import arcana.common.lib.events.WarpHelper;
import arcana.common.lib.research.ResearchManager;
import arcana.common.network.PacketHandler;
import arcana.common.tests.SmokeAssertions;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.fml.loading.FMLLoader;

public final class ArcanaCommands {
    private static final SimpleCommandExceptionType BAD_KNOWLEDGE_TYPE =
            new SimpleCommandExceptionType(Component.literal("Unknown knowledge type (THEORY|OBSERVATION)"));
    private static final SimpleCommandExceptionType BAD_WARP_TYPE =
            new SimpleCommandExceptionType(Component.literal("Unknown warp type (PERMANENT|NORMAL|STICKY|TEMPORARY)"));
    private static final SimpleCommandExceptionType NO_JAR =
            new SimpleCommandExceptionType(Component.literal("Look at a warded jar"));
    private static final SimpleCommandExceptionType NO_CRUCIBLE =
            new SimpleCommandExceptionType(Component.literal("Look at a crucible"));
    private static final SimpleCommandExceptionType BAD_ASPECT =
            new SimpleCommandExceptionType(Component.literal("Unknown aspect tag"));
    private ArcanaCommands() {
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("arcana")
                .executes(ArcanaCommands::help)
                .then(Commands.literal("help")
                        .executes(ArcanaCommands::help))
                .then(Commands.literal("aspects")
                        .executes(ArcanaCommands::listAspects))
                .then(Commands.literal("smoke")
                        .requires(src -> src.hasPermission(2) || !FMLLoader.isProduction())
                        .executes(ArcanaCommands::runSmoke))
                .then(Commands.literal("research")
                        .requires(src -> src.hasPermission(2))
                        .then(Commands.literal("list")
                                .executes(ArcanaCommands::listResearch))
                        .then(Commands.literal("add")
                                .then(Commands.argument("key", StringArgumentType.string())
                                        .executes(ArcanaCommands::addResearch)))
                        .then(Commands.literal("complete")
                                .then(Commands.argument("key", StringArgumentType.string())
                                        .executes(ArcanaCommands::completeResearch)))
                        .then(Commands.literal("remove")
                                .then(Commands.argument("key", StringArgumentType.string())
                                        .executes(ArcanaCommands::removeResearch)))
                        .then(Commands.literal("clear")
                                .executes(ArcanaCommands::clearResearch)))
                .then(Commands.literal("knowledge")
                        .requires(src -> src.hasPermission(2))
                        .then(Commands.literal("get")
                                .executes(ArcanaCommands::getKnowledge))
                        .then(Commands.literal("add")
                                .then(Commands.argument("type", StringArgumentType.word())
                                        .then(Commands.argument("amount", IntegerArgumentType.integer())
                                                .executes(ArcanaCommands::addKnowledge)))))
                .then(Commands.literal("warp")
                        .requires(src -> src.hasPermission(2))
                        .then(Commands.literal("get")
                                .executes(ArcanaCommands::getWarp))
                        .then(Commands.literal("set")
                                .then(Commands.argument("type", StringArgumentType.word())
                                        .then(Commands.argument("amount", IntegerArgumentType.integer(0, 500))
                                                .executes(ArcanaCommands::setWarp))))
                        .then(Commands.literal("add")
                                .then(Commands.argument("type", StringArgumentType.word())
                                        .then(Commands.argument("amount", IntegerArgumentType.integer())
                                                .executes(ArcanaCommands::addWarp))))
                        .then(Commands.literal("clear")
                                .executes(ArcanaCommands::clearWarp))
                        .then(Commands.literal("event")
                                .executes(ArcanaCommands::forceWarpEvent)))
                .then(Commands.literal("aura")
                        .requires(src -> src.hasPermission(2))
                        .then(Commands.literal("get")
                                .executes(ArcanaCommands::getAura))
                        .then(Commands.literal("addvis")
                                .then(Commands.argument("amount", FloatArgumentType.floatArg(0.0f))
                                        .executes(ArcanaCommands::addVis)))
                        .then(Commands.literal("drainvis")
                                .then(Commands.argument("amount", FloatArgumentType.floatArg(0.0f))
                                        .executes(ArcanaCommands::drainVis)))
                        .then(Commands.literal("pollute")
                                .then(Commands.argument("amount", FloatArgumentType.floatArg(0.0f))
                                        .executes(ArcanaCommands::polluteAura))))
                .then(Commands.literal("essentia")
                        .requires(src -> src.hasPermission(2))
                        .then(Commands.literal("get")
                                .executes(ArcanaCommands::getEssentia))
                        .then(Commands.literal("fill")
                                .then(Commands.argument("aspect", StringArgumentType.word())
                                        .then(Commands.argument("amount", IntegerArgumentType.integer(1, 250))
                                                .executes(ArcanaCommands::fillEssentia))))
                        .then(Commands.literal("clear")
                                .executes(ArcanaCommands::clearEssentia)))
                .then(Commands.literal("crucible")
                        .requires(src -> src.hasPermission(2))
                        .then(Commands.literal("get")
                                .executes(ArcanaCommands::getCrucible))
                        .then(Commands.literal("add")
                                .then(Commands.argument("aspect", StringArgumentType.word())
                                        .then(Commands.argument("amount", IntegerArgumentType.integer(1, 100))
                                                .executes(ArcanaCommands::addCrucibleAspect))))
                        .then(Commands.literal("clear")
                                .executes(ArcanaCommands::clearCrucible)))
                .then(Commands.literal("cast")
                        .requires(src -> src.hasPermission(2))
                        .executes(ctx -> forceCast(ctx, "touch_fire"))
                        .then(Commands.argument("preset", StringArgumentType.word())
                                .executes(ctx -> forceCast(ctx, StringArgumentType.getString(ctx, "preset")))))
                .then(Commands.literal("focus")
                        .requires(src -> src.hasPermission(2))
                        .then(Commands.literal("give")
                                .then(Commands.argument("preset", StringArgumentType.word())
                                        .executes(ArcanaCommands::giveFocus)))));
    }

    private static int help(CommandContext<CommandSourceStack> ctx) {
        CommandSourceStack source = ctx.getSource();
        source.sendSuccess(() -> Component.literal("Arcana commands:"), false);
        source.sendSuccess(() -> Component.literal(" /arcana aspects"), false);
        source.sendSuccess(() -> Component.literal(" /arcana smoke  (op, or anyone in dev)"), false);
        source.sendSuccess(() -> Component.literal(" /arcana research|knowledge|warp|aura|essentia|crucible|cast|focus  (op)"), false);
        source.sendSuccess(() -> Component.literal(" Warp: get|set|add|clear|event"), false);
        source.sendSuccess(() -> Component.literal(" Focus: give <touch_fire|projectile_fire|touch_frost|projectile_frost>"), false);
        source.sendSuccess(() -> Component.literal(" Cast: /arcana cast [preset]"), false);
        return 1;
    }

    private static int runSmoke(CommandContext<CommandSourceStack> ctx) {
        CommandSourceStack source = ctx.getSource();
        int fail = SmokeAssertions.runAndReport(line ->
                source.sendSuccess(() -> Component.literal(line), false));
        return fail == 0 ? 1 : 0;
    }

    private static int listAspects(CommandContext<CommandSourceStack> ctx) {
        CommandSourceStack source = ctx.getSource();
        source.sendSuccess(() -> Component.literal("Primal aspects (" + Aspect.getPrimalAspects().size() + "):"), false);
        for (Aspect aspect : Aspect.getPrimalAspects()) {
            source.sendSuccess(() -> Component.literal(" - " + aspect.getTag() + " / " + aspect.getName()), false);
        }
        source.sendSuccess(() -> Component.literal("Compound aspects (" + Aspect.getCompoundAspects().size() + "):"), false);
        for (Aspect aspect : Aspect.getCompoundAspects()) {
            String comps = aspect.getComponents() == null ? "?" :
                    aspect.getComponents()[0].getTag() + "+" + aspect.getComponents()[1].getTag();
            source.sendSuccess(() -> Component.literal(" - " + aspect.getTag() + " = " + comps), false);
        }
        return Aspect.aspects.size();
    }

    private static int listResearch(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        IPlayerKnowledge knowledge = ArcanaCapabilities.getKnowledge(player);
        ctx.getSource().sendSuccess(() -> Component.literal("Research (" + knowledge.getResearchList().size() + "):"), false);
        for (String key : knowledge.getResearchList()) {
            int stage = knowledge.getResearchStage(key);
            ctx.getSource().sendSuccess(() -> Component.literal(" - " + key + " stage=" + stage
                    + " status=" + knowledge.getResearchStatus(key)), false);
        }
        return knowledge.getResearchList().size();
    }

    private static int addResearch(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        String key = StringArgumentType.getString(ctx, "key");
        boolean ok = ResearchManager.progressResearch(player, key, true);
        ctx.getSource().sendSuccess(() -> Component.literal(ok ? "Progressed research: " + key : "Could not progress: " + key), true);
        return ok ? 1 : 0;
    }

    private static int completeResearch(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        String key = StringArgumentType.getString(ctx, "key");
        boolean ok = ResearchManager.completeResearch(player, key);
        PacketHandler.syncKnowledge(player);
        ctx.getSource().sendSuccess(() -> Component.literal(ok ? "Completed research: " + key : "Could not complete: " + key), true);
        return ok ? 1 : 0;
    }

    private static int removeResearch(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        String key = StringArgumentType.getString(ctx, "key");
        boolean ok = ArcanaCapabilities.getKnowledge(player).removeResearch(key);
        ctx.getSource().sendSuccess(() -> Component.literal(ok ? "Removed research: " + key : "Unknown research: " + key), true);
        return ok ? 1 : 0;
    }

    private static int clearResearch(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        ArcanaCapabilities.getKnowledge(player).clear();
        ctx.getSource().sendSuccess(() -> Component.literal("Cleared all research/knowledge"), true);
        return 1;
    }

    private static int getKnowledge(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        IPlayerKnowledge knowledge = ArcanaCapabilities.getKnowledge(player);
        for (IPlayerKnowledge.EnumKnowledgeType type : IPlayerKnowledge.EnumKnowledgeType.values()) {
            int raw = knowledge.getKnowledgeRaw(type, null);
            int pts = knowledge.getKnowledge(type, null);
            ctx.getSource().sendSuccess(() -> Component.literal(type.name() + ": " + pts + " (" + raw + " raw)"), false);
        }
        return 1;
    }

    private static int addKnowledge(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        IPlayerKnowledge.EnumKnowledgeType type = parseKnowledgeType(StringArgumentType.getString(ctx, "type"));
        int amount = IntegerArgumentType.getInteger(ctx, "amount");
        boolean ok = ArcanaCapabilities.getKnowledge(player).addKnowledge(type, (ResearchCategory) null, amount);
        ctx.getSource().sendSuccess(() -> Component.literal(ok
                ? "Added " + amount + " raw " + type.name()
                : "Failed to add knowledge"), true);
        return ok ? 1 : 0;
    }

    private static int getWarp(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        IPlayerWarp warp = ArcanaCapabilities.getWarp(player);
        for (IPlayerWarp.EnumWarpType type : IPlayerWarp.EnumWarpType.values()) {
            int amount = warp.get(type);
            String label = type == IPlayerWarp.EnumWarpType.NORMAL ? "NORMAL/STICKY" : type.name();
            ctx.getSource().sendSuccess(() -> Component.literal(label + ": " + amount), false);
        }
        int gear = WarpHelper.getGearWarp(player);
        ctx.getSource().sendSuccess(() -> Component.literal("gear: " + gear), false);
        ctx.getSource().sendSuccess(() -> Component.literal("counter: " + warp.getCounter()), false);
        return 1;
    }

    private static int setWarp(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        IPlayerWarp.EnumWarpType type = parseWarpType(StringArgumentType.getString(ctx, "type"));
        int amount = IntegerArgumentType.getInteger(ctx, "amount");
        ArcanaCapabilities.getWarp(player).set(type, amount);
        ctx.getSource().sendSuccess(() -> Component.literal("Set " + type.name() + " warp to " + amount), true);
        return 1;
    }

    private static int addWarp(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        IPlayerWarp.EnumWarpType type = parseWarpType(StringArgumentType.getString(ctx, "type"));
        int amount = IntegerArgumentType.getInteger(ctx, "amount");
        int total = WarpHelper.addWarpToPlayer(player, amount, type);
        int counter = ArcanaCapabilities.getWarp(player).getCounter();
        ctx.getSource().sendSuccess(() -> Component.literal(type.name() + " warp now " + total + " (counter " + counter + ")"), true);
        return total;
    }

    private static int clearWarp(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        ArcanaCapabilities.getWarp(player).clear();
        ctx.getSource().sendSuccess(() -> Component.literal("Cleared all warp"), true);
        return 1;
    }

    private static int forceWarpEvent(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        WarpEvents.checkWarpEvent(player);
        IPlayerWarp warp = ArcanaCapabilities.getWarp(player);
        ctx.getSource().sendSuccess(() -> Component.literal(
                "Forced warp event (counter now " + warp.getCounter() + ", TEMPORARY "
                        + warp.get(IPlayerWarp.EnumWarpType.TEMPORARY) + ")"), true);
        return 1;
    }

    private static int getAura(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        BlockPos pos = player.blockPosition();
        float vis = AuraHelper.getVis(player.level(), pos);
        float flux = AuraHelper.getFlux(player.level(), pos);
        int base = AuraHelper.getAuraBase(player.level(), pos);
        ctx.getSource().sendSuccess(() -> Component.literal(String.format(
                "Aura @ chunk %d,%d — base=%d vis=%.1f flux=%.1f total=%.1f",
                pos.getX() >> 4, pos.getZ() >> 4, base, vis, flux, vis + flux)), false);
        return base;
    }

    private static int addVis(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        float amount = FloatArgumentType.getFloat(ctx, "amount");
        BlockPos pos = player.blockPosition();
        AuraHelper.addVis(player.level(), pos, amount);
        float vis = AuraHelper.getVis(player.level(), pos);
        ctx.getSource().sendSuccess(() -> Component.literal("Added " + amount + " vis → now " + vis), true);
        return (int) vis;
    }

    private static int drainVis(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        float amount = FloatArgumentType.getFloat(ctx, "amount");
        BlockPos pos = player.blockPosition();
        float drained = AuraHelper.drainVis(player.level(), pos, amount, false);
        ctx.getSource().sendSuccess(() -> Component.literal("Drained " + drained + " vis (requested " + amount + ")"), true);
        return (int) drained;
    }

    private static int polluteAura(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        float amount = FloatArgumentType.getFloat(ctx, "amount");
        BlockPos pos = player.blockPosition();
        AuraHelper.polluteAura(player.level(), pos, amount, true);
        float flux = AuraHelper.getFlux(player.level(), pos);
        ctx.getSource().sendSuccess(() -> Component.literal("Added " + amount + " flux → now " + flux), true);
        return (int) flux;
    }

    private static WardedJarBlockEntity getLookedJar(ServerPlayer player) throws CommandSyntaxException {
        BlockHitResult hit = player.level().clip(new ClipContext(
                player.getEyePosition(1f),
                player.getEyePosition(1f).add(player.getLookAngle().scale(5)),
                ClipContext.Block.OUTLINE,
                ClipContext.Fluid.NONE,
                player));
        if (hit.getType() != HitResult.Type.BLOCK) {
            throw NO_JAR.create();
        }
        if (!(player.level().getBlockEntity(hit.getBlockPos()) instanceof WardedJarBlockEntity jar)) {
            throw NO_JAR.create();
        }
        return jar;
    }

    private static int getEssentia(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        WardedJarBlockEntity jar = getLookedJar(player);
        Aspect aspect = jar.getAspect();
        ctx.getSource().sendSuccess(() -> Component.literal(aspect == null
                ? "Jar empty"
                : "Jar: " + aspect.getTag() + " x" + jar.getAmount()), false);
        return jar.getAmount();
    }

    private static int fillEssentia(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        WardedJarBlockEntity jar = getLookedJar(player);
        Aspect aspect = Aspect.getAspect(StringArgumentType.getString(ctx, "aspect").toLowerCase());
        if (aspect == null) {
            throw BAD_ASPECT.create();
        }
        int amount = IntegerArgumentType.getInteger(ctx, "amount");
        jar.addToContainer(aspect, amount);
        ctx.getSource().sendSuccess(() -> Component.literal("Jar now " + jar.getAspect().getTag() + " x" + jar.getAmount()), true);
        return jar.getAmount();
    }

    private static int clearEssentia(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        WardedJarBlockEntity jar = getLookedJar(player);
        if (jar.getAspect() != null) {
            jar.takeFromContainer(jar.getAspect(), jar.getAmount());
        }
        ctx.getSource().sendSuccess(() -> Component.literal("Jar cleared"), true);
        return 1;
    }

    private static CrucibleBlockEntity getLookedCrucible(ServerPlayer player) throws CommandSyntaxException {
        BlockHitResult hit = player.level().clip(new ClipContext(
                player.getEyePosition(1f),
                player.getEyePosition(1f).add(player.getLookAngle().scale(5)),
                ClipContext.Block.OUTLINE,
                ClipContext.Fluid.NONE,
                player));
        if (hit.getType() != HitResult.Type.BLOCK) {
            throw NO_CRUCIBLE.create();
        }
        if (!(player.level().getBlockEntity(hit.getBlockPos()) instanceof CrucibleBlockEntity crucible)) {
            throw NO_CRUCIBLE.create();
        }
        return crucible;
    }

    private static int getCrucible(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        CrucibleBlockEntity crucible = getLookedCrucible(player);
        ctx.getSource().sendSuccess(() -> Component.literal("Heat=" + crucible.getHeat()
                + " water=" + crucible.getTank().getFluidAmount()
                + (crucible.isBoiling() ? " BOILING" : "")), false);
        for (Aspect aspect : crucible.getAspects().getAspects()) {
            int amt = crucible.getAspects().getAmount(aspect);
            ctx.getSource().sendSuccess(() -> Component.literal(" - " + aspect.getTag() + " x" + amt), false);
        }
        return crucible.getHeat();
    }

    private static int addCrucibleAspect(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        CrucibleBlockEntity crucible = getLookedCrucible(player);
        Aspect aspect = Aspect.getAspect(StringArgumentType.getString(ctx, "aspect").toLowerCase());
        if (aspect == null) {
            throw BAD_ASPECT.create();
        }
        int amount = IntegerArgumentType.getInteger(ctx, "amount");
        crucible.addToContainer(aspect, amount);
        ctx.getSource().sendSuccess(() -> Component.literal("Added " + amount + " " + aspect.getTag()), true);
        return amount;
    }

    private static int clearCrucible(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        CrucibleBlockEntity crucible = getLookedCrucible(player);
        crucible.setAspects(new arcana.api.aspects.AspectList());
        ctx.getSource().sendSuccess(() -> Component.literal("Crucible aspects cleared"), true);
        return 1;
    }

    private static int forceCast(CommandContext<CommandSourceStack> ctx, String preset) throws CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        FocusEngine.castPreset(player, preset);
        ctx.getSource().sendSuccess(() -> Component.literal("Cast " + preset + " (no vis cost)"), true);
        return 1;
    }

    private static int giveFocus(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        String preset = StringArgumentType.getString(ctx, "preset");
        FocusPackage pkg = FocusPackage.fromPreset(preset);
        ItemStack stack = arcana.common.items.casters.ItemFocus.createProgrammed(
                (arcana.common.items.casters.ItemFocus) arcana.registry.ModItems.FOCUS_1.get(), pkg);
        if (!player.getInventory().add(stack)) {
            player.drop(stack, false);
        }
        ctx.getSource().sendSuccess(() -> Component.literal("Gave focus: " + pkg.describe()), true);
        return 1;
    }

    private static IPlayerKnowledge.EnumKnowledgeType parseKnowledgeType(String raw) throws CommandSyntaxException {
        try {
            return IPlayerKnowledge.EnumKnowledgeType.valueOf(raw.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw BAD_KNOWLEDGE_TYPE.create();
        }
    }

    private static IPlayerWarp.EnumWarpType parseWarpType(String raw) throws CommandSyntaxException {
        String key = raw.toUpperCase();
        if ("STICKY".equals(key)) {
            return IPlayerWarp.EnumWarpType.NORMAL;
        }
        try {
            return IPlayerWarp.EnumWarpType.valueOf(key);
        } catch (IllegalArgumentException e) {
            throw BAD_WARP_TYPE.create();
        }
    }
}
