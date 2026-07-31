#!/usr/bin/env python3
"""Adapt tc6_*.json research trees → primary category JSONs + lang keys."""
from __future__ import annotations

import json
import re
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
RESEARCH = ROOT / "src/main/resources/assets/arcana/research"
LANG = ROOT / "src/main/resources/assets/arcana/lang/en_us.json"
MOD_ITEMS_JAVA = ROOT / "src/main/java/arcana/registry/ModItems.java"
MOD_BLOCKS_JAVA = ROOT / "src/main/java/arcana/registry/ModBlocks.java"

# Known TC6 → Arcana / Minecraft remaps when IDs diverge
ITEM_REMAP = {
    "arcana:nugget_iron": "minecraft:iron_nugget",
    "arcana:nugget_gold": "minecraft:gold_nugget",
    "arcana:nugget_copper": "minecraft:copper_ingot",
    "arcana:nugget_quartz": "minecraft:quartz",
    "arcana:nugget_thaumium": "arcana:thaumium_nugget",
    "arcana:nugget_void": "arcana:void_nugget",
    "arcana:ingot_thaumium": "arcana:thaumium_ingot",
    "arcana:ingot_void": "arcana:void_ingot",
    "arcana:plate_thaumium": "arcana:thaumium_plate",
    "arcana:plate_void": "arcana:void_plate",
    "arcana:plate_iron": "minecraft:iron_ingot",
    "arcana:plate_brass": "minecraft:gold_ingot",
    "arcana:quicksilver": "arcana:quicksilver",
    "arcana:salis_mundus": "arcana:salis_mundus",
    "arcana:thaumonomicon": "arcana:thaumonomicon",
    "arcana:thaumometer": "arcana:thaumometer",
    "arcana:vis_resonator": "arcana:vis_resonator",
    "arcana:caster_basic": "arcana:caster_basic",
    "arcana:focus_1": "arcana:focus_1",
    "arcana:focus_2": "arcana:focus_2",
    "arcana:focus_3": "arcana:focus_3",
    "arcana:goggles": "arcana:goggles",
    "arcana:jar_normal": "arcana:jar",
    "arcana:jar_void": "arcana:jar",
    "arcana:tube": "arcana:essentia_tube",
    "arcana:tube_filter": "arcana:essentia_filter_tube",
    "arcana:tube_valve": "arcana:essentia_valve",
    "arcana:tube_restrict": "arcana:essentia_restrict_tube",
    "arcana:tube_oneway": "arcana:essentia_oneway_tube",
    "arcana:tube_buffer": "arcana:essentia_buffer_tube",
    "arcana:smelter_basic": "arcana:smelter",
    "arcana:smelter_thaumium": "arcana:smelter",
    "arcana:alembic": "arcana:alembic",
    "arcana:bellows": "arcana:bellows",
    "arcana:centrifuge": "arcana:centrifuge",
    "arcana:crucible": "arcana:crucible",
    "arcana:arcane_workbench": "arcana:arcane_workbench",
    "arcana:research_table": "arcana:research_table",
    "arcana:infusion_matrix": "arcana:infusion_matrix",
    "arcana:pedestal_arcane": "arcana:pedestal",
    "arcana:pedestal_ancient": "arcana:pedestal",
    "arcana:pillar_arcane": "arcana:arcane_pillar",
    "arcana:pillar_ancient": "arcana:arcane_pillar",
    "arcana:focal_manipulator": "arcana:focal_manipulator",
    "arcana:levitator": "arcana:levitator",
    "arcana:mirror": "arcana:magic_mirror",
    "arcana:mirror_essentia": "arcana:magic_mirror",
    "arcana:hungry_chest": "arcana:hungry_chest",
    "arcana:lamp_growth": "arcana:lamp_of_growth",
    "arcana:lamp_arcane": "arcana:lamp_of_growth",
    "arcana:label": "arcana:label",
    "arcana:filter": "minecraft:paper",
    "arcana:phial": "minecraft:glass_bottle",
    "arcana:crystal_aer": "arcana:crystal_aer",
    "arcana:crystal_terra": "arcana:crystal_terra",
    "arcana:crystal_ignis": "arcana:crystal_ignis",
    "arcana:crystal_aqua": "arcana:crystal_aqua",
    "arcana:crystal_ordo": "arcana:crystal_ordo",
    "arcana:crystal_perditio": "arcana:crystal_perditio",
    "arcana:crystal_essence": "arcana:crystal_ordo",
    "arcana:nitor_yellow": "minecraft:glowstone_dust",
    "arcana:alumentum": "minecraft:coal",
    "arcana:tallow": "arcana:tallow",
    "arcana:brain": "arcana:zombie_brain",
    "arcana:void_seed": "arcana:void_seed",
    "arcana:primordial_pearl": "minecraft:ender_pearl",
    "arcana:causality_collapser": "minecraft:tnt",
    "arcana:sanity_soap": "minecraft:slime_ball",
    "arcana:bath_salts": "minecraft:sugar",
    "arcana:creative_flux_sponge": "minecraft:sponge",
    "arcana:amulet_vis": "arcana:amulet_vis",
    "arcana:ring_growth": "arcana:ring_apprentice",
    "arcana:baubles": "arcana:ring_apprentice",
    "arcana:charm_undying": "arcana:charm_undying",
    "arcana:cloudstepper": "arcana:cloudstepper",
    "arcana:traveller_boots": "arcana:traveller_boots",
    "arcana:golem": "arcana:golem",
    "arcana:seal": "arcana:seal_blank",
    "arcana:seal_blank": "arcana:seal_blank",
    "arcana:mind": "minecraft:paper",
    "arcana:module": "minecraft:redstone",
    "arcana:mechanism_simple": "minecraft:redstone",
    "arcana:mechanism_complex": "minecraft:comparator",
    "arcana:morphic_resonator": "minecraft:ender_eye",
    "arcana:condensator": "minecraft:glass",
    "arcana:redstone_relay": "minecraft:repeater",
    "arcana:arcane_ear": "minecraft:note_block",
    "arcana:arcane_ear_toggle": "minecraft:note_block",
    "arcana:paving_stone_travel": "minecraft:stone_bricks",
    "arcana:paving_stone_barrier": "minecraft:obsidian",
    "arcana:barrier_stone": "minecraft:obsidian",
    "arcana:inlay": "minecraft:gold_nugget",
    "arcana:transfer": "minecraft:hopper",
    "arcana:transfer_valve": "minecraft:hopper",
    "arcana:brain_box": "arcana:zombie_brain",
    "arcana:dioptra": "arcana:thaumometer",
    "arcana:recharge_pedestal": "arcana:pedestal",
    "arcana:vis_battery": "minecraft:redstone_block",
    "arcana:vis_generator": "minecraft:redstone_block",
    "arcana:stabilizer": "minecraft:obsidian",
    "arcana:workbench_charger": "minecraft:redstone",
    "arcana:pattern_crafter": "minecraft:crafting_table",
    "arcana:spa": "minecraft:cauldron",
    "arcana:water_jug": "minecraft:bucket",
    "arcana:banner": "minecraft:white_banner",
    "arcana:loot_bag": "minecraft:bundle",
    "arcana:loot_crate": "minecraft:chest",
    "arcana:loot_urn": "minecraft:flower_pot",
    "arcana:eldritch_eye": "minecraft:ender_eye",
    "arcana:crimson_blade": "arcana:void_sword",
    "arcana:crimson_praetor_helm": "arcana:void_helmet",
    "arcana:crimson_praetor_chest": "arcana:void_chestplate",
    "arcana:crimson_praetor_legs": "arcana:void_leggings",
    "arcana:crimson_robe_helm": "arcana:void_robe_helmet",
    "arcana:crimson_robe_chest": "arcana:void_robe_chest",
    "arcana:crimson_robe_legs": "arcana:void_robe_legs",
    "arcana:void_robe_helm": "arcana:void_robe_helmet",
    "arcana:void_robe_chest": "arcana:void_robe_chest",
    "arcana:void_robe_legs": "arcana:void_robe_legs",
    "arcana:void_helm": "arcana:void_helmet",
    "arcana:void_chest": "arcana:void_chestplate",
    "arcana:void_legs": "arcana:void_leggings",
    "arcana:void_boots": "arcana:void_boots",
    "arcana:thaumium_helm": "arcana:thaumium_helmet",
    "arcana:thaumium_chest": "arcana:thaumium_chestplate",
    "arcana:thaumium_legs": "arcana:thaumium_leggings",
    "arcana:thaumium_boots": "arcana:thaumium_boots",
    "arcana:thaumium_axe": "arcana:thaumium_axe",
    "arcana:thaumium_pick": "arcana:thaumium_pickaxe",
    "arcana:thaumium_shovel": "arcana:thaumium_shovel",
    "arcana:thaumium_hoe": "arcana:thaumium_hoe",
    "arcana:thaumium_sword": "arcana:thaumium_sword",
    "arcana:elemental_axe": "arcana:thaumium_axe",
    "arcana:elemental_pick": "arcana:thaumium_pickaxe",
    "arcana:elemental_shovel": "arcana:thaumium_shovel",
    "arcana:elemental_hoe": "arcana:thaumium_hoe",
    "arcana:elemental_sword": "arcana:thaumium_sword",
    "arcana:primal_crusher": "arcana:thaumium_pickaxe",
    "arcana:sanity_checker": "arcana:thaumometer",
    "arcana:resonator": "arcana:vis_resonator",
    "arcana:hand_mirror": "arcana:magic_mirror",
    "arcana:turret": "minecraft:dispenser",
    "arcana:turret_advanced": "minecraft:dispenser",
    "arcana:turret_bore": "minecraft:dispenser",
    "arcana:grappler": "minecraft:fishing_rod",
    "arcana:jar_brain": "arcana:jar",
    "arcana:pondering_table": "arcana:research_table",
    "arcana:scribing_tools": "minecraft:ink_sac",
    "arcana:celestial_notes": "minecraft:paper",
    "arcana:curio": "minecraft:book",
    "arcana:knowledge_fragment": "minecraft:paper",
    "arcana:enchanted_paper": "minecraft:paper",
    "arcana:cluster_iron": "minecraft:iron_ore",
    "arcana:cluster_gold": "minecraft:gold_ore",
    "arcana:cluster_cinnabar": "minecraft:redstone_ore",
    "arcana:cluster_copper": "minecraft:copper_ore",
    "arcana:cluster_tin": "minecraft:iron_ore",
    "arcana:cluster_silver": "minecraft:iron_ore",
    "arcana:cluster_lead": "minecraft:iron_ore",
    "arcana:cluster_quartz": "minecraft:nether_quartz_ore",
    "arcana:ore_cinnabar": "minecraft:redstone_ore",
    "arcana:ore_amber": "minecraft:coal_ore",
    "arcana:ore_quartz": "minecraft:nether_quartz_ore",
    "arcana:log_greatwood": "arcana:greatwood_log",
    "arcana:log_silverwood": "arcana:silverwood_log",
    "arcana:leaf_greatwood": "arcana:greatwood_leaves",
    "arcana:leaf_silverwood": "arcana:silverwood_leaves",
    "arcana:sapling_greatwood": "arcana:greatwood_sapling",
    "arcana:sapling_silverwood": "arcana:silverwood_sapling",
    "arcana:plank_greatwood": "arcana:greatwood_planks",
    "arcana:plank_silverwood": "arcana:silverwood_planks",
    "arcana:cinderpearl": "arcana:cinderpearl",
    "arcana:shimmerleaf": "arcana:shimmerleaf",
    "arcana:vishroom": "minecraft:brown_mushroom",
    "arcana:cinderpearl_powder": "minecraft:blaze_powder",
    "arcana:shimmerleaf_powder": "minecraft:glowstone_dust",
    "arcana:amber": "minecraft:honeycomb",
    "arcana:taint_rock": "minecraft:netherrack",
    "arcana:taint_soil": "minecraft:soul_sand",
    "arcana:taint_fibre": "minecraft:string",
    "arcana:taint_feature": "minecraft:cobweb",
    "arcana:taint_log": "minecraft:crimson_stem",
    "arcana:taint_crumb": "minecraft:nether_wart",
    "arcana:taint_geyser": "minecraft:magma_block",
    "arcana:flesh_block": "minecraft:rotten_flesh",
    "arcana:stone_arcane": "minecraft:stone",
    "arcana:stone_arcane_brick": "minecraft:stone_bricks",
    "arcana:stone_ancient": "minecraft:deepslate",
    "arcana:stone_ancient_tile": "minecraft:deepslate_tiles",
    "arcana:stone_ancient_rock": "minecraft:cobbled_deepslate",
    "arcana:stone_ancient_glyphed": "minecraft:chiseled_deepslate",
    "arcana:stone_ancient_doorway": "minecraft:deepslate_bricks",
    "arcana:stone_eldritch_tile": "arcana:eldritch_stone",
    "arcana:stone_porous": "minecraft:tuff",
    "arcana:stairs_greatwood": "minecraft:oak_stairs",
    "arcana:stairs_silverwood": "minecraft:birch_stairs",
    "arcana:stairs_arcane": "minecraft:stone_stairs",
    "arcana:stairs_arcane_brick": "minecraft:stone_brick_stairs",
    "arcana:stairs_ancient": "minecraft:deepslate_brick_stairs",
    "arcana:slab_greatwood": "minecraft:oak_slab",
    "arcana:slab_silverwood": "minecraft:birch_slab",
    "arcana:slab_arcane_stone": "minecraft:stone_slab",
    "arcana:slab_arcane_brick": "minecraft:stone_brick_slab",
    "arcana:slab_ancient_stone": "minecraft:deepslate_brick_slab",
    "arcana:void_seed": "arcana:void_seed",
}

