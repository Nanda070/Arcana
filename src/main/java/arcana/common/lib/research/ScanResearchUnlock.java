package arcana.common.lib.research;

import arcana.api.research.IScanThing;
import arcana.api.research.ScanningManager;
import arcana.registry.ModBlocks;
import arcana.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/**
 * G14/I6: Scanning specific world blocks unlocks named research entries.
 */
public class ScanResearchUnlock implements IScanThing {
    private final Block block;
    private final Item item;
    private final String researchKey;

    public ScanResearchUnlock(Block block, Item item, String researchKey) {
        this.block = block;
        this.item = item;
        this.researchKey = researchKey;
    }

    public static ScanResearchUnlock of(java.util.function.Supplier<Block> block,
                                        java.util.function.Supplier<Item> item,
                                        String researchKey) {
        return new ScanResearchUnlock(block.get(), item.get(), researchKey);
    }

    @Override
    public boolean checkThing(Player player, Object object) {
        if (object instanceof BlockPos pos && player.level() != null) {
            BlockState state = player.level().getBlockState(pos);
            return state.is(block);
        }
        ItemStack stack = stackOf(object);
        return !stack.isEmpty() && stack.is(item);
    }

    @Override
    public String getResearchKey(Player player, Object object) {
        return researchKey;
    }

    private static ItemStack stackOf(Object object) {
        if (object instanceof ItemStack stack) {
            return stack;
        }
        if (object instanceof ItemEntity itemEntity) {
            return itemEntity.getItem();
        }
        return ItemStack.EMPTY;
    }

    /** I6: register all scan→research mappings once ModBlocks/Items exist. */
    public static void registerAll() {
        ScanningManager.addScannableThing(of(ModBlocks.GREATWOOD_LOG, ModItems.GREATWOOD_LOG, "GREATWOOD"));
        ScanningManager.addScannableThing(of(ModBlocks.SILVERWOOD_LOG, ModItems.SILVERWOOD_LOG, "SILVERWOOD"));
        ScanningManager.addScannableThing(of(ModBlocks.AURA_NODE, ModItems.AURA_NODE, "AURANODE"));
        ScanningManager.addScannableThing(of(ModBlocks.INFUSION_MATRIX, ModItems.INFUSION_MATRIX, "INFUSION"));
        ScanningManager.addScannableThing(of(ModBlocks.PEDESTAL, ModItems.PEDESTAL, "INFUSION"));
        ScanningManager.addScannableThing(of(ModBlocks.CRUCIBLE, ModItems.CRUCIBLE, "FIRSTSTEPS"));
        ScanningManager.addScannableThing(of(ModBlocks.GOLEM_SEAL, ModItems.SEAL_BLANK, "GOLEMBASIC"));
        ScanningManager.addScannableThing(new ScanResearchUnlock(
                ModBlocks.GOLEM_SEAL.get(), ModItems.GOLEM.get(), "GOLEMBASIC"));
        ScanningManager.addScannableThing(new ScanResearchUnlock(
                ModBlocks.ELDRITCH_STONE.get(), ModItems.VOID_SEED.get(), "ELDRITCH"));
        ScanningManager.addScannableThing(of(ModBlocks.ELDRITCH_STONE, ModItems.ELDRITCH_STONE, "ELDRITCH"));
        ScanningManager.addScannableThing(of(ModBlocks.FOCAL_MANIPULATOR, ModItems.FOCAL_MANIPULATOR, "BASEAUROMANCY"));
        ScanningManager.addScannableThing(of(ModBlocks.CRYSTAL_CLUSTER_AER, ModItems.CRYSTAL_CLUSTER_AER, "FOCUSELEMENTAL"));
        ScanningManager.addScannableThing(of(ModBlocks.CRYSTAL_CLUSTER_TERRA, ModItems.CRYSTAL_CLUSTER_TERRA, "FOCUSELEMENTAL"));
        ScanningManager.addScannableThing(of(ModBlocks.CRYSTAL_CLUSTER_IGNIS, ModItems.CRYSTAL_CLUSTER_IGNIS, "FOCUSELEMENTAL"));
        ScanningManager.addScannableThing(of(ModBlocks.CRYSTAL_CLUSTER_AQUA, ModItems.CRYSTAL_CLUSTER_AQUA, "FOCUSELEMENTAL"));
        ScanningManager.addScannableThing(of(ModBlocks.CRYSTAL_CLUSTER_ORDO, ModItems.CRYSTAL_CLUSTER_ORDO, "FOCUSELEMENTAL"));
        ScanningManager.addScannableThing(of(ModBlocks.CRYSTAL_CLUSTER_PERDITIO, ModItems.CRYSTAL_CLUSTER_PERDITIO, "FOCUSELEMENTAL"));
    }
}
