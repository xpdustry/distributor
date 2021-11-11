# Distributor

[![Jitpack latest version](https://jitpack.io/v/Xpdustry/Distributor.svg)](https://jitpack.io/#Xpdustry/Distributor)
[![Build](https://github.com/Xpdustry/Distributor/actions/workflows/build.yml/badge.svg?branch=master)](https://github.com/Xpdustry/Distributor/actions/workflows/build.yml)
[![Mindustry 6.0 | 7.0 ](https://img.shields.io/badge/Mindustry-6.0%20%7C%207.0-ffd37f)](https://github.com/Anuken/Mindustry/releases)

## Description

**Distributor** is a Plugin that provides better Mindustry API bindings and features that can be implemented in Java or JavaScript, making development time much faster.

[Javadoc](https://javadoc.jitpack.io/fr/xpdustry/distributor/v1.6/javadoc/)

## Usage

### As a Plugin

#### Installation

- Launch your server with the plugin to deploy its file tree, for now it's only the `scripts` subdirectory.

- It will also create a property file inside the `config` directory, edit it to change the boot options of Distributor.

#### Scripting

Distributor brings its own JavaScript runtime so no worries about V6-V7 compatibilities issues. Here are some things to know:

- The init script will run once, for every `ScriptEngine` instance created (one per thread), so include your global imports and functions there.

- The startup script will run once in the main thread, same for the shutdown script.

- To not overwrite the default `js` command, use `jsx` to run javascript in the terminal with Distributor.

- The `require` function is set up to the root of the `scripts` subdirectory inside the root directory of Distributor.

- Distributor automatically kills blocking scripts after 10 seconds of runtime by default, you can change that behavior in the property file.

### As a Dependency

First, add these in your build.gradle

```gradle
repositories{
    maven { url 'https://jitpack.io' }
}

dependencies{
    compileOnly 'fr.xpdustry.Distributor:1.6'
}
```

Now, if you are running a V7 server, you will just have to add distributor in the dependency list of your plugin with:
```json
{
  "dependencies": ["xpdustry-distributor-plugin"]
}
```
Making sure you have the Distributor jar in the `mods` directory of your server, and you'll be good.

But If you are running a V6 server, since it doesn't use the same class loader for each plugin, you will have funny `ClassNotFoundException` all over the place.

Fortunately for you, you won't have to fork this plugin to just apply your stuff, you just have to use the custom build I made that adds the shared class loader + unlocking the default `js` command.
You can get it [here](https://github.com/Phinner/Mindustry/releases/tag/v126.3).

## Nice tips

- If you have a multiple servers on the same vps, you can share Distributor files between each server by changing the value of `distributor.path` inside each `distributor.properties`. Very handy :^)

## TODO

- [ ] Complete Distributor stdlib:
    - [X] Localization
    - [X] Commands
    - [ ] Moderation
    - [ ] Network (Socket API)
    - [ ] Queries on the `settings.bin` database

- [ ] Create JavaScript Debugger

- [ ] Create the dynamic plugin loader (Pretty advanced stuff)
