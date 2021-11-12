Yay, another day, another update...

- Removed `AbstractPlugin` and its `plugin` package, until Mindustry handle dependencies correctly...

- Replaced `CommandAdapter` with `CommandInvoker`, a class that can invoke a command in a `CommandHandler` and anywhere else that can provide a `CommandContext`.

- Removed the `util` package and distributed the utilities in the root package `fr.xpdustry.distributor`:
  - `io` -> `ResourceLoader`
  - `bundle` -> Contains new Localization utilities
  - `struct` -> Contains some handy structures, might include tuples... 
  - `event` -> `EventWatcher` for dynamically add/removed event listeners.

- Renamed `ScriptEngine` to `JavaScriptEngine` and `ScriptLoader` to `JavaScriptLoader` and moved them into a sub package `js` to add futures packages with other scripting languages, Kotlin might be next.
