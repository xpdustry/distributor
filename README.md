# Distributor

[![Xpdustry latest](https://maven.xpdustry.fr/api/badge/latest/releases/fr/xpdustry/distributor-core?color=00ced1&name=Distributor&prefix=v)](https://maven.xpdustry.fr/#/releases/fr/xpdustry/distributor-api)
[![Javadoc](https://img.shields.io/badge/Javadoc-latest-00ced1)](https://maven.xpdustry.fr/javadoc/releases/fr/xpdustry/distributor-api/latest/)
[![Downloads](https://img.shields.io/github/downloads/Xpdustry/Distributor/total?color=00ced1)](https://github.com/Xpdustry/Distributor/releases)
[![Mindustry 7.0](https://img.shields.io/badge/Mindustry-7.0-00ced1)](https://github.com/Anuken/Mindustry/releases)

Distributor is a framework for writing advanced Mindustry plugins in a safe and efficient way, it features :

- A powerful command system provided by [Cloud](https://github.com/Incendo/cloud).
- A very complete minecraft-like permission system (cloud commands can also use this system).
- A scheduler API for managing sync and async background tasks.
- A better event API.
- A nice localization API inspired from [KyoriPowered/adventure](https://github.com/KyoriPowered/adventure).
- A better plugin API without the quirks of vanilla Mindustry.
- **Much more...**

## Links

- [JavaDoc](https://maven.xpdustry.fr/javadoc/releases/fr/xpdustry/distributor-api/latest/)
- [Wiki](https://github.com/Xpdustry/Distributor/wiki)
- [Discord](https://discord.xpdustry.fr)

## Building

- `./gradlew build` to build the project, with tests included.
- `./gradlew :distributor-core:shadowJar` to only compile the plugin (it will be located at `distributor-core/build/libs/Distributor.jar`).
- `./gradlew test` to run the unit tests.
- `./gradlew :distributor-core:runMindustryServer` to run the plugin in a local Mindustry server.
