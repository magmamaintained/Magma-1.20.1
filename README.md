<div align="center">
<img src="https://i.imgur.com/zTCTCWG.png" alt="Magma logo" align="middle"></img>

[![](https://img.shields.io/badge/Minecraft%20Forge-1.20.1%20--%2047.2.0%20--%2012d24df4-orange.svg)](https://files.minecraftforge.net/net/minecraftforge/forge/index_1.20.1.html)
[![](https://img.shields.io/badge/Bukkit-1.20%203635fe1-blue)](https://hub.spigotmc.org/stash/projects/SPIGOT/repos/bukkit/browse)
[![](https://img.shields.io/badge/CraftBukkit-Build%2078796c9de6c-orange)](https://hub.spigotmc.org/stash/projects/SPIGOT/repos/craftbukkit/browse?at=refs%2Fheads%2Fversion%2F1.20)
[![](https://img.shields.io/badge/Spigot-Build%20d2eba2c820b-yellow)](https://hub.spigotmc.org/stash/projects/SPIGOT/repos/spigot/browse)
<!-- ![TeamCity build status](https://teamcity.magmafoundation.org/app/rest/builds/buildType:id:MagmaFoundation_Magma120x_Build/statusIcon.svg) -->
[![](https://img.shields.io/discord/612695539729039411.svg?logo=discord&logoWidth=18&colorB=7289DA)](https://discord.gg/magma)

<a href="https://bstats.org/plugin/server-implementation/Magma/17219"> <img src="https://bstats.org/signatures/server-implementation/Magma.svg" alt="Stats" width="800"></a>
</div> 

## ❓ About

Magma is the next generation of hybrid minecraft server softwares.

Magma is based on **Forge and Paper**, meaning it can run both **Craftbukkit/Spigot/Paper plugins and Forge mods**.

We hope to eliminate all issues with craftbukkit forge servers. In the end, we envision a seamless, low lag Magma experience with support for newer 1.12+ versions of Minecraft.

## 🌐 BungeeCord/Velocity

Magma 1.20 is **not** compatible with **vanilla** BungeeCord or any of its forks. This is **caused by Forge** and not a fault of Magma. We cannot fix this ourselves without modifying the client.
You might be able to use the Waterfall fork called [Lightfall](https://github.com/ArclightPowered/lightfall), but it also requires a clientside-mod in order to work and is not officially supported.

Magma 1.20 is **not** compatible with **vanilla** [Velocity](https://velocitypowered.com/downloads/). This is **caused by Forge** and not a fault of Magma. We cannot fix this ourselves without modifying the client and the proxy.

## 🧪 Magma for 1.12+/1.16+/1.18+

Magma for Minecraft 1.12 and above can be found in their own repositories.

- Click [here](https://git.magmafoundation.org/magmafoundation/Magma) to visit the 1.12 repository.
- Click [here](https://git.magmafoundation.org/magmafoundation/Magma-1-16-x) to visit the 1.16 repository. (1.16 is End Of Life and not actively maintained anymore)
- Click [here](https://git.magmafoundation.org/magmafoundation/Magma-1-18-x) to visit the 1.18 repository.

## 🪣 Deployment

### Installation

1. Download the recommended builds from the [**Releases** section](https://git.magmafoundation.org/magmafoundation/Magma-1-20-x/-/releases) (**Download** the one that ends in server)
    1. Or Download the latest jar from [Magma Site](https://magmafoundation.org/)
2. Make a new directory (folder) for the server
3. Move the jar that you downloaded into the new directory
4. Run the jar with your command prompt or terminal, going to your directory and entering `java -jar Magma-[version]-server.jar`. Change [version] to your Magma version number.

### Building the sources

- Clone the Project
    - You can use Git GUI (like GitHub Desktop/GitKraken) or clone using the terminal using:
        - `git clone http://git.magmafoundation.org/magmafoundation/Magma-1-20-x.git`
- Building
    - First you want to run the build command
        - `./gradlew setup magmaJar`
    - Now go and get a drink this may take some time
    - Navigate to `projects/magma/build/libs` directory of the compiled source code
    - Copy the Jar to a new server directory (see Installation)

## ⚙️ Contributing

If you wish to inspect Magma, submit PRs, or otherwise work with Magma itself, you're in the right place!.

Please read the [CONTRIBUTING.md](https://git.magmafoundation.org/magmafoundation/Magma-1-20-x/-/blob/1.20/CONTRIBUTING.md) to see how to contribute, setup, and run.

## 💬 Chat

You are welcome to visit Magma's Discord server [here](https://discord.gg/Magma) (recommended).

You could also go to Magma's subreddit [here](https://www.reddit.com/r/Magma).

## 👥 Partners

![YourKit-Logo](https://www.yourkit.com/images/yklogo.png)

[YourKit](http://www.yourkit.com/), makers of the outstanding java profiler, support open source projects of all kinds with their full featured [Java](https://www.yourkit.com/java/profiler/index.jsp) and [.NET](https://www.yourkit.com/.net/profiler/index.jsp) application profilers.

