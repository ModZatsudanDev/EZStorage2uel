package modzatsudan.ezstorage.block;

import net.minecraft.block.material.Material;

import modzatsudan.ezstorage.config.EZConfig;

public class BlockCondensedStorage extends BlockStorage {

    public BlockCondensedStorage() {
        super("condensed_storage_box", Material.IRON);
    }

    @Override
    public int getCapacity() {
        return EZConfig.condensedCapacity;
    }
}
