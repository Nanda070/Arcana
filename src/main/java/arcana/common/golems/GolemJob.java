package arcana.common.golems;

/**
 * Seal / GUI job ids. Ordinals 0–7 preserved from earlier Arcana builds;
 * advanced + lumber/provide/stock/breaker appended.
 */
public enum GolemJob {
    IDLE,
    GATHER,           // Pickup
    GUARD,
    FILL,
    EMPTY,
    HARVEST,
    USE,
    BUTCHER,
    GATHER_ADVANCED,  // PickupAdvanced
    FILL_ADVANCED,
    EMPTY_ADVANCED,
    GUARD_ADVANCED,
    LUMBER,
    PROVIDE,
    STOCK,
    BREAKER,
    BREAKER_ADVANCED;

    public static final int MAX_ID = values().length - 1;

    public static GolemJob byId(int id) {
        GolemJob[] values = values();
        if (id < 0 || id >= values.length) {
            return IDLE;
        }
        return values[id];
    }

    public boolean isGuard() {
        return this == GUARD || this == GUARD_ADVANCED;
    }

    public boolean isGather() {
        return this == GATHER || this == GATHER_ADVANCED;
    }

    public boolean isFill() {
        return this == FILL || this == FILL_ADVANCED;
    }

    public boolean isEmpty() {
        return this == EMPTY || this == EMPTY_ADVANCED;
    }

    public boolean isBreaker() {
        return this == BREAKER || this == BREAKER_ADVANCED;
    }

    public int gatherRangeBonus() {
        return this == GATHER_ADVANCED ? 4 : 0;
    }

    public String sealItemId() {
        return switch (this) {
            case GATHER -> "seal_gather";
            case GUARD -> "seal_guard";
            case FILL -> "seal_fill";
            case EMPTY -> "seal_empty";
            case HARVEST -> "seal_harvest";
            case USE -> "seal_use";
            case BUTCHER -> "seal_butcher";
            case GATHER_ADVANCED -> "seal_gather_advanced";
            case FILL_ADVANCED -> "seal_fill_advanced";
            case EMPTY_ADVANCED -> "seal_empty_advanced";
            case GUARD_ADVANCED -> "seal_guard_advanced";
            case LUMBER -> "seal_lumber";
            case PROVIDE -> "seal_provide";
            case STOCK -> "seal_stock";
            case BREAKER -> "seal_breaker";
            case BREAKER_ADVANCED -> "seal_breaker_advanced";
            default -> "seal_blank";
        };
    }

    public String shortLabel() {
        return switch (this) {
            case IDLE -> "Idle";
            case GATHER -> "Gather";
            case GUARD -> "Guard";
            case FILL -> "Fill";
            case EMPTY -> "Empty";
            case HARVEST -> "Harvest";
            case USE -> "Use";
            case BUTCHER -> "Butcher";
            case GATHER_ADVANCED -> "Gather+";
            case FILL_ADVANCED -> "Fill+";
            case EMPTY_ADVANCED -> "Empty+";
            case GUARD_ADVANCED -> "Guard+";
            case LUMBER -> "Lumber";
            case PROVIDE -> "Provide";
            case STOCK -> "Stock";
            case BREAKER -> "Breaker";
            case BREAKER_ADVANCED -> "Breaker+";
        };
    }
}
