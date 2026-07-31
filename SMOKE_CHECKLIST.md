# Arcana 1.0.0 — Smoke / Regression Checklist

Use after a clean `runClient` (or real Forge client). Mark each item when verified.
Quick registry check: `/arcana smoke` (op, or anyone in FG userdev).

## Full playable loop

### Aspects & research
- [ ] `/arcana aspects` lists primal + compound
- [ ] Thaumonomicon opens; BASICS / AUROMANCY / ARTIFICE / ALCHEMY / GOLEMANCY / ELDRITCH present
- [ ] Research table: Study (Observation→Theory) and Note consume work
- [ ] `/arcana smoke` → ALL PASS (aspects, ≥6 categories, key blocks registered)

### Scan
- [ ] Thaumometer RMB on iron/etc unlocks aspects; Observation gains
- [ ] Scanned aspects appear in book / research map

### Aura nodes
- [ ] Place / find aura node — no magenta, particles, regen into buffer then chunk aura
- [ ] Cycle aspect / type; thaumometer / caster nearby recharge FX
- [ ] Vis drain prefers nearby nodes within drain radius

### Essentia network
- [ ] Warded jar fill → tubes → second jar
- [ ] Filter tube respects aspect filter
- [ ] Essentia valve closes on redstone
- [ ] Bellows pushes 1 essentia behind→front (~2s)

### Crafting
- [ ] Crucible: water + heat → dump crystals; throw iron → thaumium
- [ ] Arcane Workbench: salis+table; craft gated recipe with vis + crystals
- [ ] Infusion: matrix + pedestals + jars → focus_2 / robe piece; instability on bad setup

### Casting
- [ ] Gauntlet + focus cast drains vis; Touch/Projectile Fire/Frost work
- [ ] Focal Manipulator compose / presets; research gates

### Golems & seals
- [ ] Place golem; follow / stay; dump inventory
- [ ] Seal gather/guard/fill/empty/harvest/use/butcher on golem or wall
- [ ] Stay golem seeks world seal

### Warp & threats
- [ ] `/arcana warp add NORMAL 40` then `event` → whisper / effect / spawn
- [ ] Sticky (NORMAL) and TEMPORARY decay; gear warp on void set
- [ ] High warp → mind spiders / guardians; rare Warden; cultist/captain possible

### World & structures
- [ ] Find or `/place` crystal cluster, cinderpearl, shimmerleaf, ethereal bloom
- [ ] Greatwood / silverwood grow or generate
- [ ] Eldritch ring / hilltop stones / cultist camp / flux patch
- [ ] Outer Lands portal tease (message / warp / nausea; no full dim)

### Devices
- [ ] Arcane levitator lifts entities
- [ ] Magic mirror pair teleport
- [ ] Lamp of growth accelerates crops
- [ ] Hungry chest vacuums nearby items (throttled scan)

### Soft deps & polish
- [ ] Curios: with runtime enabled / real client, ring/amulet/charm slots work; without Curios, mod still loads
- [ ] JEI: Arcane Workbench, Crucible, Infusion categories + info pages
- [ ] Locale: switch to `ru_ru` — key strings translate
- [ ] Advancements: root → first_scan / first_infusion / eldritch_contact / golemancy

## Performance notes (Phase M2)

| Area | Behavior |
|------|----------|
| AuraNodeBlockEntity | Regen every **80t**; player sensor AABB batched every **2nd** regen; silverwood scan same cadence |
| EssentiaTubeBlockEntity | Tick every **5t** when holding essentia; every **10t** when idle (empty) |
| Bellows | Push every **40t**; no entity AABB |
| HungryChest | Item vacuum every **40t**, AABB inflate **2** (not every tick) |
| WarpEvents / WarpHelper | Gear-warp cached per player-tick; warp counter read once per check |
| AuraHandler.tickRegen | Once per second over chunk map — fine at current scale; no micro-opt |

See also `PORT_PLAN.md` Phase M.
