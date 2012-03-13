/*
 * Copyright (c) 2012 GuntherDW
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package com.guntherdw.bukkit.tcutilsclientbridge;

import org.bukkit.Chunk;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * @author GuntherDW
 */
public class TCUtilsClientBridgePluginMessageListener implements PluginMessageListener {

    private TCUtilsClientBridgePlugin plugin;
    //

    public TCUtilsClientBridgePluginMessageListener(TCUtilsClientBridgePlugin instance) {
        this.plugin = instance;
    }

    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (channel.equals(plugin.getMessenger_InfDura_channel())) {
            if (message.length > 0) {
                if (message[0] == (byte) 26) {
                    plugin.getPlayerListener().sendToolDuraMode(player, player.getWorld());
                }

            }
        } else if (channel.equals(plugin.getMessenger_ImprovedChat_channel())) {
            if (message.length > 0)
                if (message[0] == (byte) 26) {
                    plugin.sendChatMode(player);
                }
        } else if (channel.equals(plugin.getMessager_tcutils_nick_channel())) {
            if (message.length > 0) {

                Chunk chunk = player.getLocation().getChunk();

                if (message[0] == (byte) 51) {
                    plugin.getPlayerListener().sendNickList(player);

                    Set<Animals> animalsToSend = new HashSet<Animals>();
                    for (Entity ent : chunk.getEntities()) {
                        if (ent instanceof Animals) {
                            Animals animal = (Animals) ent;
                            if (!animalsToSend.contains(animal))
                                animalsToSend.add(animal);
                        }
                    }

                    if (animalsToSend.size() > 0) {
                        ByteArrayOutputStream bos = new ByteArrayOutputStream();
                        bos.write((byte) 70);
                        for (Animals animal : animalsToSend) {
                            try {
                                bos.write((animal.getEntityId() + "," + animal.getAge() + "," + animal.getAgeLock()).getBytes("UTF-8"));
                                bos.write((byte) 0);
                            } catch (IOException e) {
                                ;
                            }
                        }

                        player.sendPluginMessage(plugin, plugin.getMessager_tcutils_nick_channel(), bos.toByteArray());
                    }
                } else if (message[0] == (byte) 53) {
                    String requested = "";
                    StringBuilder sb = new StringBuilder();
                    for (int i = 1; i < message.length; i++) {
                        sb.append((char) message[i]);
                    }
                    requested = sb.toString();

                    if (!requested.equals("")) plugin.getPlayerListener().sendPlayerInfo(player, requested, true);

                }

            }
        }

    }
}
