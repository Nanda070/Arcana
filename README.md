# Arcana

Dev port of **Thaumcraft 6** to **Minecraft 1.20.1 Forge** (`modid=arcana`).

Version **0.9.0** — Phase L polish & soft-deps (see `arcana_changelog.txt`).

## Requirements

- Java 17
- Minecraft 1.20.1 + Forge 47.x
- Optional: JEI, Curios (soft dependencies)

## Build / run

```powershell
$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-17.0.20.8-hotspot"
.\gradlew.bat compileJava
.\gradlew.bat runClient
```

## Curios

Soft dependency: **compileOnly** Curios API is always on the classpath.

- **FG userdev / `runClient`:** keep `arcana.enable_curios_runtime=false` in `gradle.properties` (default). Full Curios jars break userdev mixins under official mappings.
- **Real Forge client:** set `arcana.enable_curios_runtime=true` in `gradle.properties` (or `-Parcana.enable_curios_runtime=true`) so `build.gradle` adds `runtimeOnly` Curios, **or** drop Curios into the `mods` folder. Bauble helpers stay gated via `CuriosVisHelper` / `CuriosCompat` when Curios is absent.

## Config (COMMON)

`config/arcana-common.toml` after first run:

- `auraNodeRegenMultiplier` (default 1.0)
- `warpEventChanceMultiplier` (default 1.0)
- `worldgenStructureRarityMultiplier` (default 1.0; higher = rarer Eldritch Ring / Flux Patch)
- `stickyWarpDecayTicks` (default 6000)
- `tempWarpDecayTicks` (default 600)

## Features (shipped)

- Aspects, crystals, thaumium / void gear / void robes, goggles tiers, traveller & cloudstepper boots, undying charm
- Knowledge + warp events (permanent / sticky / temporary); aura / vis; research book with BASICS / AUROMANCY / ARTIFICE / ALCHEMY / GOLEMANCY / ELDRITCH
- Research table, arcane workbench, crucible (+ salis / quicksilver / tallow), jars, tubes, filter tubes, smelter, alembic
- Infusion (foci, void robes, traveller boots, advanced goggles, Outer Lands portal); Focal Manipulator
- Devices: levitator, magic mirror, lamp of growth, hungry chest
- Golems: gather/guard/fill/empty/harvest/use/butcher seals + gather/guard cores
- Worldgen: cinderpearl, shimmerleaf, ethereal bloom, flux patches, crystal clusters, greatwood, silverwood, eldritch stone / ring / hilltop stones / cultist camp / obelisk
- Threats: Crimson Cultists, Eldritch Guardians / Warden phases, mind spiders, Outer Lands portal tease
- Soft JEI (crucible / arcane / infusion + info pages); soft Curios; `en_us` + `ru_ru`; advancements tree

## Commands

- `/arcana` or `/arcana help` — summary
- `/arcana aspects`
- Op: `research`, `knowledge`, `warp` (PERMANENT|NORMAL/STICKY|TEMPORARY, incl. `event`), `aura`, `essentia`, `crucible`, `cast`, `focus give`

## Deferred / next

See **Phase M** roadmap in `PORT_PLAN.md` (1.0.0 RC).
