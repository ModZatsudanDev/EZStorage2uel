package modzatsudan.ezstorage.init;

import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import modzatsudan.ezstorage.Tags;
import modzatsudan.ezstorage.block.BlockAccessTerminal;
import modzatsudan.ezstorage.block.BlockBlankBox;
import modzatsudan.ezstorage.block.BlockCondensedStorage;
import modzatsudan.ezstorage.block.BlockCraftingBox;
import modzatsudan.ezstorage.block.BlockEjectPort;
import modzatsudan.ezstorage.block.BlockExtractPort;
import modzatsudan.ezstorage.block.BlockHyperStorage;
import modzatsudan.ezstorage.block.BlockInputPort;
import modzatsudan.ezstorage.block.BlockSearchBox;
import modzatsudan.ezstorage.block.BlockSecurityBox;
import modzatsudan.ezstorage.block.BlockSortBox;
import modzatsudan.ezstorage.block.BlockStorage;
import modzatsudan.ezstorage.block.BlockStorageCore;
import modzatsudan.ezstorage.block.BlockSuperStorage;
import modzatsudan.ezstorage.block.BlockUltraStorage;
import modzatsudan.ezstorage.block.EZBlock;
import modzatsudan.ezstorage.config.EZConfig;
import modzatsudan.ezstorage.registry.IRegistryBlock;
import modzatsudan.ezstorage.registry.RegistryHelper;
import modzatsudan.ezstorage.tileentity.TileEntityEjectPort;
import modzatsudan.ezstorage.tileentity.TileEntityExtractPort;
import modzatsudan.ezstorage.tileentity.TileEntityInputPort;
import modzatsudan.ezstorage.tileentity.TileEntitySecurityBox;
import modzatsudan.ezstorage.tileentity.TileEntityStorageCore;
import modzatsudan.ezstorage.util.JointList;

/** Mod blocks */
public class EZBlocks {

    private static JointList<IRegistryBlock> blocks;

    public static void mainRegistry() {
        blocks = new JointList();
        init();
        register();
    }

    public static EZBlock blank_box;
    public static EZBlock storage_core;
    public static EZBlock storage_box;
    public static EZBlock condensed_storage_box;
    public static EZBlock super_storage_box;
    public static EZBlock ultra_storage_box;
    public static EZBlock hyper_storage_box;
    public static EZBlock input_port;
    public static EZBlock output_port;
    public static EZBlock extract_port;
    public static EZBlock crafting_box;
    public static EZBlock search_box;
    public static EZBlock sort_box;
    public static EZBlock access_terminal;
    public static EZBlock security_box;

    private static void init() {
        blocks.join(blank_box = new BlockBlankBox(), storage_core = new BlockStorageCore(),
                storage_box = new BlockStorage(),
                condensed_storage_box = new BlockCondensedStorage(), super_storage_box = new BlockSuperStorage(),
                ultra_storage_box = new BlockUltraStorage(), hyper_storage_box = new BlockHyperStorage(),
                input_port = new BlockInputPort(),
                output_port = new BlockEjectPort(), extract_port = new BlockExtractPort(),
                crafting_box = new BlockCraftingBox(),
                search_box = new BlockSearchBox(), sort_box = new BlockSortBox(),
                access_terminal = new BlockAccessTerminal(),
                security_box = new BlockSecurityBox());
        if (!EZConfig.enableTerminal)
            blocks.remove(access_terminal); // terminal disabled
        if (!EZConfig.enableSecurity)
            blocks.remove(security_box); // security disabled
    }

    /** Register the blocks and tile entities */
    private static void register() {
        RegistryHelper.registerBlocks(blocks);
        GameRegistry.registerTileEntity(TileEntityStorageCore.class, Tags.MODID + ":TileEntityStorageCore");
        GameRegistry.registerTileEntity(TileEntityInputPort.class, Tags.MODID + ":TileEntityInputPort");
        GameRegistry.registerTileEntity(TileEntityEjectPort.class, Tags.MODID + ":TileEntityOutputPort");
        GameRegistry.registerTileEntity(TileEntityExtractPort.class, Tags.MODID + ":TileEntityExtractPort");
        GameRegistry.registerTileEntity(TileEntitySecurityBox.class, Tags.MODID + ":TileEntitySecurityBox");
    }

    /** Register model information */
    @SideOnly(Side.CLIENT)
    public static void registerRenders() {
        for (IRegistryBlock block : blocks) {
            block.registerRender();
        }
    }
}
