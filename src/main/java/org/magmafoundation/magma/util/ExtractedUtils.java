package org.magmafoundation.magma.util;

import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.Entity;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_20_R1.util.LazyPlayerSet;
import org.bukkit.craftbukkit.v1_20_R1.util.Waitable;
import org.bukkit.event.player.PlayerChatEvent;

public class ExtractedUtils {
    
    public static Runnable serverGamePacketListenerImpl(CraftPlayer player, String conversationInput) {
        return () -> player.acceptConversationInput(conversationInput);
    }
    
    public static Waitable<?> serverGamePacketListenerImpl(ServerGamePacketListenerImpl serverGamePacketListener, String s) {
        return new Waitable<>() {
            @Override
            protected Object evaluate() {
                serverGamePacketListener.disconnect(s);
                return null;
            }
        };
    }
    
    public static Waitable<?> serverGamePacketListenerImpl(ServerGamePacketListenerImpl serverGamePacketListener, PlayerChatEvent queueEvent, String originalMessage, String originalFormat, PlayerChatMessage original) {
        return new Waitable<>() {
            @Override
            protected Object evaluate() {
                org.bukkit.Bukkit.getPluginManager().callEvent(queueEvent);
                if (queueEvent.isCancelled()) {
                    return null;
                }
                String message = String.format(queueEvent.getFormat(), queueEvent.getPlayer().getDisplayName(), queueEvent.getMessage());
                if (((LazyPlayerSet) queueEvent.getRecipients()).isLazy()) {
                    if (!org.spigotmc.SpigotConfig.bungee && originalFormat.equals(queueEvent.getFormat()) && originalMessage.equals(queueEvent.getMessage()) && queueEvent.getPlayer().getName().equalsIgnoreCase(queueEvent.getPlayer().getDisplayName())) { // Spigot
                        serverGamePacketListener.getServer().getPlayerList().broadcastChatMessage(original, serverGamePacketListener.player, ChatType.bind(ChatType.CHAT, (Entity) serverGamePacketListener.player));
                        return null;
                    }
                    for (ServerPlayer recipient : serverGamePacketListener.getServer().getPlayerList().players) {
                        recipient.getBukkitEntity().sendMessage(serverGamePacketListener.player.getUUID(), message);
                    }
                } else {
                    for (org.bukkit.entity.Player player : queueEvent.getRecipients()) {
                        player.sendMessage(serverGamePacketListener.player.getUUID(), message);
                    }
                }
                serverGamePacketListener.getServer().console.sendMessage(message);
                return null;
            }};
    }
}