VANILLA_FALLBACK = "minecraft:paper"


def load_registered_ids() -> set[str]:
    ids: set[str] = set()
    for path in (MOD_ITEMS_JAVA, MOD_BLOCKS_JAVA):
        if not path.exists():
            continue
        text = path.read_text(encoding="utf-8")
        ids.update(re.findall(r'register\(\s*"([^"]+)"', text))
    return ids


def remap_item_id(item_id: str, known: set[str]) -> str | None:
    if item_id in ITEM_REMAP:
        item_id = ITEM_REMAP[item_id]
    if item_id.startswith("minecraft:"):
        return item_id
    if item_id.startswith("arcana:"):
        path = item_id.split(":", 1)[1]
        if path in known:
            return item_id
        # try common aliases
        for alt in (path.replace("-", "_"), path):
            if alt in known:
                return f"arcana:{alt}"
        mapped = ITEM_REMAP.get(item_id)
        if mapped:
            return mapped
        return VANILLA_FALLBACK
    return item_id


def scrub_stage_lists(obj: dict, known: set[str]) -> None:
    """Drop or remap item refs in recipes/obtain/craft arrays."""
    for key in ("recipes", "recipes_craft", "obtain", "craft", "know"):
        if key not in obj or not isinstance(obj[key], list):
            continue
        cleaned = []
        for entry in obj[key]:
            if not isinstance(entry, str):
                cleaned.append(entry)
                continue
            if ":" not in entry:
                cleaned.append(entry)
                continue
            # recipe ids like arcana:thaumometer keep if recipe likely exists
            if entry.startswith("arcana:") or entry.startswith("thaumcraft:"):
                entry = entry.replace("thaumcraft:", "arcana:")
                remapped = remap_item_id(entry, known)
                if remapped is None:
                    continue
                cleaned.append(remapped)
            else:
                cleaned.append(entry)
        obj[key] = cleaned


