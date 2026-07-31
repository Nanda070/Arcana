package arcana.api.casters;

import net.minecraft.world.phys.Vec3;

public final class Trajectory {
    public final Vec3 source;
    public final Vec3 direction;

    public Trajectory(Vec3 source, Vec3 direction) {
        this.source = source;
        this.direction = direction;
    }
}
