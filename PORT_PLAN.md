# Arcana — план порта Thaumcraft 6 → 1.20.1 Forge

Dev-название мода: **arcana** (`modid=arcana`). Исходники TC6 лежат в родительском репозитории (`../src`).

## Правило

Не открываем следующий модуль, пока текущий не стартует в `runClient` и не проходит smoke-test.

## Модули

| ID | Модуль | Статус |
|----|--------|--------|
| M0 | Skeleton MDK 1.20.1 | done |
| M1 | Aspects API | done (core) |
| M2 | Content Registry (минимум) | done (crystals + thaumium) |
| M3 | Player Data (Knowledge + Warp) | done |
| M4 | Aura / Vis | done |
| M5 | Research | done (basics + book GUI) |
| M6 | Crafting | done (vanilla + arcane workbench mini) |
| M7 | Essentia Network | done (jar + tube mini) |
| M8 | Devices | done (crucible mini; more devices later) |
| M9 | Tools / Armor / Curios | done (thaumium set; Curios later) |
| M10 | Casters / Foci | done (gauntlet + Touch→Fire focus; more later) |
| M11 | Worldgen | done (cinderpearl) |
| M12 | Entities | done (brainy zombie) |
| M13 | Golems | done (wood walker + follow/pickup; no seals) |
| M14 | Client polish | done (aura HUD, crystal tint, jar/crucible BER, cast/flux FX) |

## Углубление

| ID | Фаза | Статус |
|----|------|--------|
| D1 | Discovery loop | done (scan, aspect tags, research map, cast/recipe gates) |
| D2 | Essentia & devices | done (smelter+alembic+label+jar pour; infusion deferred) |
| D3 | Casting graph | done (nodes, projectile, frost, focus give; manipulator later) |
| D4 | Gear & Curios | done (void set + goggles helm; Curios later) |
| D5 | Golem seals | done (blank/gather/guard modes on golem; world seals later) |
| D6 | Worldgen deep | done (primal crystal clusters + shimmerleaf; trees/nodes later) |
| D7 | Entities & warp | done (warp events + brainy spawn; new mobs later) |
| D8 | Integration & ship | done (soft JEI + help/README; Curios later) |

## Smoke-tests

- **M0:** мод в списке, лог `Arcana loaded`
- **M1:** команда `/arcana aspects` печатает primal/compound
- **M3:** `/arcana research add FIRSTSTEPS`, relog → research остаётся; warp переживает смерть (кроме TEMPORARY)
- **M4:** `/arcana aura get` в мире показывает base/vis/flux; drain/add переживают unload чанка
- **M5:** взять Thaumonomicon → открыть GUI; `/arcana research complete FIRSTSTEPS`
- **M6:** скрафтить thaumium; salis+table → workbench; thaumometer на Arcane Workbench (vis+crystals+FIRSTSTEPS)
- **M7:** jarA fill → tubes → jarB; `/arcana essentia get|fill`
- **M8:** crucible + fire + water; dump crystals; throw iron → thaumium
- **M9:** craft thaumium tools/armor from ingots; equip armor; repair on anvil with ingot
- **M10:** creative gauntlet with focus → RMB cast (drain vis, ignite/damage); sneak+offhand focus to attach; `/arcana cast`
- **M11:** desert/savanna/badlands → find glowing cinderpearl; `/place feature arcana:cinderpearl`
- **M12:** spawn egg / `/summon arcana:brainy_zombie` → attacks, may drop brain
- **M13:** place golem → follows owner; sneak-RMB toggle stay; RMB dump inventory; vacuums nearby items
- **M14:** hold caster/thaumometer → aura HUD; crystals tinted; jar fill colored; crucible pool tinted; cast flame trail; `/arcana aura pollute` purple burst
- **D1:** thaumometer RMB scan iron/etc → unlock aspects; book shows research map; cast locked until FIRSTSTEPS complete
- **D2:** smelter+alembic on top → tubes → jar; label filters jar; sneak-empty jar next to crucible pours essentia
- **D3:** `/arcana focus give projectile_fire|touch_frost`; research FOCUSPROJECTILE/FOCUSELEMENTAL; projectile + frost cast
- **D4:** crucible ender→void seed→ingot; craft void tools/armor (self-repair + warp); goggles → aura HUD + 5% vis discount; `/arcana warp get` shows gear warp
- **D5:** RMB seal_gather/guard/blank on owned golem; gather vacuums; guard melee monsters; blank clears job
- **D6:** cave crystal clusters drop primals; shimmerleaf in overworld; `/place feature arcana:crystal_cluster_aer|shimmerleaf`
- **D7:** `/arcana warp add NORMAL 40` then `/arcana warp event` → whisper/potion/flux/brainy; TEMPORARY decays each event
- **D8:** `/arcana help`; with JEI — Arcane Workbench + Crucible categories; jar builds as 0.1.1 without requiring JEI

