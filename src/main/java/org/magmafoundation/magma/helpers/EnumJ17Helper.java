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

package org.magmafoundation.magma.helpers;

import io.izzel.arclight.api.Unsafe;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class EnumJ17Helper {

    @SuppressWarnings("unchecked")
    public static <T> T addEnum(Class<T> cl, String name, List<Class<?>> ctorTypes, List<Object> ctorParams) {
        try {
            Unsafe.ensureClassInitialized(cl);
            Field field = cl.getDeclaredField("$VALUES");
            Object base = Unsafe.staticFieldBase(field);
            long offset = Unsafe.staticFieldOffset(field);
            T[] arr = (T[]) Unsafe.getObject(base, offset);
            T[] newArr = (T[]) Array.newInstance(cl, arr.length + 1);
            System.arraycopy(arr, 0, newArr, 0, arr.length);

            T newInstance = makeEnum(cl, name, arr.length, ctorTypes, ctorParams);

            newArr[arr.length] = newInstance;
            Unsafe.putObject(base, offset, newArr);
            reset(cl);
            return newInstance;
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> void addEnums(Class<T> cl, List<T> list) {
        if (list.isEmpty())
            return;

        try {
            Field field = cl.getDeclaredField("$VALUES");
            Object base = Unsafe.staticFieldBase(field);
            long offset = Unsafe.staticFieldOffset(field);
            T[] arr = (T[]) Unsafe.getObject(base, offset);
            T[] newArr = (T[]) Array.newInstance(cl, arr.length + list.size());
            System.arraycopy(arr, 0, newArr, 0, arr.length);
            for (int i = 0; i < list.size(); i++) {
                newArr[arr.length + i] = list.get(i);
            }
            Unsafe.putObject(base, offset, newArr);
            reset(cl);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T makeEnum(Class<T> cl, String name, int ordinal, List<Class<?>> ctorTypes,
            List<Object> ctorParams) {
        try {
            Unsafe.ensureClassInitialized(cl);
            List<Class<?>> ctor = new ArrayList<>(ctorTypes.size() + 2);
            ctor.add(String.class);
            ctor.add(int.class);
            ctor.addAll(ctorTypes);
            MethodHandle constructor = Unsafe.lookup().findConstructor(cl, MethodType.methodType(void.class, ctor));
            List<Object> param = new ArrayList<>(ctorParams.size() + 2);
            param.add(name);
            param.add(ordinal);
            param.addAll(ctorParams);
            return (T) constructor.invokeWithArguments(param);
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }

    private static final long[] ENUM_CACHE_OFFSETS;

    static {
        List<Long> offsets = new ArrayList<>();
        for (String s : new String[] { "enumConstantDirectory", "enumConstants", "enumVars" }) {
            try {
                Field field = Class.class.getDeclaredField(s);
                offsets.add(Unsafe.objectFieldOffset(field));
            } catch (NoSuchFieldException ignored) {}
        }
        if (offsets.isEmpty()) {
            throw new IllegalStateException("Unable to find offsets for Enum");
        }
        long[] arr = new long[offsets.size()];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = offsets.get(i);
        }
        ENUM_CACHE_OFFSETS = arr;
    }

    private static void reset(Class<?> cl) {
        for (long offset : ENUM_CACHE_OFFSETS) {
            Unsafe.putObjectVolatile(cl, offset, null);
        }
    }
}