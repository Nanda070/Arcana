package arcana.common.blockentities;

import arcana.api.aspects.Aspect;
import arcana.api.aspects.AspectList;
import arcana.api.aspects.IAspectContainer;
import arcana.api.aura.AuraHelper;
import arcana.common.crafting.ArcaneShapedRecipe;
import arcana.common.crafting.CrucibleRecipe;
import arcana.registry.ModBlockEntities;
import arcana.registry.ModRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.Nullable;

public class CrucibleBlockEntity extends BlockEntity implements IAspectContainer {
    public static final int MAX_ASPECTS = 500;
    public static final short BOIL_HEAT = 151;

    private final AspectList aspects = new AspectList();
    private short heat;
    private long spillCounter;
    private final FluidTank tank = new FluidTank(1000) {
        @Override
        public boolean isFluidValid(FluidStack stack) {
            return stack.getFluid() == Fluids.WATER;
        }

        @Override
        protected void onContentsChanged() {
            setChangedAndSync();
        }
    };
    private final LazyOptional<IFluidHandler> fluidOptional = LazyOptional.of(() -> tank);

    public CrucibleBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CRUCIBLE.get(), pos, state);
    }

    public FluidTank getTank() {
        return tank;
    }

    public short getHeat() {
        return heat;
    }

    public boolean isBoiling() {
        return heat >= BOIL_HEAT && tank.getFluidAmount() > 0;
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, CrucibleBlockEntity crucible) {
        crucible.tickHeat();
        if (crucible.aspects.visSize() > MAX_ASPECTS) {
            crucible.spillRandom();
        }
        if (++crucible.spillCounter >= 100) {
            crucible.spillCounter = 0;
            if (crucible.aspects.size() > 0) {
                crucible.spillRandom();
            }
        }
    }

    private void tickHeat() {
        if (level == null) {
            return;
        }
        short prev = heat;
        if (tank.getFluidAmount() > 0 && isHeatSource(level.getBlockState(worldPosition.below()))) {
            if (heat < 200) {
                heat++;
            }
        } else if (heat > 0) {
            heat--;
        }
        if ((prev < BOIL_HEAT && heat >= BOIL_HEAT) || (prev >= BOIL_HEAT && heat < BOIL_HEAT)) {
            setChangedAndSync();
        }
    }

    private static boolean isHeatSource(BlockState state) {
        return state.is(Blocks.FIRE) || state.is(Blocks.SOUL_FIRE) || state.is(Blocks.LAVA)
                || state.is(Blocks.MAGMA_BLOCK) || state.is(BlockTags.CAMPFIRES);
    }

    public void attemptSmelt(ItemEntity entity) {
        if (level == null || level.isClientSide() || !isBoiling()) {
            return;
        }
        ItemStack stack = entity.getItem();
        Player thrower = level.getNearestPlayer(entity, 8);
        ItemStack leftover = attemptSmelt(stack.copy(), thrower);
        if (leftover.isEmpty()) {
            entity.discard();
        } else {
            entity.setItem(leftover);
        }
    }

    public ItemStack attemptSmelt(ItemStack stack, @Nullable Player player) {
        if (level == null || !isBoiling() || stack.isEmpty()) {
            return stack;
        }
        int remaining = stack.getCount();
        boolean bubbled = false;
        boolean crafted = false;

        for (int i = 0; i < stack.getCount(); i++) {
            ItemStack one = stack.copyWithCount(1);
            CrucibleRecipe recipe = findRecipe(one, player);
            if (recipe != null) {
                recipe.consumeAspects(aspects);
                tank.drain(50, IFluidHandler.FluidAction.EXECUTE);
                ejectItem(recipe.getResultItem(level.registryAccess()).copy());
                remaining--;
                crafted = true;
                spillCounter = -250;
            } else {
                Aspect crystalAspect = aspectFromCrystal(one.getItem());
                if (crystalAspect != null) {
                    aspects.add(crystalAspect, 2);
                    remaining--;
                    bubbled = true;
                    spillCounter = -150;
                } else {
                    break;
                }
            }
        }

        if (bubbled || crafted) {
            level.playSound(null, worldPosition, SoundEvents.BUBBLE_COLUMN_BUBBLE_POP, SoundSource.BLOCKS, 0.4f, 1.0f);
            setChangedAndSync();
        }
        if (remaining <= 0) {
            return ItemStack.EMPTY;
        }
        ItemStack out = stack.copy();
        out.setCount(remaining);
        return out;
    }

    @Nullable
    private CrucibleRecipe findRecipe(ItemStack catalyst, @Nullable Player player) {
        if (level == null) {
            return null;
        }
        for (CrucibleRecipe recipe : level.getRecipeManager().getAllRecipesFor(ModRecipes.CRUCIBLE_TYPE.get())) {
            if (recipe.matches(aspects, catalyst, player)) {
                return recipe;
            }
        }
        return null;
    }

    @Nullable
    private static Aspect aspectFromCrystal(Item item) {
        for (Aspect aspect : Aspect.getPrimalAspects()) {
            Item crystal = ArcaneShapedRecipe.crystalItem(aspect);
            if (crystal != null && item == crystal) {
                return aspect;
            }
        }
        return null;
    }

    private void ejectItem(ItemStack stack) {
        if (level instanceof ServerLevel serverLevel) {
            ItemEntity entity = new ItemEntity(serverLevel,
                    worldPosition.getX() + 0.5,
                    worldPosition.getY() + 1.1,
                    worldPosition.getZ() + 0.5,
                    stack);
            entity.setDeltaMovement(0, 0.2, 0);
            serverLevel.addFreshEntity(entity);
        }
    }

    public void spillRandom() {
        if (level == null || aspects.size() == 0) {
            return;
        }
        Aspect[] list = aspects.getAspects();
        Aspect tag = list[level.random.nextInt(list.length)];
        aspects.remove(tag, 1);
        AuraHelper.polluteAura(level, worldPosition, tag == Aspect.FLUX ? 1.0f : 0.25f, true);
        setChangedAndSync();
    }

    @Override
    public AspectList getAspects() {
        return aspects.copy();
    }

    @Override
    public void setAspects(AspectList list) {
        aspects.aspects.clear();
        aspects.add(list);
        setChangedAndSync();
    }

    @Override
    public boolean doesContainerAccept(Aspect tag) {
        return true;
    }

    @Override
    public int addToContainer(Aspect tag, int amount) {
        aspects.add(tag, amount);
        setChangedAndSync();
        return 0;
    }

    @Override
    public boolean takeFromContainer(Aspect tag, int amount) {
        if (aspects.getAmount(tag) >= amount) {
            aspects.remove(tag, amount);
            setChangedAndSync();
            return true;
        }
        return false;
    }

    @Override
    public boolean doesContainerContainAmount(Aspect tag, int amount) {
        return aspects.getAmount(tag) >= amount;
    }

    @Override
    public int containerContains(Aspect tag) {
        return aspects.getAmount(tag);
    }

    private void setChangedAndSync() {
        setChanged();
        if (level != null && !level.isClientSide()) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putShort("Heat", heat);
        tank.writeToNBT(tag);
        aspects.writeToNBT(tag);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        heat = tag.getShort("Heat");
        tank.readFromNBT(tag);
        aspects.readFromNBT(tag);
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

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.FLUID_HANDLER) {
            return fluidOptional.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        fluidOptional.invalidate();
    }
}
