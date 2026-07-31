# Arcana

Dev port of **Thaumcraft 6** to **Minecraft 1.20.1 Forge** (`modid=arcana`).

Version **0.6.0** — Phase I systems (see `arcana_changelog.txt`).

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

- Aspects, crystals, thaumium / void gear / void robes (full set + boots), goggles, apprentice ring + vis amulet (Curios recharge)
- Knowledge + warp events (mind spiders, guardians, rare eldritch warden; wisps from flux)
- Aura / vis, flux goo, research book + map, thaumometer scan, aura nodes with types + buffer drain
- Research table (Study / Note theorycraft), arcane workbench, crucible, jars, tubes + filter tubes, smelter, alembic
- Infusion matrix + pedestals with layout checks and FX (`focus_1` → `focus_2`, void robes)
- Caster + Focal Manipulator compose graph with complexity caps
- Golems with blank / gather / guard / fill / empty seals
- Worldgen: cinderpearl, shimmerleaf, crystal clusters, greatwood, silverwood, eldritch stone → void seed
- Soft JEI (arcane + crucible + infusion)

## Commands

- `/arcana` or `/arcana help` — summary
- `/arcana aspects`
- Op: `research`, `knowledge`, `warp` (incl. `event`), `aura`, `essentia`, `crucible`, `cast`, `focus give`

## Deferred / next

See **Phase J+ roadmap** in `PORT_PLAN.md` (0.7.0 content → 1.0.0 RC).
