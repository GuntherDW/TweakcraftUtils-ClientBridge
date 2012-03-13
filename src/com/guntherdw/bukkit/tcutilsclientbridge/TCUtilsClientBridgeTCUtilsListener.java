package com.guntherdw.bukkit.tcutilsclientbridge;

import com.guntherdw.bukkit.tweakcraft.Events.TweakcraftUtilsEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * @author GuntherDW
 */
public class TCUtilsClientBridgeTCUtilsListener implements Listener {

    protected TCUtilsClientBridgePlugin plugin;

    public TCUtilsClientBridgeTCUtilsListener(TCUtilsClientBridgePlugin instance) {
        this.plugin = instance;
    }


    @EventHandler
    public void onTweakcraftUtilsEvent(TweakcraftUtilsEvent event) {
        TweakcraftUtilsEvent.Action action = event.getAction();
        switch(action) {
            case CHATMODE_CHANGED:
                plugin.sendChatMode(event.getPlayer().getBukkitPlayerSafe());
                break;
            case NICK_CHANGED:
                for(Player p : plugin.getServer().getOnlinePlayers()) {
                    plugin.getPlayerListener().sendPlayerInfo(p, event.getPlayer().getBukkitPlayerSafe().getName(), true);
                }
                break;
            case RELOAD_INFO:
                plugin.getPlayerListener().reloadInfo();
                break;
        }
    }

}