def adapt_tree(src: Path, dest: Path, known: set[str]) -> int:
    raw = src.read_text(encoding="utf-8-sig")
    raw = raw.replace("thaumcraft:", "arcana:")
    data = json.loads(raw)
    entries = data.get("entries", [])
    for entry in entries:
        # icons
        for field in ("icons", "icon"):
            if field in entry and isinstance(entry[field], list):
                new_icons = []
                for ic in entry[field]:
                    if isinstance(ic, str) and ":" in ic:
                        rem = remap_item_id(ic, known)
                        if rem:
                            new_icons.append(rem)
                    else:
                        new_icons.append(ic)
                entry[field] = new_icons if new_icons else ["minecraft:book"]
            elif field in entry and isinstance(entry[field], str) and ":" in entry[field]:
                entry[field] = remap_item_id(entry[field], known) or "minecraft:book"
        for stage in entry.get("stages", []) or []:
            if isinstance(stage, dict):
                scrub_stage_lists(stage, known)
                for rk in ("required_item", "required_craft", "required_knowledge"):
                    if rk in stage and isinstance(stage[rk], list):
                        stage[rk] = scrub_list(stage[rk], known)
    dest.write_text(json.dumps(data, indent=2, ensure_ascii=False) + "\n", encoding="utf-8")
    return len(entries)


