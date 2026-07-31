package arcana.common.config;

import arcana.api.aspects.Aspect;
import arcana.api.aspects.AspectList;
import arcana.common.lib.aspects.AspectTagStore;
import arcana.registry.ModItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.ForgeRegistries;

public final class ConfigAspects {
    private ConfigAspects() {
    }

    public static void register() {
        AspectTagStore.clear();

        tag(Items.COBBLESTONE, new AspectList().add(Aspect.EARTH, 5).add(Aspect.ENTROPY, 1));
        tag(Items.STONE, new AspectList().add(Aspect.EARTH, 5));
        tag(Items.DIRT, new AspectList().add(Aspect.EARTH, 5));
        tag(Items.GRASS_BLOCK, new AspectList().add(Aspect.EARTH, 5).add(Aspect.PLANT, 2));
        tag(Items.SAND, new AspectList().add(Aspect.EARTH, 5).add(Aspect.ENTROPY, 5));
        tag(Items.IRON_ORE, new AspectList().add(Aspect.EARTH, 5).add(Aspect.METAL, 15));
        tag(Items.DEEPSLATE_IRON_ORE, new AspectList().add(Aspect.EARTH, 5).add(Aspect.METAL, 15));
        tag(Items.IRON_INGOT, new AspectList().add(Aspect.METAL, 15));
        tag(Items.GOLD_ORE, new AspectList().add(Aspect.EARTH, 5).add(Aspect.METAL, 10).add(Aspect.DESIRE, 10));
        tag(Items.GOLD_INGOT, new AspectList().add(Aspect.METAL, 10).add(Aspect.DESIRE, 10));
        tag(Items.COAL, new AspectList().add(Aspect.ENERGY, 10).add(Aspect.FIRE, 10));
        tag(Items.REDSTONE, new AspectList().add(Aspect.ENERGY, 10));
        tag(Items.LAPIS_LAZULI, new AspectList().add(Aspect.EARTH, 5).add(Aspect.SENSES, 15));
        tag(Items.DIAMOND, new AspectList().add(Aspect.CRYSTAL, 15).add(Aspect.DESIRE, 15));
        tag(Items.OAK_LOG, new AspectList().add(Aspect.PLANT, 20));
        tag(Items.OAK_PLANKS, new AspectList().add(Aspect.PLANT, 3));
        tag(Items.WATER_BUCKET, new AspectList().add(Aspect.WATER, 15));
        tag(Items.LAVA_BUCKET, new AspectList().add(Aspect.FIRE, 15).add(Aspect.EARTH, 5));
        tag(Items.GLASS, new AspectList().add(Aspect.CRYSTAL, 5));
        tag(Items.CLAY_BALL, new AspectList().add(Aspect.WATER, 5).add(Aspect.EARTH, 5));
        tag(Items.BONE, new AspectList().add(Aspect.DEATH, 5));
        tag(Items.ROTTEN_FLESH, new AspectList().add(Aspect.DEATH, 5).add(Aspect.MAN, 5));
        tag(Items.WHITE_WOOL, new AspectList().add(Aspect.BEAST, 5));
        tag(Items.OAK_SAPLING, new AspectList().add(Aspect.PLANT, 5));
        tag(Items.OAK_LEAVES, new AspectList().add(Aspect.PLANT, 2));
        tag(Items.GLASS_PANE, new AspectList().add(Aspect.CRYSTAL, 1));
        tag(Items.STICK, new AspectList().add(Aspect.PLANT, 1));

        // Arcana items
        tag(ModItems.THAUMIUM_INGOT.get(), new AspectList().add(Aspect.METAL, 10).add(Aspect.MAGIC, 5));
        tag(ModItems.CRYSTAL_AER.get(), new AspectList().add(Aspect.AIR, 2).add(Aspect.CRYSTAL, 2));
        tag(ModItems.CRYSTAL_TERRA.get(), new AspectList().add(Aspect.EARTH, 2).add(Aspect.CRYSTAL, 2));
        tag(ModItems.CRYSTAL_IGNIS.get(), new AspectList().add(Aspect.FIRE, 2).add(Aspect.CRYSTAL, 2));
        tag(ModItems.CRYSTAL_AQUA.get(), new AspectList().add(Aspect.WATER, 2).add(Aspect.CRYSTAL, 2));
        tag(ModItems.CRYSTAL_ORDO.get(), new AspectList().add(Aspect.ORDER, 2).add(Aspect.CRYSTAL, 2));
        tag(ModItems.CRYSTAL_PERDITIO.get(), new AspectList().add(Aspect.ENTROPY, 2).add(Aspect.CRYSTAL, 2));
        tag(ModItems.SALIS_MUNDUS.get(), new AspectList().add(Aspect.MAGIC, 5).add(Aspect.EARTH, 2));
        tag(ModItems.BRAIN.get(), new AspectList().add(Aspect.MIND, 10).add(Aspect.UNDEAD, 5));
        tag(ModItems.CINDERPEARL.get(), new AspectList().add(Aspect.FIRE, 5).add(Aspect.PLANT, 2));
        tag(ModItems.SHIMMERLEAF.get(), new AspectList().add(Aspect.AURA, 5).add(Aspect.PLANT, 2));
        tag(ModItems.GREATWOOD_LOG.get(), new AspectList().add(Aspect.PLANT, 20).add(Aspect.MAGIC, 2));
        tag(ModItems.GREATWOOD_PLANKS.get(), new AspectList().add(Aspect.PLANT, 3));
        tag(ModItems.GREATWOOD_SAPLING.get(), new AspectList().add(Aspect.PLANT, 5).add(Aspect.MAGIC, 1));
        tag(ModItems.GREATWOOD_LEAVES.get(), new AspectList().add(Aspect.PLANT, 2));
        tag(ModItems.SILVERWOOD_LOG.get(), new AspectList().add(Aspect.PLANT, 20).add(Aspect.AURA, 5).add(Aspect.MAGIC, 2));
        tag(ModItems.SILVERWOOD_PLANKS.get(), new AspectList().add(Aspect.PLANT, 3).add(Aspect.AURA, 1));
        tag(ModItems.SILVERWOOD_SAPLING.get(), new AspectList().add(Aspect.PLANT, 5).add(Aspect.AURA, 2).add(Aspect.MAGIC, 1));
        tag(ModItems.SILVERWOOD_LEAVES.get(), new AspectList().add(Aspect.PLANT, 2).add(Aspect.AURA, 1));
        tag(ModItems.AURA_NODE.get(), new AspectList().add(Aspect.AURA, 20).add(Aspect.AIR, 10));
        tag(ModItems.NITOR.get(), new AspectList().add(Aspect.FIRE, 10).add(Aspect.LIGHT, 10).add(Aspect.ENERGY, 5));
        tag(ModItems.ALUMENTUM.get(), new AspectList().add(Aspect.FIRE, 10).add(Aspect.ENERGY, 10).add(Aspect.ENTROPY, 5));
        tag(ModItems.ELDRITCH_STONE.get(), new AspectList().add(Aspect.ELDRITCH, 5).add(Aspect.EARTH, 5).add(Aspect.VOID, 5));
        tag(ModItems.VOID_SEED.get(), new AspectList().add(Aspect.VOID, 10).add(Aspect.ELDRITCH, 5));
        tag(ModItems.VOID_ROBE_HELMET.get(), new AspectList().add(Aspect.VOID, 10).add(Aspect.ELDRITCH, 8).add(Aspect.PROTECT, 5));
        tag(ModItems.VOID_ROBE_CHESTPLATE.get(), new AspectList().add(Aspect.VOID, 15).add(Aspect.ELDRITCH, 10).add(Aspect.PROTECT, 10));
        tag(ModItems.VOID_ROBE_LEGGINGS.get(), new AspectList().add(Aspect.VOID, 12).add(Aspect.ELDRITCH, 8).add(Aspect.PROTECT, 8));
        tag(ModItems.VOID_ROBE_BOOTS.get(), new AspectList().add(Aspect.VOID, 8).add(Aspect.ELDRITCH, 6).add(Aspect.PROTECT, 5).add(Aspect.MOTION, 5));
        tag(ModItems.FLUX_GOO.get(), new AspectList().add(Aspect.FLUX, 10).add(Aspect.DARKNESS, 5).add(Aspect.EARTH, 2));
        tag(ModItems.AMULET_VIS.get(), new AspectList().add(Aspect.AURA, 10).add(Aspect.MAGIC, 8).add(Aspect.DESIRE, 5));
        tag(ModItems.CRYSTAL_CLUSTER_AER.get(), new AspectList().add(Aspect.AIR, 15).add(Aspect.CRYSTAL, 10));
        tag(ModItems.CRYSTAL_CLUSTER_TERRA.get(), new AspectList().add(Aspect.EARTH, 15).add(Aspect.CRYSTAL, 10));
        tag(ModItems.CRYSTAL_CLUSTER_IGNIS.get(), new AspectList().add(Aspect.FIRE, 15).add(Aspect.CRYSTAL, 10));
        tag(ModItems.CRYSTAL_CLUSTER_AQUA.get(), new AspectList().add(Aspect.WATER, 15).add(Aspect.CRYSTAL, 10));
        tag(ModItems.CRYSTAL_CLUSTER_ORDO.get(), new AspectList().add(Aspect.ORDER, 15).add(Aspect.CRYSTAL, 10));
        tag(ModItems.CRYSTAL_CLUSTER_PERDITIO.get(), new AspectList().add(Aspect.ENTROPY, 15).add(Aspect.CRYSTAL, 10));

        AspectTagStore.registerEntity(ForgeRegistries.ENTITY_TYPES.getKey(EntityType.ZOMBIE),
                new AspectList().add(Aspect.UNDEAD, 10).add(Aspect.MAN, 5));
        AspectTagStore.registerEntity(ForgeRegistries.ENTITY_TYPES.getKey(EntityType.SKELETON),
                new AspectList().add(Aspect.UNDEAD, 10).add(Aspect.DEATH, 5));
        AspectTagStore.registerEntity(new ResourceLocation("arcana", "brainy_zombie"),
                new AspectList().add(Aspect.UNDEAD, 10).add(Aspect.MIND, 10));
    }

    private static void tag(net.minecraft.world.level.ItemLike item, AspectList aspects) {
        AspectTagStore.registerItem(item.asItem(), aspects);
    }
}
