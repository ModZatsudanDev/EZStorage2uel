package modzatsudan.ezstorage.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import modzatsudan.ezstorage.EZStorage;
import modzatsudan.ezstorage.registry.IRegistryItem;

/** Superclass for all mod items */
public abstract class EZItem extends Item implements IRegistryItem {

    public EZItem(String name) {
        this.setRegistryName(name);
        this.setCreativeTab(EZStorage.instance.creativeTab);
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos,
                                      EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack heldItem = player != null ? player.getHeldItem(hand) : ItemStack.EMPTY;
        return onItemUse(heldItem, player, world, pos, hand, facing, hitX, hitY, hitZ);
    }

    /** Wrapper for old method signature */
    public EnumActionResult onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos,
                                      EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        return super.onItemUse(player, world, pos, hand, facing, hitX, hitY, hitZ);
    }
}
