package modzatsudan.ezstorage.ref;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import modzatsudan.ezstorage.init.EZBlocks;

public class EZTab extends CreativeTabs {

    public EZTab() {
        super("EZStorage");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ItemStack createIcon() {
        return new ItemStack(EZBlocks.condensed_storage_box);
    }
}
