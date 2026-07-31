package arcana.common.golems;

/**
 * Golem body material — scales HP and attack.
 */
public enum GolemMaterial {
    WOOD(20.0, 4.0),
    IRON(30.0, 6.0),
    THAUMIUM(40.0, 8.0);

    private final double maxHealth;
    private final double attackDamage;

    GolemMaterial(double maxHealth, double attackDamage) {
        this.maxHealth = maxHealth;
        this.attackDamage = attackDamage;
    }

    public double getMaxHealth() {
        return maxHealth;
    }

    public double getAttackDamage() {
        return attackDamage;
    }

    public static GolemMaterial byId(int id) {
        GolemMaterial[] values = values();
        if (id < 0 || id >= values.length) {
            return WOOD;
        }
        return values[id];
    }

    public static GolemMaterial byName(String name) {
        if (name == null) {
            return WOOD;
        }
        return switch (name.toLowerCase()) {
            case "iron" -> IRON;
            case "thaumium" -> THAUMIUM;
            default -> WOOD;
        };
    }
}
