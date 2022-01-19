# Distributor

[![Xpdustry latest](https://repo.xpdustry.fr/api/badge/latest/releases/fr/xpdustry/distributor-core?color=00FFFF&name=Distributor&prefix=v)](https://github.com/Xpdustry/Distributor/releases)
[![Build](https://github.com/Xpdustry/Distributor/actions/workflows/commit.yml/badge.svg?branch=master)](https://github.com/Xpdustry/Distributor/actions/workflows/commit.yml)
[![Mindustry 7.0 ](https://img.shields.io/badge/Mindustry-7.0-ffd37f)](https://github.com/Anuken/Mindustry/releases)

## Description

**Distributor** is a Plugin framework which provides a powerful command system, better Mindustry API bindings and other nice features, making plugin development time much faster.

Here is the list of the available modules:
| name | description |
|-|-|
| `distributor-core`    | The core library needed by all distributor modules. |
| `distributor-js`      | This module provides a better javascript management with class blacklists/whitelists, shared class loaders, maximum script runtime, etc... |

## Usage

To develop a plugin using Distributor, you will first need to add the Xpdustry repository in your `build.gradle` such as:

```gradle
repositories{
    // Replace with "https://repo.xpdustry.fr/snapshots" if you want to use snapshots
    maven{ url "https://repo.xpdustry.fr/releases" }
}
```

Then, add the needed artifacts in your dependencies, such as:

```gradle
dependencies{
    // Add "-SNAPSHOT" at the end if you are using the snapshot repository
    compileOnly "fr.xpdustry:{distributor-module}:{version}" 
}
```

After that, add the internal name of the Distributor module you are using in your `plugin.json`, such as:

```json
{
  "dependencies": [
    "distributor-module-internal-name"
  ]
}
```

Finally, when you are ready for testing, put the necessary Distributor jars in your `config/mods` and your plugin.
> If you used the official version, you can get the jars in the [releases](https://github.com/Xpdustry/Distributor/releases).

> If you used the snapshots, you can get the jars in the [commit workflows](https://github.com/Xpdustry/Distributor/actions/workflows/commit.yml).

## TODO

- [ ] CachedBundleProvider (BundleProvider that can cache localization bundles)
- [ ] ArcMap (Map view of an ObjectMap)
- [ ] JavaScript Debugger
- [ ] Services (Anti-VPN, AutoMod)
- [ ] Better command execution (Give the possibility to run the command asynchronously)
