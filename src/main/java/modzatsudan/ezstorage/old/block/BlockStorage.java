package modzatsudan.ezstorage.block;

import net.minecraft.block.material.Material;

import modzatsudan.ezstorage.config.EZConfig;

public class BlockStorage extends StorageMultiblock {

    public BlockStorage() {
        super("storage_box", Material.WOOD);
    }

    public BlockStorage(String name, Material material) {
        super(name, material);
    }

    public int getCapacity() {
        return EZConfig.basicCapacity;
    }
}
