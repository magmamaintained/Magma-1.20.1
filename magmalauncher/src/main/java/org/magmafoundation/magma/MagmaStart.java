/*
 * Magma Server
 * Copyright (C) 2019-${year}.
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

package org.magmafoundation.magma;/*
 * Magma Server
 * Copyright (C) 2019-2022.
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

import org.magmafoundation.magma.common.betterui.BetterUI;
import org.magmafoundation.magma.common.utils.JarTool;
import org.magmafoundation.magma.common.utils.SystemType;
import org.magmafoundation.magma.installer.MagmaInstaller;
import org.magmafoundation.magma.updater.MagmaUpdater;
import org.magmafoundation.magma.utils.BootstrapLauncher;
import org.magmafoundation.magma.utils.ServerInitHelper;

import java.io.File;
import java.lang.reflect.Array;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.magmafoundation.magma.common.MagmaConstants.*;


/**
 * Project: Magma
 *
 * @author Malcolm (M1lc0lm) / Hexeption
 * @date 03.07.2022 - 17:19
 */
public class MagmaStart {

    private static String[] args;

    public static void main(String[] args) throws Exception {
        MagmaStart.args = args;

        if (containsArg("-noui"))
            BetterUI.setEnabled(false);

        if (containsArg("-nologo"))
            BetterUI.setEnableBigLogo(false);

        Path eula = Paths.get("eula.txt");
        if (containsArg("-accepteula"))
            BetterUI.forceAcceptEULA(eula);

        boolean enableUpdate = !containsArg("-dau");

        containsArg("-nojline"); //For some reason when passing -nojline to the console the whole thing crashes, remove this

        BetterUI.printTitle(NAME, BRAND, System.getProperty("java.version") + " (" + System.getProperty("java.vendor") + ")", VERSION, BUKKIT_VERSION, FORGE_VERSION);
        System.out.println("If the server crashes while installing, try removing the libraries folder and launching the server again.");

        if(!BetterUI.checkEula(eula)) System.exit(0);

        List<String> launchArgs = JarTool.readFileLinesFromJar("data/" + (SystemType.getOS().equals(SystemType.OS.WINDOWS) ? "win" : "unix") + "_args.txt");
        List<String> forgeArgs = new ArrayList<>();
        launchArgs.stream().filter(s -> s.startsWith("--launchTarget") || s.startsWith("--fml.forgeVersion") || s.startsWith("--fml.mcVersion") || s.startsWith("--fml.forgeGroup") || s.startsWith("--fml.mcpVersion")).toList().forEach(arg -> {
            forgeArgs.add(arg.split(" ")[0]);
            forgeArgs.add(arg.split(" ")[1]);
        });

        MagmaInstaller.run();

        ServerInitHelper.init(launchArgs);

        ServerInitHelper.addToPath(new File("libraries/com/google/code/gson/gson/2.10/gson-2.10.jar").toPath());
        ServerInitHelper.addToPath(new File("libraries/org/yaml/snakeyaml/1.33/snakeyaml-1.33.jar").toPath());
        if (enableUpdate)
            MagmaUpdater.checkForUpdates();

        String[] invokeArgs = Stream.concat(forgeArgs.stream(), Stream.of(MagmaStart.args)).toArray(String[]::new);
        BootstrapLauncher.startServer(invokeArgs);
    }

    private static boolean containsArg(String arg) {
        if (Arrays.stream(MagmaStart.args).anyMatch(s -> s.equalsIgnoreCase(arg))) {
            MagmaStart.args = remove(MagmaStart.args, arg);
            return true;
        }
        return false;
    }

    private static String[] remove(String[] array, String element) {
        if (array.length > 0) {
            int index = -1;
            for (int i = 0; i < array.length; i++) {
                if (array[i].equalsIgnoreCase(element)) {
                    index = i;
                    break;
                }
            }
            if (index >= 0) {
                String[] copy = (String[]) Array.newInstance(array.getClass()
                        .getComponentType(), array.length - 1);
                if (copy.length > 0) {
                    System.arraycopy(array, 0, copy, 0, index);
                    System.arraycopy(array, index + 1, copy, index, copy.length - index);
                }
                return copy;
            }
        }
        return array;
    }
}
