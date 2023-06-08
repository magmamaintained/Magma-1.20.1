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

package org.magmafoundation.magma.common.betterui;

import java.util.Scanner;
import org.magmafoundation.magma.common.utils.ShortenedStackTrace;

import java.io.*;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BetterUI {

    private static boolean enabled = true,  enableBigLogo = true;

    private static final String bigLogo =
            "    __  ___                           \n" +
                    "   /  |/  /___ _____ _____ ___  ____ _\n" +
                    "  / /|_/ / __ `/ __ `/ __ `__ \\/ __ `/\n" +
                    " / /  / / /_/ / /_/ / / / / / / /_/ / \n" +
                    "/_/  /_/\\__,_/\\__, /_/ /_/ /_/\\__,_/  \n" +
                    "             /____/                   ";

    public static void printTitle(String name, String brand, String javaVersion, String version, String bukkitVersion, String forgeVersion) {
        if (!enabled)
            return;

        String[] split = forgeVersion.split("-");
        if (split.length > 1)
            forgeVersion = forgeVersion.substring(forgeVersion.indexOf("-") + 1, forgeVersion.lastIndexOf("-"));

        if (enableBigLogo)
            System.out.println(bigLogo);
        else System.out.println(name);
        System.out.println("Copyright (c) " + new SimpleDateFormat("yyyy").format(new Date()) + " " + brand + ".");
        System.out.println("--------------------------------------");
        System.out.println("Running on Java " + javaVersion);
        System.out.println(name + " version   " + version); //Spacing here is intentional
        System.out.println("Bukkit version  " + bukkitVersion);
        System.out.println("Forge version   " + forgeVersion);
        System.out.println("--------------------------------------");
    }

    public static void printError(String errorType, String cause, ShortenedStackTrace... trace) {
        if (!enabled)
            return;
        System.err.println("------------------------------------------------------------");
        System.err.println("A critical error has occurred and your server was shut down.");
        System.err.println("Please send this info to the Magma team on Discord or GitLab");
        System.err.println("Please also include the full log file!                      ");
        System.err.println("------------------------------------------------------------");
        System.err.println("Error type: " + errorType);
        System.err.println("Caused by: " + (cause == null ? "Unknown reason" : cause));
        System.err.println();
        System.err.println("Short stack trace(s):");
        for (ShortenedStackTrace s : trace) {
            s.print();
            System.err.println();
        }
        System.err.println("------------------------------------------------------------");
    }

    public static boolean checkEula(Path path_to_eula) throws IOException {
        File file = path_to_eula.toFile();
        ServerEula eula = new ServerEula(path_to_eula);

        if (!enabled)
            return eula.hasAgreedToEULA();

        if (!eula.hasAgreedToEULA()) {
            System.out.println("WARNING: It appears you have not agreed to the EULA.\nPlease read the EULA (https://account.mojang.com/documents/minecraft_eula) and type 'yes' to continue.");
            System.out.print("Do you accept? (yes/no): ");

            int wrong = 0;

            Scanner console = new Scanner(System.in);
            while (true) {
                String answer = console.nextLine();
                if (answer == null || answer.isBlank()) {
                    if (wrong++ >= 2) {
                        System.err.println("You have typed the wrong answer too many times. Exiting.");
                        return false;
                    }
                    System.out.println("Please type 'yes' or 'no'.");
                    System.out.print("Do you accept? (yes/no): ");
                    continue;
                }

                switch (answer.toLowerCase()) {
                    case "y", "yes" -> {
                        file.delete();
                        file.createNewFile();
                        try (FileWriter writer = new FileWriter(file)) {
                            writer.write("eula=true");
                        }
                        return true;
                    }
                    case "n", "no" -> {
                        System.err.println("You must accept the EULA to continue. Exiting.");
                        return false;
                    }
                    default -> {
                        if (wrong++ >= 2) {
                            System.err.println("You have typed the wrong answer too many times. Exiting.");
                            return false;
                        }
                        System.out.println("Please type 'yes' or 'no'.");
                        System.out.print("Do you accept? (yes/no): ");
                    }
                }
            }

        } else return true;
    }

    public static void forceAcceptEULA(Path path_to_eula) throws IOException {
        File file = path_to_eula.toFile();
        if (file.exists())
            file.delete();
        file.createNewFile();
        try (FileWriter writer = new FileWriter(file)) {
            writer.write("eula=true");
        }
    }

    public static void setEnabled(boolean enabled) {
        BetterUI.enabled = enabled;
    }

    public static void setEnableBigLogo(boolean enableBigLogo) {
        BetterUI.enableBigLogo = enableBigLogo;
    }

    public static boolean isEnabled() {
        return enabled;
    }
}