package modzatsudan.ezstorage.init;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import modzatsudan.ezstorage.config.EZConfig;
import modzatsudan.ezstorage.item.EZItem;
import modzatsudan.ezstorage.item.ItemDolly;
import modzatsudan.ezstorage.item.ItemKey;
import modzatsudan.ezstorage.registry.IRegistryItem;
import modzatsudan.ezstorage.registry.RegistryHelper;
import modzatsudan.ezstorage.util.JointList;

/** Mod items */
public class EZItems {

    private static JointList<IRegistryItem> items;

    public static void mainRegistry() {
        items = new JointList();
        init();
        register();
    }

    public static EZItem key;
    public static EZItem dolly_basic;
    public static EZItem dolly_super;

    private static void init() {
        items.join(
                key = new ItemKey(),
                dolly_basic = new ItemDolly(6, "dolly"),
                dolly_super = new ItemDolly(16, "dolly_super"));
        if (!EZConfig.enableSecurity) items.remove(key); // security disabled
        if (!EZConfig.enableDolly) {
            items.remove(dolly_basic); // dollies disabled
            items.remove(dolly_super);
        }
    }

    private static void register() {
        RegistryHelper.registerItems(items);
    }

    /** Register model information */
    @SideOnly(Side.CLIENT)
    public static void registerRenders() {
        for (IRegistryItem item : items) {
            item.registerRender();
        }
    }
}
