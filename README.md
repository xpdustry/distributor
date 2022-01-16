# Distributor

[![Jitpack latest version](https://jitpack.io/v2.0/Xpdustry/Distributor.svg)](https://jitpack.io/#Xpdustry/Distributor)
[![Build](https://github.com/Xpdustry/Distributor/actions/workflows/build.yml/badge.svg?branch=master)](https://github.com/Xpdustry/Distributor/actions/workflows/build.yml)
[![Mindustry 7.0 ](https://img.shields.io/badge/Mindustry-7.0-ffd37f)](https://github.com/Anuken/Mindustry/releases)

## Description

**Distributor** is a Plugin that provides better Mindustry API bindings and other nice features, making development time much faster.

[Javadoc](https://javadoc.jitpack.io/fr/xpdustry/distributor/v2.0/javadoc/)

## Usage

### For plugin development

Let's say you want the core plugin.

First, include the internal name in your `plugin.json`, such as:

```json
{
  "dependencies": [
    "xpdustry-distributor-core"
  ]
}
```

Then, get the dependency for your project:

```gradle
repositories{
    maven { url 'https://jitpack.io' }
}

dependencies{
    compileOnly 'fr.xpdustry.Distributor:distributor-core:{version}'
}
```

When you have finished your plugin, grab the needed [artifacts](https://github.com/Xpdustry/Distributor/releases) for your version. Put them in your `config/mods` with your plugin, and enjoy.

## TODO

- Tools
    - [X] Localization
        - [ ] CachedBundleProvider
    - [ ] Moderation
    - [ ] Services (Anti-VPN)

- Command framework
    - [ ] Async execution

- Helper
    - [ ] Implement ArcMap
    - [ ] JavaScript Debugger
