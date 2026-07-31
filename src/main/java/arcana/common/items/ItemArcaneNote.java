package arcana.common.items;

import arcana.api.capabilities.ArcanaCapabilities;
import arcana.api.capabilities.IPlayerKnowledge;
import arcana.api.research.ResearchCategories;
import arcana.api.research.ResearchCategory;
import arcana.common.lib.research.ResearchManager;
import arcana.common.network.PacketHandler;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;

/** G15: Right-click grants 1 observation knowledge point (or advances THEORYRESEARCH). */
public class ItemArcaneNote extends Item {
    public ItemArcaneNote() {
        super(new Item.Properties().stacksTo(16).rarity(Rarity.UNCOMMON));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide) {
            return InteractionResultHolder.success(stack);
        }
        IPlayerKnowledge knowledge = ArcanaCapabilities.getKnowledge(player);
        ResearchCategory cat = ResearchCategories.getResearchCategory("BASICS");
        boolean granted = knowledge.addKnowledge(IPlayerKnowledge.EnumKnowledgeType.OBSERVATION, cat, 16);
        if (!granted) {
            ResearchManager.progressResearch(player, "THEORYRESEARCH", true);
        } else {
            player.displayClientMessage(Component.translatable("arcana.note.observation"), true);
        }
        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }
        if (player instanceof ServerPlayer serverPlayer) {
            PacketHandler.syncKnowledge(serverPlayer);
        }
        return InteractionResultHolder.consume(stack);
    }
}
