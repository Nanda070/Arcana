package arcana.api.casters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;

/**
 * Linear focus package: ROOT → medium → effect(s).
 * Migrates legacy medium/effect NBT from M10.
 */
public final class FocusPackage {
    public static final String ROOT = "arcana.ROOT";
    public static final String MEDIUM_TOUCH = "arcana.TOUCH";
    public static final String MEDIUM_PROJECTILE = "arcana.PROJECTILE";
    public static final String EFFECT_FIRE = "arcana.FIRE";
    public static final String EFFECT_FROST = "arcana.FROST";
    public static final String EFFECT_SHOCK = "arcana.SHOCK";
    public static final String EFFECT_EARTH = "arcana.EARTH";
    public static final String EFFECT_HEAL = "arcana.HEAL";

    private final List<String> nodes = new ArrayList<>();
    private int complexity = 8;

    public FocusPackage() {
    }

    public FocusPackage(List<String> nodes) {
        this.nodes.addAll(nodes);
        this.complexity = computeComplexity(this.nodes);
    }

    public static FocusPackage of(String... nodeKeys) {
        return new FocusPackage(Arrays.asList(nodeKeys));
    }

    public static FocusPackage touchFire() {
        return of(ROOT, MEDIUM_TOUCH, EFFECT_FIRE);
    }

    public static FocusPackage projectileFire() {
        return of(ROOT, MEDIUM_PROJECTILE, EFFECT_FIRE);
    }

    public static FocusPackage touchFrost() {
        return of(ROOT, MEDIUM_TOUCH, EFFECT_FROST);
    }

    public static FocusPackage projectileFrost() {
        return of(ROOT, MEDIUM_PROJECTILE, EFFECT_FROST);
    }

    public static FocusPackage touchShock() {
        return of(ROOT, MEDIUM_TOUCH, EFFECT_SHOCK);
    }

    public static FocusPackage projectileShock() {
        return of(ROOT, MEDIUM_PROJECTILE, EFFECT_SHOCK);
    }

    public static FocusPackage touchEarth() {
        return of(ROOT, MEDIUM_TOUCH, EFFECT_EARTH);
    }

    public static FocusPackage touchHeal() {
        return of(ROOT, MEDIUM_TOUCH, EFFECT_HEAL);
    }

    public static FocusPackage fromPreset(String preset) {
        return switch (preset.toLowerCase()) {
            case "touch_fire", "touchfire", "fire" -> touchFire();
            case "projectile_fire", "proj_fire", "bolt_fire" -> projectileFire();
            case "touch_frost", "touchfrost", "frost" -> touchFrost();
            case "projectile_frost", "proj_frost", "bolt_frost" -> projectileFrost();
            case "touch_shock", "touchshock", "shock" -> touchShock();
            case "projectile_shock", "proj_shock", "bolt_shock" -> projectileShock();
            case "touch_earth", "touchearth", "earth" -> touchEarth();
            case "touch_heal", "touchheal", "heal" -> touchHeal();
            default -> touchFire();
        };
    }

    public List<String> getNodes() {
        return Collections.unmodifiableList(nodes);
    }

    public boolean hasNode(String key) {
        return nodes.contains(key);
    }

    public String getMedium() {
        if (hasNode(MEDIUM_PROJECTILE)) {
            return MEDIUM_PROJECTILE;
        }
        return MEDIUM_TOUCH;
    }

    public String getEffect() {
        if (hasNode(EFFECT_HEAL)) {
            return EFFECT_HEAL;
        }
        if (hasNode(EFFECT_SHOCK)) {
            return EFFECT_SHOCK;
        }
        if (hasNode(EFFECT_EARTH)) {
            return EFFECT_EARTH;
        }
        if (hasNode(EFFECT_FROST)) {
            return EFFECT_FROST;
        }
        return EFFECT_FIRE;
    }

