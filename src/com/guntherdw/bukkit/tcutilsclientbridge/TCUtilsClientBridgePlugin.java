package com.guntherdw.bukkit.tcutilsclientbridge;

import com.guntherdw.bukkit.tweakcraft.Chat.ChatMode;
import com.guntherdw.bukkit.tweakcraft.TweakcraftUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.Messenger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * @author GuntherDW
 */
public class TCUtilsClientBridgePlugin extends JavaPlugin {

    private Messenger messenger;

    private TweakcraftUtils tcutilsInstance = null;
    private TCUtilsClientBridgePlayerListener playerListener = new TCUtilsClientBridgePlayerListener(this);
    private TCUtilsClientBridgeWorldListener worldListener = new TCUtilsClientBridgeWorldListener(this);
    private TCUtilsClientBridgePluginMessageListener pluginMessageListener = new TCUtilsClientBridgePluginMessageListener(this);
    private TCUtilsClientBridgeTCUtilsListener tcUtilsListener = new TCUtilsClientBridgeTCUtilsListener(this);

    private final String messager_infdura_channel = "mod_InfDura";
    private final String messager_impchat_channel = "ImprovedChat";
    private final String messager_tcutils_nick_channel = "TCUtils:nick";

    private Logger log = Logger.getLogger("Minecraft");

    public void onDisable() {

    }

    public void onEnable() {

        PluginDescriptionFile pdfFile = this.getDescription();

        log.info("[" + pdfFile.getName() + "] Loading " + pdfFile.getName() + " version " + pdfFile.getVersion() + "!");

        messenger = getServer().getMessenger();

        tcutilsInstance = TweakcraftUtils.getInstance();
        if (tcutilsInstance == null) {
            getServer().getPluginManager().disablePlugin(this);
            log.warning("[" + pdfFile.getName() + "] Couldn't find valid TweakcraftUtils instance, disabling plugin!");
            return;
        } else {
            log.info("[" + pdfFile.getName() + "] Successfully registered with TweakcraftUtils (version " + tcutilsInstance.getVersion() + ")!");
        }

        this.registerEvents();
        playerListener.reloadInfo();

        messenger.registerOutgoingPluginChannel(this, messager_impchat_channel);
        messenger.registerIncomingPluginChannel(this, messager_impchat_channel, pluginMessageListener);

        messenger.registerOutgoingPluginChannel(this, messager_infdura_channel);
        messenger.registerIncomingPluginChannel(this, messager_infdura_channel, pluginMessageListener);

        messenger.registerOutgoingPluginChannel(this, messager_tcutils_nick_channel);
        messenger.registerIncomingPluginChannel(this, messager_tcutils_nick_channel, pluginMessageListener);

        log.info("[" + pdfFile.getName() + "] " + pdfFile.getName() + " enabled!");
    }

    public void registerEvents() {
        PluginManager manager = getServer().getPluginManager();

        // manager.registerEvent(Event.Type.CHUNK_LOAD, worldListener, Event.Priority.Monitor, this);
        manager.registerEvents(worldListener, this);
        manager.registerEvents(playerListener, this);
        manager.registerEvents(tcUtilsListener, this);
        /* manager.registerEvent(Event.Type.CHUNK_LOAD, worldListener, Event.Priority.Monitor, this);
        manager.registerEvent(Event.Type.CHUNK_LOAD, worldListener, Event.Priority.Monitor, this); */
    }

    public String getMessenger_InfDura_channel() {
        return messager_infdura_channel;
    }

    public String getMessenger_ImprovedChat_channel() {
        return messager_impchat_channel;
    }

    public String getMessager_tcutils_nick_channel() {
        return messager_tcutils_nick_channel;
    }

    protected TweakcraftUtils getTweakcraftUtilsInstance() {
        return tcutilsInstance;
    }

    public TCUtilsClientBridgePlayerListener getPlayerListener() {
        return playerListener;
    }

    public void sendChatMode(Player player) {
        ChatMode cm = tcutilsInstance.getChathandler().getPlayerChatMode(player);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        String ChatModeString = null;

        if (cm == null) ChatModeString = "null";
        else ChatModeString = "[" + cm.getPrefix() + "]";

        try {
            bytes.write((byte) 25);
            bytes.write(ChatModeString.replace("§", "&c").getBytes());
        } catch (IOException e) {
            getLogger().warning("[TweakcraftUtils] Exception thrown in sendCUIChatMode, please report!");
            e.printStackTrace();
        }

        player.sendPluginMessage(this, messager_impchat_channel, bytes.toByteArray());

    }
}
