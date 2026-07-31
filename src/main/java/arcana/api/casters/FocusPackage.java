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
 * Complexity/research costs mirror TC6 default (power≈1) settings.
 */
public final class FocusPackage {
    public static final String ROOT = "arcana.ROOT";

    public static final String MEDIUM_TOUCH = "arcana.TOUCH";
    public static final String MEDIUM_PROJECTILE = "arcana.PROJECTILE";
    public static final String MEDIUM_BOLT = "arcana.BOLT";
    public static final String MEDIUM_CLOUD = "arcana.CLOUD";
    /** Stub medium — reserved for mining-beam focus (not cast yet). */
    public static final String MEDIUM_MINE = "arcana.MINE";

    public static final String MOD_SCATTER = "arcana.SCATTER";
    public static final String MOD_SPLIT = "arcana.SPLIT";

    public static final String EFFECT_AIR = "arcana.AIR";
    public static final String EFFECT_BREAK = "arcana.BREAK";
    public static final String EFFECT_CURSE = "arcana.CURSE";
    public static final String EFFECT_EARTH = "arcana.EARTH";
    public static final String EFFECT_EXCHANGE = "arcana.EXCHANGE";
    public static final String EFFECT_FIRE = "arcana.FIRE";
    public static final String EFFECT_FLUX = "arcana.FLUX";
    public static final String EFFECT_FROST = "arcana.FROST";
    public static final String EFFECT_HEAL = "arcana.HEAL";
    public static final String EFFECT_RIFT = "arcana.RIFT";
    /** Legacy alias — maps to AIR lightning (shock) behavior at cast time. */
    public static final String EFFECT_SHOCK = "arcana.SHOCK";

    public static final List<String> ALL_MEDIA = List.of(
            MEDIUM_TOUCH, MEDIUM_PROJECTILE, MEDIUM_BOLT, MEDIUM_CLOUD, MEDIUM_MINE);
    public static final List<String> ALL_MODS = List.of(MOD_SCATTER, MOD_SPLIT);
    public static final List<String> ALL_EFFECTS = List.of(
            EFFECT_AIR, EFFECT_BREAK, EFFECT_CURSE, EFFECT_EARTH, EFFECT_EXCHANGE,
            EFFECT_FIRE, EFFECT_FLUX, EFFECT_FROST, EFFECT_HEAL, EFFECT_RIFT, EFFECT_SHOCK);

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

    public static FocusPackage compose(String medium, List<String> effects) {
        return compose(medium, effects, null);
    }

