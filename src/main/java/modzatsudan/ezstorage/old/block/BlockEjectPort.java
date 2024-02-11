package modzatsudan.ezstorage.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import modzatsudan.ezstorage.old.tileentity.TileEntityEjectPort;

public class BlockEjectPort extends EZBlockContainer {

    public BlockEjectPort() {
        super("output_port", Material.IRON);
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileEntityEjectPort();
    }
}
