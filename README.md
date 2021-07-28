# Distributor

[![Jitpack latest version](https://jitpack.io/v/fr.xpdustry/Distributor.svg)](https://jitpack.io/#fr.xpdustry/Distributor)
[![Build status](https://github.com/Xpdustry/Distributor/actions/workflows/build.yml/badge.svg?branch=master&event=push)](https://github.com/Xpdustry/Distributor/actions/workflows/build.yml)
[![Mindustry 5.0 | 6.0](https://img.shields.io/badge/Mindustry-5.0%20%7C%206.0-ffd37f)](https://github.com/Anuken/Mindustry/releases)

## Description

**Distributor** is a library to make mindustry plugin development easier.
It can also be used for Server Manager plugins. It is still in developement so feel free to pr.
For a better understanding of the library, the Xpdustry core plugin is also here so check the code to see how to use correctly the library.

## Roadmaps

### Core

- Commands
    - [X] Base
    - [X] Container
    - [ ] Javadoc / Tests (need some fixes and upgrades)

- Formatter (Advanced string interpolation)
    - [X] Base
    - [ ] Javadoc / Tests

- Localization
    - [X] Base
    - [ ] Auto-loading ?
    - [ ] Javadoc / Tests
    
- Discord (Embedable Mindustry Discord Bot)
    - [ ] Base
    - [ ] Javadoc / Tests
    
- Threads
    - [ ] Base (PostMan)
    - [ ] Advanced (Shared Threads and sequential tasks)
    - [ ] Javadoc / Tests

- Monitoring
    - [ ] Statistics
    - [ ] Javadoc / Tests

- Services
    - [ ] Anti-VPN
    - [ ] Anti-Raids
    - [ ] Javadoc / Tests
    
- Administration
    - [ ] Permissions
    - [ ] Roles (access number)
    - [ ] Javadoc / Tests

### Plugin

- [ ] Base commands
- [ ] Admin tools

## Usage

Add these in your build.gradle

```gradle
repositories {
        maven { url 'https://jitpack.io' }
    }
}

dependencies {
    implementation 'fr.xpdustry.Distributor:(core or plugin):(latest version tag)'
}
```
