ANOTHER BREAKING UPDATE !!!

- `distributor-js` brings its own javascript runtime instead of `mindustry-rhino` for better compatibility with V6 and more control.

- Simplified the javascript settings :

  - Removed `distributor.script.js.shutdown` (I don't think you will put cleanup logic in javascript...).

  - Removed `distributor.script.js.init`, the init script will serve for builtins functions now.

  - `distributor.script.js.startup` is now a list of startup scripts (separated by commas).

- The `js` command no longer shares the same scope with every user, each one has its own (player and server).

  - This means users can no longer mess with the variables in the global scope.

- Renamed the config getters of both plugins from `config()` to `getConf()`.

- Much more !!!