/*
 * Magma Server
 * Copyright (C) 2019-2023.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.magmafoundation.magma.craftbukkit;

import net.minecraft.network.Connection;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.util.CraftIconCache;
import org.bukkit.entity.Player;
import org.bukkit.event.server.ServerListPingEvent;

import java.net.InetSocketAddress;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Moved from ServerStatusNetHandler as it was causing patch errors.
 */
public class ServerListPingEventImpl extends ServerListPingEvent {

	public CraftIconCache icon;
	public Object[] players;

	public ServerListPingEventImpl(Connection connection, MinecraftServer server) {
		super(connection.hostname, ((InetSocketAddress) connection.getRemoteAddress()).getAddress(), server.getMotd(), server.getPlayerList().getMaxPlayers());
		this.icon = ((CraftServer) Bukkit.getServer()).getServerIcon();
		this.players = server.getPlayerList().players.toArray();
	}

	@Override
	public void setServerIcon(org.bukkit.util.CachedServerIcon icon) {
		if (!(icon instanceof CraftIconCache)) {
			throw new IllegalArgumentException(icon + " was not created by " + CraftServer.class);
		}
		this.icon = (CraftIconCache) icon;
	}

	public Object[] getPlayers() {
		return players;
	}

	@Override
	public Iterator<Player> iterator() throws UnsupportedOperationException {
		return new Iterator<Player>() {
			int i;
			int ret = Integer.MIN_VALUE;
			ServerPlayer player;

			@Override
			public boolean hasNext() {
				if (this.player != null) {
					return true;
				}
				final Object[] currentPlayers = players;
				for (int length = currentPlayers.length, i = this.i; i < length; ++i) {
					final ServerPlayer player = (ServerPlayer) currentPlayers[i];
					if (player != null) {
						this.i = i + 1;
						this.player = player;
						return true;
					}
				}
				return false;
			}

			@Override
			public Player next() {
				if (!this.hasNext()) {
					throw new NoSuchElementException();
				}
				final ServerPlayer player = this.player;
				this.player = null;
				this.ret = this.i - 1;
				return player.getBukkitEntity();
			}

			@Override
			public void remove() {
				final Object[] currentPlayers = players;
				final int i = this.ret;
				if (i < 0 || currentPlayers[i] == null) {
					throw new IllegalStateException();
				}
				currentPlayers[i] = null;
			}
		};
	}


}