def scrub_list(items: list, known: set[str]) -> list:
    out = []
    for entry in items:
        if isinstance(entry, str) and ":" in entry:
            rem = remap_item_id(entry.replace("thaumcraft:", "arcana:"), known)
            if rem:
                out.append(rem)
        else:
            out.append(entry)
    return out


def title_from_key(key: str) -> str:
    words = re.findall(r"[A-Z]+(?=[A-Z][a-z]|$)|[A-Z]?[a-z]+|\d+", key)
    if not words:
        return key.title()
    return " ".join(w.capitalize() if w.islower() or w.isupper() else w for w in words)


def sync_lang(keys: set[str]) -> int:
    lang = json.loads(LANG.read_text(encoding="utf-8-sig"))
    added = 0
    for key in sorted(keys):
        title_k = f"research.{key}.title"
        stage_k = f"research.{key}.stage.1"
        if title_k not in lang:
            lang[title_k] = title_from_key(key)
            added += 1
        if stage_k not in lang:
            lang[stage_k] = f"Progress research {title_from_key(key)}."
            added += 1
    # Stable-ish sort: keep existing order then append new? Prefer sorted keys for reproducibility of new ones.
    # Write with indent preserving readability.
    LANG.write_text(json.dumps(lang, indent=2, ensure_ascii=False) + "\n", encoding="utf-8")
    return added


def main() -> None:
    known = load_registered_ids()
    print(f"Registered item/block ids: {len(known)}")
    mapping = {
        "tc6_basics.json": "basics.json",
        "tc6_alchemy.json": "alchemy.json",
        "tc6_auromancy.json": "auromancy.json",
        "tc6_artifice.json": "artifice.json",
        "tc6_infusion.json": "infusion.json",
        "tc6_golemancy.json": "golemancy.json",
        "tc6_eldritch.json": "eldritch.json",
        "tc6_scans.json": "scans.json",
    }
    all_keys: set[str] = set()
    total = 0
    for src_name, dest_name in mapping.items():
        src = RESEARCH / src_name
        dest = RESEARCH / dest_name
        if not src.exists():
            print(f"SKIP missing {src_name}")
            continue
        n = adapt_tree(src, dest, known)
        data = json.loads(dest.read_text(encoding="utf-8"))
        for e in data.get("entries", []):
            all_keys.add(e["key"])
        total += n
        print(f"{src_name} -> {dest_name}: {n} entries")
    added = sync_lang(all_keys)
    print(f"Total entries: {total}; lang keys added: {added}; unique keys: {len(all_keys)}")


if __name__ == "__main__":
    main()
