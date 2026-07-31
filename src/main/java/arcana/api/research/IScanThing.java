package arcana.api.research;

import net.minecraft.world.entity.player.Player;

public interface IScanThing {
    boolean checkThing(Player player, Object object);

    String getResearchKey(Player player, Object object);

    default void onSuccess(Player player, Object object) {
    }
}
