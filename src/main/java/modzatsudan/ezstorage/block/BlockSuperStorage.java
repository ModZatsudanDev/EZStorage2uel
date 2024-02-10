package modzatsudan.ezstorage.block;

import net.minecraft.block.material.Material;

import modzatsudan.ezstorage.config.EZConfig;
import modzatsudan.ezstorage.registry.IRegistryBlock;

/** A super storage box */
public class BlockSuperStorage extends BlockStorage implements IRegistryBlock {

    public BlockSuperStorage() {
        super("super_storage_box", Material.IRON);
    }

    @Override
    public int getCapacity() {
        return EZConfig.superCapacity;
    }
}
