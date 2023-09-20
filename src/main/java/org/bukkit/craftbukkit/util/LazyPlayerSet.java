package org.bukkit.craftbukkit.util;

import com.google.common.base.Preconditions;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.List;

public class LazyPlayerSet extends LazyHashSet<Player> {

    private final MinecraftServer server;

    public LazyPlayerSet(MinecraftServer server) {
        this.server = server;
    }

    @Override
    HashSet<Player> makeReference() {
        Preconditions.checkState(reference == null, "Reference already created!");
        List<ServerPlayer> players = server.getPlayerList().players;
        HashSet<Player> reference = new HashSet<Player>(players.size());
        for (ServerPlayer player : players) {
            reference.add(player.getBukkitEntity());
        }
        return reference;
    }
}
