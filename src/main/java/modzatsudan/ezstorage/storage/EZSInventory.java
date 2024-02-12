package modzatsudan.ezstorage.storage;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;

import org.jetbrains.annotations.NotNull;

import modzatsudan.ezstorage.util.ItemStackUtil;

public class EZSInventory {

    public List<ItemData> inventory;
    public long maxCapacity = 0;

    public EZSInventory() {
        this.inventory = new ArrayList<>();
    }

    private @NotNull void mergeStack(ItemStack itemStack, int amount) {
        for (ItemData data : inventory) {
            if (ItemStackUtil.stacksEqual(data.itemStack, itemStack)) {
                data.count += amount;
            }
        }
    }
}
