package arcana.common.items;

import arcana.client.ClientHooks;
import arcana.common.lib.research.ResearchManager;
import arcana.common.network.PacketHandler;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;

public class ItemThaumonomicon extends Item {
    public ItemThaumonomicon() {
        super(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            // Flag research that unlocks FIRSTSTEPS parents.
            ResearchManager.completeResearch(serverPlayer, "gotthaumonomicon");
            ResearchManager.startResearchWithPopup(serverPlayer, "FIRSTSTEPS");
            PacketHandler.syncKnowledge(serverPlayer);
        }
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> ClientHooks::openThaumonomicon);
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }
}
