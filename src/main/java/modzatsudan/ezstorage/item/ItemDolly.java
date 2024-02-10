package modzatsudan.ezstorage.item;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import modzatsudan.ezstorage.tileentity.TileEntityStorageCore;

/** A dolly item for moving chests and storage cores */
public class ItemDolly extends EZItem {

    public ItemDolly(int maxDamage, String name) {
        super(name);
        this.setMaxDamage(maxDamage);
    }

    // take on a package
    @Override
    public EnumActionResult onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos,
                                      EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!world.isRemote && hand == EnumHand.MAIN_HAND) {

            // get the tag compound
            NBTTagCompound stackTag = stack.hasTagCompound() ? stack.getTagCompound() : new NBTTagCompound();

            // check if the dolly is full
            if (stackTag.getBoolean("isFull")) {

                // place down the contents of the dolly
                BlockPos placePos = pos.offset(facing);
                Block block = Block.getBlockFromName(stackTag.getString("blockType"));
                IBlockState state = null;
                boolean isChest = stackTag.getBoolean("isChest");
                boolean isCore = stackTag.getBoolean("isStorageCore");

                // check for type and rotate accordingly
                if (block != null && isChest) {
                    state = block.getDefaultState().withProperty(BlockChest.FACING,
                            player.getHorizontalFacing().getOpposite());
                } else if (block != null && isCore) {
                    state = block.getDefaultState();
                }

                // place the block down and refill it
                if (state != null) {
                    world.setBlockState(placePos, state);
                    TileEntity t;
                    if (isChest) {
                        t = new TileEntityChest();
                    } else {
                        t = new TileEntityStorageCore();
                    }
                    t.readFromNBT((NBTTagCompound) stackTag.getTag("stored"));
                    world.setTileEntity(placePos, t);
                    stack.setTagCompound(new NBTTagCompound()); // clear the tags
                    stack.damageItem(1, player); // damage the item
                    if (stack.getItemDamage() >= stack.getMaxDamage()) {
                        player.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, null); // destroy it
                    }
                    return EnumActionResult.SUCCESS;
                }

            } else {
                // check if the clicked block is a valid tile
                IBlockState state = world.getBlockState(pos);
                TileEntity t = world.getTileEntity(pos);
                if (t != null) {
                    if (t instanceof TileEntityChest || t instanceof TileEntityStorageCore) {
                        NBTTagCompound storageData = t.writeToNBT(new NBTTagCompound());
                        stackTag.setBoolean("isFull", true);
                        stackTag.setString("blockType", state.getBlock().getRegistryName().toString());
                        stackTag.setBoolean("isChest", t instanceof TileEntityChest);
                        stackTag.setBoolean("isStorageCore", t instanceof TileEntityStorageCore);
                        stackTag.setTag("stored", storageData);
                        stack.setTagCompound(stackTag);
                        world.removeTileEntity(pos); // no item dupes
                        world.setBlockToAir(pos); // good-bye, storage block!
                        return EnumActionResult.SUCCESS;
                    }
                }
            }
        }

        return EnumActionResult.PASS;
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        String name = super.getItemStackDisplayName(stack);
        if (stack.hasTagCompound() && stack.getTagCompound().getBoolean("isFull")) {
            return name + " (Full)";
        }
        return name + " (Empty)";
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag advanced) {
        String name = super.getItemStackDisplayName(stack);
        if (stack.hasTagCompound() && stack.getTagCompound().getBoolean("isFull")) {
            Block block = Block.getBlockFromName(stack.getTagCompound().getString("blockType"));
            tooltip.add(block.getLocalizedName());
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerRender() {
        ModelResourceLocation[] locations = new ModelResourceLocation[] {
                new ModelResourceLocation(this.getRegistryName() + "_empty", "inventory"),
                new ModelResourceLocation(this.getRegistryName() + "_chest", "inventory"),
                new ModelResourceLocation(this.getRegistryName() + "_storage_core", "inventory")
        };
        ModelBakery.registerItemVariants(this, locations);
        ModelLoader.setCustomMeshDefinition(this, new ItemMeshDefinition() {

            @Override
            public ModelResourceLocation getModelLocation(ItemStack stack) {
                if (stack.hasTagCompound() && stack.getTagCompound().getBoolean("isFull")) {
                    if (stack.getTagCompound().getBoolean("isChest")) {
                        return locations[1];
                    }
                    if (stack.getTagCompound().getBoolean("isStorageCore")) {
                        return locations[2];
                    }
                }
                return locations[0];
            }
        });
    }
}
