package modzatsudan.ezstorage;

import net.minecraft.block.BlockChest;

public class MultiPageChest extends BlockChest {

    public MultiPageChest() {
        super(BlockChest.Type.BASIC);
        setTranslationKey("multipagechest");
        setHarvestLevel("pickaxe", 1);
    }
}
