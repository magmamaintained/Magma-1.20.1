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

import io.izzel.arclight.api.Unsafe;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.function.Function;

public class Enumerations {

    public static <A, B> Enumeration<B> transform(Enumeration<A> enumeration, Function<A, B> mapper) {
        return new Enumeration<B>() {
            @Override
            public boolean hasMoreElements() {
                return enumeration.hasMoreElements();
            }

            @Override
            public B nextElement() {
                return mapper.apply(enumeration.nextElement());
            }
        };
    }

    public static Enumeration<URL> remapped(Enumeration<URL> enumeration) {
        return transform(enumeration, url -> {
            try {
                return new URL("remap:" + url);
            } catch (MalformedURLException e) {
                Unsafe.throwException(e);
                return null;
            }
        });
    }
}