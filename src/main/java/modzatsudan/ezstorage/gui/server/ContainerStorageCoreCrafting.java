package modzatsudan.ezstorage.gui.server;

import javax.annotation.Nonnull;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.world.World;

import org.jetbrains.annotations.NotNull;

import modzatsudan.ezstorage.events.CoreEvents;

/** The crafting-expansion storage core container */
public class ContainerStorageCoreCrafting extends ContainerStorageCore {

    public InventoryCrafting craftMatrix = new InventoryCrafting(this, 3, 3);
    public IInventory craftResult = new InventoryCraftResult();
    private World worldObj;
    private long lastTick = -1;
    private boolean craftMatrixChanged = true;
    private ItemStack craftResultCache;

    public ContainerStorageCoreCrafting(EntityPlayer player, World world, int x, int y, int z) {
        super(player, world, x, y, z);
        this.worldObj = world;
        this.addSlotToContainer(new SlotCrafting(player, this.craftMatrix, this.craftResult, 0, 116, 117));
        int i;
        int j;

        for (i = 0; i < 3; ++i) {
            for (j = 0; j < 3; ++j) {
                this.addSlotToContainer(new Slot(this.craftMatrix, j + i * 3, 44 + j * 18, 99 + i * 18));
            }
        }
        this.onCraftMatrixChanged(this.craftMatrix);
    }

    @Override
    public void onCraftMatrixChanged(IInventory inventoryIn) {
        if (!this.craftMatrixChanged) {
            return;
        }
        this.craftResult.setInventorySlotContents(0,
                CraftingManager.findMatchingResult(this.craftMatrix, this.worldObj));
    }

    // Shift clicking
    @Override
    public @Nonnull ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        // make sure there's no multiclicking shenanigans going on
        if (playerIn instanceof EntityPlayerMP) {
            if (CoreEvents.serverTicks == lastTick) {
                EntityPlayerMP mp = (EntityPlayerMP) playerIn;
                mp.sendContainerToPlayer(this); // send an inventory sync
                                                // message just in case
                this.craftResultCache = null;
                this.craftMatrixChanged = true;
                return ItemStack.EMPTY;
            }
            lastTick = CoreEvents.serverTicks; // keep track of server ticks
        } else {
            if (CoreEvents.clientTicks == lastTick) {
                this.craftResultCache = null;
                this.craftMatrixChanged = true;
                return ItemStack.EMPTY;
            }
            lastTick = CoreEvents.clientTicks; // keep track of client ticks
        }

