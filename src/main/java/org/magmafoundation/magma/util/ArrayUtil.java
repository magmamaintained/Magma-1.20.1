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

import java.util.function.IntFunction;

public class ArrayUtil {

    public static Object[] append(Object[] array, Object obj) {
        Object[] newArray = new Object[array.length + 1];
        newArray[array.length] = obj;
        System.arraycopy(array, 0, newArray, 0, array.length);
        return newArray;
    }

    public static Object[] prepend(Object[] array, Object obj) {
        Object[] newArray = new Object[array.length + 1];
        newArray[0] = obj;
        System.arraycopy(array, 0, newArray, 1, array.length);
        return newArray;
    }

    public static <T> T[] prepend(T[] array, T obj, IntFunction<T[]> factory) {
        T[] newArray = factory.apply(array.length + 1);
        newArray[0] = obj;
        System.arraycopy(array, 0, newArray, 1, array.length);
        return newArray;
    }
}