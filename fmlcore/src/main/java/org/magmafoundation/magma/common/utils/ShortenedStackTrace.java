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

package org.magmafoundation.magma.common.utils;

public class ShortenedStackTrace {

    private final Throwable cause;
    private final StackTraceElement[] stackTrace;
    private final int maxElements;

    public ShortenedStackTrace(Throwable error, int maxElements) {
        this.cause = findCause(error);
        this.stackTrace = cause.getStackTrace();
        this.maxElements = maxElements;
    }

    public static Throwable findCause(Throwable error) {
        while (error.getCause() != null) {
            error = error.getCause();
        }
        return error;
    }

    public void print() {
        if (cause.getMessage() != null || !cause.getMessage().isEmpty())
            System.err.println(cause.getMessage());
        for (int i = 0; i < maxElements; i++) {
            System.err.println(stackTrace[i]);
        }
    }
}