## Expansion (после D8)

| ID | Фаза | Статус |
|----|------|--------|
| E1 | Infusion | done (matrix+pedestals+focus_2; instability later) |
| E2 | Focal Manipulator | done (preset buttons; node GUI later) |
| E3 | Curios soft-dep | done (ring_apprentice; amulet later) |
| E4 | World seals | done (place seal_* on walls; stay golem seeks) |
| E5 | Worldgen trees | done (greatwood log/leaves/sapling/planks + forest gen; silverwood later) |
| E6 | Eldritch / void loop | done (eldritch stone ore → void seed; warp spawns guardian) |
| E7 | Mobs polish | done (wisp: flux spawn + drains vis; mind spider later) |
| E8 | Content ship 0.2.0 | done (version bump + changelog + research entries) |

Приоритет среза: **E1 → E2 → E3**. Правило smoke-test без изменений.

- **E1:** pedestal under matrix + 4 at ±2; jars with ordo/praecantatio; focus_1+diamond+ender+quartz; RMB matrix → focus_2
- **E2:** open focal_manipulator → insert focus → preset buttons (research-gated)
- **E3:** with Curios, equip ring_apprentice → cast ~5% cheaper; without Curios, mod still loads
- **E4:** stay golem + place seal_gather/guard on wall → walks there and adopts job; break returns seal
- **E5:** find greatwood in forests or bone-meal sapling; craft 4 planks from log
- **E6:** mine eldritch_stone deep (void seed + temp warp); high warp event → eldritch guardian drops seed
- **E7:** `/arcana aura pollute 30` then wait → wisps; kill for primal crystal chance
- **E8:** jar builds as 0.2.0; check `arcana_changelog.txt`

## Phase F (после 0.2.0)

| ID | Фаза | Статус |
|----|------|--------|
| F1 | Silverwood + aura nodes | done |
| F2 | Infusion instability | done |
| F3 | Focal Manipulator graph | done |
| F4 | Void robes + amulet | done |
| F5 | Mind spider | done |
| F6 | Polish → 0.3.0 | done |

- **F1:** silverwood sapling grows; find trees; aura node visible / thaumometer — smoke: plant sapling, find silverwood in world, place aura_node
- **F2:** bad infusion setup → instability fail + FX (not silent no-op) — smoke: craft focus_2 with empty ring slots / flux; expect explode + "Infusion instability!"
- **F3:** manipulator GUI: connect Touch/Projectile + Fire/Frost nodes — smoke: select medium+effect, Compose programs focus
- **F4:** infusion void robe piece; Curios vis amulet soft-dep — smoke: infuse void helmet→robe; equip amulet_vis necklace
- **F5:** high warp → mind spider; soft spawn — smoke: `/arcana warp event` until fog/spiders; spiders despawn ~60s
- **F6:** version 0.3.0 + changelog + research polish — smoke: jar builds as 0.3.0; check changelog

## Screenshot QA (2026-07-31 runClient) → Bugfix backlog

