# Distributor

[![Xpdustry latest](https://repo.xpdustry.fr/api/badge/latest/releases/fr/xpdustry/distributor?color=00FFFF&name=Distributor&prefix=v)](https://github.com/Xpdustry/Distributor/releases)
[![Build](https://github.com/Xpdustry/Distributor/actions/workflows/commit.yml/badge.svg?branch=master)](https://github.com/Xpdustry/Distributor/actions/workflows/commit.yml)
[![Mindustry 7.0 ](https://img.shields.io/badge/Mindustry-7.0-ffd37f)](https://github.com/Anuken/Mindustry/releases)

## Description

**Distributor** is a Plugin that provides better Mindustry API bindings and other nice features, making development time much faster.

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
