# Distributor

[![Xpdustry latest](https://maven.xpdustry.fr/api/badge/latest/releases/fr/xpdustry/distributor-core?color=00FFFF&name=Distributor&prefix=v)](https://github.com/Xpdustry/Distributor/releases)
[![Build](https://github.com/Xpdustry/Distributor/actions/workflows/build.yml/badge.svg?branch=master)](https://github.com/Xpdustry/Distributor/actions/workflows/build.yml)
[![Mindustry 6.0 | 7.0 ](https://img.shields.io/badge/Mindustry-7.0-ffd37f)](https://github.com/Anuken/Mindustry/releases)

## Description

**Distributor** is a plugin framework which provides a powerful command system,
better Mindustry API bindings and other nice features, making plugin development time much faster.

## Usage

To develop a plugin using Distributor, you will first need to add the Xpdustry repository in your `build.gradle` such as :

```gradle
repositories {
    maven { url = uri("https://maven.xpdustry.fr/releases") }
}
```

Then, add the needed artifacts in your dependencies :

```gradle
dependencies {
    compileOnly("fr.xpdustry:distributor-api:3.0.0-rc1")
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

- If you use the snapshots, you can get the jars in the [commit workflows](https://github.com/Xpdustry/Distributor/actions/workflows/build.yml).

## Building

- `./gradlew jar` for a simple jar that contains only the plugin code.

- `./gradlew shadowJar` for a fatJar that contains the plugin and its dependencies (use this for your server).

## Testing

- `./gradlew :distributor-core:runMindustryClient`: Run the plugin in a Mindustry client (desktop).

- `./gradlew :distributor-core:runMindustryServer`: Run the plugin in a Mindustry server (headless).

## Running

This plugin is compatible with v137+.

## Credits

- I want to thank Icendo and their amazing work on [cloud](https://github.com/Incendo/cloud), the command library of my dreams.

- The localization system is based on the one you can find in [KyoriPowered/adventure](https://github.com/KyoriPowered/adventure).
