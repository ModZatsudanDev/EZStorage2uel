package modzatsudan.ezstorage.storage;

import net.minecraft.item.ItemStack;

import org.jetbrains.annotations.NotNull;

public class ItemData {

    public ItemStack itemStack;
    public long count;
    public String translationKey;

    private boolean highlighted;

    public ItemData(@NotNull ItemStack itemStack) {
        this.itemStack = itemStack;
        this.count = itemStack.getCount();
        this.translationKey = itemStack.getTranslationKey();
    }

    public ItemData(@NotNull ItemStack itemStack, long count) {
        this.itemStack = itemStack;
        this.count = itemStack.getCount();
        this.translationKey = itemStack.getTranslationKey();
    }
}
