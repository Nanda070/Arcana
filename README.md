# Arcana

Dev port of **Thaumcraft 6** to **Minecraft 1.20.1 Forge** (`modid=arcana`).

Version **1.1.0** — Phase N shipped (see `arcana_changelog.txt`, `PORT_PLAN.md` N1–N10 done).

## Requirements

- Java 17
- Minecraft 1.20.1 + Forge 47.x
- Optional: JEI, Curios (soft dependencies)

## Build / run

```powershell
$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-17.0.20.8-hotspot"
.\gradlew.bat compileJava
.\gradlew.bat jar
.\gradlew.bat runClient
```

Artifact: `build/libs/arcana-1.1.0.jar`

## Publish (manual)

No automated Modrinth/CurseForge pipeline. After `.\gradlew.bat jar --offline`:

1. Upload `build/libs/arcana-1.1.0.jar` to [Modrinth](https://modrinth.com) and/or [CurseForge](https://www.curseforge.com)
2. Set game version **1.20.1**, loader **Forge**, optional deps JEI / Curios
3. Paste summary from `arcana_changelog.txt` (1.1.0 section) and link `NOTICE.md` / Credits & Legal below

## Curios

Soft dependency: **compileOnly** Curios API is always on the classpath.

- **FG userdev / `runClient`:** keep `arcana.enable_curios_runtime=false` in `gradle.properties` (default). Full Curios jars break userdev mixins under official mappings.
- **Real Forge client:** set `arcana.enable_curios_runtime=true` in `gradle.properties` (or `-Parcana.enable_curios_runtime=true`) so `build.gradle` adds `runtimeOnly` Curios, **or** drop Curios into the `mods` folder. Bauble helpers stay gated via `CuriosVisHelper` / `CuriosCompat` when Curios is absent.

### Curios real-client verified checklist

- [ ] Install Curios on a **real** Forge 1.20.1 client (not FG userdev)
- [ ] Confirm `arcana.enable_curios_runtime` stays **false** for userdev; enable only for real-client jars / `-P`
- [ ] Equip `ring_apprentice` → cast cost ~5% cheaper
- [ ] Equip `amulet_vis` → vis recharge while near aura
- [ ] Launch without Curios → Arcana still loads (soft-dep)

## Config (COMMON)

`config/arcana-common.toml` after first run:

- `auraNodeRegenMultiplier` (default 1.0)
- `warpEventChanceMultiplier` (default 1.0)
- `worldgenStructureRarityMultiplier` (default 1.0; higher = rarer Eldritch Ring / Flux Patch)
- `stickyWarpDecayTicks` (default 6000)
- `tempWarpDecayTicks` (default 600)
- `infusionStabilityMultiplier` (default 1.0; lower = more stable infusions)
- `focusVisCostMultiplier` (default 1.0)
- `golemWorkIntervalTicks` (default 20)
- `theorycraftInspirationBase` (default 5)

## Outer Lands (N7)

TC6 treated Outer Lands as a **biome**, not a dimension. Arcana ships:

- Datapack biome `arcana:outer_lands` (dark purple fog/sky/water) — **not injected into the overworld** without TerraBlender
- `OuterLandsPocketFeature` — rare ~16×16 eldritch/end-stone pockets with flux goo circle + wisp, portal/node, cultists, and Endermen (placed in dark forest / swamp / mangrove swamp / soul sand valley)

Full biome region registration needs **TerraBlender** (not a current dependency). Add it later to map `outer_lands` into the overworld via a Region.

## Features (shipped)

- Aspects, crystals, thaumium / void gear / void robes, goggles tiers, traveller & cloudstepper boots, undying charm
- Knowledge + warp events (permanent / sticky / temporary); aura / vis; research book with BASICS / AUROMANCY / ARTIFICE / ALCHEMY / INFUSION / GOLEMANCY / ELDRITCH (~148 TC6-adapted entries)
- Research table theorycraft (~33 cards); arcane workbench, crucible (+ salis / quicksilver / tallow), jars, tubes (filter/valve/restrict/oneway/buffer), smelter, alembic, centrifuge
- Infusion (pillars + stability, foci, void robes, traveller boots, advanced goggles, Outer Lands portal); Focal Manipulator (multi-effect + Scatter stub)
- Devices: levitator, magic mirror, lamp of growth, hungry chest
- Golems: gather/guard/fill/empty/harvest/use/butcher seals + gather/guard cores
- Worldgen: cinderpearl, shimmerleaf, ethereal bloom, flux patches, crystal clusters, greatwood, silverwood, eldritch stone / ring / hilltop stones / cultist camp / obelisk
- Threats: Crimson Cultists, Eldritch Guardians / Warden phases, mind spiders, Outer Lands portal tease
- Soft JEI (crucible / arcane / infusion + info pages); soft Curios; `en_us` + `ru_ru`; advancements tree

## Commands

- `/arcana` or `/arcana help` — summary
- `/arcana aspects`
- `/arcana smoke` — registry smoke assertions (op, or anyone in FG userdev)
- Op: `research`, `knowledge`, `warp` (PERMANENT|NORMAL/STICKY|TEMPORARY, incl. `event`), `aura`, `essentia`, `crucible`, `cast`, `focus give`

## Credits & Legal

Arcana is an **unofficial** port inspired by **Thaumcraft 6** by **Azanor**. It is **not affiliated with** Mojang Studios, Microsoft, or Azanor.

Original TC6 sources may be used as reference in sibling repo paths. Arcana code is **All Rights Reserved** (see `gradle.properties` / `mods.toml`). Forge MDK licensing remains in `LICENSE.txt`.

Authors: **Nanda070** / Arcana Dev. Full disclosure: `NOTICE.md`, `CREDITS.md`.

## Roadmap

**1.0.0 shipped** (Phases M0–M through L + Phase M RC). See `PORT_PLAN.md` and `SMOKE_CHECKLIST.md`.
