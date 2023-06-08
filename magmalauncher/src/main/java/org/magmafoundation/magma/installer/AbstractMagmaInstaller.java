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

package org.magmafoundation.magma.installer;

import org.magmafoundation.magma.MagmaStart;
import org.magmafoundation.magma.common.MagmaConstants;
import org.magmafoundation.magma.common.utils.JarTool;
import org.magmafoundation.magma.common.utils.MD5;

import java.io.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarFile;

/**
 * Project: Magma
 *
 * @author Malcolm (M1lc0lm)
 * @date 03.07.2022 - 17:19
 *
 * Inspired by Shawiiz_z (https://github.com/Shawiizz)
 */
public abstract class AbstractMagmaInstaller {

    public static final String LIB_PATH = new File(JarTool.getJarDir(), MagmaConstants.INSTALLER_LIBRARIES_FOLDER).getAbsolutePath() + "/";
    public static final File INSTALL_DIR = new File(LIB_PATH + "org/magma/install/");
    public static final File INSTALL_INFO = new File(INSTALL_DIR.getAbsolutePath() + "/installInfo");

    private PrintStream origin = System.out;
    public String forgeVer;
    public String mcpVer;
    public String mcVer;

    public String forgeStart;
    public File universalJar;
    public File serverJar;

    public File lzma;

    public String otherStart;
    public File extra;
    public File slim;
    public File srg;

    public String mcpStart;
    public File mcpZip;
    public File mcpTxt;

    public File minecraft_server;

    private PrintStream installerLog;

    protected AbstractMagmaInstaller() throws IOException {
        this.forgeVer = MagmaConstants.FORGE_VERSION_FULL.split("-")[1];
        this.mcpVer = MagmaConstants.FORGE_VERSION_FULL.split("-")[3];
        this.mcVer = MagmaConstants.FORGE_VERSION_FULL.split("-")[0];

        this.forgeStart = LIB_PATH + "net/minecraftforge/forge/" + mcVer + "-" + forgeVer + "/forge-" + mcVer + "-" + forgeVer;
        this.universalJar = new File(forgeStart + "-universal.jar");
        this.serverJar = new File(forgeStart + "-server.jar");

        this.lzma = new File(LIB_PATH + "org/magma/install/data/server.lzma");

        this.otherStart = LIB_PATH + "net/minecraft/server/" + mcVer + "-" + mcpVer + "/server-" + mcVer + "-" + mcpVer;

        this.extra = new File(otherStart + "-extra.jar");
        this.slim = new File(otherStart + "-slim.jar");
        this.srg = new File(otherStart + "-srg.jar");

        this.mcpStart = LIB_PATH + "de/oceanlabs/mcp/mcp_config/" + mcVer + "-" + mcpVer + "/mcp_config-" + mcVer + "-" + mcpVer;
        this.mcpZip = new File(mcpStart + ".zip");
        this.mcpTxt = new File(mcpStart + "-mappings.txt");

        this.minecraft_server = new File(LIB_PATH + "minecraft_server." + mcVer + ".jar");

        File out = new File("logs/installer.log");
        if(!out.exists()) {
            out.getParentFile().mkdirs();
            out.createNewFile();
        } else {
            out.delete();
            out.createNewFile();
        }

        this.installerLog = new PrintStream(new BufferedOutputStream(new FileOutputStream(out)));
    }

    protected void launchService(String mainClass, List<String> args, List<URL> classPath) throws Exception {
        try {
            Class.forName(mainClass);
        } catch (ClassNotFoundException e) {
            System.out.println("Class not found: " + e.getMessage());
        }
        URLClassLoader loader = URLClassLoader.newInstance(classPath.toArray(new URL[0]));
        Class.forName(mainClass, true, loader).getDeclaredMethod("main", String[].class).invoke(null, (Object) args.toArray(new String[0]));
        loader.clearAssertionStatus();
        loader.close();
    }

    protected List<URL> stringToUrl(List<String> strs) throws Exception {
        List<URL> temp = new ArrayList<>();
        for (String t : strs) {
            temp.add(new File(t).toURI().toURL());
        }
        return temp;
    }

    /*
    THIS IS TO NOT SPAM CONSOLE WHEN IT WILL PRINT A LOT OF THINGS
     */
    protected void mute() {
        System.setOut(installerLog);
    }

    protected void unmute() {
        System.setOut(origin);
    }

    protected void copyFileFromJar(File file, String pathInJar) throws Exception {
        InputStream is = MagmaStart.class.getClassLoader().getResourceAsStream(pathInJar);
        if(!file.exists() || !MD5.getMd5(file).equals(MD5.getMd5(is)) || file.length() <= 1) {
            file.getParentFile().mkdirs();
            file.createNewFile();
            if(is != null) Files.copy(is, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
            else {
                System.out.println("The file " + file.getName() + " was not found in the jar.");
                System.exit(0);
            }
        }
    }

    protected void deleteIfExists(File file) throws IOException {
        Files.deleteIfExists(file.toPath());
        File dir = file.getParentFile();
        if (dir.isDirectory() && dir.list().length == 0)
            Files.delete(dir.toPath());
    }

    protected static void deleteFolder(File folder) {
        File[] files = folder.listFiles();
        if (files != null) { //some JVMs return null for empty dirs
            for(File f : files) {
                if (f.isDirectory())
                    deleteFolder(f);
                else
                    f.delete();
            }
        }
        folder.delete();
    }

    protected boolean isCorrupted(File f) {
        try {
            JarFile j = new JarFile(f);
            j.close();
            return false;
        } catch (IOException e) {
            return true;
        }
    }
}