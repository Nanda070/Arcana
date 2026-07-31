package arcana.common.menu;

import arcana.common.golems.ArcanaGolem;
import arcana.common.golems.GolemJob;
import arcana.registry.ModMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

public class GolemJobMenu extends AbstractContainerMenu {
    public static final int BTN_IDLE = 0;
    public static final int BTN_GATHER = 1;
    public static final int BTN_GUARD = 2;
    public static final int BTN_FILL = 3;
    public static final int BTN_EMPTY = 4;

    private final int golemId;

    public GolemJobMenu(int id, Inventory inv, FriendlyByteBuf buf) {
        this(id, inv, buf.readVarInt());
    }

    public GolemJobMenu(int id, Inventory inv, int golemId) {
        super(ModMenus.GOLEM_JOB.get(), id);
        this.golemId = golemId;
    }

    public int getGolemId() {
        return golemId;
    }

    private ArcanaGolem golem(Player player) {
        Entity entity = player.level().getEntity(golemId);
        return entity instanceof ArcanaGolem g ? g : null;
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        ArcanaGolem golem = golem(player);
        if (golem == null || golem.getOwnerUUID() == null
                || !player.getUUID().equals(golem.getOwnerUUID())) {
            return false;
        }
        GolemJob job = switch (id) {
            case BTN_GATHER -> GolemJob.GATHER;
            case BTN_GUARD -> GolemJob.GUARD;
            case BTN_FILL -> GolemJob.FILL;
            case BTN_EMPTY -> GolemJob.EMPTY;
            default -> GolemJob.IDLE;
        };
        golem.setJob(job);
        player.displayClientMessage(Component.translatable("arcana.golem.job." + job.name().toLowerCase()), true);
        return true;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        ArcanaGolem golem = golem(player);
        return golem != null && golem.isAlive() && player.distanceToSqr(golem) < 64.0;
    }
}
