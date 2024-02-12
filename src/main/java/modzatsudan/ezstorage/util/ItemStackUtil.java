package modzatsudan.ezstorage.util;

import net.minecraft.item.ItemStack;

import org.jetbrains.annotations.NotNull;

public class ItemStackUtil {

    public static boolean stacksEqual(@NotNull ItemStack stack1, @NotNull ItemStack stack2) {
        if (stack1.isEmpty() && stack2.isEmpty()) return true;
        if (stack1.isEmpty() || stack2.isEmpty()) return false;

        if (stack1.getItem() == stack2.getItem()) {
            if (stack1.getItemDamage() == stack2.getItemDamage()) {
                return (!stack1.hasTagCompound() && !stack2.hasTagCompound()) ||
                        (stack1.hasTagCompound() && stack1.getTagCompound().equals(stack2.getTagCompound()));
            }
        }
        return false;
    }
}
