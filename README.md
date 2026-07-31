# Arcana

Dev port of **Thaumcraft 6** to **Minecraft 1.20.1 Forge** (`modid=arcana`).

Version **0.8.0** — Phase K world & threats (see `arcana_changelog.txt`).

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

## Features (shipped)

- Aspects, crystals, thaumium / void gear / void robes, goggles tiers, traveller & cloudstepper boots, undying charm
- Knowledge + warp events (permanent / sticky / temporary); aura / vis; research book with BASICS / AUROMANCY / ARTIFICE / ALCHEMY / GOLEMANCY / ELDRITCH
- Research table, arcane workbench, crucible (+ salis / quicksilver / tallow), jars, tubes, filter tubes, smelter, alembic
- Infusion (foci, void robes, traveller boots, advanced goggles, Outer Lands portal); Focal Manipulator
- Devices: levitator, magic mirror, lamp of growth, hungry chest
- Golems: gather/guard/fill/empty/harvest/use/butcher seals + gather/guard cores
- Worldgen: cinderpearl, shimmerleaf, ethereal bloom, flux patches, crystal clusters, greatwood, silverwood, eldritch stone / ring / hilltop stones / cultist camp / obelisk
- Threats: Crimson Cultists, Eldritch Guardians / Warden phases, mind spiders, Outer Lands portal tease
- Soft JEI + soft Curios (compileOnly)

## Commands

- `/arcana` or `/arcana help` — summary
- `/arcana aspects`
- Op: `research`, `knowledge`, `warp` (PERMANENT|NORMAL/STICKY|TEMPORARY, incl. `event`), `aura`, `essentia`, `crucible`, `cast`, `focus give`

## Deferred / next

See **Phase L+ roadmap** in `PORT_PLAN.md` (0.9.0 polish → 1.0.0 RC).
