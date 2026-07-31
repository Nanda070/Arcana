package arcana.common.golems;

public enum GolemJob {
    IDLE,
    GATHER,
    GUARD,
    FILL,
    EMPTY,
    HARVEST,
    USE,
    BUTCHER;

    public static GolemJob byId(int id) {
        GolemJob[] values = values();
        if (id < 0 || id >= values.length) {
            return IDLE;
        }
        return values[id];
    }
}
