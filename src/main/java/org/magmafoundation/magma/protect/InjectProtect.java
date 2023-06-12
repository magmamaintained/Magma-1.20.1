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

import net.minecraftforge.forgespi.language.IModInfo;
import org.jetbrains.annotations.NotNull;
import org.magmafoundation.magma.common.betterui.BetterUI;
import org.magmafoundation.magma.common.utils.ShortenedStackTrace;
import org.magmafoundation.magma.util.InjectSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixins;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.spongepowered.asm.mixin.throwables.MixinError;

import java.util.ArrayList;
import java.util.List;

public class InjectProtect {

    private static final Logger LOGGER = LoggerFactory.getLogger(InjectProtect.class);
    private static final List<InjectSet> errors = new ArrayList<>();
    private static boolean shutdownCalled = false;

    public static void init() {
        LOGGER.info("Booting up InjectProtect");
        Mixins.registerErrorHandlerClass(MixinErrorHandler.class.getCanonicalName());
    }

    public static void onBootErrorCaught(MixinError error) {
        LOGGER.warn("Caught exception during server boot phase, shutting down server", ShortenedStackTrace.findCause(error));
        BetterUI.printError("Mixin related error", InjectionProcessor.getErroringMixin(error), new ShortenedStackTrace(error, 3));
        System.exit(1);
    }

    public static void mixinInjectCaught(IMixinInfo info, Throwable t) {
        LOGGER.warn("Caught mixin injection error!");
        errors.add(InjectSet.of(info, t));
    }

    public static void shutdownCalled() {
        if (shutdownCalled)
            return;
        shutdownCalled = true;

        LOGGER.debug("Processing shutdown request");
        if (errors.isEmpty()) {
            LOGGER.debug("No errors found, shutting down");
            return;
        }

        if (errors.size() == 1) {
            LOGGER.debug("Found 1 error, showing user friendly error");
            Throwable t = errors.get(0).getThrowable();
            BetterUI.printError("Mixin injection error", InjectionProcessor.getErroringMixin(t) + getMod(t), new ShortenedStackTrace(t, 3));
            return;
        }

        LOGGER.debug("Found {} errors, showing user friendly error", errors.size());
        ShortenedStackTrace[] traces = new ShortenedStackTrace[errors.size()];
        String modIDS = "";
        for (int i = 0; i < errors.size(); i++) {
            Throwable t = errors.get(i).getThrowable();
            traces[i] = new ShortenedStackTrace(t, 3);
            modIDS += InjectionProcessor.getErroringMixin(t) + ", ";
        }

        modIDS = modIDS.substring(0, modIDS.length() - 2);

        BetterUI.printError("Mixin injection errors", "Multiple errors: " + modIDS, traces);
    }

    private static @NotNull String getMod(Throwable t) {
        IModInfo modInfo = InjectionProcessor.getModInfo(t);
        return modInfo == null ? "" : " (" + modInfo.getDisplayName() + ")";
    }
}
