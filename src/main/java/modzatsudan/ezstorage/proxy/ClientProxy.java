package modzatsudan.ezstorage.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;

import modzatsudan.ezstorage.gui.client.GuiStorageCore;
import modzatsudan.ezstorage.init.EZBlocks;
import modzatsudan.ezstorage.init.EZItems;
import modzatsudan.ezstorage.registry.ClientRegistryHelper;

/** The mod's client proxy */
public class ClientProxy extends CommonProxy {

    @Override
    public void initRegistryEvents() {
        super.initRegistryEvents();
        MinecraftForge.EVENT_BUS.register(new ClientRegistryHelper());
    }

    @Override
    public void registerRenders() {
        EZBlocks.registerRenders();
        EZItems.registerRenders();
    }

    @Override
    public EntityPlayer getClientPlayer() {
        return Minecraft.getMinecraft().player;
    }

    @Override
    public void markGuiDirty() {
        GuiScreen scr = Minecraft.getMinecraft().currentScreen;
        if (scr instanceof GuiStorageCore) {
            GuiStorageCore gui = (GuiStorageCore) scr;
            gui.markFilterUpdate();
        }
    }
}
