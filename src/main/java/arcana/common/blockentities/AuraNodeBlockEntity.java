package arcana.common.blockentities;

import arcana.api.aspects.Aspect;
import arcana.api.aura.AuraHelper;
import arcana.common.items.casters.ItemCaster;
import arcana.common.lib.ArcanaSounds;
import arcana.config.ArcanaConfig;
import arcana.registry.ModBlockEntities;
import arcana.registry.ModBlocks;
import arcana.registry.ModItems;
import java.util.Comparator;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

/**
 * Vis fountain: regenerates into a local buffer then overflows to chunk aura.
 * Node types alter regen rate; nearby casters see recharge FX.
 */
public class AuraNodeBlockEntity extends BlockEntity {
    public enum NodeType {
        NORMAL(0.75f, 0.0f),
        HUNGRY(0.25f, 0.0f),
        PURE(1.25f, 0.0f),
        TAINTED(0.5f, 0.1f);

        public final float regen;
        public final float fluxPollute;

        NodeType(float regen, float fluxPollute) {
            this.regen = regen;
            this.fluxPollute = fluxPollute;
        }

        public NodeType next() {
            NodeType[] values = values();
            return values[(ordinal() + 1) % values.length];
        }

        public static NodeType fromName(String name) {
            if (name == null || name.isEmpty()) {
                return NORMAL;
            }
            try {
                return NodeType.valueOf(name.toUpperCase());
            } catch (IllegalArgumentException e) {
                return NORMAL;
            }
        }
    }

    private static final String[] PRIMAL_TAGS = {"aer", "terra", "ignis", "aqua", "ordo", "perditio"};
    private static final int REGEN_INTERVAL = 80;
    /** Player sensor + silverwood scans run every Nth regen (batch; not every tick). */
    private static final int SCAN_EVERY_N_REGEN = 2;
    private static final float SILVERWOOD_BONUS = 0.25f;
    private static final int SILVERWOOD_RANGE = 6;
    public static final int RECHARGE_RADIUS = 8;
    public static final int DRAIN_RADIUS = 12;
    public static final float BUFFER_MAX = 100.0f;

    private String aspect = Aspect.AIR.getTag();
    private NodeType nodeType = NodeType.NORMAL;
    private float visBuffer;
    private int regenCount;
    private boolean cachedSilverwood;
    private boolean cachedSensorNearby;