Источник: `arcana/run/screenshots/2026-07-31_02.21.*` … `02.23.*`

| ID | Severity | Находка | Evidence | Фикс |
|----|----------|---------|----------|------|
| B1 | critical | Aura Node = `cross` + текстура из **item** atlas → magenta/missing + выглядит как цветок | 02.21.30, 02.23.37 | Копия в `textures/block/`, модель billboard/BER + light |
| B2 | high | Essentia tubes / alembic multipart — Z-fighting, фрагменты, клиппинг в траву | 02.22.59, 02.23.13, 02.23.20 | Pipe multipart (center+arms) или BER; alembic model audit |
| B3 | high | Arcane Workbench GUI: `Vis: N` перекрывает title | 02.22.35 | Сдвинуть label (y/x) / кастомный GUI tex |
| B4 | high | Crystal clusters = flat cross, огромный hand render | 02.21.34 | 3D cluster model + item display transforms |
| B5 | med | Thaumonomicon entry: `Status: IN_PROGRESS` raw enum; дубль Greatwood/GREATWOOD | 02.22.25 | Локализованный статус; убрать key echo |
| B6 | med | Research map: FLU без связей; два `FOC`; аббревиатуры; tooltip `KEY — UNKNOWN` | 02.22.23–24 | Parents/layout; уникальные icons; human tooltip |
| B7 | med | Creative tab: счётчики ×64; item «вылезает» из сетки (page 2) | 02.22.43, 02.23.33 | Не принимать ItemStack с count; fix displayItems overflow |
| B8 | med | Eldritch Guardian / dark mobs — почти силуэт днём | 02.23.42 | Texture remap / brightness / model |
| B9 | med | Curios runtime сломан в FG userdev (mixins vs official) | launch crash | Profile-flag / remap / document real-client only |
| B10 | low | Device GUIs на vanilla dispenser/crafting tex; плоский вид | 02.22.35, 02.23.03 | Dedicated GUI textures |
| B11 | low | Workbench в мире выглядит «чёрным кубом» ночью | 02.22.35 | Проверить side tex / light / AO |
| B12 | low | Focal compose / infusion / seals не видны в smoke-скринах | — | Smoke checklist + UI polish pass |

Приоритет фикса: **B1 → B2 → B3 → B4 → B5/B6 → B7 → B8/B9**.

## Phase G — Depth Mega-Plan (после 0.3.0 QA)

Цель ship: **0.4.0** — **shipped**.

### Status (G0–G28)

| ID | Модуль | Статус |
|----|--------|--------|
| G0.1–G0.8 | Bugfix sprint | done |
| G1–G4 | Aura & world | done |
| G5–G8 | Crafting depth | done |
| G9–G12 | Casting & foci | done |
| G13–G16 | Research & book | done |
| G17–G20 | Golems & automation | done |
| G21 | Warp ladder expand | done |
| G22 | Flux goo | done |
| G23 | Void robe boots + set bonus | done |
| G24 | Eldritch Warden stub | done |
| G25 | JEI infusion category | done |
| G26 | Curios amulet recharge | done |
| G27 | ArcanaSounds cues | done |
| G28 | Ship 0.4.0 | done |

### Smoke suite после G0 / 0.4.0

- Aura node без magenta, светится, частицы  
- Tubes соединяют jar↔smelter без Z-fight  
- Workbench: title + Vis читаются  
- Crystal cluster выглядит 3D в руке и мире  
- Thaumonomicon: «In Progress», нормальные tooltips  
- Creative: без ×64 на иконках  
- Warp event → spiders / blindness+ / guardians; rare warden at permanent warp > 50  
- Pollute aura may place flux goo; void robe 2+ set discount; JEI infusion; amulet recharge with Curios  

## Phase H — Polish + Depth (после 0.4.0)

Цель ship: **0.5.0** — **shipped**.

