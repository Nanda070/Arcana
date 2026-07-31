#!/usr/bin/env python3
"""Add research gates to arcana:* recipes missing the research key."""
from __future__ import annotations

import json
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
RECIPES = ROOT / "src/main/resources/data/arcana/recipes"

# Prefer specific research keys when result/item basename matches.
BETTER = {
    "thaumometer": "FIRSTSTEPS",
    "thaumonomicon": "FIRSTSTEPS",
    "salis_mundus": "FIRSTSTEPS",
    "arcane_workbench": "FIRSTSTEPS",
    "research_table": "RESEARCHEXPERTISE",
    "crucible": "CRUCIBLE",
    "essentia_tube": "TUBES",
    "essentia_filter_tube": "TUBES",
    "essentia_valve": "TUBES",
    "essentia_restrict_tube": "TUBES",
    "essentia_oneway_tube": "TUBES",
    "essentia_buffer_tube": "TUBES",
    "smelter": "IMPROVEDSMELTING",
    "alembic": "DISTILLATION",
    "bellows": "BELLOWS",
    "centrifuge": "CENTRIFUGE",
    "jar": "WARDEDJARS",
    "label": "WARDEDJARS",
    "infusion_matrix": "INFUSION",
    "pedestal": "INFUSION",
    "arcane_pillar": "INFUSION",
    "focal_manipulator": "BASEAUROMANCY",
    "focus_1": "BASEAUROMANCY",
    "caster_basic": "BASEAUROMANCY",
    "goggles": "GOGGLES",
    "golem": "GOLEMBASIC",
    "seal_blank": "GOLEMBASIC",
    "seal_gather": "SEALCOLLECT",
    "seal_guard": "SEALGUARD",
    "seal_fill": "SEALSTORE",
    "seal_empty": "SEALSTORE",
    "seal_harvest": "SEALHARVEST",
    "seal_butcher": "SEALBUTCHER",
    "seal_use": "SEALUSE",
    "void_seed": "BASEELDRITCH",
    "void_ingot": "BASEELDRITCH",
    "outer_lands_portal": "OUTERLANDS",
    "magic_mirror": "MIRRORHAND",
    "levitator": "LEVITATOR",
    "lamp_of_growth": "LAMPOFGROWTH",
    "hungry_chest": "HUNGRYCHEST",
    "ring_apprentice": "BASEAUROMANCY",
    "amulet_vis": "BASEAUROMANCY",
}


def guess_research(path: Path, data: dict) -> str:
    name = path.stem
    if name in BETTER:
        return BETTER[name]
    result = data.get("result", {})
    if isinstance(result, dict):
        item = result.get("item", "")
        if isinstance(item, str) and ":" in item:
            bare = item.split(":", 1)[1]
            if bare in BETTER:
                return BETTER[bare]
    # Heuristics by prefix
    if name.startswith("void_"):
        return "BASEELDRITCH"
    if name.startswith("thaumium_"):
        return "METALLURGY"
    if name.startswith("focus_"):
        return "BASEAUROMANCY"
    if name.startswith("seal_"):
        return "GOLEMBASIC"
    if name.startswith("golem"):
        return "GOLEMBASIC"
    if "infusion" in name or name in ("pedestal", "arcane_pillar"):
        return "INFUSION"
    return "FIRSTSTEPS"


def main() -> None:
    updated = 0
    already = 0
    for path in sorted(RECIPES.rglob("*.json")):
        raw = path.read_text(encoding="utf-8-sig")
        data = json.loads(raw)
        typ = data.get("type", "")
        if not isinstance(typ, str) or not typ.startswith("arcana:"):
            continue
        if "research" in data and data["research"]:
            already += 1
            continue
        data["research"] = guess_research(path, data)
        path.write_text(json.dumps(data, indent=2, ensure_ascii=False) + "\n", encoding="utf-8")
        updated += 1
        print(f"+ {path.name} -> {data['research']}")
    print(f"Updated {updated}; already gated {already}")


if __name__ == "__main__":
    main()
