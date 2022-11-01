# Distributor

[![Xpdustry latest](https://maven.xpdustry.fr/api/badge/latest/releases/fr/xpdustry/distributor-core?color=00FFFF&name=Distributor&prefix=v)](https://github.com/Xpdustry/Distributor/releases)
[![Build](https://github.com/Xpdustry/Distributor/actions/workflows/build.yml/badge.svg?branch=master)](https://github.com/Xpdustry/Distributor/actions/workflows/build.yml)
[![Mindustry 7.0](https://img.shields.io/badge/Mindustry-7.0-ffd37f)](https://github.com/Anuken/Mindustry/releases)

## Description

**Distributor** is a plugin framework which provides a powerful command system,
better Mindustry API bindings and other nice features, making plugin development time much faster.
You can find the documentation [here](https://github.com/Xpdustry/Distributor/wiki), the official plugin jars in the
[releases](https://github.com/Xpdustry/Distributor/releases) and the snapshot jars in
the [commit workflows](https://github.com/Xpdustry/Distributor/actions/workflows/build.yml).

## Building

- `./gradlew jar` for a simple jar that contains only the plugin code.

- `./gradlew shadowJar` for a fatJar that contains the plugin and its dependencies (use this for your server).

## Testing

- `./gradlew :distributor-core:runMindustryClient`: Run the plugin in a Mindustry client (desktop).

- `./gradlew :distributor-core:runMindustryServer`: Run the plugin in a Mindustry server (headless).

## Running

This plugin is compatible with v137+.

## Credits

- I want to thank Incendo and their amazing work on [Cloud](https://github.com/Incendo/cloud), the command library of my
  dreams.

- The localization system is based on the translation system of 
  [KyoriPowered/adventure](https://github.com/KyoriPowered/adventure).
