package modzatsudan.ezstorage;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

import gregtech.GTInternalTags;
import modzatsudan.ezstorage.config.EZConfig;
import modzatsudan.ezstorage.events.CoreEvents;
import modzatsudan.ezstorage.events.SecurityEvents;
import modzatsudan.ezstorage.gui.GuiHandler;
import modzatsudan.ezstorage.old.network.EZNetwork;
import modzatsudan.ezstorage.old.proxy.CommonProxy;
import modzatsudan.ezstorage.old.ref.EZTab;
import modzatsudan.ezstorage.old.ref.Log;
import modzatsudan.ezstorage.old.ref.RefStrings;
import modzatsudan.ezstorage.old.util.EZStorageUtils;

/** EZStorage main mod class */
@Mod(modid = Tags.MODID,
     name = Tags.MODNAME,
     version = Tags.VERSION,
     dependencies = GTInternalTags.DEP_VERSION_STRING + "after:gregtechfoodoption",
     acceptedMinecraftVersions = "[1.12, 1.13)")
public class EZStorage {

    @Mod.Instance(Tags.MODID)
    public static EZStorage instance;

    @SidedProxy(clientSide = RefStrings.CLIENT_PROXY, serverSide = RefStrings.SERVER_PROXY)
    public static CommonProxy proxy;

    public static SimpleNetworkWrapper nw;
    public static Configuration config;

    public EZTab creativeTab;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        proxy.initRegistryEvents();
        config = new Configuration(event.getSuggestedConfigurationFile());
        EZConfig.syncConfig();
        this.creativeTab = new EZTab();
        NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());
        nw = EZNetwork.registerNetwork();
        MinecraftForge.EVENT_BUS.register(new CoreEvents());
        MinecraftForge.EVENT_BUS.register(new SecurityEvents());
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {}

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        EZStorageUtils.getModNameFromID(Tags.MODID); // build the mod map
        Log.logger.info("Loading complete.");
    }
}
