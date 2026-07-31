package arcana.api.research.theorycraft;

import java.util.HashMap;

public class TheorycraftManager {

    public static final HashMap<String, ITheorycraftAid> aids = new HashMap<>();
    public static final HashMap<String, Class<? extends TheorycraftCard>> cards = new HashMap<>();

    public static void registerAid(ITheorycraftAid aid) {
        String key = aid.getClass().getName();
        if (!aids.containsKey(key)) {
            aids.put(key, aid);
        }
    }

    public static void registerCard(Class<? extends TheorycraftCard> cardClass) {
        String key = cardClass.getName();
        if (!cards.containsKey(key)) {
            cards.put(key, cardClass);
        }
    }
}
