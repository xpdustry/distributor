# distributor-js

## Description

`distributor-js` provides a better js runtime. Here are some things to know about this module :

- The `js` command is overridden to use the `JavaScriptEngine` provided by the plugin.

- The `require` function is set up to search for scripts from the root of the `distributor/script/js` subdirectory.

## Config

Here is the config you can change in `./distributor/plugins/xpdustry-distributor-script-js/config.properties` :

- `distributor.script.js.startup`: List of startup scripts (separated by `,`, the scripts must be located in the `./distributor/script/js` directory).

- `distributor.script.js.blacklist`: Blacklist for packages and classes (separated by `,`), makes them invisible to scripts. 

- `distributor.script.js.whitelist`: WhiteList for packages and classes.

- `distributor.script.js.max-runtime`: Max runtime before killing the script (in case you did the foridden `while(true){}`).

## Building

- `./gradlew jar` for a simple jar that contains only the plugin code.

- `./gradlew shadowJar` for a fatJar that contains the plugin and its dependencies (use this for your server).

## Testing

- `./gradlew runMindustryClient`: Run Mindustry in desktop with the plugin.

- `./gradlew runMindustryServer`: Run Mindustry in a server with the plugin.

## Running

[distributor-core](https://github.com/Xpdustry/Distributor) is required as a dependency.

This plugin is compatible with V6 and V7.

**/!\ Up to v135, you will need [mod-loader](https://github.com/Xpdustry/ModLoaderPlugin) for the dependency resolution.**
