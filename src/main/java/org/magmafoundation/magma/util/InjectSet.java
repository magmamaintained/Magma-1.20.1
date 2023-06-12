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

package org.magmafoundation.magma.util;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

public class InjectSet {

    private final IMixinInfo info;
    private final Throwable t;

    public InjectSet(IMixinInfo info, Throwable t) {
        this.info = info;
        this.t = t;
    }

    @Contract(value = "_, _ -> new", pure = true)
    public static @NotNull InjectSet of(IMixinInfo info, Throwable t) {
        return new InjectSet(info, t);
    }

    public @Nullable IMixinInfo getInfo() {
        return info;
    }

    public Throwable getThrowable() {
        return t;
    }
}
