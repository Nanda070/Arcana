package arcana.common.lib.research;

import arcana.api.aspects.Aspect;
import arcana.api.aspects.AspectList;
import arcana.api.research.IScanThing;
import net.minecraft.world.entity.player.Player;

/** Unlock "!aspectTag" when scanning something that contains that aspect. */
public class ScanAspect implements IScanThing {
    private final Aspect aspect;

    public ScanAspect(Aspect aspect) {
        this.aspect = aspect;
    }

    @Override
    public boolean checkThing(Player player, Object object) {
        AspectList list = ScanGeneric.aspectsOf(player, object);
        return list != null && list.getAmount(aspect) > 0;
    }

    @Override
    public String getResearchKey(Player player, Object object) {
        return "!" + aspect.getTag();
    }
}
