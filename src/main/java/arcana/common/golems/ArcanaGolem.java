package arcana.common.golems;

import arcana.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.LeverBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

/**
 * Owned walker with seal-applied jobs: IDLE / GATHER / GUARD / FILL / EMPTY / HARVEST / USE / BUTCHER.
 */
public class ArcanaGolem extends TamableAnimal {
    private static final EntityDataAccessor<Boolean> DATA_FOLLOWING =
            SynchedEntityData.defineId(ArcanaGolem.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Byte> DATA_JOB =
            SynchedEntityData.defineId(ArcanaGolem.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Byte> DATA_MATERIAL =
            SynchedEntityData.defineId(ArcanaGolem.class, EntityDataSerializers.BYTE);

    private final SimpleContainer inventory = new SimpleContainer(9);
    private int gatherRange = 4;
    private int guardBonus;

    public ArcanaGolem(EntityType<? extends TamableAnimal> type, Level level) {
        super(type, level);
        setTame(true);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return TamableAnimal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0)
                .add(Attributes.MOVEMENT_SPEED, 0.28)
                .add(Attributes.FOLLOW_RANGE, 24.0)
                .add(Attributes.ATTACK_DAMAGE, 4.0);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_FOLLOWING, true);
        this.entityData.define(DATA_JOB, (byte) GolemJob.IDLE.ordinal());
        this.entityData.define(DATA_MATERIAL, (byte) GolemMaterial.WOOD.ordinal());
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.2D, true));
        this.goalSelector.addGoal(3, new GolemSeekSealGoal(this));
        this.goalSelector.addGoal(4, new FollowOwnerGoal(this, 1.1D, 5.0F, 2.0F, false));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.8D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Monster.class, 10, true, false,
                target -> getJob() == GolemJob.GUARD));
        this.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, Animal.class, 10, true, false,
                target -> getJob() == GolemJob.BUTCHER && !(target instanceof ArcanaGolem)));
    }

    public boolean isFollowing() {
        return this.entityData.get(DATA_FOLLOWING);
    }

    public void setFollowing(boolean following) {
        this.entityData.set(DATA_FOLLOWING, following);
        setOrderedToSit(!following);
    }

    public GolemJob getJob() {
        return GolemJob.byId(this.entityData.get(DATA_JOB));
    }

    public void setJob(GolemJob job) {
        GolemJob next = job == null ? GolemJob.IDLE : job;
        this.entityData.set(DATA_JOB, (byte) next.ordinal());
        if (next != GolemJob.GUARD && next != GolemJob.BUTCHER) {
            setTarget(null);
        }
    }

    public GolemMaterial getMaterial() {
        return GolemMaterial.byId(this.entityData.get(DATA_MATERIAL));
    }

    public void setMaterial(GolemMaterial material) {
        GolemMaterial next = material == null ? GolemMaterial.WOOD : material;
        this.entityData.set(DATA_MATERIAL, (byte) next.ordinal());
        applyMaterialAttributes();
    }

    public int getGatherRange() {
        return gatherRange;
    }

    public void setGatherRange(int gatherRange) {
        this.gatherRange = Math.max(2, gatherRange);
    }

    public int getGuardBonus() {
        return guardBonus;
    }

    public void setGuardBonus(int guardBonus) {
        this.guardBonus = Math.max(0, guardBonus);
    }

    private void applyMaterialAttributes() {
        GolemMaterial mat = getMaterial();
        AttributeInstance health = getAttribute(Attributes.MAX_HEALTH);
        AttributeInstance damage = getAttribute(Attributes.ATTACK_DAMAGE);
        if (health != null) {
            health.setBaseValue(mat.getMaxHealth());
            setHealth(Math.min(getHealth(), (float) mat.getMaxHealth()));
        }
        if (damage != null) {
            damage.setBaseValue(mat.getAttackDamage() + guardBonus);
        }
    }

    public boolean tryUpgradeMaterial(GolemMaterial target) {
        if (target.ordinal() <= getMaterial().ordinal()) {
            return false;
        }
        setMaterial(target);
        return true;
    }

    public SimpleContainer getGolemInventory() {
        return inventory;
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide) {
            return;
        }
        GolemJob job = getJob();
        if (tickCount % 20 == 0 && job == GolemJob.GATHER) {
            vacuumItems();
        }
        if (tickCount % 40 == 0 && job == GolemJob.HARVEST) {
            harvestCrops();
        }
        if (tickCount % 60 == 0 && job == GolemJob.USE) {
            activateNearby();
        }
        if (tickCount % 200 == 0) {
            if (job == GolemJob.FILL) {
                depositIntoChest();
            } else if (job == GolemJob.EMPTY) {
                pullFromChest();
            }
        }
    }

    private void vacuumItems() {
        AABB box = getBoundingBox().inflate(gatherRange);
        for (ItemEntity item : level().getEntitiesOfClass(ItemEntity.class, box)) {
            if (!item.isAlive() || item.hasPickUpDelay()) {
                continue;
            }
            ItemStack stack = item.getItem();
            ItemStack leftover = inventory.addItem(stack.copy());
            if (leftover.isEmpty()) {
                item.discard();
            } else if (leftover.getCount() < stack.getCount()) {
                item.setItem(leftover);
            }
        }
    }

    private void harvestCrops() {
        BlockPos origin = blockPosition();
        for (BlockPos pos : BlockPos.betweenClosed(origin.offset(-4, -1, -4), origin.offset(4, 2, 4))) {
            BlockState state = level().getBlockState(pos);
            if (state.getBlock() instanceof CropBlock crop && crop.isMaxAge(state)) {
                level().destroyBlock(pos, true, this);
                return;
            }
        }
    }

    private void activateNearby() {
        BlockPos origin = blockPosition();
        for (BlockPos pos : BlockPos.betweenClosed(origin.offset(-3, -1, -3), origin.offset(3, 2, 3))) {
            BlockState state = level().getBlockState(pos);
            Block block = state.getBlock();
            if (block instanceof LeverBlock || block instanceof ButtonBlock) {
                if (state.hasProperty(BlockStateProperties.POWERED) && !state.getValue(BlockStateProperties.POWERED)) {
                    BlockState toggled = state.cycle(BlockStateProperties.POWERED);
                    level().setBlock(pos, toggled, 3);
                    level().updateNeighborsAt(pos, block);
                    return;
                }
            }
        }
    }

    private Container findNearbyChest() {
        BlockPos origin = blockPosition();
        for (BlockPos pos : BlockPos.betweenClosed(origin.offset(-6, -3, -6), origin.offset(6, 3, 6))) {
            BlockEntity be = level().getBlockEntity(pos);
            if (be instanceof Container container && container.getContainerSize() >= 9) {
                return container;
            }
        }
        return null;
    }

    private void depositIntoChest() {
        Container chest = findNearbyChest();
        if (chest == null) {
            return;
        }
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            if (stack.isEmpty()) {
                continue;
            }
            ItemStack move = stack.copy();
            ItemStack leftover = insertInto(chest, move);
            inventory.setItem(i, leftover);
            return;
        }
    }

    private void pullFromChest() {
        Container chest = findNearbyChest();
        if (chest == null) {
            return;
        }
        for (int i = 0; i < chest.getContainerSize(); i++) {
            ItemStack stack = chest.getItem(i);
            if (stack.isEmpty()) {
                continue;
            }
            ItemStack take = stack.copy();
            ItemStack leftover = inventory.addItem(take);
            int moved = take.getCount() - leftover.getCount();
            if (moved > 0) {
                stack.shrink(moved);
                chest.setChanged();
                return;
            }
        }
    }

    private static ItemStack insertInto(Container container, ItemStack stack) {
        for (int i = 0; i < container.getContainerSize() && !stack.isEmpty(); i++) {
            ItemStack slot = container.getItem(i);
            if (slot.isEmpty()) {
                container.setItem(i, stack);
                return ItemStack.EMPTY;
            }
            if (ItemStack.isSameItemSameTags(slot, stack) && slot.getCount() < slot.getMaxStackSize()) {
                int move = Math.min(stack.getCount(), slot.getMaxStackSize() - slot.getCount());
                slot.grow(move);
                stack.shrink(move);
                container.setChanged();
            }
        }
        return stack;
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack held = player.getItemInHand(hand);
        if (held.getItem() instanceof GolemSealItem || held.getItem() instanceof GolemCoreItem) {
            return InteractionResult.PASS;
        }
        if (!level().isClientSide && player.getUUID().equals(getOwnerUUID())) {
            if (held.is(Items.IRON_INGOT) && tryUpgradeMaterial(GolemMaterial.IRON)) {
                if (!player.getAbilities().instabuild) {
                    held.shrink(1);
                }
                player.displayClientMessage(net.minecraft.network.chat.Component.translatable(
                        "arcana.golem.material.iron"), true);
                return InteractionResult.SUCCESS;
            }
            if (held.is(ModItems.THAUMIUM_INGOT.get()) && tryUpgradeMaterial(GolemMaterial.THAUMIUM)) {
                if (!player.getAbilities().instabuild) {
                    held.shrink(1);
                }
                player.displayClientMessage(net.minecraft.network.chat.Component.translatable(
                        "arcana.golem.material.thaumium"), true);
                return InteractionResult.SUCCESS;
            }
            if (player.isShiftKeyDown()) {
                setFollowing(!isFollowing());
                player.displayClientMessage(net.minecraft.network.chat.Component.literal(
                        isFollowing() ? "Golem: follow" : "Golem: stay"), true);
                return InteractionResult.SUCCESS;
            }
            for (int i = 0; i < inventory.getContainerSize(); i++) {
                ItemStack stack = inventory.removeItemNoUpdate(i);
                if (!stack.isEmpty() && !player.getInventory().add(stack)) {
                    player.drop(stack, false);
                }
            }
            return InteractionResult.SUCCESS;
        }
        return super.mobInteract(player, hand);
    }

    @Override
    public boolean wantsToAttack(LivingEntity target, LivingEntity owner) {
        GolemJob job = getJob();
        if (job != GolemJob.GUARD && job != GolemJob.BUTCHER) {
            return false;
        }
        if (target instanceof ArcanaGolem) {
            return false;
        }
        if (job == GolemJob.BUTCHER) {
            return target instanceof Animal;
        }
        return target instanceof Monster || super.wantsToAttack(target, owner);
    }

    @Override
    public boolean canAttack(LivingEntity target) {
        GolemJob job = getJob();
        if (job == GolemJob.GUARD) {
            return super.canAttack(target);
        }
        if (job == GolemJob.BUTCHER) {
            return target instanceof Animal && super.canAttack(target);
        }
        return false;
    }

    @Override
    protected void dropCustomDeathLoot(DamageSource source, int looting, boolean recentlyHit) {
        super.dropCustomDeathLoot(source, looting, recentlyHit);
        spawnAtLocation(new ItemStack(ModItems.GOLEM.get()));
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            if (!stack.isEmpty()) {
                spawnAtLocation(stack.copy());
            }
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("Following", isFollowing());
        tag.putByte("Job", (byte) getJob().ordinal());
        tag.putString("Material", getMaterial().name().toLowerCase());
        tag.putInt("GatherRange", gatherRange);
        tag.putInt("GuardBonus", guardBonus);
        tag.put("Inventory", inventory.createTag());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("Following")) {
            setFollowing(tag.getBoolean("Following"));
        }
        if (tag.contains("Job")) {
            setJob(GolemJob.byId(tag.getByte("Job")));
        }
        if (tag.contains("Material")) {
            setMaterial(GolemMaterial.byName(tag.getString("Material")));
        }
        if (tag.contains("GatherRange")) {
            gatherRange = tag.getInt("GatherRange");
        }
        if (tag.contains("GuardBonus")) {
            guardBonus = tag.getInt("GuardBonus");
            applyMaterialAttributes();
        }
        if (tag.contains("Inventory")) {
            inventory.fromTag(tag.getList("Inventory", 10));
        }
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob other) {
        return null;
    }

    @Override
    public boolean canMate(Animal other) {
        return false;
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return false;
    }
}
