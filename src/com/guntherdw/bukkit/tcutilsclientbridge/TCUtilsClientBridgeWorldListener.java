package com.guntherdw.bukkit.tcutilsclientbridge;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.*;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.world.ChunkLoadEvent;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author GuntherDW
 */
public class TCUtilsClientBridgeWorldListener implements Listener {

    private TCUtilsClientBridgePlugin plugin;

    public TCUtilsClientBridgeWorldListener(TCUtilsClientBridgePlugin instance) {
        this.plugin = instance;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {

        List<byte[]> packets = new ArrayList<byte[]>();
        Set<Animals> livingentities = new HashSet<Animals>();

        Player p = event.getPlayer();
        Set<Chunk> chunks = new HashSet<Chunk>();
        int viewdistance = plugin.getServer().getViewDistance();
        Chunk origChunk = p.getLocation().getChunk();
        int origcz = origChunk.getZ();
        int origcx = origChunk.getX();
        for(int cx = origcx - viewdistance; cx<origcx + viewdistance; cx++) {
            for(int cz = origcz - viewdistance; cz<origcz + viewdistance; cz++) {
                chunks.add(p.getLocation().getWorld().getChunkAt(cx, cz));
            }
        }
        for(Chunk c : chunks) {
            if(c.isLoaded()) {
                for (Entity ent : c.getEntities()) {
                    if (ent instanceof Animals) {
                        if(((Animals)ent).getAgeLock())
                            livingentities.add((Animals) ent);
                    }
                }
                if (livingentities.size() > 0) {
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    bos.write((byte) 73);
                    String parsedString = "";
                    for (Animals animal : livingentities) {
                        try {
                            parsedString = animal.getEntityId() + "," + animal.getAge() + "," + animal.getAgeLock();
                            bos.write(parsedString.getBytes("UTF-8"));
                            bos.write((byte) 0);
                        } catch (IOException e) {
                            ;
                        }
                    }
                    packets.add(bos.toByteArray());
                }
            }

        }


        if (packets.size() > 0)
            for (byte[] packet : packets)
                for (Player pl : plugin.getServer().getOnlinePlayers())
                    pl.sendPluginMessage(plugin, plugin.getMessager_tcutils_nick_channel(), packet);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onCreaturespawn(CreatureSpawnEvent event) {
        if(event.isCancelled()) return;

        LivingEntity ent = event.getEntity();
        if(!(ent instanceof Animals /* || ent instanceof Villager */)) return;

        Animals animal = (Animals) ent;
        if(!animal.getAgeLock()) return;

        List<byte[]> packets = new ArrayList<byte[]>();


        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bos.write((byte) 73);
        String parsedString = "";
        try {
            parsedString = animal.getEntityId() + "," + animal.getAge() + "," + animal.getAgeLock();
            bos.write(parsedString.getBytes("UTF-8"));
            bos.write((byte) 0);
        } catch (IOException e) {
            ;
        }
        packets.add(bos.toByteArray());


        if (packets.size() > 0)
            for (byte[] packet : packets)
                for (Player pl : plugin.getServer().getOnlinePlayers())
                    pl.sendPluginMessage(plugin, plugin.getMessager_tcutils_nick_channel(), packet);
    }


    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if(event.isCancelled()) return;

        List<byte[]> packets = new ArrayList<byte[]>();
        Set<Animals> livingentities = new HashSet<Animals>();

        Player p = event.getPlayer();
        Set<Chunk> chunks = new HashSet<Chunk>();
        int viewdistance = plugin.getServer().getViewDistance();
        Chunk origChunk = event.getTo().getChunk();
        int origcz = origChunk.getZ();
        int origcx = origChunk.getX();
        for(int cx = origcx - viewdistance; cx<origcx + viewdistance; cx++) {
            for(int cz = origcz - viewdistance; cz<origcz + viewdistance; cz++) {
                chunks.add(event.getTo().getWorld().getChunkAt(cx, cz));
            }
        }
        for(Chunk c : chunks) {
            if(c.isLoaded()) {
                for (Entity ent : c.getEntities()) {
                    if (ent instanceof Animals) {
                        if(((Animals)ent).getAgeLock())
                            livingentities.add((Animals) ent);
                    }
                }
                if (livingentities.size() > 0) {
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    bos.write((byte) 73);
                    String parsedString = "";
                    for (Animals animal : livingentities) {
                        try {
                            parsedString = animal.getEntityId() + "," + animal.getAge() + "," + animal.getAgeLock();
                            bos.write(parsedString.getBytes("UTF-8"));
                            bos.write((byte) 0);
                        } catch (IOException e) {
                            ;
                        }
                    }
                    packets.add(bos.toByteArray());
                }
            }

        }


        if (packets.size() > 0)
            for (byte[] packet : packets)
                for (Player pl : plugin.getServer().getOnlinePlayers())
                    pl.sendPluginMessage(plugin, plugin.getMessager_tcutils_nick_channel(), packet);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onChunkLoad(ChunkLoadEvent event) {

        Chunk c = event.getChunk();
        List<byte[]> packets = new ArrayList<byte[]>();
        Set<BlockState> states = new HashSet<BlockState>();
        Set<Animals> livingentities = new HashSet<Animals>();
        BlockState[] blockStates = c.getTileEntities();
        if(blockStates==null || blockStates.length == 0) return;

        for (BlockState state : blockStates) {
            if (state.getTypeId() == 52) {
                states.add(state);
            }
        }

        for (Entity ent : c.getEntities()) {
            if (ent instanceof Animals) {
                if(((Animals)ent).getAgeLock())
                    livingentities.add((Animals) ent);
            }
        }

        /**
         * NOT NEEDED ANYMORE
         */
        /* if (states.size() > 0) {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bos.write((byte) 80);
            for (BlockState state : states) {
                try {
                    Location loc = state.getBlock().getLocation();
                    CreatureSpawner mobBlock = (CreatureSpawner) state;
                    bos.write((loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ() + "," + mobBlock.getCreatureTypeId()).getBytes("UTF-8"));
                    bos.write((byte) 0);
                } catch (IOException e) {
                    ;
                }
            }
            packets.add(bos.toByteArray());
        } */

        if (livingentities.size() > 0) {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bos.write((byte) 73);
            String parsedString = "";
            for (Animals animal : livingentities) {
                try {
                    parsedString = animal.getEntityId() + "," + animal.getAge() + "," + animal.getAgeLock();
                    bos.write(parsedString.getBytes("UTF-8"));
                    bos.write((byte) 0);
                } catch (IOException e) {
                    ;
                }
            }
            packets.add(bos.toByteArray());
        }

        if (packets.size() > 0)
            for (byte[] packet : packets)
                for (Player p : plugin.getServer().getOnlinePlayers())
                    p.sendPluginMessage(plugin, plugin.getMessager_tcutils_nick_channel(), packet);

    }


}
