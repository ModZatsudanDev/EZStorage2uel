package modzatsudan.ezstorage.gui.server;

import javax.annotation.Nonnull;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import org.jetbrains.annotations.NotNull;

import modzatsudan.ezstorage.tileentity.TileEntityStorageCore;

/** The storage core container */
public class ContainerStorageCore extends Container {

    public TileEntityStorageCore tileEntity;
    private IInventory inventory;

    public ContainerStorageCore(EntityPlayer player, World world, int x, int y, int z) {
        this.tileEntity = ((TileEntityStorageCore) world.getTileEntity(new BlockPos(x, y, z)));
        int startingY = 18;
        int startingX = 8;

        // the EZStorage slots
        IInventory inventory = new InventoryBasic("title", false, this.rowCount() * 9);
        for (int i = 0; i < this.rowCount(); i++) {
            for (int j = 0; j < 9; j++) {
                addSlotToContainer(new Slot(inventory, j + i * 9, startingX + j * 18, startingY + i * 18));
            }
        }

        // the player inventory
        bindPlayerInventory(player.inventory);
        this.inventory = inventory;
    }

    protected void bindPlayerInventory(InventoryPlayer inventoryPlayer) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                addSlotToContainer(new Slot(inventoryPlayer, (j + i * 9) + 9, playerInventoryX() + j * 18,
                        playerInventoryY() + i * 18));
            }
        }

        for (int i = 0; i < 9; i++) {
            addSlotToContainer(new Slot(inventoryPlayer, i, playerInventoryX() + i * 18, playerInventoryY() + 58));
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return true;
    }

    @Override
    public boolean enchantItem(EntityPlayer player, int action) {
        switch (action) {
            case 0: // change sort mode and update
                tileEntity.sortMode = tileEntity.sortMode.rotateMode();
                tileEntity.sortInventory();
                return true;
            case 1: // clear the crafting grid if it exists
                if (this instanceof ContainerStorageCoreCrafting) {
                    ((ContainerStorageCoreCrafting) this).clearGrid(player);
                    tileEntity.sortInventory();
                    return true;
                }
        }
        return false;
    }

    /** Shift click a slot */
    @Override
    public @Nonnull ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        Slot slotObject = inventorySlots.get(index);
        if (slotObject != null && slotObject.getHasStack()) {
            ItemStack stackInSlot = slotObject.getStack();
            slotObject.putStack(this.tileEntity.inventory.input(stackInSlot));
        }
        return ItemStack.EMPTY;
    }

    /** Default slot click handling. Also checks for shift-clicking to sort the inventory appropriately */
    @Override
    public @Nonnull ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player) {
        ItemStack val = ItemStack.EMPTY;
        if (slotId < this.rowCount() * 9 && slotId >= 0) {
            val = ItemStack.EMPTY; // use custom handler for clicks on the inventory
        } else {
            val = super.slotClick(slotId, dragType, clickTypeIn, player);
            if (slotId >= 0) {
                Slot slot = this.getSlot(slotId);
                if (!(slot instanceof SlotCrafting) && clickTypeIn == ClickType.QUICK_MOVE &&
                        slot.canTakeStack(player)) {
                    ItemStack itemStack = slot.getStack();
                    ItemStack result = this.tileEntity.inventory.input(itemStack, true);
                    slot.onSlotChanged();
                    super.detectAndSendChanges();
                    val = result.copy();
                }
            }
            if (clickTypeIn == ClickType.QUICK_MOVE)
                this.tileEntity.sortInventory(); // sort only on insert shift-click
        }
        return val;
    }

    /** Click a custom slot to take or insert items */
    public @Nonnull ItemStack customSlotClick(int slotId, int clickedButton, int mode, EntityPlayer playerIn) {
        // Added Code

        int _type = 0;
        if (clickedButton == 1) {
            _type = (mode == 0) ? 1 : 2;
        }

        // isShiftLeftClick
        if (clickedButton == 0 && mode == 1) {
            int playerInventoryStartIndex = this.rowCount() * 9;
            int playerInventoryEndIndex = playerInventoryStartIndex + playerIn.inventory.mainInventory.size();

            if (playerIn.inventory.getFirstEmptyStack() < 0) {
                ItemStack targetStack = this.tileEntity.inventory.getItemWithoutExtractAt(slotId);
                int emptyCapacity = this.inventorySlots.subList(playerInventoryStartIndex, playerInventoryEndIndex)
                        .stream().mapToInt(slot -> {
                            ItemStack slotStack = slot.getStack();
                            if (slotStack.isItemEqual(targetStack) &&
                                    ItemStack.areItemStackTagsEqual(slotStack, targetStack)) {
                                return slotStack.getMaxStackSize() - slotStack.getCount();
                            }
                            return 0;
                        }).sum();

                ItemStack retrievedStack = this.tileEntity.inventory.getItemsAt(slotId, _type,
                        Math.min(emptyCapacity, targetStack.getMaxStackSize()));
                if (!retrievedStack.isEmpty()) {
                    this.mergeItemStack(retrievedStack, playerInventoryStartIndex, playerInventoryEndIndex, true);
                }
            } else {
                ItemStack retrievedStack = this.tileEntity.inventory.getItemsAt(slotId, _type);
                if (!retrievedStack.isEmpty()) {
                    this.mergeItemStack(retrievedStack, playerInventoryStartIndex, playerInventoryEndIndex, true);
                }
            }
        } else {
            ItemStack heldStack = playerIn.inventory.getItemStack();
            if (heldStack.isEmpty()) {
                ItemStack retrievedStack = this.tileEntity.inventory.getItemsAt(slotId, _type);
                playerIn.inventory.setItemStack(retrievedStack);
            } else if (clickedButton == 0) {
                playerIn.inventory.setItemStack(this.tileEntity.inventory.input(heldStack));
            } else if (clickedButton == 1 && mode != 1) {
                playerIn.inventory.setItemStack(this.tileEntity.inventory.input(heldStack, true));
            }
        }
        // Added area

        int itemIndex = slotId;
        ItemStack heldStack = playerIn.inventory.getItemStack();

        // grab a stack from the inventory
        if (heldStack.isEmpty()) {
            int type = 0;
            if (clickedButton == 1) {
                type = 1;
            }
            ItemStack stack = this.tileEntity.inventory.getItemsAt(itemIndex, type);
            if (stack.isEmpty()) {
                return ItemStack.EMPTY;
            }
            // player -> inventory
            if (clickedButton == 0 && mode == 1) {
                if (!this.mergeItemStack(stack, this.rowCount() * 9, this.rowCount() * 9 + 36, true)) {
                    this.tileEntity.inventory.input(stack);
                }
                // inventory -> player
            } else {
                playerIn.inventory.setItemStack(stack);
            }
            return stack;

            // place a stack into the inventory
        } else {
            playerIn.inventory.setItemStack(this.tileEntity.inventory.input(heldStack));
        }
        return ItemStack.EMPTY;
    }

    protected int playerInventoryX() {
        return 8;
    }

    protected int playerInventoryY() {
        return 140;
    }

    protected int rowCount() {
        return 6;
    }

    @Override
    public void onContainerClosed(EntityPlayer playerIn) {
        super.onContainerClosed(playerIn);
        this.tileEntity.sortInventory();
    }

    @Override
    public boolean canDragIntoSlot(@NotNull Slot slotIn) {
        return !slotIn.inventory.equals(this.inventory);
    }
}
