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

//Utility class for wrapping random values, this is used to bypass LuckPerms grabbing the wrong dispatcher on boot
public class Wrapped<T> {

    private final T value;

    private Wrapped(T value) {
        this.value = value;
    }

    public static <T> Wrapped<T> wrap(T value) {
        return new Wrapped<>(value);
    }

    public T unwrap() {
        return value;
    }
}
