package arcana.registry;

import arcana.Arcana;
import arcana.api.aspects.Aspect;
import arcana.common.items.ItemArcaneNote;
import arcana.common.items.ItemGoggles;
import arcana.common.items.ItemLabel;
import arcana.common.items.ItemThaumonomicon;
import arcana.common.items.ItemThaumometer;
import arcana.common.items.ThaumiumArmorMaterial;
import arcana.common.items.ThaumiumTier;
import arcana.common.items.VoidArmorItem;
import arcana.common.items.VoidArmorMaterial;
import arcana.common.items.VoidAxeItem;
import arcana.common.items.VoidHoeItem;
import arcana.common.items.VoidPickaxeItem;
import arcana.common.items.VoidRobeArmorItem;
import arcana.common.items.VoidShovelItem;
import arcana.common.items.VoidSwordItem;
import arcana.common.items.VoidTier;
import arcana.common.items.baubles.ItemAmuletVis;
import arcana.common.items.baubles.ItemRingApprentice;
import arcana.common.items.casters.ItemCaster;
import arcana.common.items.casters.ItemFocus;
import arcana.common.golems.GolemJob;
import arcana.common.golems.GolemPlacerItem;
import arcana.common.golems.GolemSealItem;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.SwordItem;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Arcana.MODID);

    public static final RegistryObject<Item> CRYSTAL_AER = crystal("crystal_aer");
    public static final RegistryObject<Item> CRYSTAL_TERRA = crystal("crystal_terra");
    public static final RegistryObject<Item> CRYSTAL_IGNIS = crystal("crystal_ignis");
    public static final RegistryObject<Item> CRYSTAL_AQUA = crystal("crystal_aqua");
    public static final RegistryObject<Item> CRYSTAL_ORDO = crystal("crystal_ordo");
    public static final RegistryObject<Item> CRYSTAL_PERDITIO = crystal("crystal_perditio");

    public static final RegistryObject<Item> THAUMIUM_INGOT = ITEMS.register("thaumium_ingot",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> THAUMIUM_NUGGET = ITEMS.register("thaumium_nugget",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> THAUMIUM_PLATE = ITEMS.register("thaumium_plate",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> SALIS_MUNDUS = ITEMS.register("salis_mundus",
            () -> new Item(new Item.Properties().rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> NITOR = ITEMS.register("nitor",
            () -> new Item(new Item.Properties().rarity(Rarity.UNCOMMON)) {
                @Override
                public boolean isFoil(ItemStack stack) {
                    return true;
                }
            });
    public static final RegistryObject<Item> ALUMENTUM = ITEMS.register("alumentum",
            () -> new Item(new Item.Properties().rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> THAUMOMETER = ITEMS.register("thaumometer",
            ItemThaumometer::new);

    public static final RegistryObject<Item> THAUMONOMICON = ITEMS.register("thaumonomicon",
            ItemThaumonomicon::new);

    // Tools
    public static final RegistryObject<Item> THAUMIUM_SWORD = ITEMS.register("thaumium_sword",
            () -> new SwordItem(ThaumiumTier.INSTANCE, 3, -2.4f, new Item.Properties().rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> THAUMIUM_PICKAXE = ITEMS.register("thaumium_pickaxe",
            () -> new PickaxeItem(ThaumiumTier.INSTANCE, 1, -2.8f, new Item.Properties().rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> THAUMIUM_AXE = ITEMS.register("thaumium_axe",
            () -> new AxeItem(ThaumiumTier.INSTANCE, 5.5f, -3.0f, new Item.Properties().rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> THAUMIUM_SHOVEL = ITEMS.register("thaumium_shovel",
            () -> new ShovelItem(ThaumiumTier.INSTANCE, 1.5f, -3.0f, new Item.Properties().rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> THAUMIUM_HOE = ITEMS.register("thaumium_hoe",
            () -> new HoeItem(ThaumiumTier.INSTANCE, -2, -1.0f, new Item.Properties().rarity(Rarity.UNCOMMON)));

    // Armor
    public static final RegistryObject<Item> THAUMIUM_HELMET = ITEMS.register("thaumium_helmet",
            () -> new ArmorItem(ThaumiumArmorMaterial.INSTANCE, ArmorItem.Type.HELMET, new Item.Properties().rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> THAUMIUM_CHESTPLATE = ITEMS.register("thaumium_chestplate",
            () -> new ArmorItem(ThaumiumArmorMaterial.INSTANCE, ArmorItem.Type.CHESTPLATE, new Item.Properties().rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> THAUMIUM_LEGGINGS = ITEMS.register("thaumium_leggings",
            () -> new ArmorItem(ThaumiumArmorMaterial.INSTANCE, ArmorItem.Type.LEGGINGS, new Item.Properties().rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> THAUMIUM_BOOTS = ITEMS.register("thaumium_boots",
            () -> new ArmorItem(ThaumiumArmorMaterial.INSTANCE, ArmorItem.Type.BOOTS, new Item.Properties().rarity(Rarity.UNCOMMON)));

    // Void metal (D4)
    public static final RegistryObject<Item> VOID_SEED = ITEMS.register("void_seed",
            () -> new Item(new Item.Properties().rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> VOID_INGOT = ITEMS.register("void_ingot",
            () -> new Item(new Item.Properties().rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> VOID_NUGGET = ITEMS.register("void_nugget",
            () -> new Item(new Item.Properties().rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> VOID_PLATE = ITEMS.register("void_plate",
            () -> new Item(new Item.Properties().rarity(Rarity.UNCOMMON)));

    public static final RegistryObject<Item> VOID_SWORD = ITEMS.register("void_sword",
            () -> new VoidSwordItem(VoidTier.INSTANCE, 3, -2.4f, new Item.Properties().rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> VOID_PICKAXE = ITEMS.register("void_pickaxe",
            () -> new VoidPickaxeItem(VoidTier.INSTANCE, 1, -2.8f, new Item.Properties().rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> VOID_AXE = ITEMS.register("void_axe",
            () -> new VoidAxeItem(VoidTier.INSTANCE, 5.5f, -3.0f, new Item.Properties().rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> VOID_SHOVEL = ITEMS.register("void_shovel",
            () -> new VoidShovelItem(VoidTier.INSTANCE, 1.5f, -3.0f, new Item.Properties().rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> VOID_HOE = ITEMS.register("void_hoe",
            () -> new VoidHoeItem(VoidTier.INSTANCE, -2, -1.0f, new Item.Properties().rarity(Rarity.UNCOMMON)));

    public static final RegistryObject<Item> VOID_HELMET = ITEMS.register("void_helmet",
            () -> new VoidArmorItem(VoidArmorMaterial.INSTANCE, ArmorItem.Type.HELMET, new Item.Properties().rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> VOID_CHESTPLATE = ITEMS.register("void_chestplate",
            () -> new VoidArmorItem(VoidArmorMaterial.INSTANCE, ArmorItem.Type.CHESTPLATE, new Item.Properties().rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> VOID_LEGGINGS = ITEMS.register("void_leggings",
            () -> new VoidArmorItem(VoidArmorMaterial.INSTANCE, ArmorItem.Type.LEGGINGS, new Item.Properties().rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> VOID_BOOTS = ITEMS.register("void_boots",
            () -> new VoidArmorItem(VoidArmorMaterial.INSTANCE, ArmorItem.Type.BOOTS, new Item.Properties().rarity(Rarity.UNCOMMON)));

    // Void robes (F4)
    public static final RegistryObject<Item> VOID_ROBE_HELMET = ITEMS.register("void_robe_helmet",
            () -> new VoidRobeArmorItem(VoidArmorMaterial.INSTANCE, ArmorItem.Type.HELMET, new Item.Properties().rarity(Rarity.RARE)));
    public static final RegistryObject<Item> VOID_ROBE_CHESTPLATE = ITEMS.register("void_robe_chestplate",
            () -> new VoidRobeArmorItem(VoidArmorMaterial.INSTANCE, ArmorItem.Type.CHESTPLATE, new Item.Properties().rarity(Rarity.RARE)));
    public static final RegistryObject<Item> VOID_ROBE_LEGGINGS = ITEMS.register("void_robe_leggings",
            () -> new VoidRobeArmorItem(VoidArmorMaterial.INSTANCE, ArmorItem.Type.LEGGINGS, new Item.Properties().rarity(Rarity.RARE)));
    public static final RegistryObject<Item> VOID_ROBE_BOOTS = ITEMS.register("void_robe_boots",
            () -> new VoidRobeArmorItem(VoidArmorMaterial.INSTANCE, ArmorItem.Type.BOOTS, new Item.Properties().rarity(Rarity.RARE)));

    public static final RegistryObject<Item> GOGGLES = ITEMS.register("goggles", ItemGoggles::new);

    public static final RegistryObject<Item> ARCANE_WORKBENCH = ITEMS.register("arcane_workbench",
            () -> new BlockItem(ModBlocks.ARCANE_WORKBENCH.get(), new Item.Properties()));

    public static final RegistryObject<Item> RESEARCH_TABLE = ITEMS.register("research_table",
            () -> new BlockItem(ModBlocks.RESEARCH_TABLE.get(), new Item.Properties()));

    public static final RegistryObject<Item> WARDED_JAR = ITEMS.register("warded_jar",
            () -> new BlockItem(ModBlocks.WARDED_JAR.get(), new Item.Properties()));

    public static final RegistryObject<Item> ESSENTIA_TUBE = ITEMS.register("essentia_tube",
            () -> new BlockItem(ModBlocks.ESSENTIA_TUBE.get(), new Item.Properties()));

    public static final RegistryObject<Item> ESSENTIA_FILTER_TUBE = ITEMS.register("essentia_filter_tube",
            () -> new BlockItem(ModBlocks.ESSENTIA_FILTER_TUBE.get(), new Item.Properties()));

    public static final RegistryObject<Item> ESSENTIA_VALVE = ITEMS.register("essentia_valve",
            () -> new BlockItem(ModBlocks.ESSENTIA_VALVE.get(), new Item.Properties()));

    public static final RegistryObject<Item> CRUCIBLE = ITEMS.register("crucible",
            () -> new BlockItem(ModBlocks.CRUCIBLE.get(), new Item.Properties()));

    public static final RegistryObject<Item> ESSENTIA_SMELTER = ITEMS.register("essentia_smelter",
            () -> new BlockItem(ModBlocks.ESSENTIA_SMELTER.get(), new Item.Properties()));

    public static final RegistryObject<Item> ALEMBIC = ITEMS.register("alembic",
            () -> new BlockItem(ModBlocks.ALEMBIC.get(), new Item.Properties()));
    public static final RegistryObject<Item> PEDESTAL = ITEMS.register("pedestal",
            () -> new BlockItem(ModBlocks.PEDESTAL.get(), new Item.Properties()));
    public static final RegistryObject<Item> INFUSION_MATRIX = ITEMS.register("infusion_matrix",
            () -> new BlockItem(ModBlocks.INFUSION_MATRIX.get(), new Item.Properties()));
    public static final RegistryObject<Item> FOCAL_MANIPULATOR = ITEMS.register("focal_manipulator",
            () -> new BlockItem(ModBlocks.FOCAL_MANIPULATOR.get(), new Item.Properties()));

    public static final RegistryObject<Item> LABEL = ITEMS.register("label", ItemLabel::new);
    public static final RegistryObject<Item> BELLOWS = ITEMS.register("bellows",
            () -> new BlockItem(ModBlocks.BELLOWS.get(), new Item.Properties()));

    // Casters / Foci (M10 / E1-E2)
    public static final RegistryObject<Item> CASTER_BASIC = ITEMS.register("caster_basic", ItemCaster::new);
    public static final RegistryObject<Item> FOCUS_1 = ITEMS.register("focus_1",
            () -> new ItemFocus(25));
    public static final RegistryObject<Item> FOCUS_2 = ITEMS.register("focus_2",
            () -> new ItemFocus(50));
    public static final RegistryObject<Item> FOCUS_3 = ITEMS.register("focus_3",
            () -> new ItemFocus(75));
    public static final RegistryObject<Item> ARCANE_NOTE = ITEMS.register("arcane_note", ItemArcaneNote::new);
    public static final RegistryObject<Item> RING_APPRENTICE = ITEMS.register("ring_apprentice", ItemRingApprentice::new);
    public static final RegistryObject<Item> AMULET_VIS = ITEMS.register("amulet_vis", ItemAmuletVis::new);

    // Worldgen (M11 / D6)
    public static final RegistryObject<Item> CINDERPEARL = ITEMS.register("cinderpearl",
            () -> new BlockItem(ModBlocks.CINDERPEARL.get(), new Item.Properties()));
    public static final RegistryObject<Item> SHIMMERLEAF = ITEMS.register("shimmerleaf",
            () -> new BlockItem(ModBlocks.SHIMMERLEAF.get(), new Item.Properties()));
    public static final RegistryObject<Item> CRYSTAL_CLUSTER_AER = ITEMS.register("crystal_cluster_aer",
            () -> new BlockItem(ModBlocks.CRYSTAL_CLUSTER_AER.get(), new Item.Properties()));
    public static final RegistryObject<Item> CRYSTAL_CLUSTER_TERRA = ITEMS.register("crystal_cluster_terra",
            () -> new BlockItem(ModBlocks.CRYSTAL_CLUSTER_TERRA.get(), new Item.Properties()));
    public static final RegistryObject<Item> CRYSTAL_CLUSTER_IGNIS = ITEMS.register("crystal_cluster_ignis",
            () -> new BlockItem(ModBlocks.CRYSTAL_CLUSTER_IGNIS.get(), new Item.Properties()));
    public static final RegistryObject<Item> CRYSTAL_CLUSTER_AQUA = ITEMS.register("crystal_cluster_aqua",
            () -> new BlockItem(ModBlocks.CRYSTAL_CLUSTER_AQUA.get(), new Item.Properties()));
    public static final RegistryObject<Item> CRYSTAL_CLUSTER_ORDO = ITEMS.register("crystal_cluster_ordo",
            () -> new BlockItem(ModBlocks.CRYSTAL_CLUSTER_ORDO.get(), new Item.Properties()));
    public static final RegistryObject<Item> CRYSTAL_CLUSTER_PERDITIO = ITEMS.register("crystal_cluster_perditio",
            () -> new BlockItem(ModBlocks.CRYSTAL_CLUSTER_PERDITIO.get(), new Item.Properties()));

    // Trees (E5)
    public static final RegistryObject<Item> GREATWOOD_LOG = ITEMS.register("greatwood_log",
            () -> new BlockItem(ModBlocks.GREATWOOD_LOG.get(), new Item.Properties()));
    public static final RegistryObject<Item> GREATWOOD_LEAVES = ITEMS.register("greatwood_leaves",
            () -> new BlockItem(ModBlocks.GREATWOOD_LEAVES.get(), new Item.Properties()));
    public static final RegistryObject<Item> GREATWOOD_SAPLING = ITEMS.register("greatwood_sapling",
            () -> new BlockItem(ModBlocks.GREATWOOD_SAPLING.get(), new Item.Properties()));
    public static final RegistryObject<Item> GREATWOOD_PLANKS = ITEMS.register("greatwood_planks",
            () -> new BlockItem(ModBlocks.GREATWOOD_PLANKS.get(), new Item.Properties()));

    // Trees / aura (F1)
    public static final RegistryObject<Item> SILVERWOOD_LOG = ITEMS.register("silverwood_log",
            () -> new BlockItem(ModBlocks.SILVERWOOD_LOG.get(), new Item.Properties()));
    public static final RegistryObject<Item> SILVERWOOD_LEAVES = ITEMS.register("silverwood_leaves",
            () -> new BlockItem(ModBlocks.SILVERWOOD_LEAVES.get(), new Item.Properties()));
    public static final RegistryObject<Item> SILVERWOOD_SAPLING = ITEMS.register("silverwood_sapling",
            () -> new BlockItem(ModBlocks.SILVERWOOD_SAPLING.get(), new Item.Properties()));
    public static final RegistryObject<Item> SILVERWOOD_PLANKS = ITEMS.register("silverwood_planks",
            () -> new BlockItem(ModBlocks.SILVERWOOD_PLANKS.get(), new Item.Properties()));
    public static final RegistryObject<Item> AURA_NODE = ITEMS.register("aura_node",
            () -> new BlockItem(ModBlocks.AURA_NODE.get(), new Item.Properties().rarity(Rarity.UNCOMMON)));

    public static final RegistryObject<Item> ELDRITCH_STONE = ITEMS.register("eldritch_stone",
            () -> new BlockItem(ModBlocks.ELDRITCH_STONE.get(), new Item.Properties().rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> FLUX_GOO = ITEMS.register("flux_goo",
            () -> new BlockItem(ModBlocks.FLUX_GOO.get(), new Item.Properties()));

    // Entities (M12)
    public static final RegistryObject<Item> BRAIN = ITEMS.register("brain",
            () -> new Item(new Item.Properties().rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> BRAINY_ZOMBIE_SPAWN_EGG = ITEMS.register("brainy_zombie_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.BRAINY_ZOMBIE, 0x3B622F, 0x9D4DBB, new Item.Properties()));
    public static final RegistryObject<Item> ELDRITCH_GUARDIAN_SPAWN_EGG = ITEMS.register("eldritch_guardian_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.ELDRITCH_GUARDIAN, 0x1A0A1A, 0x6B2D8B, new Item.Properties()));
    public static final RegistryObject<Item> WISP_SPAWN_EGG = ITEMS.register("wisp_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.WISP, 0xAAAAFF, 0x5555AA, new Item.Properties()));
    public static final RegistryObject<Item> MIND_SPIDER_SPAWN_EGG = ITEMS.register("mind_spider_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.MIND_SPIDER, 0x2A1A2A, 0xC8A0C8, new Item.Properties()));
    public static final RegistryObject<Item> ELDRITCH_WARDEN_SPAWN_EGG = ITEMS.register("eldritch_warden_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.ELDRITCH_WARDEN, 0x0A0510, 0x9B3DFF, new Item.Properties()));

    // Golems (M13 / D5)
    public static final RegistryObject<Item> GOLEM = ITEMS.register("golem", GolemPlacerItem::new);
    public static final RegistryObject<Item> SEAL_BLANK = ITEMS.register("seal_blank",
            () -> new GolemSealItem(GolemJob.IDLE));
    public static final RegistryObject<Item> SEAL_GATHER = ITEMS.register("seal_gather",
            () -> new GolemSealItem(GolemJob.GATHER));
    public static final RegistryObject<Item> SEAL_GUARD = ITEMS.register("seal_guard",
            () -> new GolemSealItem(GolemJob.GUARD));
    public static final RegistryObject<Item> SEAL_FILL = ITEMS.register("seal_fill",
            () -> new GolemSealItem(GolemJob.FILL));
    public static final RegistryObject<Item> SEAL_EMPTY = ITEMS.register("seal_empty",
            () -> new GolemSealItem(GolemJob.EMPTY));

    private ModItems() {
    }

    private static RegistryObject<Item> crystal(String id) {
        return ITEMS.register(id, () -> new Item(new Item.Properties()));
    }

    public static RegistryObject<Item> crystalFor(Aspect aspect) {
        return switch (aspect.getTag()) {
            case "aer" -> CRYSTAL_AER;
            case "terra" -> CRYSTAL_TERRA;
            case "ignis" -> CRYSTAL_IGNIS;
            case "aqua" -> CRYSTAL_AQUA;
            case "ordo" -> CRYSTAL_ORDO;
            case "perditio" -> CRYSTAL_PERDITIO;
            default -> CRYSTAL_AER;
        };
    }
}
