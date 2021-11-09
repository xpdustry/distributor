The v1.6 release is a big overhaul of distributor design, I realised how much v1.5 was atrocious, sorry about that:

- Included the default init script in distributor.
> It's much more efficient than downloading it.

- Removed the CommandRegistry, use the new static methods of Commands instead.
> It's much more flexible for scripts.

- Renamed CommandWrapper to CommandAdapter
> That name reflects more its function.

- Deleted the `event` with `PostMan` and `EventRunner`. `EventWatcher` has been moved to the `util` package.
> It is useless to have a separate event bus which only had multithreading support (`PostMan`) compared to `Events`

- `ScriptEngine` now implements `AutoCloseable` for cleaning resources if it is used in a temporary thread.

- `WrappedBundle` can now send localized message to players with the `send` method.

- Moved Distributor initialization in the `init` method instead of a static block.
> More control

- Added more javadocs for ya :^)

- A lot of small changes I probably forgot...
