# Distributor

[![Jitpack latest version](https://jitpack.io/v2.0/Xpdustry/Distributor.svg)](https://jitpack.io/#Xpdustry/Distributor)
[![Build](https://github.com/Xpdustry/Distributor/actions/workflows/build.yml/badge.svg?branch=master)](https://github.com/Xpdustry/Distributor/actions/workflows/build.yml)
[![Mindustry 7.0 ](https://img.shields.io/badge/Mindustry-7.0-ffd37f)](https://github.com/Anuken/Mindustry/releases)

## Description

**Distributor** is a Plugin that provides better Mindustry API bindings and other nice features, making development time much faster.

[Javadoc](https://javadoc.jitpack.io/fr/xpdustry/distributor/v2.0/javadoc/)

## Usage

### For plugin development

**Attention**: You can't use this plugin as a dependency right now, until [#6328](https://github.com/Anuken/Mindustry/pull/6328) is merged...

Let's say you want the core plugin.

First, include the internal name in your `plugin.json`, such as:
```json
{
  "dependencies": ["xpdustry-distributor-core"]
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

### For scripting

`distributor-js` provides a better js runtime on top of the mindustry one. Here are some things to know:

- The init script will run once, for every `JavaScriptEngine` instance created (one per thread), so include your global imports and functions there.

- The startup script will run once in the main thread, same for the shutdown script.

- The `js` command is overridden to use the `JavaScriptEngine`.

- The `require` function is set up to the root of the `distributor/script/js` subdirectory inside the root directory of Distributor.

- Distributor automatically kills blocking scripts after 10 seconds of runtime by default, you can change that behavior in the property file.

## TODO

- [ ] Complete Distributor stdlib:
    - [X] Localization
    - [X] Commands
    - [ ] Moderation
    - [ ] Network (Socket API)

- [ ] Create JavaScript Debugger
