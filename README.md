# Distributor

[![Xpdustry latest](https://maven.xpdustry.com/api/badge/latest/releases/com/xpdustry/distributor-common-api?color=00ced1&name=distributor&prefix=v)](https://maven.xpdustry.com/#/releases/com/xpdustry/distributor-common-api)
[![Downloads](https://img.shields.io/github/downloads/xpdustry/distributor/total?color=00ced1)](https://github.com/xpdustry/distributor/releases)
[![Mindustry 7.0](https://img.shields.io/badge/Mindustry-7.0%20%7C%208.0-00ced1)](https://github.com/Anuken/Mindustry/releases)

Distributor is a framework for writing advanced Mindustry plugins in a safe and efficient way, it features:

- Powerful wrappers and utilities for the Mindustry core API.
- An audience/component system for sending media to players (shameless port of [KyoriPowered/adventure](https://github.com/KyoriPowered/adventure)).
- A powerful command system provided by [Cloud](https://github.com/Incendo/cloud).
- A nice permission system.
- A scheduler API for managing sync and async background tasks.
- A translation system for localizing messages.
- **Much more...**

## Links

- [JavaDoc](https://maven.xpdustry.com/javadoc/releases/com/xpdustry/distributor-common/latest/)
- [Wiki](https://github.com/xpdustry/distributor/wiki)
- [Discord](https://discord.xpdustry.com)

## Installation

This plugin requires at least :

- Mindustry v146

- Java 17

- [SLF4MD](https://github.com/xpdustry/slf4md) v1.0.4

## Building

- `./gradlew :distributor-$module:build` to build a module, with tests included.
- `./gradlew :distributor-$module:shadowJar` to only compile a plugin module (it will be located at `distributor-$module/build/libs/distributor-$module-$version-plugin.jar`).
- `./gradlew :distributor-$module:test` to run the unit tests of a module.
- `./gradlew :distributor-$module:runMindustryServer` to run a plugin module in a local Mindustry server.
