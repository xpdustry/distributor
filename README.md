# Distributor

[![Xpdustry latest](https://repo.xpdustry.fr/api/badge/latest/releases/fr/xpdustry/distributor-core?color=00FFFF&name=Distributor&prefix=v)](https://github.com/Xpdustry/Distributor/releases)
[![Build](https://github.com/Xpdustry/Distributor/actions/workflows/build.yml/badge.svg?branch=master)](https://github.com/Xpdustry/Distributor/actions/workflows/build.yml)
[![Mindustry 7.0 ](https://img.shields.io/badge/Mindustry-7.0-ffd37f)](https://github.com/Anuken/Mindustry/releases)

## Description

**Distributor** is a plugin framework which provides a powerful command system,
better Mindustry API bindings and other nice features, making plugin development time much faster.

Here is the list of the available modules:

| name                                                                                                    | description                                                                                                                                |
|---------------------------------------------------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------|
| distributor-core                                                                                        | The core library needed by all distributor modules.                                                                                        |
| [distributor-js](https://github.com/Xpdustry/Distributor/tree/master/distributor-script/distributor-js) | This module provides a better javascript management with class blacklists/whitelists, shared class loaders, maximum script runtime, etc... |

Follow the development [here](https://github.com/orgs/Xpdustry/projects/3). 

## Usage

To develop a plugin using Distributor, you will first need to add the Xpdustry repository in your `build.gradle` such as :

```gradle
repositories {
    // Replace with "https://repo.xpdustry.fr/snapshots" if you want to use snapshots
    maven { url = uri("https://repo.xpdustry.fr/releases") }
}
```

Then, add the needed artifacts in your dependencies :

```gradle
dependencies {
    // Add "-SNAPSHOT" after the version if you are using the snapshot repository
    compileOnly("fr.xpdustry:distributor-core:{version}" )
}
```

After that, add the internal name of the module you are using in your `plugin.json` :

```json
{
  "dependencies": [
    "xpdustry-distributor-core"
  ]
}
```

Finally, when you are ready to deploy your plugin, get the necessary Distributor jars :

- If you use the official version, you can get the jars in the [releases](https://github.com/Xpdustry/Distributor/releases).

- If you use the snapshots, you can get the jars in the [commit workflows](https://github.com/Xpdustry/Distributor/actions/workflows/commit.yml).

## Building

- `./gradlew jar` for a simple jar that contains only the plugin code.

- `./gradlew shadowJar` for a fatJar that contains the plugin and its dependencies (use this for your server).

## Testing

- `./gradlew runMindustryClient`: Run Mindustry in desktop with the plugin.

- `./gradlew runMindustryServer`: Run Mindustry in a server with the plugin.

## Running

This plugin is compatible with V6 and V7.

**/!\ Up to v135, you will need [mod-loader](https://github.com/Xpdustry/ModLoaderPlugin) if you have plugins that relies on Distributor.**

## Credits

The translation system is based on the one you can find in [KyoriPowered/adventure](https://github.com/KyoriPowered/adventure).

pls run workflow...
