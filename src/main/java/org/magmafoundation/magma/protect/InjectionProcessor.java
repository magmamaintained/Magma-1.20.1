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

package org.magmafoundation.magma.protect;

import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.IModInfo;
import org.jetbrains.annotations.Nullable;
import org.magmafoundation.magma.common.utils.ShortenedStackTrace;

import java.util.Optional;

public class InjectionProcessor {

    public static @Nullable String getErroringMixin(Throwable error) {
        Throwable cause = ShortenedStackTrace.findCause(error);
        String message = cause.getMessage();

        //Special case, the function does not get references after the .json, so we have to grab it from the message
        String annot = "annotation on";
        if (message.contains(annot)) {
            String function = getRangeFromChars(annot, " could not find", message);
            if (function == null)
                return null;

            String json = getJsonFromString(message);
            if (json == null)
                return null;

            return json + ":" + function;
        }

        return getJsonFromString(message);
    }

    private static @Nullable String getRangeFromChars(String startIndex, String endIndex, String message) {
        int index = message.indexOf(startIndex) + startIndex.length() + 1; //begin after the specified start index + 1 to skip the space
        try {
            return message.substring(index, message.indexOf(endIndex, index));
        } catch (StringIndexOutOfBoundsException e) {
            return null;
        }
    }

    private static @Nullable String getJsonFromString(String message) {
        int index = message.indexOf(".json"); //should be required in all mixin errors
        if (index == -1)
            return null;

        int start = message.substring(0, index).lastIndexOf(" "); //gets the space before the json name -> ex. "( )MODHERE.mixins.json "
        if (start == -1)
            return null;

        int end = message.indexOf(" ", index); //gets the space after the json name -> ex. " MODHERE.mixins.json( )"
        if (end == -1)
            return null;

        String raw = message.substring(start + 1, end); //gets the json name -> ex. "MODHERE.mixins.json"

        if (raw.contains("(")) //if the mixin inject references a class, it will be cut off -> ex. "MODHERE.mixins.json:doThing(Lnet/minecraft/entity/Entity;)V"
            raw = raw.substring(0, raw.indexOf("("));

        return raw;
    }

    public static @Nullable String getModID(Throwable error) {
        String file = getErroringMixin(error);
        if (file == null)
            return null;

        int index = file.indexOf(".mixins.json");

        if (index == -1)
            index = file.indexOf(".mixin.json");

        if (index == -1)
            return null;

        return file.substring(0, index);
    }

    public static @Nullable IModInfo getModInfo(Throwable error) {
        ModList modList = ModList.get();
        if (modList == null)
            return null;

        String modid = getModID(error);
        if (modid == null)
            return null;

        Optional<? extends ModContainer> modContainerById = modList.getModContainerById(modid);
        return modContainerById.map(ModContainer::getModInfo).orElse(null);
    }
}
