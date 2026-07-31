package arcana.api.research.theorycraft;

import net.minecraft.world.level.block.Blocks;

public class AidBookshelf implements ITheorycraftAid {

    @Override
    public Object getAidObject() {
        return Blocks.BOOKSHELF;
    }

    @Override
    @SuppressWarnings("rawtypes")
    public Class[] getCards() {
        return new Class[] {
                CardBalance.class, CardNotation.class, CardNotation.class,
                CardStudy.class, CardStudy.class, CardStudy.class
        };
    }
}