    /** Nodes after the medium — applied on touch hit or projectile impact. */
    public FocusPackage remainingEffects() {
        List<String> rem = new ArrayList<>();
        boolean pastMedium = false;
        for (String n : nodes) {
            if (pastMedium) {
                rem.add(n);
            }
            if (MEDIUM_TOUCH.equals(n) || MEDIUM_PROJECTILE.equals(n)) {
                pastMedium = true;
            }
        }
        if (rem.isEmpty()) {
            rem.add(getEffect());
        }
        FocusPackage p = new FocusPackage(rem);
        p.complexity = this.complexity;
        return p;
    }

    public int getComplexity() {
        return complexity;
    }

    public float getVisCost() {
        return complexity / 5.0f;
    }

    public int getActivationTicks() {
        return Math.max(5, (complexity / 5) * Math.max(1, complexity / 4));
    }

    public String describe() {
        StringBuilder sb = new StringBuilder();
        for (String n : nodes) {
            if (ROOT.equals(n)) {
                continue;
            }
            if (sb.length() > 0) {
                sb.append(" → ");
            }
            sb.append(n.substring(n.indexOf('.') + 1));
        }
        return sb.length() == 0 ? "Empty" : sb.toString();
    }

    public String describeComplexity() {
        return "Complexity " + getComplexity();
    }

    public static String researchForNode(String node) {
        return switch (node) {
            case MEDIUM_PROJECTILE -> "FOCUSPROJECTILE";
            case EFFECT_FROST -> "FOCUSELEMENTAL";
            case EFFECT_SHOCK -> "FOCUSSHOCK";
            case EFFECT_HEAL -> "FOCUSHEAL";
            case EFFECT_EARTH -> "FOCUSELEMENTAL";
            case ROOT, MEDIUM_TOUCH, EFFECT_FIRE -> "BASEAUROMANCY";
            default -> "BASEAUROMANCY";
        };
    }

    public static int costForNode(String node) {
        return switch (node) {
            case ROOT -> 0;
            case MEDIUM_TOUCH -> 2;
            case MEDIUM_PROJECTILE -> 4;
            case EFFECT_FIRE -> 4;
            case EFFECT_FROST -> 5;
            case EFFECT_SHOCK -> 5;
            case EFFECT_EARTH -> 4;
            case EFFECT_HEAL -> 6;
            default -> 2;
        };
    }

    private static int computeComplexity(List<String> nodes) {
        int c = 0;
        for (String n : nodes) {
            c += costForNode(n);
        }
        return Math.max(4, c);
    }

    public CompoundTag serialize() {
        CompoundTag tag = new CompoundTag();
        ListTag list = new ListTag();
        for (String n : nodes) {
            list.add(StringTag.valueOf(n));
        }
        tag.put("nodes", list);
        tag.putInt("complexity", complexity);
        // legacy mirrors for older readers
        tag.putString("medium", getMedium());
        tag.putString("effect", getEffect());
        return tag;
    }

    public void deserialize(CompoundTag tag) {
        nodes.clear();
        if (tag.contains("nodes", Tag.TAG_LIST)) {
            ListTag list = tag.getList("nodes", Tag.TAG_STRING);
            for (int i = 0; i < list.size(); i++) {
                nodes.add(list.getString(i));
            }
        } else {
            // M10 migrate
            String medium = tag.contains("medium") ? tag.getString("medium") : MEDIUM_TOUCH;
            String effect = tag.contains("effect") ? tag.getString("effect") : EFFECT_FIRE;
            nodes.add(ROOT);
            nodes.add(medium);
            nodes.add(effect);
        }
        if (nodes.isEmpty()) {
            nodes.addAll(Arrays.asList(ROOT, MEDIUM_TOUCH, EFFECT_FIRE));
        }
        complexity = tag.contains("complexity") ? tag.getInt("complexity") : computeComplexity(nodes);
    }

    public FocusPackage copy() {
        FocusPackage p = new FocusPackage(new ArrayList<>(nodes));
        p.complexity = complexity;
        return p;
    }
}
