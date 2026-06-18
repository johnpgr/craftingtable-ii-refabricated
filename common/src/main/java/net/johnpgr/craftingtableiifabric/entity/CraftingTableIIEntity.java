package net.johnpgr.craftingtableiifabric.entity;

import net.johnpgr.craftingtableiifabric.CraftingTableII;
import net.johnpgr.craftingtableiifabric.inventory.CraftingTableIIInventory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class CraftingTableIIEntity extends BlockEntity implements WorldlyContainer {
    private static final float OPEN_SPEED = 0.2f;

    private final net.minecraft.core.NonNullList<ItemStack> inventory =
            net.minecraft.core.NonNullList.withSize(CraftingTableIIInventory.SIZE, ItemStack.EMPTY);
    private DoorState doorState = DoorState.CLOSED;
    public float doorAngle = 0.0f;

    public CraftingTableIIEntity(BlockPos pos, BlockState state) {
        super(CraftingTableII.ENTITY_TYPE, pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, CraftingTableIIEntity entity) {
        Player player = level.getNearestPlayer(pos.getX(), pos.getY(), pos.getZ(), 10.0, false);
        if (player == null) {
            return;
        }

        double distance = player.position().distanceToSqr(Vec3.atCenterOf(pos));

        if (distance < 7.0) {
            entity.doorAngle += OPEN_SPEED;
            if (entity.doorAngle > 1.8f) {
                entity.doorAngle = 1.8f;
            }

            if (entity.doorState != DoorState.OPEN) {
                entity.doorState = DoorState.OPEN;
                level.playSound(
                        null,
                        pos,
                        SoundEvents.CHEST_OPEN,
                        net.minecraft.sounds.SoundSource.BLOCKS,
                        0.2f,
                        level.random.nextFloat() * 0.1f + 0.2f
                );
            }
        } else if (distance > 7.0) {
            entity.doorAngle -= OPEN_SPEED;
            if (entity.doorAngle < 0f) {
                entity.doorAngle = 0f;
            }

            if (entity.doorState != DoorState.CLOSED) {
                entity.doorState = DoorState.CLOSED;
                level.playSound(
                        null,
                        pos,
                        SoundEvents.CHEST_CLOSE,
                        net.minecraft.sounds.SoundSource.BLOCKS,
                        0.2f,
                        level.random.nextFloat() * 0.1f + 0.2f
                );
            }
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        ContainerHelper.loadAllItems(tag, inventory, registries);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        ContainerHelper.saveAllItems(tag, inventory, registries);
    }

    @Override
    public int getContainerSize() {
        return inventory.size();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : inventory) {
            if (!stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getItem(int slot) {
        return inventory.get(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        return ContainerHelper.removeItem(inventory, slot, amount);
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return ContainerHelper.takeItem(inventory, slot);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        if (stack.getCount() > stack.getMaxStackSize()) {
            stack.setCount(stack.getMaxStackSize());
        }
        inventory.set(slot, stack);
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public void clearContent() {
        inventory.clear();
    }

    @Override
    public int[] getSlotsForFace(net.minecraft.core.Direction side) {
        return new int[0];
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @org.jetbrains.annotations.Nullable net.minecraft.core.Direction direction) {
        return false;
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, net.minecraft.core.Direction direction) {
        return false;
    }

    public enum DoorState {
        OPEN,
        CLOSED
    }
}
