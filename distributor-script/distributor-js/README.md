# distributor-js

## Config

Here is the config you can change in `distributor/plugin/xpdustry-distributor-script-js.properties`

- `distributor.script.js.init`: Init script, leave empty for no init script.
- `distributor.script.js.startup`: Startup script (see above for details).
- `distributor.script.js.shutdown`: Shutdown script (see above for details).
- `distributor.script.js.blacklist`: Blacklist for packages and classes (separated by `,`), makes them invisible to scripts. Regex compatible.
- `distributor.script.js.whitelist`: WhiteList for packages and class, (see above for details)
- `distributor.script.js.max-runtime`: Max runtime before killing the script
