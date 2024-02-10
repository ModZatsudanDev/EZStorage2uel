package modzatsudan.ezstorage.util;

import java.util.List;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.text.TextComponentString;

import modzatsudan.ezstorage.Tags;
import modzatsudan.ezstorage.ref.Log;
import modzatsudan.ezstorage.tileentity.TileEntitySecurityBox.SecurePlayer;

/** Helps with overrides for the security system */
public class SecurityOverrideHelper {

    /** Returns true if the given player is opped and has at least level 2 permissions */
    public static boolean isPlayerOpLv2(EntityPlayerMP p) {
        return getPlayerOpLevel(p) >= 2;
    }

    /** Players with op permission levels over zero are ops. */
    public static int getPlayerOpLevel(EntityPlayerMP p) {
        try {
            return p.getServer().getPlayerList().getOppedPlayers().getEntry(p.getGameProfile()).getPermissionLevel();
        } catch (NullPointerException npe) {
            return 0;
        }
    }

    /** Sends the op intervention notification */
    public static void sendOpNotification(EntityPlayerMP op, List<SecurePlayer> securePlayers) {
        String to = "Operator " + op.getName() + " has overridden your " + Tags.MODNAME + " system lockout.";
        String from = "You have overridden the lockout to the " + Tags.MODNAME + " system owned by " +
                securePlayers.get(0).name + ".";
        sendMessageToSecurePlayers(to, from, securePlayers, op);
        Log.logger.info("Operator " + op.getName() + " has overridden the lockout to the storage system owned by " +
                securePlayers.get(0).name + ".");
    }

    /** Sends a chat message to a list of secure players from another player */
    public static void sendMessageToSecurePlayers(String chat, String chatSender, Iterable<SecurePlayer> list,
                                                  EntityPlayerMP from) {
        PlayerList pl = from.getServer().getPlayerList();
        for (SecurePlayer p : list) {
            EntityPlayerMP match = pl.getPlayerByUsername(p.name);
            if (match != null)
                match.sendMessage(new TextComponentString(chat));
        }
        from.sendMessage(new TextComponentString(chatSender));
    }
}
