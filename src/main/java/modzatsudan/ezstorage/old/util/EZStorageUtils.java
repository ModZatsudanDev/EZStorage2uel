package modzatsudan.ezstorage.old.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import modzatsudan.ezstorage.old.tileentity.TileEntitySecurityBox;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;

import modzatsudan.ezstorage.block.BlockSecurityBox;
import modzatsudan.ezstorage.block.StorageMultiblock;

/** Useful stuff */
public class EZStorageUtils {

    private static HashMap<String, String> modMap = new HashMap();

    /** Get a cached mod name from the mod map */
    public static String getModNameFromID(String modid) {
        // build the map
        if (modMap.isEmpty()) {
            for (ModContainer m : Loader.instance().getModList()) {
                modMap.put(m.getModId(), m.getName());
            }
        }
        if (modMap.containsKey(modid)) {
            return modMap.get(modid);
        } else {
            return "";
        }
    }

    /** Gets an ItemStack's display name, compatible with common code */
    public static String getStackDisplayName(ItemStack stack) {
        if (stack.isEmpty()) return "null";
        try { // try the default display name getter
            return stack.getDisplayName();
        } catch (Exception e) { // if any problem occurs, go to fallback translation
            return FallbackTranslator.translate(stack.getTranslationKey());
        }
    }

    /** Get a block's neighbors */
    public static List<BlockRef> getNeighbors(int xCoord, int yCoord, int zCoord, World world) {
        List<BlockRef> blockList = new ArrayList<BlockRef>();

        // build the list
        blockList.add(new BlockRef(world.getBlockState(new BlockPos(xCoord - 1, yCoord, zCoord)).getBlock(), xCoord - 1,
                yCoord, zCoord));
        blockList.add(new BlockRef(world.getBlockState(new BlockPos(xCoord + 1, yCoord, zCoord)).getBlock(), xCoord + 1,
                yCoord, zCoord));
        blockList.add(new BlockRef(world.getBlockState(new BlockPos(xCoord, yCoord - 1, zCoord)).getBlock(), xCoord,
                yCoord - 1, zCoord));
        blockList.add(new BlockRef(world.getBlockState(new BlockPos(xCoord, yCoord + 1, zCoord)).getBlock(), xCoord,
                yCoord + 1, zCoord));
        blockList.add(new BlockRef(world.getBlockState(new BlockPos(xCoord, yCoord, zCoord - 1)).getBlock(), xCoord,
                yCoord, zCoord - 1));
        blockList.add(new BlockRef(world.getBlockState(new BlockPos(xCoord - 1, yCoord, zCoord)).getBlock(), xCoord - 1,
                yCoord, zCoord));
        blockList.add(new BlockRef(world.getBlockState(new BlockPos(xCoord, yCoord, zCoord + 1)).getBlock(), xCoord,
                yCoord, zCoord + 1));

        // load each block's chunk if not loaded
        for (BlockRef r : blockList) {
            if (!world.isBlockLoaded(r.pos)) {
                world.getChunk(r.pos); // loads the chunk
            }
        }

        // return the list
        return blockList;
    }

    /** Update the block position */
    public static void notifyBlockUpdate(TileEntity entity) {
        notifyBlockUpdate(entity.getWorld(), entity.getPos());
    }

    /** Update the block position */
    public static void notifyBlockUpdate(World world, BlockPos pos) {
        world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
    }

    /** Instead of searching for a core, find a secure block */
    public static TileEntitySecurityBox findSecurityBox(BlockRef br, World world, Set<BlockRef> scanned) {
        if (scanned == null)
            scanned = new HashSet<BlockRef>();
        List<BlockRef> neighbors = EZStorageUtils.getNeighbors(br.pos.getX(), br.pos.getY(), br.pos.getZ(), world);
        for (BlockRef blockRef : neighbors) {
            if (blockRef.block instanceof StorageMultiblock) {
                if (blockRef.block instanceof BlockSecurityBox) {
                    return (TileEntitySecurityBox) world.getTileEntity(blockRef.pos);
                } else {
                    if (scanned.add(blockRef) == true) {
                        TileEntitySecurityBox entity = findSecurityBox(blockRef, world, scanned);
                        if (entity != null) {
                            return entity;
                        }
                    }
                }
            }
        }
        return null;
    }

    /** Get up to maxCount of nearby players */
    public static List<EntityPlayer> getNearbyPlayers(World world, BlockPos pos, double distance, int maxCount) {
        int count = 0;
        JointList<EntityPlayer> list = new JointList();
        for (EntityPlayer p : world.playerEntities) {
            if (count < maxCount && p.getDistanceSq(pos) < distance * distance) {
                list.add(p);
                count++;
            }
        }
        return list;
    }
}