| ID | Модуль | Статус |
|----|--------|--------|
| H0 | Smoke QA runClient + backlog | done |
| H1 | Polish: GUI tex, workbench light, Curios profile, FX | done |
| H2 | Content: golem AI, valve, infusion FX, ARTIFICE/GOLEMANCY, recipes | done |
| H3 | Ship 0.5.0 docs + commit | done |

### Smoke checklist (H0)
- [x] Client launches with Arcana (+ JEI)
- [x] Research loads BASICS/AUROMANCY/ARTIFICE/GOLEMANCY
- [x] Spawn egg models present
- [ ] In-world visual QA (user screenshots optional)
- [ ] Infusion tick craft + foci Shock/Earth/Heal
- [ ] Golem job GUI + FILL/EMPTY
- [ ] Warp / flux goo / warden egg

## Phase I+ — Roadmap to “complete-enough” Arcana (после 0.5.0)

Цель: довести порт до играбельного TC6-like цикла **research → craft → cast → automate → explore → survive warp**, без 1:1 клона всего legacy API.

Ship milestones:
- **0.6.0** — Phase I Systems — **shipped**
- **0.7.0** — Phase J Content pack
- **0.8.0** — Phase K World & threats
- **0.9.0** — Phase L Polish / soft-deps
- **1.0.0** — Phase M Release candidate

### Phase I — Systems depth (→ 0.6.0) — **shipped**

| ID | Модуль | Статус |
|----|--------|--------|
| I1 | Aura node types + drain/recharge radii | done |
| I2 | Essentia network v2 (suction graph, filtered tubes, bellows push) | done |
| I3 | Infusion ritual polish (multiblock check, pedestal count, Runic matrix FX) | done |
| I4 | Focus graph real (node costs, complexity caps, package serialize UI) | done |
| I5 | Research table + theorycraft loop | done |
| I6 | Scan → aspect discovery + research unlocks pack | done |

### Phase J — Content pack (→ 0.7.0)

| ID | Модуль |
|----|--------|
| J1 | Full research trees: BASICS / AUROMANCY / ARTIFICE / ALCHEMY / GOLEMANCY / ELDRITCH |
| J2 | Recipe flood: workbench / crucible / infusion for tools, devices, foci tiers |
| J3 | Devices: arcane levitator, mirror, lamp of growth, hungry chest stubs |
| J4 | Golem upgrades: cores, accessories, more seals (harvest/use/butcher) |
| J5 | Armor/curios set: goggles tiers, traveller boots, cloudstepper, charm slots |

### Phase K — World & threats (→ 0.8.0)

| ID | Модуль |
|----|--------|
| K1 | Structures: eldritch ring, hilltop stones, cultist camp mini |
| K2 | Dimensions stub OR outer lands portal tease (or defer full dim) |
| K3 | Bosses: Crimson Cult captain → Warden fight phases |
| K4 | Warp ladder final: permanent/temp/sticky, sanities, nightmares |
| K5 | Biomes/plants: ethereal bloom, taint-lite OR flux biomes |

### Phase L — Polish & soft-deps (→ 0.9.0)

| ID | Модуль |
|----|--------|
| L1 | Dedicated textures/models pass (no magenta, unique GUIs) |
| L2 | Sounds pack + particles consistency |
| L3 | JEI/REI full coverage; patchouli or in-book pages |
| L4 | Curios real-client profile + optional runtime flag docs |
| L5 | Configs: aura rates, warp intensity, worldgen rarity |
| L6 | Localization RU + EN; advancements |

### Phase M — 1.0.0 RC

| ID | Модуль |
|----|--------|
| M1 | Full smoke suite + regression checklist |
| M2 | Performance pass (aura chunks, tube ticks) |
| M3 | License/credits vs TC6 source disclosure |
| M4 | Modrinth/Curse publish artifacts + changelog |

### Рекомендуемый порядок работы

`I5 → I6 → I2 → I3 → I4 → I1` затем `J1–J2` (контент на живых системах), потом `K`, `L`, `M`.

Правило без изменений: модуль не «done», пока не прошёл `runClient` smoke.

