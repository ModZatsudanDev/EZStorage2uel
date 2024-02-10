package modzatsudan.ezstorage.block;

import net.minecraft.block.material.Material;

import modzatsudan.ezstorage.config.EZConfig;

/** An ultra storage box */
public class BlockUltraStorage extends BlockStorage {

    public BlockUltraStorage() {
        super("ultra_storage_box", Material.IRON);
    }

    @Override
    public int getCapacity() {
        return EZConfig.ultraCapacity;
    }
}
