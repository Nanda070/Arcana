package arcana.client;

/**
 * Client-side cache of local chunk aura (updated by PacketAuraToClient).
 */
public final class ClientAuraCache {
    private static short base;
    private static float vis;
    private static float flux;
    private static long lastUpdateMs;
    private static boolean hasData;

    private ClientAuraCache() {
    }

    public static void update(short baseIn, float visIn, float fluxIn) {
        base = baseIn;
        vis = visIn;
        flux = fluxIn;
        lastUpdateMs = System.currentTimeMillis();
        hasData = true;
    }

    public static boolean isFresh() {
        return hasData && System.currentTimeMillis() - lastUpdateMs < 2000L;
    }

    public static short getBase() {
        return base;
    }

    public static float getVis() {
        return vis;
    }

    public static float getFlux() {
        return flux;
    }
}
