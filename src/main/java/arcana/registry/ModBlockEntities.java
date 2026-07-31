package arcana.registry;

import arcana.Arcana;
import arcana.common.blockentities.AlembicBlockEntity;
import arcana.common.blockentities.ArcaneWorkbenchBlockEntity;
import arcana.common.blockentities.AuraNodeBlockEntity;
import arcana.common.blockentities.BellowsBlockEntity;
import arcana.common.blockentities.CrucibleBlockEntity;
import arcana.common.blockentities.EssentiaSmelterBlockEntity;
import arcana.common.blockentities.EssentiaTubeBlockEntity;
import arcana.common.blockentities.EssentiaValveBlockEntity;
import arcana.common.blockentities.FocalManipulatorBlockEntity;
import arcana.common.blockentities.InfusionMatrixBlockEntity;
import arcana.common.blockentities.EssentiaFilterTubeBlockEntity;
import arcana.common.blockentities.PedestalBlockEntity;
import arcana.common.blockentities.ResearchTableBlockEntity;
import arcana.common.blockentities.WardedJarBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, Arcana.MODID);

    public static final RegistryObject<BlockEntityType<ArcaneWorkbenchBlockEntity>> ARCANE_WORKBENCH =
            BLOCK_ENTITIES.register("arcane_workbench",
                    () -> BlockEntityType.Builder.of(ArcaneWorkbenchBlockEntity::new, ModBlocks.ARCANE_WORKBENCH.get()).build(null));

    public static final RegistryObject<BlockEntityType<ResearchTableBlockEntity>> RESEARCH_TABLE =
            BLOCK_ENTITIES.register("research_table",
                    () -> BlockEntityType.Builder.of(ResearchTableBlockEntity::new, ModBlocks.RESEARCH_TABLE.get()).build(null));

    public static final RegistryObject<BlockEntityType<WardedJarBlockEntity>> WARDED_JAR =
            BLOCK_ENTITIES.register("warded_jar",
                    () -> BlockEntityType.Builder.of(WardedJarBlockEntity::new, ModBlocks.WARDED_JAR.get()).build(null));

    public static final RegistryObject<BlockEntityType<EssentiaTubeBlockEntity>> ESSENTIA_TUBE =
            BLOCK_ENTITIES.register("essentia_tube",
                    () -> BlockEntityType.Builder.of(EssentiaTubeBlockEntity::new, ModBlocks.ESSENTIA_TUBE.get()).build(null));

    public static final RegistryObject<BlockEntityType<EssentiaFilterTubeBlockEntity>> ESSENTIA_FILTER_TUBE =
            BLOCK_ENTITIES.register("essentia_filter_tube",
                    () -> BlockEntityType.Builder.of(EssentiaFilterTubeBlockEntity::new, ModBlocks.ESSENTIA_FILTER_TUBE.get()).build(null));

    public static final RegistryObject<BlockEntityType<EssentiaValveBlockEntity>> ESSENTIA_VALVE =
            BLOCK_ENTITIES.register("essentia_valve",
                    () -> BlockEntityType.Builder.of(EssentiaValveBlockEntity::new, ModBlocks.ESSENTIA_VALVE.get()).build(null));

    public static final RegistryObject<BlockEntityType<CrucibleBlockEntity>> CRUCIBLE =
            BLOCK_ENTITIES.register("crucible",
                    () -> BlockEntityType.Builder.of(CrucibleBlockEntity::new, ModBlocks.CRUCIBLE.get()).build(null));

    public static final RegistryObject<BlockEntityType<EssentiaSmelterBlockEntity>> ESSENTIA_SMELTER =
            BLOCK_ENTITIES.register("essentia_smelter",
                    () -> BlockEntityType.Builder.of(EssentiaSmelterBlockEntity::new, ModBlocks.ESSENTIA_SMELTER.get()).build(null));

    public static final RegistryObject<BlockEntityType<AlembicBlockEntity>> ALEMBIC =
            BLOCK_ENTITIES.register("alembic",
                    () -> BlockEntityType.Builder.of(AlembicBlockEntity::new, ModBlocks.ALEMBIC.get()).build(null));

    public static final RegistryObject<BlockEntityType<PedestalBlockEntity>> PEDESTAL =
            BLOCK_ENTITIES.register("pedestal",
                    () -> BlockEntityType.Builder.of(PedestalBlockEntity::new, ModBlocks.PEDESTAL.get()).build(null));

    public static final RegistryObject<BlockEntityType<InfusionMatrixBlockEntity>> INFUSION_MATRIX =
            BLOCK_ENTITIES.register("infusion_matrix",
                    () -> BlockEntityType.Builder.of(InfusionMatrixBlockEntity::new, ModBlocks.INFUSION_MATRIX.get()).build(null));

    public static final RegistryObject<BlockEntityType<FocalManipulatorBlockEntity>> FOCAL_MANIPULATOR =
            BLOCK_ENTITIES.register("focal_manipulator",
                    () -> BlockEntityType.Builder.of(FocalManipulatorBlockEntity::new, ModBlocks.FOCAL_MANIPULATOR.get()).build(null));

    public static final RegistryObject<BlockEntityType<AuraNodeBlockEntity>> AURA_NODE =
            BLOCK_ENTITIES.register("aura_node",
                    () -> BlockEntityType.Builder.of(AuraNodeBlockEntity::new, ModBlocks.AURA_NODE.get()).build(null));

    public static final RegistryObject<BlockEntityType<BellowsBlockEntity>> BELLOWS =
            BLOCK_ENTITIES.register("bellows",
                    () -> BlockEntityType.Builder.of(BellowsBlockEntity::new, ModBlocks.BELLOWS.get()).build(null));

    private ModBlockEntities() {
    }
}
