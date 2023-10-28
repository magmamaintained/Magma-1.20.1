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

import dev.vankka.dependencydownload.DependencyManager;
import dev.vankka.dependencydownload.dependency.Dependency;
import dev.vankka.dependencydownload.path.CleanupPathProvider;
import dev.vankka.dependencydownload.path.DependencyPathProvider;
import dev.vankka.dependencydownload.repository.Repository;
import dev.vankka.dependencydownload.repository.StandardRepository;
import me.tongfei.progressbar.ProgressBar;
import me.tongfei.progressbar.ProgressBarBuilder;
import me.tongfei.progressbar.ProgressBarStyle;
import org.magmafoundation.magma.common.MagmaConstants;
import org.magmafoundation.magma.common.utils.JarTool;
import org.magmafoundation.magma.common.utils.MD5;
import org.magmafoundation.magma.utils.LibHelper;
import org.magmafoundation.magma.utils.ServerInitHelper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.spi.FileSystemProvider;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Project: Magma
 *
 * @author Malcolm (M1lc0lm)
 * @date 03.07.2022 - 17:19
 *
 * Inspired by Shawiiz_z (https://github.com/Shawiizz)
 */
public class MagmaInstaller extends AbstractMagmaInstaller {

    private static final List<String> loadedLibsPaths = new ArrayList<>();

    private final String magmaVersion = MagmaConstants.VERSION;

    public final File fmlloader = new File(LIB_PATH + "net/minecraftforge/fmlloader/" + mcVer + "-" + forgeVer + "/fmlloader-" + mcVer + "-" + forgeVer + ".jar");
    public final File fmlcore = new File(LIB_PATH + "net/minecraftforge/fmlcore/" + mcVer + "-" + forgeVer + "/fmlcore-" + mcVer + "-" + forgeVer + ".jar");
    public final File javafmllanguage = new File(LIB_PATH + "net/minecraftforge/javafmllanguage/" + mcVer + "-" + forgeVer + "/javafmllanguage-" + mcVer + "-" + forgeVer + ".jar");
    public final File mclanguage = new File(LIB_PATH + "net/minecraftforge/mclanguage/" + mcVer + "-" + forgeVer + "/mclanguage-" + mcVer + "-" + forgeVer + ".jar");
    public final File lowcodelanguage = new File(LIB_PATH + "net/minecraftforge/lowcodelanguage/" + mcVer + "-" + forgeVer + "/lowcodelanguage-" + mcVer + "-" + forgeVer + ".jar");

    public final File mojmap = new File(otherStart + "-mappings.txt");
    public final File mc_unpacked = new File(otherStart + "-unpacked.jar");

    public final File mergedMapping = new File(mcpStart + "-mappings-merged.txt");

    public MagmaInstaller() throws Exception {
        new Dependencies(mcVer, mcpVer, minecraft_server);
        install();
        unmute(); //just to be sure ;)
    }

    //Inspired by the Mohist 1.19 installer
    private void install() throws Exception {
        ProgressBarBuilder builder = new ProgressBarBuilder()
                .setTaskName("Patching server...")
                .setStyle(ProgressBarStyle.ASCII)
                .setUpdateIntervalMillis(100)
                .setInitialMax(8);

        try (ProgressBar pb = builder.build()) {
            copyFileFromJar(lzma, "data/server.lzma");
            copyFileFromJar(universalJar, "data/forge-" + mcVer + "-" + forgeVer + "-universal.jar");
            copyFileFromJar(fmlloader, "data/fmlloader-" + mcVer + "-" + forgeVer + ".jar");
            copyFileFromJar(fmlcore, "data/fmlcore-" + mcVer + "-" + forgeVer + ".jar");
            copyFileFromJar(javafmllanguage, "data/javafmllanguage-" + mcVer + "-" + forgeVer + ".jar");
            copyFileFromJar(mclanguage, "data/mclanguage-" + mcVer + "-" + forgeVer + ".jar");
            copyFileFromJar(lowcodelanguage, "data/lowcodelanguage-" + mcVer + "-" + forgeVer + ".jar");

            if (magmaVersion == null || mcpVer == null) {
                System.out.println("The server has an invalid version and cannot proceed. Please report this to the developer.");
                System.exit(-1);
            }

            if (minecraft_server.exists()) {
                mute();
                System.out.println("[STEP ONE] Extracting bundled resources...");
                launchService("net.minecraftforge.installertools.ConsoleTool",
                        new ArrayList<>(Arrays.asList("--task", "BUNDLER_EXTRACT", "--input", minecraft_server.getAbsolutePath(), "--output", LIB_PATH, "--libraries")),
                        stringToUrl(loadedLibsPaths));

                //Delete brigadier, we have our own implementation
                deleteLib("com/mojang/brigadier");
                //Delete datafixers, we have our own implementation
                deleteLib("com/mojang/datafixerupper");
                System.out.println();
                unmute();
                pb.step();
                if (!mc_unpacked.exists()) {
                    mute();
                    System.out.println("[STEP TWO] Extracting jars...");
                    launchService("net.minecraftforge.installertools.ConsoleTool",
                            new ArrayList<>(Arrays.asList("--task", "BUNDLER_EXTRACT", "--input", minecraft_server.getAbsolutePath(), "--output", mc_unpacked.getAbsolutePath(), "--jar-only")),
                            stringToUrl(loadedLibsPaths));
                    System.out.println();
                    unmute();
                }
                pb.step();
            } else {
                System.err.println("The server is missing essential files to install properly, delete your libraries folder and try again.");
                System.exit(-1);
            }

            if (mcpZip.exists()) {
                if (!mcpTxt.exists()) {
                    mute();
                    System.out.println("[STEP THREE] Getting mappings...");
                    launchService("net.minecraftforge.installertools.ConsoleTool",
                            new ArrayList<>(Arrays.asList("--task", "MCP_DATA", "--input", mcpZip.getAbsolutePath(), "--output", mcpTxt.getAbsolutePath(), "--key", "mappings")),
                            stringToUrl(loadedLibsPaths));
                    System.out.println();
                    unmute();
                }
            } else {
                System.err.println("The server is missing essential files to install properly, delete your libraries folder and try again.");
                System.exit(-1);
            }
            pb.step();

            if (isCorrupted(extra)) extra.delete();
            if (isCorrupted(slim)) slim.delete();
            if (isCorrupted(srg)) srg.delete();

            if (!mojmap.exists()) {
                mute();
                System.out.println("[STEP FOUR] Downloading mojang mappings...");
                launchService("net.minecraftforge.installertools.ConsoleTool",
                        new ArrayList<>(Arrays.asList("--task", "DOWNLOAD_MOJMAPS", "--version", mcVer, "--side", "server", "--output", mojmap.getAbsolutePath())),
                        stringToUrl(loadedLibsPaths));
                System.out.println();
                unmute();
            }
            pb.step();

            if (!mergedMapping.exists()) {
                mute();
                System.out.println("[STEP FIVE] Merging mappings...");
                launchService("net.minecraftforge.installertools.ConsoleTool",
                        new ArrayList<>(Arrays.asList("--task", "MERGE_MAPPING", "--left", mcpTxt.getAbsolutePath(), "--right", mojmap.getAbsolutePath(), "--output", mergedMapping.getAbsolutePath(), "--classes", "--reverse-right")),
                        stringToUrl(loadedLibsPaths));
                System.out.println();
                unmute();
            }
            pb.step();

            if (!slim.exists() || !extra.exists()) {
                mute();
                System.out.println("[STEP SIX] Splitting server jar...");
                launchService("net.minecraftforge.jarsplitter.ConsoleTool",
                        new ArrayList<>(Arrays.asList("--input", minecraft_server.getAbsolutePath(), "--slim", slim.getAbsolutePath(), "--extra", extra.getAbsolutePath(), "--srg", mergedMapping.getAbsolutePath())),
                        stringToUrl(loadedLibsPaths));
                launchService("net.minecraftforge.jarsplitter.ConsoleTool",
                        new ArrayList<>(Arrays.asList("--input", mc_unpacked.getAbsolutePath(), "--slim", slim.getAbsolutePath(), "--extra", extra.getAbsolutePath(), "--srg", mergedMapping.getAbsolutePath())),
                        stringToUrl(loadedLibsPaths));
                System.out.println();
                unmute();
            }
            pb.step();

            if (!srg.exists()) {
                mute();
                System.out.println("[STEP SEVEN] Creating srg jar file...");
                launchService("net.minecraftforge.fart.Main",
                        new ArrayList<>(Arrays.asList("--input", slim.getAbsolutePath(), "--output", srg.getAbsolutePath(), "--names", mergedMapping.getAbsolutePath(), "--ann-fix", "--ids-fix", "--src-fix", "--record-fix")),
                        stringToUrl(loadedLibsPaths));
                System.out.println();
                unmute();
            }
            pb.step();

            String storedServerMD5 = null;
            String storedMagmaMD5 = null;
            String serverMD5 = MD5.getMd5(serverJar);
            String magmaMD5 = MD5.getMd5(JarTool.getFile());

            if (INSTALL_INFO.exists()) {
                List<String> infoLines = Files.readAllLines(INSTALL_INFO.toPath());
                if (infoLines.size() > 0)
                    storedServerMD5 = infoLines.get(0);
                if (infoLines.size() > 1)
                    storedMagmaMD5 = infoLines.get(1);
            }

            if (!serverJar.exists()
                    || storedServerMD5 == null
                    || storedMagmaMD5 == null
                    || !storedServerMD5.equals(serverMD5)
                    || !storedMagmaMD5.equals(magmaMD5)) {
                mute();
                System.out.println("[STEP EIGHT] Patching forge jar...");
                launchService("net.minecraftforge.binarypatcher.ConsoleTool",
                        new ArrayList<>(Arrays.asList("--clean", srg.getAbsolutePath(), "--output", serverJar.getAbsolutePath(), "--apply", lzma.getAbsolutePath())),
                        stringToUrl(new ArrayList<>(Arrays.asList(
                                LIB_PATH + "net/minecraftforge/binarypatcher/1.1.1/binarypatcher-1.1.1.jar",
                                LIB_PATH + "commons-io/commons-io/2.11.0/commons-io-2.11.0.jar",
                                LIB_PATH + "com/google/guava/guava/31.1-jre/guava-31.1-jre.jar",
                                LIB_PATH + "net/sf/jopt-simple/jopt-simple/5.0.4/jopt-simple-5.0.4.jar",
                                LIB_PATH + "com/github/jponge/lzma-java/1.3/lzma-java-1.3.jar",
                                LIB_PATH + "com/nothome/javaxdelta/2.0.1/javaxdelta-2.0.1.jar",
                                LIB_PATH + "com/google/code/findbugs/jsr305/3.0.2/jsr305-3.0.2.jar",
                                LIB_PATH + "org/checkerframework/checker-qual/2.0.0/checker-qual-2.0.0.jar",
                                LIB_PATH + "com/google/errorprone/error_prone_annotations/2.1.3/error_prone_annotations-2.1.3.jar",
                                LIB_PATH + "com/google/j2objc/j2objc-annotations/1.1/j2objc-annotations-1.1.jar",
                                LIB_PATH + "org/codehaus/mojo/animal-sniffer-annotations/1.14/animal-sniffer-annotations-1.14.jar",
                                LIB_PATH + "trove/trove/1.0.2/trove-1.0.2.jar"
                        ))));
                unmute();
                serverMD5 = MD5.getMd5(serverJar);
            }
            pb.step();

            FileWriter fw = new FileWriter(INSTALL_INFO);
            fw.write(serverMD5 + "\n");
            fw.write(magmaMD5);
            fw.close();
        }
    }

    private void deleteLib(String path) throws IOException {
        File libDir = new File(LIB_PATH + path);
        if (libDir.exists()) {
            Files.walk(libDir.toPath())
                    .map(Path::toFile)
                    .forEach(File::delete);
            libDir.delete();
        }
    }

    public static void run() {
        try {
            if (!checkDependencies())
                return;

            var urls = loadInternalDependencies();
            urls.add(MagmaInstaller.class.getProtectionDomain().getCodeSource().getLocation().toURI().toURL());
            var installerClassLoader = new URLClassLoader(urls.toArray(URL[]::new), null);
            var installerClass = Class.forName(MagmaInstaller.class.getName(), false, installerClassLoader);
            installerClass.getConstructor().newInstance();
            installerClassLoader.close();

            // Who needs file systems anyway
            ServerInitHelper.addOpens("java.base", "java.nio.file.spi", "ALL-UNNAMED");
            var loadingProvidersField = FileSystemProvider.class.getDeclaredField("loadingProviders");
            loadingProvidersField.setAccessible(true);
            loadingProvidersField.set(null, false);
            var installedProvidersField = FileSystemProvider.class.getDeclaredField("installedProviders");
            installedProvidersField.setAccessible(true);
            installedProvidersField.set(null, null);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private static boolean checkDependencies() throws IOException {
        if (INSTALL_INFO.exists()) {
            String magmaMD5 = MD5.getMd5(JarTool.getFile());
            List<String> lines = Files.readAllLines(INSTALL_INFO.toPath());
            if (lines.size() >= 2 && magmaMD5.equals(lines.get(1))) //Latest patch is installed
                return false;
            else {
                System.out.println("Update found! Magma will now update itself...");
                //extracted libs
                deleteFolder(new File(LIB_PATH + "net/minecraftforge/"));
                deleteFolder(new File(LIB_PATH + "net/minecraft/server/"));

                //install info
                deleteFolder(INSTALL_DIR);

                //mcp
                deleteFolder(new File(LIB_PATH + "de/oceanlabs/mcp/"));

                //libraries in path
                deleteFolder(new File(LIB_PATH + "cpw/mods"));
                deleteFolder(new File(LIB_PATH + "org/ow2/asm/"));
                return true;
            }
        }
        return true;
    }

    private static List<URL> loadInternalDependencies() throws Exception {
        var dependencies = new InternalDependency[] {
                new InternalDependency(LIB_PATH + "dev/vankka/dependencydownload-common/1.3.0/dependencydownload-common-1.3.0.jar", "b6d32a6d0c4d4407f54e601cfa3f0a5a", "https://repo1.maven.org/maven2/dev/vankka/dependencydownload-common/1.3.0/dependencydownload-common-1.3.0.jar"),
                new InternalDependency(LIB_PATH + "dev/vankka/dependencydownload-runtime/1.3.0/dependencydownload-runtime-1.3.0.jar", "ec35cf4906c6151111d9eabe4f4ea949", "https://repo1.maven.org/maven2/dev/vankka/dependencydownload-runtime/1.3.0/dependencydownload-runtime-1.3.0.jar"),
                new InternalDependency(LIB_PATH + "org/jline/jline/3.21.0/jline-3.21.0.jar", "859778f9cdd3bd42bbaaf0f6f7fe5e6a", "https://repo1.maven.org/maven2/org/jline/jline/3.21.0/jline-3.21.0.jar"),
                new InternalDependency(LIB_PATH + "me/tongfei/progressbar/0.9.3/progressbar-0.9.3.jar", "25d3101d2ca7f0847a804208d5411d78", "https://repo1.maven.org/maven2/me/tongfei/progressbar/0.9.3/progressbar-0.9.3.jar")
        };
        var urls = new ArrayList<URL>();
        for (var dependency : dependencies) {
            if (!dependency.file().exists() || !dependency.signature().equals(MD5.getMd5(dependency.file()))) {
                dependency.file().getParentFile().mkdirs();
                dependency.download();
            }
            urls.add(dependency.url());
        }
        return urls;
    }

    private record InternalDependency(File file, String signature, String link) {
        private InternalDependency(String path, String signature, String link) {
            this(new File(path), signature, link);
        }

        private void download() throws Exception {
            NetworkUtils.downloadFile(this.link(), this.file(), this.signature());
        }

        private URL url() {
            try {
                return file.toURI().toURL();
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    protected class Dependencies {

        private String mcVersion;
        private String mcpVersion;
        private File minecraft_server;

        public Dependencies(String mcVersion, String mcpVersion, File minecraft_server) throws Exception {
            this.mcVersion = mcVersion;
            this.mcpVersion = mcpVersion;
            this.minecraft_server = minecraft_server;

            downloadLibraries();
        }

        public void downloadLibraries() throws Exception {
            DependencyPathProvider dependencyPathProvider = new CleanupPathProvider() {

                public final Path baseDirPath = JarTool.getJarDir().toPath();

                @Override
                public Path getCleanupPath() {
                    return baseDirPath;
                }

                @Override
                public Path getDependencyPath(Dependency dependency, boolean relocated) {
                    return baseDirPath.resolve("libraries")
                            .resolve(dependency.getGroupId().replace(".", "/"))
                            .resolve(dependency.getArtifactId())
                            .resolve(dependency.getVersion())
                            .resolve(dependency.getFileName());
                }
            };

            DependencyManager manager = new DependencyManager(dependencyPathProvider);
            manager.loadFromResource(new URL("jar:file:" + JarTool.getJarPath() + "!/data/magma_libraries.txt"));

            List<Repository> standardRepositories = new ArrayList<>();
            standardRepositories.add(new StandardRepository("https://nexus.c0d3m4513r.com/repository/Magma/"));
            standardRepositories.add(new StandardRepository("https://maven.minecraftforge.net"));
            standardRepositories.add(new StandardRepository("https://repo1.maven.org/maven2"));
            standardRepositories.add(new StandardRepository("https://git.magmafoundation.org/magmafoundation/magma-maven-repo/-/raw/repository/"));
            standardRepositories.add(new StandardRepository("https://maven.izzel.io/releases"));

            List<Dependency> dependencies = manager.getDependencies();

            ProgressBarBuilder builder = new ProgressBarBuilder()
                    .setTaskName("Loading libraries...")
                    .setStyle(ProgressBarStyle.ASCII)
                    .setUpdateIntervalMillis(100)
                    .setInitialMax(dependencies.size());

            mute();
            System.out.println("[INITIAL SETUP] Loading libraries...");
            unmute();

            //AtomicReference<Throwable> error = new AtomicReference<>(null);
            ProgressBar.wrap(dependencies.stream(), builder).forEach(dep -> {
                try {
                    mute();
                    System.out.println("Considering library " + dep.getFileName() + "...");
                    LibHelper.downloadDependency(manager, dep, standardRepositories);
                    LibHelper.loadDependency(manager, dep, path -> loadedLibsPaths.add(path.toFile().getAbsolutePath()));
                    System.out.println("Library " + dep.getFileName() + " loaded!");
                    unmute();
                } catch (Exception e) {
                    unmute();
                    throw new RuntimeException("Something went wrong while trying to load dependencies", e);
                }
            });

            downloadMcp(mcVersion, mcpVersion);
            downloadMinecraftServer(minecraft_server);
        }

        public void downloadMcp(String mc_version, String mcp_version) {
            File mcp_config = new File(LIB_PATH + "de/oceanlabs/mcp/mcp_config/" + mc_version + "-" + mcp_version + "/mcp_config-" + mc_version + "-" + mcp_version + ".zip");
            if (Files.exists(mcp_config.toPath()))
                return;
            mcp_config.getParentFile().mkdirs();
            try {
                NetworkUtils.downloadFile("https://maven.minecraftforge.net/de/oceanlabs/mcp/mcp_config/"+ mc_version + "-"+ mcp_version + "/mcp_config-"+ mc_version + "-"+ mcp_version +".zip",
                        mcp_config);
            } catch (Exception e) {
                System.out.println("Can't find mcp_config");
                e.printStackTrace();
            }
        }

        public void downloadMinecraftServer(File minecraft_server) throws IOException {
            if (Files.exists(minecraft_server.toPath()))
                return;
            minecraft_server.getParentFile().mkdirs();
            try {
                NetworkUtils.downloadFile("https://piston-data.mojang.com/v1/objects/84194a2f286ef7c14ed7ce0090dba59902951553/server.jar", minecraft_server);
            } catch (Exception e) {
                System.out.println("Can't download minecraft_server");
                e.printStackTrace();
            }
        }
    }
}
