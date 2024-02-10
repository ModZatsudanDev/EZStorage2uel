package modzatsudan.ezstorage.item;

import java.util.List;
import java.util.Objects;

import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.jetbrains.annotations.NotNull;

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
        // if (!world.isRemote && hand == EnumHand.MAIN_HAND) {
        //
        // // get the tag compound
        // NBTTagCompound stackTag = stack.hasTagCompound() ? stack.getTagCompound() : new NBTTagCompound();
        //
        // // check if the dolly is full
        // if (stackTag.getBoolean("isFull")) {
        //
        // // place down the contents of the dolly
        // BlockPos placePos = pos.offset(facing);
        // Block block = Block.getBlockFromName(stackTag.getString("blockType"));
        // IBlockState state = null;
        // boolean isChest = stackTag.getBoolean("isChest");
        // boolean isCore = stackTag.getBoolean("isStorageCore");
        //
        // // check for type and rotate accordingly
        // if (block != null && isChest) {
        // state = block.getDefaultState().withProperty(BlockChest.FACING,
        // player.getHorizontalFacing().getOpposite());
        // } else if (block != null && isCore) {
        // state = block.getDefaultState();
        // }
        //
        // // place the block down and refill it
        // if (state != null) {
        // world.setBlockState(placePos, state);
        // TileEntity t;
        // if (isChest) {
        // t = new TileEntityChest();
        // } else {
        // t = new TileEntityStorageCore();
        // }
        // t.readFromNBT((NBTTagCompound) stackTag.getTag("stored"));
        // world.setTileEntity(placePos, t);
        // stack.setTagCompound(new NBTTagCompound()); // clear the tags
        // stack.damageItem(1, player); // damage the item
        // if (stack.getItemDamage() >= stack.getMaxDamage()) {
        // player.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, null); // destroy it
        // }
        // return EnumActionResult.SUCCESS;
        // }
        //
        // } else {
        // // check if the clicked block is a valid tile
        // IBlockState state = world.getBlockState(pos);
        // TileEntity t = world.getTileEntity(pos);
        // if (t != null) {
        // if (t instanceof TileEntityChest || t instanceof TileEntityStorageCore) {
        // NBTTagCompound storageData = t.writeToNBT(new NBTTagCompound());
        // stackTag.setBoolean("isFull", true);
        // stackTag.setString("blockType", state.getBlock().getRegistryName().toString());
        // stackTag.setBoolean("isChest", t instanceof TileEntityChest);
        // stackTag.setBoolean("isStorageCore", t instanceof TileEntityStorageCore);
        // stackTag.setTag("stored", storageData);
        // stack.setTagCompound(stackTag);
        // world.removeTileEntity(pos); // no item dupes
        // world.setBlockToAir(pos); // good-bye, storage block!
        // return EnumActionResult.SUCCESS;
        // }
        // }
        // }
        // }

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

    public @NotNull ActionResult<ItemStack> onItemRightClick(@NotNull World world, @NotNull EntityPlayer player,
                                                             @NotNull EnumHand hand) {
        ItemStack heldItem = player.getHeldItem(hand);
        if (world.isRemote) {
            return new ActionResult<>(EnumActionResult.PASS, heldItem);
        }

        NBTTagCompound nbt = heldItem.getTagCompound();
        RayTraceResult raytraceresult = this.rayTrace(world, player, false);
        BlockPos pos = raytraceresult.getBlockPos();
        if (nbt != null && nbt.getBoolean("isFull")) {
            BlockPos placePos = pos.offset(raytraceresult.sideHit);
            Block block = Block.getBlockFromName(nbt.getString("blockType"));
            if (block == null || !world.getBlockState(placePos).getBlock().isReplaceable(world, placePos) ||
                    !block.canPlaceBlockAt(world, placePos)) {
                return new ActionResult<>(EnumActionResult.FAIL, heldItem);
            }

            IBlockState state = block.getDefaultState();
            if (block.equals(Blocks.CHEST)) {
                state = state.withProperty(BlockChest.FACING, player.getHorizontalFacing().getOpposite());
            }

            world.setBlockState(placePos, state);
            TileEntity targetTileEntity = world.getTileEntity(placePos);
            if (targetTileEntity != null) {
                NBTTagCompound stored = nbt.getCompoundTag("stored");
                stored.setInteger("x", placePos.getX());
                stored.setInteger("y", placePos.getY());
                stored.setInteger("z", placePos.getZ());
                targetTileEntity.readFromNBT(stored);
            } else {
                world.setBlockToAir(placePos);
                return new ActionResult<>(EnumActionResult.FAIL, heldItem);
            }

            if (player.isCreative()) {
                emptyDolly(nbt);
            } else {
                ItemStack emptyDolly = heldItem.splitStack(1);
                NBTTagCompound emptyDollyNBT = emptyDolly.getTagCompound();
                if (emptyDollyNBT != null) {
                    emptyDolly(emptyDollyNBT);
                }
                emptyDolly.damageItem(1, player);

                if (heldItem.isEmpty()) {
                    return new ActionResult<>(EnumActionResult.SUCCESS, emptyDolly);
                }

                if (!player.addItemStackToInventory(emptyDolly)) {
                    player.dropItem(emptyDolly, false);
                }
            }
        } else {
            if (!world.isBlockModifiable(player, pos)) {
                return new ActionResult<>(EnumActionResult.FAIL, heldItem);
            }

            if (raytraceresult.typeOfHit != RayTraceResult.Type.BLOCK) {
                return new ActionResult<>(EnumActionResult.PASS, heldItem);
            }

            IBlockState state = world.getBlockState(pos);
            TileEntity tileEntity = world.getTileEntity(pos);
            boolean isChest = tileEntity instanceof TileEntityChest;
            boolean isStorageCore = tileEntity instanceof TileEntityStorageCore;
            if (!(isChest || isStorageCore)) {
                return new ActionResult<>(EnumActionResult.PASS, heldItem);
            }

            NBTTagCompound storageData = tileEntity.writeToNBT(new NBTTagCompound());

            if (isStorageCore) {
                world.setBlockToAir(pos);
                world.removeTileEntity(pos);
            } else {
                world.removeTileEntity(pos);
                world.setBlockToAir(pos);
            }

            if (player.isCreative()) {
                fillDolly(heldItem, nbt, state, isChest, isStorageCore, storageData);
            } else {
                ItemStack filledDolly = heldItem.splitStack(1);
                NBTTagCompound filledDollyNBT = filledDolly.getTagCompound();
                fillDolly(filledDolly, filledDollyNBT, state, isChest, isStorageCore, storageData);

                if (heldItem.isEmpty()) {
                    return new ActionResult<>(EnumActionResult.SUCCESS, filledDolly);
                }

                if (!player.addItemStackToInventory(filledDolly)) {
                    player.dropItem(filledDolly, false);
                }
            }
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, heldItem);
    }

    private void emptyDolly(NBTTagCompound nbt) {
        nbt.setBoolean("isFull", false);
        nbt.removeTag("blockType");
        nbt.removeTag("isChest");
        nbt.removeTag("isStorageCore");
        nbt.removeTag("stored");
    }

    private void fillDolly(ItemStack dolly, NBTTagCompound dollyNBT, IBlockState state, boolean isChest,
                           boolean isStorageCore, NBTTagCompound storageData) {
        if (dollyNBT == null) {
            dollyNBT = new NBTTagCompound();
            dolly.setTagCompound(dollyNBT);
        }

        dollyNBT.setBoolean("isFull", true);
        dollyNBT.setString("blockType", Objects.requireNonNull(state.getBlock().getRegistryName()).toString());
        dollyNBT.setBoolean("isChest", isChest);
        dollyNBT.setBoolean("isStorageCore", isStorageCore);
        dollyNBT.setTag("stored", storageData);
    }

    @Override
    public int getItemStackLimit(ItemStack stack) {
        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt != null && nbt.getBoolean("isFull")) {
            return 1;
        }
        return super.getItemStackLimit(stack);
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
