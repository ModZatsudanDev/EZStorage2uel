package modzatsudan.ezstorage.block;

import net.minecraft.block.material.Material;

import modzatsudan.ezstorage.config.EZConfig;

public class BlockHyperStorage extends BlockStorage {

    public BlockHyperStorage() {
        super("hyper_storage_box", Material.IRON);
    }

    @Override
    public int getCapacity() {
        return EZConfig.hyperCapacity;
    }
}
