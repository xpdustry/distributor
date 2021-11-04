# Distributor

[![Jitpack latest version](https://jitpack.io/v/Xpdustry/Distributor.svg)](https://jitpack.io/#Xpdustry/Distributor)
[![Build](https://github.com/Xpdustry/Distributor/actions/workflows/build.yml/badge.svg?branch=master)](https://github.com/Xpdustry/Distributor/actions/workflows/build.yml)
[![Mindustry 6.0 | 7.0 ](https://img.shields.io/badge/Mindustry-6.0%20%7C%207.0-ffd37f)](https://github.com/Anuken/Mindustry/releases)

## Description

**Distributor** is a Mindustry plugin that can be used as a library to make plugin development easier, or as a Server Manager to automate your server tasks via javascript.

## Usage

### As a Library

Add these in your build.gradle

```gradle
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    compileOnly 'fr.xpdustry.Distributor:1.5'
}
```

No proper javadoc for now, but feel free to ask me stuff through the pr or discord at Phinner#0867 (Make sure you join the mindustry discord first).

### As a Plugin

#### First use

When you will launch this plugin for the first time, it will create a property file inside the config directory that Distributor will use as its config.
Distributor will also deploy its file tree and download some resources from this repo such as the default init script.

#### Scripting with Distributor

- The init script will run for every ScriptEngine created (one per thread), so include your global imports and functions there.
- The startup scripts will run only once in the main thread, same for the shutdown scripts.
- To not overwrite the default `js` command, use `jscript` to run javascript with distributor instead.
- The `require` function is set up to the root of the `scripts` directory inside the root directory of Distributor. Use it to load your scripts.
- If you are scared of blocking scripts, don't worry, distributor automatically kills it after 10 seconds of runtime by default, you can change that setting in the property file.