    public AuraNodeBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.AURA_NODE.get(), pos, state);
    }

    public String getAspectTag() {
        return aspect;
    }

    public Aspect getAspect() {
        Aspect a = Aspect.getAspect(aspect);
        return a != null ? a : Aspect.AIR;
    }

    public NodeType getNodeType() {
        return nodeType;
    }

    public float getVisBuffer() {
        return visBuffer;
    }

    public void cycleAspect(Player player) {
        int idx = 0;
        for (int i = 0; i < PRIMAL_TAGS.length; i++) {
            if (PRIMAL_TAGS[i].equals(aspect)) {
                idx = i;
                break;
            }
        }
        aspect = PRIMAL_TAGS[(idx + 1) % PRIMAL_TAGS.length];
        setChangedAndSync();
        if (player != null) {
            player.displayClientMessage(Component.literal("Aura node aspect: " + aspect), true);
        }
    }

    public void cycleType(Player player) {
        nodeType = nodeType.next();
        setChangedAndSync();
        if (player != null) {
            player.displayClientMessage(Component.literal("Aura node type: " + nodeType.name()), true);
        }
    }

    /** Drain from local buffer; returns amount actually taken. */
    public float drainBuffer(float amount, boolean simulate) {
        if (amount <= 0.0f || visBuffer <= 0.0f) {
            return 0.0f;
        }
        float taken = Math.min(amount, visBuffer);
        if (!simulate) {
            visBuffer -= taken;
            setChangedAndSync();
        }
        return taken;
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, AuraNodeBlockEntity be) {
        if (level.getGameTime() % REGEN_INTERVAL != 0) {
            return;
        }
        be.regenCount++;
        boolean doScan = be.regenCount % SCAN_EVERY_N_REGEN == 0;
        if (doScan) {
            be.cachedSilverwood = be.hasNearbySilverwood(level, pos);
            if (level instanceof ServerLevel server) {
                be.cachedSensorNearby = !server.getEntitiesOfClass(Player.class,
                        new AABB(pos).inflate(RECHARGE_RADIUS),
                        p -> p.isAlive() && holdsSensor(p)).isEmpty();
            }
        }
        float amount = be.nodeType.regen;
        if (be.cachedSilverwood) {
            amount += SILVERWOOD_BONUS;
        }
        amount *= (float) ArcanaConfig.COMMON.auraNodeRegenMultiplier.get().doubleValue();
        float room = BUFFER_MAX - be.visBuffer;
        float toBuffer = Math.min(room, amount);
        be.visBuffer += toBuffer;
        float overflow = amount - toBuffer;
        if (overflow > 0.0f) {
            AuraHelper.addVis(level, pos, overflow);
        }
        if (be.nodeType.fluxPollute > 0.0f) {
            AuraHelper.polluteAura(level, pos, be.nodeType.fluxPollute, false);
        }
        be.setChanged();

        if (!(level instanceof ServerLevel server)) {
            return;
        }
        if (be.cachedSensorNearby) {
            server.sendParticles(ParticleTypes.END_ROD,
                    pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                    8, 0.25, 0.25, 0.25, 0.02);
        } else if (server.random.nextInt(4) == 0) {
            server.sendParticles(ParticleTypes.END_ROD,
                    pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                    1, 0.12, 0.12, 0.12, 0.005);
        }
        if (level.random.nextInt(12) == 0) {
            ArcanaSounds.nodeHum(level, pos);
        }
    }

    private static boolean holdsSensor(Player p) {
        return p.getMainHandItem().is(ModItems.THAUMOMETER.get())
                || p.getOffhandItem().is(ModItems.THAUMOMETER.get())
                || p.getMainHandItem().getItem() instanceof ItemCaster
                || p.getOffhandItem().getItem() instanceof ItemCaster;
    }

    /** Find nearest aura node within radius (inclusive). */
    @Nullable
    public static AuraNodeBlockEntity findNearest(Level level, BlockPos pos, int radius) {
        if (level == null) {
            return null;
        }
        AuraNodeBlockEntity best = null;
        double bestDist = Double.MAX_VALUE;
        int r = Math.max(1, radius);
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        for (int dx = -r; dx <= r; dx++) {
            for (int dy = -r; dy <= r; dy++) {
                for (int dz = -r; dz <= r; dz++) {
                    double distSq = dx * dx + dy * dy + dz * dz;
                    if (distSq > r * r || distSq >= bestDist) {
                        continue;
                    }
                    cursor.set(pos.getX() + dx, pos.getY() + dy, pos.getZ() + dz);
                    if (level.getBlockEntity(cursor) instanceof AuraNodeBlockEntity node) {
                        best = node;
                        bestDist = distSq;
                    }
                }
            }
        }
        return best;
    }

    /** All aura nodes in radius sorted nearest-first. */
    public static List<AuraNodeBlockEntity> findInRadius(Level level, BlockPos pos, int radius) {
        java.util.ArrayList<AuraNodeBlockEntity> list = new java.util.ArrayList<>();
        if (level == null) {
            return list;
        }
        int r = Math.max(1, radius);
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        for (int dx = -r; dx <= r; dx++) {
            for (int dy = -r; dy <= r; dy++) {
                for (int dz = -r; dz <= r; dz++) {
                    if (dx * dx + dy * dy + dz * dz > r * r) {
                        continue;
                    }
                    cursor.set(pos.getX() + dx, pos.getY() + dy, pos.getZ() + dz);
                    if (level.getBlockEntity(cursor) instanceof AuraNodeBlockEntity node) {
                        list.add(node);
                    }
                }
            }
        }
        list.sort(Comparator.comparingDouble(n -> n.worldPosition.distSqr(pos)));
        return list;
    }

    private boolean hasNearbySilverwood(Level level, BlockPos pos) {
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        for (int dx = -SILVERWOOD_RANGE; dx <= SILVERWOOD_RANGE; dx++) {
            for (int dy = -SILVERWOOD_RANGE; dy <= SILVERWOOD_RANGE; dy++) {
                for (int dz = -SILVERWOOD_RANGE; dz <= SILVERWOOD_RANGE; dz++) {
                    if (dx * dx + dy * dy + dz * dz > SILVERWOOD_RANGE * SILVERWOOD_RANGE) {
                        continue;
                    }
                    cursor.set(pos.getX() + dx, pos.getY() + dy, pos.getZ() + dz);
                    if (level.getBlockState(cursor).is(ModBlocks.SILVERWOOD_LOG.get())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void setChangedAndSync() {
        setChanged();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putString("aspect", aspect);
        tag.putString("nodeType", nodeType.name());
        tag.putFloat("visBuffer", visBuffer);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("aspect")) {
            String tagAspect = tag.getString("aspect");
            aspect = Aspect.getAspect(tagAspect) != null ? tagAspect : Aspect.AIR.getTag();
        }
        if (tag.contains("nodeType")) {
            nodeType = NodeType.fromName(tag.getString("nodeType"));
        }
        visBuffer = Mth.clamp(tag.getFloat("visBuffer"), 0.0f, BUFFER_MAX);
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        saveAdditional(tag);
        return tag;
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
