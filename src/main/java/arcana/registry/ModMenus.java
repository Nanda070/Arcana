package arcana.registry;

import arcana.Arcana;
import arcana.common.menu.ArcaneWorkbenchMenu;
import arcana.common.menu.EssentiaSmelterMenu;
import arcana.common.menu.FocalManipulatorMenu;
import arcana.common.menu.GolemJobMenu;
import arcana.common.menu.ResearchTableMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModMenus {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, Arcana.MODID);

    public static final RegistryObject<MenuType<ArcaneWorkbenchMenu>> ARCANE_WORKBENCH =
            MENUS.register("arcane_workbench", () -> IForgeMenuType.create(ArcaneWorkbenchMenu::new));

    public static final RegistryObject<MenuType<ResearchTableMenu>> RESEARCH_TABLE =
            MENUS.register("research_table", () -> IForgeMenuType.create(ResearchTableMenu::new));

    public static final RegistryObject<MenuType<EssentiaSmelterMenu>> ESSENTIA_SMELTER =
            MENUS.register("essentia_smelter", () -> IForgeMenuType.create(EssentiaSmelterMenu::new));

    public static final RegistryObject<MenuType<FocalManipulatorMenu>> FOCAL_MANIPULATOR =
            MENUS.register("focal_manipulator", () -> IForgeMenuType.create(FocalManipulatorMenu::new));

    public static final RegistryObject<MenuType<GolemJobMenu>> GOLEM_JOB =
            MENUS.register("golem_job", () -> IForgeMenuType.create(GolemJobMenu::new));

    private ModMenus() {
    }
}
