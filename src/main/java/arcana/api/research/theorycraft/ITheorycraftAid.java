package arcana.api.research.theorycraft;

/**
 * Nearby block/item/entity that injects aid-only cards into draws.
 * A 9x9x3 area around the table is checked (see ResearchTableBlockEntity).
 */
public interface ITheorycraftAid {

    /**
     * Block, ItemStack (matched against block drops), or Entity Class that triggers this aid.
     */
    Object getAidObject();

    /**
     * Cards added to the draw rotation. Each drawn aid card is removed from the pool.
     */
    @SuppressWarnings("rawtypes")
    Class[] getCards();
}
