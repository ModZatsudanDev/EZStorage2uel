package modzatsudan.ezstorage.gui.server;

import javax.annotation.Nonnull;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import modzatsudan.ezstorage.tileentity.TileEntityExtractPort;
import modzatsudan.ezstorage.util.EZStorageUtils;

/** The secure box container */
public class ContainerExtractPort extends Container {

    private TileEntityExtractPort tileExtract;

    public ContainerExtractPort(InventoryPlayer player, TileEntityExtractPort tile) {
        this.tileExtract = tile;
        int i;

        // listing inventory placement
        for (i = 0; i < 9; ++i) {
            this.addSlotToContainer(new SlotExtractList(tile.extractList, i, 8 + i * 18, 20));
        }

        // player inventory placement
        int yoff_inv = -15;
        for (i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlotToContainer(new Slot(player, j + i * 9 + 9, 8 + j * 18, 84 + i * 18 + yoff_inv));
            }
        }
        for (i = 0; i < 9; ++i) {
            this.addSlotToContainer(new Slot(player, i, 8 + i * 18, 142 + yoff_inv));
        }
    }

    @Override
    public void addListener(IContainerListener listener) {
        super.addListener(listener);
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return true;
    }

    @Override
    public boolean enchantItem(EntityPlayer player, int action) {
        boolean update = false;
        if (action == 0) {
            tileExtract.listMode = tileExtract.listMode.rotateMode();
            update = true;
        }
        if (action >= 10) {
            tileExtract.roundRobin = (action % 10) > 0;
            update = true;
        }
        if (update) {
            EZStorageUtils.notifyBlockUpdate(tileExtract);
            tileExtract.markDirty();
        }
        return false;
    }

    @Override
    public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player) {
        tileExtract.markDirty();
        return super.slotClick(slotId, dragType, clickTypeIn, player);
    }

    /** Called when a player shift-clicks on a slot. You must override this or you will crash when someone does that. */
    @Override
    public @Nonnull ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int par2) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(par2);

        int INPUT = 8;

        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            // itemstack is in player inventory, try to place in slot
            if (par2 != INPUT) {
                // item in player's inventory, but not in action bar
                if (par2 >= INPUT + 1 && par2 < INPUT + 28) {
                    // place in action bar
                    if (!this.mergeItemStack(itemstack1, INPUT + 28, INPUT + 37, false)) {
                        return ItemStack.EMPTY;
                    }
                }
                // item in action bar - place in player inventory
                else if (par2 >= INPUT + 28 && par2 < INPUT + 37 &&
                        !this.mergeItemStack(itemstack1, INPUT + 1, INPUT + 28, false)) {
                            return ItemStack.EMPTY;
                        }
            }

            if (itemstack1.getCount() == 0) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }

            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(par1EntityPlayer, itemstack1);
        }
        return itemstack;
    }
}
