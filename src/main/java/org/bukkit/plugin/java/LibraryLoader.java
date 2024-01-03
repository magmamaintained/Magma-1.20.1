package org.bukkit.plugin.java;

import mjson.Json;
import org.bukkit.plugin.PluginDescriptionFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.magmafoundation.magma.Magma;
import org.magmafoundation.magma.remapping.generated.RemappingURLClassLoader;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

// This is a modified version of https://github.com/MohistMC/Mohist/blob/1.20.1/src/main/java/org/bukkit/plugin/java/LibraryLoader.java

class LibraryLoader {

    public LibraryLoader() {
    }

    @Nullable
    public ClassLoader createLoader(@NotNull PluginDescriptionFile desc) throws IOException {
        if (desc.getLibraries().isEmpty()) {
            return null;
        }
        Magma.LOGGER.info("[{}] Loading {} libraries... please wait", desc.getName(), desc.getLibraries().size());

        List<Dependency> dependencies = new ArrayList<>();
        for (String libraries : desc.getLibraries()) {
            String[] args = libraries.split(":");
            if (args.length > 1) {
                Dependency dependency = new Dependency(args[0], args[1], args[2], false);
                dependencies.add(dependency);
            }
        }

        List<File> libraries = new ArrayList<>();
        List<Dependency> newDependencies = new ArrayList<>();
        var d = magmaLibs();
        for (Dependency dependency : dependencies) {
            String group = dependency.group().replace(".", "/");
            String fileName = "%s-%s.jar".formatted(dependency.name(), dependency.version());
            if (!d.contains(fileName)) {
                if (dependency.version().toString().equalsIgnoreCase("LATEST")) {
                    URL mavenUrl = URI.create("https://repo.maven.apache.org/maven2/" + "%s/%s/%s".formatted(group, dependency.name(), "maven-metadata.xml")).toURL();
                    Json compile_json2Json = Json.readXml(mavenUrl).at("metadata");
                    List<Object> v = compile_json2Json.at("versioning").at("versions").at("version").asList();
                    Dependency dependency0 = new Dependency(group, dependency.name(),  v.get(v.size() - 1), false);
                    newDependencies.add(dependency0);
                } else {
                    newDependencies.add(dependency);
                    String pomUrl = "https://repo.maven.apache.org/maven2/" + "%s/%s/%s/%s".formatted(group, dependency.name(), dependency.version(), fileName.replace("jar", "pom"));
                    newDependencies.addAll(initDependencies0(new URL(pomUrl)));
                }
            }
        }

        Magma.LOGGER.info("[{}] Loading {} extra libraries... please wait", desc.getName(), newDependencies.size() - desc.getLibraries().size());

        for (Dependency dependency : newDependencies) {
            String group = dependency.group().replace(".", "/");
            String fileName = "%s-%s.jar".formatted(dependency.name(), dependency.version());
            String mavenUrl = "https://repo.maven.apache.org/maven2/" + "%s/%s/%s/%s".formatted(group, dependency.name(), dependency.version(), fileName);

            File file = new File(new File("libraries", "plugins-lib"), "%s/%s/%s/%s".formatted(group, dependency.name(), dependency.version(), fileName));

            if (file.exists()) {
                Magma.LOGGER.info("[{}] Found libraries {}", desc.getName(), file);
                libraries.add(file);
                continue;
            }
            try {
                file.getParentFile().mkdirs();

                InputStream inputStream = new URL(mavenUrl).openStream();
                ReadableByteChannel rbc = Channels.newChannel(inputStream);
                FileChannel fc = FileChannel.open(file.toPath(), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);

                fc.transferFrom(rbc, 0, Long.MAX_VALUE);
                fc.close();
                rbc.close();

                libraries.add(file);
            } catch (IOException e) {
            }
        }

        List<URL> jarFiles = new ArrayList<>();
        for (File file : libraries) {
            try {
                jarFiles.add(file.toURI().toURL());
                Magma.LOGGER.info("[{}] Loaded libraries {}", desc.getName(), file);
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        }

        return new RemappingURLClassLoader(jarFiles.toArray(new URL[0]), getClass().getClassLoader());
    }

    public List<Dependency> initDependencies0(URL url) throws IOException {
        List<Dependency> list = new ArrayList<>();
        for (Dependency dependency : initDependencies(url)) {
            list.add(dependency);
            if (dependency.extra()) {
                String group = dependency.group().replace(".", "/");
                String fileName = "%s-%s.jar".formatted(dependency.name(), dependency.version());
                String pomUrl = "https://repo.maven.apache.org/maven2/" + "%s/%s/%s/%s".formatted(group, dependency.name(), dependency.version(), fileName.replace("jar", "pom"));
                if (hasUrl(pomUrl)) list.addAll(initDependencies(new URL(pomUrl)));
            }
        }
        return list;
    }

    public List<Dependency> initDependencies(URL url) {
        List<Dependency> list = new ArrayList<>();
        Json json2Json = Json.readXml(url).at("project");
        String version = json2Json.has("parent") ? json2Json.at("parent").asString("version") : json2Json.asString("version");
        String groupId = json2Json.has("parent") ? json2Json.at("parent").asString("groupId") : json2Json.asString("groupId");

        if (!json2Json.has("dependencies")) return list;
        if (!json2Json.at("dependencies").toString().startsWith("{\"dependency\"")) return list;
        Json json3Json = json2Json.at("dependencies").at("dependency");
        if (json3Json.isArray()) {
            for (Json o : json2Json.at("dependencies").asJsonList("dependency")) {
                dependency(o, list, version, groupId);
            }
        } else {
            dependency(json3Json, list, version, groupId);
        }
        return list;
    }

    public void dependency(Json json, List<Dependency> list, String version, String parent_groupId) {
        try {
            if (json.toString().contains("groupId") && json.toString().contains("artifactId")) {
                String groupId = json.asString("groupId");
                String artifactId = json.asString("artifactId");
                if (json.toString().contains("version")) {
                    if (json.has("scope") && json.asString("scope").equals("test")) {
                        return;
                    }
                    if (groupId.equals("${project.parent.groupId}")) {
                        groupId = parent_groupId;
                    }
                    String versionAsString = json.asString("version");
                    if (versionAsString.contains("${project.version}") || versionAsString.contains("${project.parent.version}")) {
                        Dependency dependency = new Dependency(groupId, artifactId, version, true);
                        list.add(dependency);
                    } else if (!versionAsString.contains("${")) {
                        Dependency dependency = new Dependency(groupId, artifactId, versionAsString, true);
                        list.add(dependency);
                    }
                } else {
                    if (json.has("scope") && json.asString("scope").equals("compile")) {
                        URL mavenUrl = URI.create("https://repo.maven.apache.org/maven2/" + "%s/%s/%s".formatted(groupId.replace(".", "/"), artifactId, "maven-metadata.xml")).toURL();
                        Json compile_json2Json = Json.readXml(mavenUrl).at("metadata");
                        List<Object> v = compile_json2Json.at("versioning").at("versions").at("version").asList();
                        Dependency dependency = new Dependency(groupId, artifactId, v.get(v.size() - 1), true);
                        list.add(dependency);
                    }
                }
            }
        } catch (Exception ignored) {}
    }

    public List<String> magmaLibs() {
        List<String> temp = new ArrayList<>();
        BufferedReader b;
        try {
            b = new BufferedReader(new FileReader("libraries" + File.separator + "libraries.txt"));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        String str;
        try {
            while ((str = b.readLine()) != null) {
                String[] s = str.split(" ");
                String[] s1 = s[0].split(":");

                String fileName = s1[1] + "-" + s1[2] + ".jar";
                temp.add(new File("libraries/" + s1[0].replaceAll("\\.", "/") + "/" + s1[1] + "/" + s1[2] + "/" + fileName).getName());
            }
            b.close();
        } catch (Exception ignored) {}
        return temp;
    }

    public record Dependency(String group, String name, Object version, boolean extra) {
    }

    private boolean hasUrl(String s) {
        try {
            URL url = new URL(s);
            url.openStream();
            return true;
        } catch (Exception var2) {
            return false;
        }
    }
}