    public static FocusPackage compose(String medium, List<String> effects, List<String> mods) {
        List<String> nodes = new ArrayList<>();
        nodes.add(ROOT);
        nodes.add(medium == null || medium.isEmpty() ? MEDIUM_TOUCH : medium);
        if (mods != null) {
            for (String mod : mods) {
                if (mod != null && !mod.isEmpty() && !nodes.contains(mod)) {
                    nodes.add(mod);
                }
            }
        }
        if (effects == null || effects.isEmpty()) {
            nodes.add(EFFECT_FIRE);
        } else {
            nodes.addAll(effects);
        }
        return new FocusPackage(nodes);
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
            case "touch_shock", "touchshock", "shock", "touch_air", "air" -> touchShock();
            case "projectile_shock", "proj_shock", "bolt_shock" -> projectileShock();
            case "touch_earth", "touchearth", "earth" -> touchEarth();
            case "touch_heal", "touchheal", "heal" -> touchHeal();
            case "hitscan_fire", "bolt_only_fire" -> of(ROOT, MEDIUM_BOLT, EFFECT_FIRE);
            case "cloud_fire" -> of(ROOT, MEDIUM_CLOUD, EFFECT_FIRE);
            default -> touchFire();
        };
    }

    public List<String> getNodes() {
        return Collections.unmodifiableList(nodes);
    }

    public boolean hasNode(String key) {
        return nodes.contains(key);
    }

    public static boolean isMedium(String key) {
        return MEDIUM_TOUCH.equals(key) || MEDIUM_PROJECTILE.equals(key)
                || MEDIUM_BOLT.equals(key) || MEDIUM_CLOUD.equals(key)
                || MEDIUM_MINE.equals(key);
    }

    public static boolean isMod(String key) {
        return MOD_SCATTER.equals(key) || MOD_SPLIT.equals(key);
    }

    public static boolean isEffect(String key) {
        return key != null && key.startsWith("arcana.") && !ROOT.equals(key)
                && !isMedium(key) && !isMod(key);
    }

    public List<String> getModNodes() {
        List<String> out = new ArrayList<>();
        for (String n : nodes) {
            if (isMod(n)) {
                out.add(n);
            }
        }
        return out;
    }

    public String getMedium() {
        for (String n : nodes) {
            if (isMedium(n)) {
                return n;
            }
        }
        return MEDIUM_TOUCH;
    }

    public String getEffect() {
        List<String> effects = getEffectNodes();
        return effects.isEmpty() ? EFFECT_FIRE : effects.get(0);
    }

    /** All effect nodes in package order (supports multi-effect packages). */
    public List<String> getEffectNodes() {
        List<String> out = new ArrayList<>();
        for (String n : nodes) {
            if (isEffect(n)) {
                out.add(n);
            }
        }
        return out;
    }

    /** Nodes after the medium — applied on touch/bolt hit or projectile/cloud impact. */
    public FocusPackage remainingEffects() {
        List<String> rem = new ArrayList<>();
        boolean pastMedium = false;
        for (String n : nodes) {
            if (pastMedium) {
                rem.add(n);
            }
            if (isMedium(n)) {
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
            sb.append(shortLabel(n));
        }
        return sb.length() == 0 ? "Empty" : sb.toString();
    }

    public String describeComplexity() {
        return "Complexity " + getComplexity();
    }

    public static String shortLabel(String node) {
        int dot = node.indexOf('.');
        return dot >= 0 ? node.substring(dot + 1) : node;
    }

    public static String researchForNode(String node) {
        return switch (node) {
            case MEDIUM_PROJECTILE -> "FOCUSPROJECTILE";
            case MEDIUM_BOLT -> "FOCUSBOLT";
            case MEDIUM_CLOUD -> "FOCUSCLOUD";
            case MEDIUM_MINE, MOD_SCATTER, MOD_SPLIT -> "BASEAUROMANCY";
            case EFFECT_AIR, EFFECT_FROST, EFFECT_EARTH -> "FOCUSELEMENTAL";
            case EFFECT_SHOCK -> "FOCUSSHOCK";
            case EFFECT_HEAL -> "FOCUSHEAL";
            case EFFECT_BREAK -> "FOCUSBREAK";
            case EFFECT_CURSE -> "FOCUSCURSE";
            case EFFECT_EXCHANGE -> "FOCUSEXCHANGE";
            case EFFECT_FLUX -> "FOCUSFLUX";
            case EFFECT_RIFT -> "FOCUSRIFT";
            case ROOT, MEDIUM_TOUCH, EFFECT_FIRE -> "BASEAUROMANCY";
            default -> "BASEAUROMANCY";
        };
    }

    /** TC6-like default complexity (power/setting floor ≈ 1). */
    public static int costForNode(String node) {
        return switch (node) {
            case ROOT -> 0;
            case MEDIUM_TOUCH -> 2;
            case MEDIUM_PROJECTILE -> 4;
            case MEDIUM_BOLT -> 5;
            case MEDIUM_CLOUD -> 4;
            case MEDIUM_MINE -> 5;
            case MOD_SCATTER -> 3;
            case MOD_SPLIT -> 4;
            case EFFECT_AIR -> 2;
            case EFFECT_BREAK -> 3;
            case EFFECT_CURSE -> 4;
            case EFFECT_EARTH -> 3;
            case EFFECT_EXCHANGE -> 5;
            case EFFECT_FIRE -> 3;
            case EFFECT_FLUX -> 3;
            case EFFECT_FROST -> 3;
            case EFFECT_HEAL -> 4;
            case EFFECT_RIFT -> 5;
            case EFFECT_SHOCK -> 5;
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
