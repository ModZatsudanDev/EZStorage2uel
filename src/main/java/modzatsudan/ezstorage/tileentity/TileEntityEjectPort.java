package modzatsudan.ezstorage.tileentity;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

import modzatsudan.ezstorage.util.ItemGroup;

/** The ejection port (now with fewer bugs!) */
public class TileEntityEjectPort extends TileEntityMultiblock {

    @Override
    public void update() {
        super.update();

        if (!world.isRemote && hasCore() && !world.isBlockPowered(pos)) {
            boolean updateCore = false;
            BlockPos targetPos = getPos().offset(EnumFacing.UP);
            TileEntity targetTile = world.getTileEntity(targetPos);

            // make sure there's a inventory tile entity above it
            if (targetTile != null && targetTile instanceof IInventory) {
                IInventory targetInv = (IInventory) targetTile;
                Block targetBlock = world.getBlockState(targetPos).getBlock();

                // double chest support
                if (targetInv != null && targetInv instanceof TileEntityChest && targetBlock instanceof BlockChest) {
                    targetInv = ((BlockChest) targetBlock).getContainer(world, targetPos, true);
                }

                // make sure the inventory exists
                if (targetInv != null) {

                    // now spit the items into the above inventory
                    List<ItemGroup> inventoryList = core.inventory.inventory;
                    if (inventoryList != null && inventoryList.size() > 0) {
                        ItemGroup group = inventoryList.get(0);
                        if (group != null) {
                            ItemStack stack = group.itemStack.copy(); // wasn't a copy before....
                                                                      // WEIRD STUFF HAPPENED.
                            stack.setCount((int) Math.min(stack.getMaxStackSize(), group.count));
                            int stackSize = stack.getCount();
                            ItemStack leftOver = TileEntityHopper.putStackInInventoryAllSlots(targetInv, targetInv,
                                    stack, EnumFacing.DOWN);
                            if (!leftOver.isEmpty()) {
                                int remaining = stackSize - leftOver.getCount();
                                if (remaining > 0) {
                                    group.count -= remaining;
                                    updateCore = true;
                                }
                            } else {
                                group.count -= stackSize;
                                updateCore = true;
                            }
                            if (group.count <= 0) {
                                core.inventory.inventory.remove(0);
                            }
                        }
                    }
                }
            }

            // make sure to sort the inventory on change
            if (updateCore) {
                core.sortInventory();
            }
        }
    }

    @Override
    public NBTTagCompound writeDataToNBT(NBTTagCompound paramNBTTagCompound) {
        return paramNBTTagCompound;
    }

    @Override
    public void readDataFromNBT(NBTTagCompound paramNBTTagCompound) {}
}
