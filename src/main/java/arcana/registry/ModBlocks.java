package arcana.registry;

import arcana.Arcana;
import arcana.api.aspects.Aspect;
import arcana.common.blocks.AlembicBlock;
import arcana.common.blocks.ArcaneLevitatorBlock;
import arcana.common.blocks.ArcaneWorkbenchBlock;
import arcana.common.blocks.AuraNodeBlock;
import arcana.common.blocks.BellowsBlock;
import arcana.common.blocks.CinderpearlBlock;
import arcana.common.blocks.CrucibleBlock;
import arcana.common.blocks.CrystalClusterBlock;
import arcana.common.blocks.EldritchStoneBlock;
import arcana.common.blocks.EssentiaSmelterBlock;
import arcana.common.blocks.FluxGooBlock;
import arcana.common.blocks.EssentiaTubeBlock;
import arcana.common.blocks.EssentiaFilterTubeBlock;
import arcana.common.blocks.EssentiaValveBlock;
import arcana.common.blocks.FocalManipulatorBlock;
import arcana.common.blocks.HungryChestBlock;
import arcana.common.blocks.InfusionMatrixBlock;
import arcana.common.blocks.LampOfGrowthBlock;
import arcana.common.blocks.MagicMirrorBlock;
import arcana.common.blocks.PedestalBlock;
import arcana.common.blocks.ResearchTableBlock;
import arcana.common.blocks.ShimmerleafBlock;
import arcana.common.blocks.WardedJarBlock;
import arcana.common.golems.SealBlock;
import arcana.common.worldgen.GreatwoodTreeGrower;
import arcana.common.worldgen.SilverwoodTreeGrower;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Arcana.MODID);

    public static final RegistryObject<Block> ARCANE_WORKBENCH = BLOCKS.register("arcane_workbench",
            () -> new ArcaneWorkbenchBlock(BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).strength(2.5f)
                    .lightLevel(s -> 4).noOcclusion()));

    public static final RegistryObject<Block> RESEARCH_TABLE = BLOCKS.register("research_table",
            () -> new ResearchTableBlock(BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).strength(2.5f)
                    .sound(SoundType.WOOD).noOcclusion()));

    public static final RegistryObject<Block> ESSENTIA_FILTER_TUBE = BLOCKS.register("essentia_filter_tube",
            () -> new EssentiaFilterTubeBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL).strength(1.0f).sound(SoundType.METAL).noOcclusion()));

    public static final RegistryObject<Block> WARDED_JAR = BLOCKS.register("warded_jar",
            () -> new WardedJarBlock(BlockBehaviour.Properties.of().mapColor(MapColor.NONE).strength(0.3f)
                    .sound(SoundType.GLASS).noOcclusion()));

    public static final RegistryObject<Block> ESSENTIA_TUBE = BLOCKS.register("essentia_tube",
            () -> new EssentiaTubeBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(1.0f)
                    .sound(SoundType.METAL).noOcclusion()));

    public static final RegistryObject<Block> ESSENTIA_VALVE = BLOCKS.register("essentia_valve",
            () -> new EssentiaValveBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(1.5f)
                    .sound(SoundType.METAL).noOcclusion()));

    public static final RegistryObject<Block> CRUCIBLE = BLOCKS.register("crucible",
            () -> new CrucibleBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(3.0f)
                    .sound(SoundType.METAL).noOcclusion()));

    public static final RegistryObject<Block> CINDERPEARL = BLOCKS.register("cinderpearl", CinderpearlBlock::new);
    public static final RegistryObject<Block> SHIMMERLEAF = BLOCKS.register("shimmerleaf", ShimmerleafBlock::new);

    public static final RegistryObject<Block> CRYSTAL_CLUSTER_AER = BLOCKS.register("crystal_cluster_aer",
            () -> new CrystalClusterBlock(Aspect.AIR));
    public static final RegistryObject<Block> CRYSTAL_CLUSTER_TERRA = BLOCKS.register("crystal_cluster_terra",
            () -> new CrystalClusterBlock(Aspect.EARTH));
    public static final RegistryObject<Block> CRYSTAL_CLUSTER_IGNIS = BLOCKS.register("crystal_cluster_ignis",
            () -> new CrystalClusterBlock(Aspect.FIRE));
    public static final RegistryObject<Block> CRYSTAL_CLUSTER_AQUA = BLOCKS.register("crystal_cluster_aqua",
            () -> new CrystalClusterBlock(Aspect.WATER));
    public static final RegistryObject<Block> CRYSTAL_CLUSTER_ORDO = BLOCKS.register("crystal_cluster_ordo",
            () -> new CrystalClusterBlock(Aspect.ORDER));
    public static final RegistryObject<Block> CRYSTAL_CLUSTER_PERDITIO = BLOCKS.register("crystal_cluster_perditio",
            () -> new CrystalClusterBlock(Aspect.ENTROPY));

    public static final RegistryObject<Block> ESSENTIA_SMELTER = BLOCKS.register("essentia_smelter",
            () -> new EssentiaSmelterBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(3.5f)
                    .sound(SoundType.METAL).lightLevel(s -> s.getValue(EssentiaSmelterBlock.LIT) ? 13 : 0)
                    .requiresCorrectToolForDrops()));

    public static final RegistryObject<Block> ALEMBIC = BLOCKS.register("alembic",
            () -> new AlembicBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(2.0f)
                    .sound(SoundType.METAL).noOcclusion()));

    public static final RegistryObject<Block> PEDESTAL = BLOCKS.register("pedestal", PedestalBlock::new);
    public static final RegistryObject<Block> INFUSION_MATRIX = BLOCKS.register("infusion_matrix", InfusionMatrixBlock::new);
    public static final RegistryObject<Block> FOCAL_MANIPULATOR = BLOCKS.register("focal_manipulator", FocalManipulatorBlock::new);
    public static final RegistryObject<Block> GOLEM_SEAL = BLOCKS.register("golem_seal", SealBlock::new);
    public static final RegistryObject<Block> BELLOWS = BLOCKS.register("bellows",
            () -> new BellowsBlock(BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).strength(2.0f)
                    .sound(SoundType.WOOD).noOcclusion()));

    // E5 Greatwood
    public static final RegistryObject<Block> GREATWOOD_LOG = BLOCKS.register("greatwood_log",
            () -> new RotatedPillarBlock(BlockBehaviour.Properties.of().mapColor(MapColor.WOOD)
                    .instrument(NoteBlockInstrument.BASS)
                    .strength(2.0f).sound(SoundType.WOOD).ignitedByLava()));
    public static final RegistryObject<Block> GREATWOOD_LEAVES = BLOCKS.register("greatwood_leaves",
            () -> new LeavesBlock(BlockBehaviour.Properties.copy(Blocks.OAK_LEAVES).mapColor(MapColor.PLANT)));
    public static final RegistryObject<Block> GREATWOOD_SAPLING = BLOCKS.register("greatwood_sapling",
            () -> new SaplingBlock(new GreatwoodTreeGrower(),
                    BlockBehaviour.Properties.copy(Blocks.OAK_SAPLING).mapColor(MapColor.PLANT)));
    public static final RegistryObject<Block> GREATWOOD_PLANKS = BLOCKS.register("greatwood_planks",
            () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.WOOD)
                    .instrument(NoteBlockInstrument.BASS)
                    .strength(2.0f, 3.0f).sound(SoundType.WOOD).ignitedByLava()));

    // F1 Silverwood + aura node
    public static final RegistryObject<Block> SILVERWOOD_LOG = BLOCKS.register("silverwood_log",
            () -> new RotatedPillarBlock(BlockBehaviour.Properties.of().mapColor(MapColor.WOOD)
                    .instrument(NoteBlockInstrument.BASS)
                    .strength(2.0f).sound(SoundType.WOOD).ignitedByLava()));
    public static final RegistryObject<Block> SILVERWOOD_LEAVES = BLOCKS.register("silverwood_leaves",
            () -> new LeavesBlock(BlockBehaviour.Properties.copy(Blocks.OAK_LEAVES).mapColor(MapColor.PLANT)));
    public static final RegistryObject<Block> SILVERWOOD_SAPLING = BLOCKS.register("silverwood_sapling",
            () -> new SaplingBlock(new SilverwoodTreeGrower(),
                    BlockBehaviour.Properties.copy(Blocks.OAK_SAPLING).mapColor(MapColor.PLANT)));
    public static final RegistryObject<Block> SILVERWOOD_PLANKS = BLOCKS.register("silverwood_planks",
            () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.WOOD)
                    .instrument(NoteBlockInstrument.BASS)
                    .strength(2.0f, 3.0f).sound(SoundType.WOOD).ignitedByLava()));
    public static final RegistryObject<Block> AURA_NODE = BLOCKS.register("aura_node", AuraNodeBlock::new);

    // E6 Eldritch
    public static final RegistryObject<Block> ELDRITCH_STONE = BLOCKS.register("eldritch_stone", EldritchStoneBlock::new);

    // G22 Flux goo
    public static final RegistryObject<Block> FLUX_GOO = BLOCKS.register("flux_goo", FluxGooBlock::new);

    // J3 Devices
    public static final RegistryObject<Block> ARCANE_LEVITATOR = BLOCKS.register("arcane_levitator",
            () -> new ArcaneLevitatorBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(2.5f)
                    .sound(SoundType.STONE).lightLevel(s -> 4)));
    public static final RegistryObject<Block> MAGIC_MIRROR = BLOCKS.register("magic_mirror",
            () -> new MagicMirrorBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(2.0f)
                    .sound(SoundType.GLASS).noOcclusion()));
    public static final RegistryObject<Block> LAMP_OF_GROWTH = BLOCKS.register("lamp_of_growth",
            () -> new LampOfGrowthBlock(BlockBehaviour.Properties.of().mapColor(MapColor.GOLD).strength(1.5f)
                    .sound(SoundType.METAL).lightLevel(s -> 10)));
    public static final RegistryObject<Block> HUNGRY_CHEST = BLOCKS.register("hungry_chest",
            () -> new HungryChestBlock(BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).strength(2.5f)
                    .sound(SoundType.WOOD)));

    private ModBlocks() {
    }
}
