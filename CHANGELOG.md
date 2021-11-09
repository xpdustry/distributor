
- Included the default init script in distributor.
> It's much more einstead of downloading it, it's much more efficient
- Removed the CommandRegistry, use the new static methods of Commands instead,
much more flexible than creating a new registry for each script that needs to add a command.

- Renamed CommandWrapper to CommandAdapter, that name reflects more its function.

- Deleted the event package, it is useless to have a separate event bus and the only feature it had was multihtreading support.
The EventWatcher was spared, moved to the util package.

