package com.guntherdw.bukkit.tcutilsclientbridge;

import com.guntherdw.bukkit.tweakcraft.Events.TweakcraftUtilsEvent;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.plugin.messaging.Messenger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author GuntherDW
 */
public class TCUtilsClientBridgePlayerListener implements Listener {

    private TCUtilsClientBridgePlugin plugin;
    private Map<String, String> capes;
    private Map<String, String> nicks;

    public TCUtilsClientBridgePlayerListener(TCUtilsClientBridgePlugin instance) {
        this.plugin = instance;
        capes = new HashMap<String, String>();
        nicks = new HashMap<String, String>();
    }

    public void reloadInfo() {
        // plugin.getLogger().info("[TweakcratUtils-ClientBridge] Reloading info!");
        this.capes = plugin.getTweakcraftUtilsInstance().getPlayerListener().getCapeURLs();
        this.nicks = plugin.getTweakcraftUtilsInstance().getPlayerListener().getNicks();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
        this.sendToolDuraMode(event.getPlayer(), event.getPlayer().getWorld());
    }

    /**
     * Send the player a mod_InfDura string, setting the new ToolDurabilty mode
     *
     * @param player The player to send the mode to
     * @param world  The world to check the ToolDurability mode
     */
    public void sendToolDuraMode(Player player, World world) {
        byte[] output = new byte[2];
        output[0] = 25;
        output[1] = (byte) (world.getToolDurability() ? 0 : 1);
        // output.length = output.length;
        player.sendPluginMessage(plugin, plugin.getMessenger_InfDura_channel(), output);
    }

    public void sendPlayerInfo(Player player, String request, boolean forceUpdate) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bos.write(forceUpdate ? (byte) 53 : (byte) 52);
        try {
            bos.write((request + "," + plugin.getTweakcraftUtilsInstance().getNickWithColors(request).replace("§", "&c")).getBytes("UTF-8"));
            bos.write((byte) 0);
            player.sendPluginMessage(plugin, plugin.getMessager_tcutils_nick_channel(), bos.toByteArray());
            bos.reset();
            bos = new ByteArrayOutputStream();
            if (capes.containsKey(request)) {
                bos.write(forceUpdate ? (byte) 63 : (byte) 62);
                bos.write((request + "," + capes.get(request)).getBytes("UTF-8"));
                bos.write((byte) 0);
            }

            byte[] packet = bos.toByteArray();

            if (packet.length > 0) player.sendPluginMessage(plugin, plugin.getMessager_tcutils_nick_channel(), packet);
        } catch (IOException ex) {
            return;
        }

    }

    public void sendNickList(Player player) {
        List<byte[]> packets = new ArrayList<byte[]>();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        int maxLength = Messenger.MAX_MESSAGE_SIZE;
        String tempString = null;
        // maxLength /= 100;

        bos.write((byte) 50);

        for (Map.Entry<String, String> nickEntry : nicks.entrySet()) {
            try {
                tempString = nickEntry.getKey() + "," + plugin.getTweakcraftUtilsInstance().getNickWithColors(nickEntry.getKey()).replace("§", "&c");
                if ((bos.size() + tempString.length()) > maxLength) {
                    packets.add(bos.toByteArray());
                    bos.reset();
                    bos = new ByteArrayOutputStream();
                    // bos.reset();
                    bos.write((byte) 52);
                }
                bos.write(tempString.getBytes("UTF-8"));
                bos.write((byte) 0);
            } catch (IOException ex) {
                ;
            }
        }
        if (bos.size() > 0) packets.add(bos.toByteArray());


        // Send capeURL's
        bos.reset();
        bos = new ByteArrayOutputStream();
        bos.write((byte) 60);
        for (Map.Entry<String, String> capeEntry : capes.entrySet()) {
            try {
                tempString = capeEntry.getKey() + "," + capeEntry.getValue();
                if ((bos.size() + tempString.length()) > maxLength) {
                    packets.add(bos.toByteArray());
                    bos.reset();
                    bos = new ByteArrayOutputStream();
                    // bos.reset();
                    bos.write((byte) 62);
                }
                bos.write(tempString.getBytes("UTF-8"));
                bos.write((byte) 0);
            } catch (IOException ex) {
                ;
            }
        }

        if (bos.size() > 0) packets.add(bos.toByteArray());

        for (byte[] packet : packets) {

            player.sendPluginMessage(plugin, plugin.getMessager_tcutils_nick_channel(), packet);
        }
    }
}