        // now do shift-click processing
        Slot slotObject = inventorySlots.get(index);
        if (slotObject != null && slotObject.getHasStack()) {
            if (slotObject instanceof SlotCrafting) {
                ItemStack[] recipe = new ItemStack[9];
                for (int i = 0; i < 9; i++) {
                    recipe[i] = this.craftMatrix.getStackInSlot(i).copy();
                }

                ItemStack itemstack1 = slotObject.getStack();
                ItemStack itemstack = ItemStack.EMPTY;
                ItemStack original = itemstack1.copy();
                int crafted = 0;
                int maxStackSize = itemstack1.getMaxStackSize();
                int crafting = itemstack1.getCount();
                for (int i = 0; i < itemstack1.getMaxStackSize(); i++) {

                    if (slotObject.getHasStack()) {
                        if (crafting > maxStackSize) {
                            this.craftResultCache = null;
                            this.craftMatrixChanged = true;
                            return ItemStack.EMPTY;
                        }
                        itemstack1 = slotObject.getStack();
                        itemstack = itemstack1.copy();
                        this.craftMatrixChanged = false;
                        this.craftResultCache = itemstack;
                        if (crafted + itemstack1.getCount() > itemstack1.getMaxStackSize()) {
                            this.craftResultCache = null;
                            this.craftMatrixChanged = true;
                            return ItemStack.EMPTY;
                        }
                        boolean merged = this.mergeItemStack(itemstack1, this.rowCount() * 9, this.rowCount() * 9 + 36,
                                true);
                        if (!merged) {
                            this.craftResultCache = null;
                            this.craftMatrixChanged = true;
                            return ItemStack.EMPTY;
                        } else {

                            // It merged! grab another
                            crafted += itemstack.getCount();
                            slotObject.onSlotChange(itemstack1, itemstack);
                            slotObject.onTake(playerIn, itemstack1);

                            tryToPopulateCraftingGrid(recipe, playerIn);
                            if (this.craftMatrixChanged) {
                                this.onCraftMatrixChanged(this.craftMatrix);
                                this.craftResultCache = null;
                                this.craftMatrixChanged = true;
                                return ItemStack.EMPTY;
                            } else {
                                slotObject.putStack(this.craftResultCache);
                            }
                        }
                    } else {
                        break;
                    }
                }

                if (itemstack1.getCount() == itemstack.getCount()) {
                    this.craftResultCache = null;
                    this.craftMatrixChanged = true;
                    return ItemStack.EMPTY;
                }

                this.craftResultCache = null;
                this.craftMatrixChanged = true;
                return itemstack;
            } else {
                ItemStack stackInSlot = slotObject.getStack();
                slotObject.putStack(this.tileEntity.inventory.input(stackInSlot));
            }
        }
        this.craftResultCache = null;
        this.craftMatrixChanged = true;
        return ItemStack.EMPTY;
    }

    @Override
    public @Nonnull ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player) {
        if (slotId >= 0 && slotId < inventorySlots.size()) {
            Slot slotObject = inventorySlots.get(slotId);
            if (slotObject != null && slotObject instanceof SlotCrafting) { // user clicked on result slot
                ItemStack[] recipe = new ItemStack[9];
                for (int i = 0; i < 9; i++) {
                    recipe[i] = this.craftMatrix.getStackInSlot(i).copy();
                }

                ItemStack result;
                if (!slotObject.getHasStack()) {
                    result = ItemStack.EMPTY;
                } else {

                    int heldItemCountBeforeCraft = player.inventory.getItemStack().getCount();
                    ItemStack resultBeforeCraft = slotObject.getStack().copy();
                    this.craftMatrixChanged = false;
                    result = super.slotClick(slotId, dragType, clickTypeIn, player);
                    int heldItemCountAfterCraft = player.inventory.getItemStack().getCount();
                    if (clickTypeIn == ClickType.PICKUP && heldItemCountBeforeCraft < heldItemCountAfterCraft) {
                        this.tryToPopulateCraftingGrid(recipe, player);
                        if (this.craftMatrixChanged) {
                            this.onCraftMatrixChanged(this.craftMatrix);
                        } else {
                            slotObject.putStack(resultBeforeCraft);
                        }
                    }
                    this.craftMatrixChanged = true;
                }

                if (!result.isEmpty()) {
                    tryToPopulateCraftingGrid(recipe, player);
                }
                return result;
            }
        }
        return super.slotClick(slotId, dragType, clickTypeIn, player);
    }

    private void tryToPopulateCraftingGrid(ItemStack[] recipe, EntityPlayer playerIn) {
        for (int i = 0; i < 9; ++i) {
            ItemStack stack = this.craftMatrix.getStackInSlot(i);
            if (!stack.isEmpty() && !stack.getItem().hasContainerItem(stack)) {
                ItemStack result = this.tileEntity.input(stack);
                this.craftMatrix.setInventorySlotContents(i, ItemStack.EMPTY);
                if (!result.isEmpty()) {
                    playerIn.dropItem(result, false);
                }
            }
        }

        for (int j = 0; j < recipe.length; j++) {
            if (!recipe[j].isEmpty()) {
                if (recipe[j].getCount() > 1) {
                    recipe[j].setCount(recipe[j].getCount() - 1);
                }
                Slot slot = getSlotFromInventory(this.craftMatrix, j);
                if (slot != null && slot.getHasStack()) {
                    slot = null;
                }
                if (slot != null) {
                    ItemStack retreived = tileEntity.inventory.getItemsForRecipeSync(new ItemStack[] { recipe[j] });
                    if (!retreived.isEmpty()) {
                        slot.putStack(retreived);
                    } else {
                        this.craftMatrixChanged = true;
                    }
                }
            }
        }
    }

    @Override
    protected int playerInventoryY() {
        return 162;
    }

    @Override
    protected int rowCount() {
        return 4;
    }

    @Override
    public void onContainerClosed(EntityPlayer playerIn) {
        clearGrid(playerIn);
        super.onContainerClosed(playerIn);
    }

    @Override
    public boolean canMergeSlot(@NotNull ItemStack stack, Slot slotIn) {
        return !slotIn.inventory.equals(this.craftResult) && super.canMergeSlot(stack, slotIn);
    }

    public void clearGrid(EntityPlayer playerIn) {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = this.craftMatrix.getStackInSlot(i);
            if (!stack.isEmpty()) {
                ItemStack result = this.tileEntity.input(stack);
                this.craftMatrix.setInventorySlotContents(i, ItemStack.EMPTY);
                if (!result.isEmpty()) {
                    playerIn.dropItem(result, false);
                }
            }
        }
    }
}
