package arcana.registry;

import arcana.Arcana;
import arcana.api.casters.FocusPackage;
import arcana.common.items.casters.ItemCaster;
import arcana.common.items.casters.ItemFocus;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public final class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Arcana.MODID);

    public static final RegistryObject<CreativeModeTab> MAIN = CREATIVE_MODE_TABS.register("main",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.arcana"))
                    .icon(() -> new ItemStack(ModItems.THAUMONOMICON.get()))
                    .displayItems((params, output) -> {
                        output.accept(ModItems.CRYSTAL_AER.get());
                        output.accept(ModItems.CRYSTAL_TERRA.get());
                        output.accept(ModItems.CRYSTAL_IGNIS.get());
                        output.accept(ModItems.CRYSTAL_AQUA.get());
                        output.accept(ModItems.CRYSTAL_ORDO.get());
                        output.accept(ModItems.CRYSTAL_PERDITIO.get());
                        output.accept(ModItems.CRYSTAL_CLUSTER_AER.get());
                        output.accept(ModItems.CRYSTAL_CLUSTER_TERRA.get());
                        output.accept(ModItems.CRYSTAL_CLUSTER_IGNIS.get());
                        output.accept(ModItems.CRYSTAL_CLUSTER_AQUA.get());
                        output.accept(ModItems.CRYSTAL_CLUSTER_ORDO.get());
                        output.accept(ModItems.CRYSTAL_CLUSTER_PERDITIO.get());
                        output.accept(ModItems.THAUMIUM_INGOT.get());
                        output.accept(ModItems.THAUMIUM_NUGGET.get());
                        output.accept(ModItems.THAUMIUM_PLATE.get());
                        output.accept(ModItems.THAUMIUM_SWORD.get());
                        output.accept(ModItems.THAUMIUM_PICKAXE.get());
                        output.accept(ModItems.THAUMIUM_AXE.get());
                        output.accept(ModItems.THAUMIUM_SHOVEL.get());
                        output.accept(ModItems.THAUMIUM_HOE.get());
                        output.accept(ModItems.THAUMIUM_HELMET.get());
                        output.accept(ModItems.THAUMIUM_CHESTPLATE.get());
                        output.accept(ModItems.THAUMIUM_LEGGINGS.get());
                        output.accept(ModItems.THAUMIUM_BOOTS.get());
                        output.accept(ModItems.VOID_SEED.get());
                        output.accept(ModItems.VOID_INGOT.get());
                        output.accept(ModItems.VOID_NUGGET.get());
                        output.accept(ModItems.VOID_PLATE.get());
                        output.accept(ModItems.VOID_SWORD.get());
                        output.accept(ModItems.VOID_PICKAXE.get());
                        output.accept(ModItems.VOID_AXE.get());
                        output.accept(ModItems.VOID_SHOVEL.get());
                        output.accept(ModItems.VOID_HOE.get());
                        output.accept(ModItems.VOID_HELMET.get());
                        output.accept(ModItems.VOID_CHESTPLATE.get());
                        output.accept(ModItems.VOID_LEGGINGS.get());
                        output.accept(ModItems.VOID_BOOTS.get());
                        output.accept(ModItems.VOID_ROBE_HELMET.get());
                        output.accept(ModItems.VOID_ROBE_CHESTPLATE.get());
                        output.accept(ModItems.VOID_ROBE_LEGGINGS.get());
                        output.accept(ModItems.VOID_ROBE_BOOTS.get());
                        output.accept(ModItems.GOGGLES.get());
                        output.accept(ModItems.GOGGLES_ADVANCED.get());
                        output.accept(ModItems.TRAVELLER_BOOTS.get());
                        output.accept(ModItems.CLOUDSTEPPER.get());
                        output.accept(ModItems.CHARM_UNDYING.get());
                        output.accept(ModItems.SALIS_MUNDUS.get());
                        output.accept(ModItems.QUICKSILVER.get());
                        output.accept(ModItems.MAGIC_TALLOW.get());
                        output.accept(ModItems.NITOR.get());
                        output.accept(ModItems.ALUMENTUM.get());
                        output.accept(ModItems.THAUMOMETER.get());
                        output.accept(ModItems.THAUMONOMICON.get());
                        output.accept(ModItems.ARCANE_WORKBENCH.get());
                        output.accept(ModItems.RESEARCH_TABLE.get());
                        output.accept(ModItems.WARDED_JAR.get());
                        output.accept(ModItems.ESSENTIA_TUBE.get());
                        output.accept(ModItems.ESSENTIA_FILTER_TUBE.get());
                        output.accept(ModItems.ESSENTIA_VALVE.get());
                        output.accept(ModItems.CRUCIBLE.get());
                        output.accept(ModItems.ESSENTIA_SMELTER.get());
                        output.accept(ModItems.ALEMBIC.get());
                        output.accept(ModItems.PEDESTAL.get());
                        output.accept(ModItems.INFUSION_MATRIX.get());
                        output.accept(ModItems.FOCAL_MANIPULATOR.get());
                        output.accept(ModItems.LABEL.get());
                        output.accept(ModItems.BELLOWS.get());
                        output.accept(ModItems.ARCANE_LEVITATOR.get());
                        output.accept(ModItems.MAGIC_MIRROR.get());
                        output.accept(ModItems.LAMP_OF_GROWTH.get());
                        output.accept(ModItems.HUNGRY_CHEST.get());
                        output.accept(ModItems.CASTER_BASIC.get());
                        output.accept(ModItems.FOCUS_1.get());
                        output.accept(ModItems.FOCUS_2.get());
                        output.accept(ModItems.FOCUS_3.get());
                        output.accept(ModItems.ARCANE_NOTE.get());
                        output.accept(ModItems.RING_APPRENTICE.get());
                        output.accept(ModItems.AMULET_VIS.get());
                        output.accept(single(ItemFocus.createProgrammed(
                                (ItemFocus) ModItems.FOCUS_1.get(), FocusPackage.touchFire())));
                        output.accept(single(ItemFocus.createProgrammed(
                                (ItemFocus) ModItems.FOCUS_1.get(), FocusPackage.projectileFire())));
                        output.accept(single(ItemFocus.createProgrammed(
                                (ItemFocus) ModItems.FOCUS_1.get(), FocusPackage.touchFrost())));
                        output.accept(single(ItemFocus.createProgrammed(
                                (ItemFocus) ModItems.FOCUS_1.get(), FocusPackage.projectileFrost())));
                        output.accept(single(programmedCaster(FocusPackage.touchFire())));
                        output.accept(single(programmedCaster(FocusPackage.projectileFire())));
                        output.accept(ModItems.CINDERPEARL.get());
                        output.accept(ModItems.SHIMMERLEAF.get());
                        output.accept(ModItems.GREATWOOD_LOG.get());
                        output.accept(ModItems.GREATWOOD_LEAVES.get());
                        output.accept(ModItems.GREATWOOD_SAPLING.get());
                        output.accept(ModItems.GREATWOOD_PLANKS.get());
                        output.accept(ModItems.SILVERWOOD_LOG.get());
                        output.accept(ModItems.SILVERWOOD_LEAVES.get());
                        output.accept(ModItems.SILVERWOOD_SAPLING.get());
                        output.accept(ModItems.SILVERWOOD_PLANKS.get());
                        output.accept(ModItems.AURA_NODE.get());
                        output.accept(ModItems.ELDRITCH_STONE.get());
                        output.accept(ModItems.FLUX_GOO.get());
                        output.accept(ModItems.BRAIN.get());
                        output.accept(ModItems.BRAINY_ZOMBIE_SPAWN_EGG.get());
                        output.accept(ModItems.ELDRITCH_GUARDIAN_SPAWN_EGG.get());
                        output.accept(ModItems.ELDRITCH_WARDEN_SPAWN_EGG.get());
                        output.accept(ModItems.WISP_SPAWN_EGG.get());
                        output.accept(ModItems.MIND_SPIDER_SPAWN_EGG.get());
                        output.accept(ModItems.GOLEM.get());
                        output.accept(ModItems.SEAL_BLANK.get());
                        output.accept(ModItems.SEAL_GATHER.get());
                        output.accept(ModItems.SEAL_GUARD.get());
                        output.accept(ModItems.SEAL_FILL.get());
                        output.accept(ModItems.SEAL_EMPTY.get());
                        output.accept(ModItems.SEAL_HARVEST.get());
                        output.accept(ModItems.SEAL_USE.get());
                        output.accept(ModItems.SEAL_BUTCHER.get());
                        output.accept(ModItems.GOLEM_CORE_GATHER.get());
                        output.accept(ModItems.GOLEM_CORE_GUARD.get());
                    })
                    .build());

    /** Creative tab entries must always be single items, never a full stack. */
    private static ItemStack single(ItemStack stack) {
        stack.setCount(1);
        return stack;
    }

    private static ItemStack programmedCaster(FocusPackage pkg) {
        ItemStack caster = new ItemStack(ModItems.CASTER_BASIC.get());
        ItemStack focus = ItemFocus.createProgrammed((ItemFocus) ModItems.FOCUS_1.get(), pkg);
        ((ItemCaster) ModItems.CASTER_BASIC.get()).setFocus(caster, focus);
        return caster;
    }

    private ModCreativeTabs() {
    }
}
