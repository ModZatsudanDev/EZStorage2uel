package modzatsudan.ezstorage.tileentity;

import modzatsudan.ezstorage.block.StorageMultiblock;
import modzatsudan.ezstorage.util.BlockRef;

/** Multiblock tile entity with default core-searching functionality */
public abstract class TileEntityMultiblock extends TileEntityBase {

    /** The storage core */
    public TileEntityStorageCore core;

    /** The period in ticks at which to scan given that scanning is possible */
    private static final int updatePeriod = 10;

    // Scan the multiblock if the storage core is null
    @Override
    public void update() {
        if (!hasCore()) {
            if (!world.isRemote && world.getTotalWorldTime() % updatePeriod == 0) {
                StorageMultiblock block = (StorageMultiblock) world.getBlockState(pos).getBlock();
                core = block.attemptMultiblock(world, pos);
            }
        } else {
            if (!isCorePartOfMultiblock()) core = null;
        }
    }

    /** Does this block have a valid core? */
    public boolean hasCore() {
        return core != null;
    }

    /** Is the core at a valid position? */
    public boolean isCorePartOfMultiblock() {
        return hasCore() && core.isPartOfMultiblock(new BlockRef(this));
    }
}
