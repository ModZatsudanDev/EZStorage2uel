package modzatsudan.ezstorage.integration.jei;

import java.awt.*;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import mezz.jei.api.gui.IAdvancedGuiHandler;
import modzatsudan.ezstorage.gui.client.GuiStorageCore;

public class JEIGuiHandler implements IAdvancedGuiHandler<GuiStorageCore> {

    @Override
    public @NotNull Class<GuiStorageCore> getGuiContainerClass() {
        return GuiStorageCore.class;
    }

    @Nullable
    @Override
    public Object getIngredientUnderMouse(@NotNull GuiStorageCore guiStorageCore, int mouseX, int mouseY) {
        Integer slot = guiStorageCore.getSlotAt(mouseX, mouseY);
        if (slot == null || guiStorageCore.getFilteredList().size() <= slot || guiStorageCore.isSearchFieldFocused()) {
            return null;
        }
        return guiStorageCore.getFilteredList().get(slot).itemStack;
    }

    @Nullable
    @Override
    public List<Rectangle> getGuiExtraAreas(@NotNull GuiStorageCore guiStorageCore) {
        return guiStorageCore.getJEIExclusionArea();
    }
}
