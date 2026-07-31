package arcana.common.blocks;

import arcana.api.capabilities.ArcanaCapabilities;
import arcana.api.capabilities.IPlayerWarp;
import arcana.common.lib.events.WarpHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;

/**
 * Outer Lands portal tease — no dimension teleport.
 */
public class OuterLandsPortalBlock extends Block {
    public OuterLandsPortalBlock() {
        super(BlockBehaviour.Properties.of()
                .mapColor(MapColor.COLOR_PURPLE)
                .strength(4.0f, 1200.0f)
                .sound(SoundType.GLASS)
                .lightLevel(state -> 8)
                .requiresCorrectToolForDrops());
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        super.stepOn(level, pos, state, entity);
        if (level.isClientSide || !(entity instanceof ServerPlayer player)) {
            return;
        }
        if (player.tickCount % 40 != 0) {
            return;
        }
        player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 160, 0, false, true));
        WarpHelper.addWarpToPlayer(player, 1, IPlayerWarp.EnumWarpType.NORMAL);
        WarpHelper.addWarpToPlayer(player, 1, IPlayerWarp.EnumWarpType.TEMPORARY);

        boolean researched = ArcanaCapabilities.getKnowledge(player).isResearchComplete("OUTERLANDS_TEASE")
                || ArcanaCapabilities.getKnowledge(player).isResearchKnown("OUTERLANDS_TEASE");
        if (researched || player.tickCount % 200 < 40) {
            player.displayClientMessage(
                    Component.translatable("block.arcana.outer_lands_portal.sealed")
                            .withStyle(ChatFormatting.DARK_PURPLE, ChatFormatting.ITALIC),
                    true);
        }
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        for (int i = 0; i < 3; i++) {
            double x = pos.getX() + random.nextDouble();
            double y = pos.getY() + 0.1 + random.nextDouble() * 0.9;
            double z = pos.getZ() + random.nextDouble();
            level.addParticle(ParticleTypes.PORTAL, x, y, z,
                    (random.nextDouble() - 0.5) * 0.5, random.nextDouble() * 0.3,
                    (random.nextDouble() - 0.5) * 0.5);
        }
    }
}
