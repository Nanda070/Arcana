package arcana.common.blocks;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

/**
 * Sticky flux residue that slows movement (G22).
 */
public class FluxGooBlock extends Block {
    public FluxGooBlock() {
        super(BlockBehaviour.Properties.of()
                .mapColor(MapColor.COLOR_PURPLE)
                .strength(0.4f)
                .sound(SoundType.SLIME_BLOCK)
                .speedFactor(0.4f)
                .lightLevel(state -> 1)
                .friction(0.8f));
    }
}
