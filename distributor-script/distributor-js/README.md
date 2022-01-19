# distributor-js

## Description

`distributor-js` provides a better js runtime on top of Mindustry rhino. Here are some things to know about this module:

- The init script will run once, for every `JavaScriptEngine` instance created (one per thread), so include your global imports and functions there.
- The startup script will run once in the main thread, same for the shutdown script.
- The `js` command is overridden to use the `JavaScriptEngine`.
- The `require` function is set up to the root of the `distributor/script/js` subdirectory inside the root directory of Distributor.
- Distributor automatically kills blocking scripts after 10 seconds of runtime by default, you can change that behavior in the property file.

## Config

Here is the config you can change in `distributor/plugins/xpdustry-distributor-script-js.properties`

- `distributor.script.js.init`: Init script, leave empty for no init script.
- `distributor.script.js.startup`: Startup script (see above for details).
- `distributor.script.js.shutdown`: Shutdown script (see above for details).
- `distributor.script.js.blacklist`: Blacklist for packages and classes (separated by `,`), makes them invisible to scripts. Regex compatible.
- `distributor.script.js.whitelist`: WhiteList for packages and class, (see above for details)
- `distributor.script.js.max-runtime`: Max runtime before killing the script
