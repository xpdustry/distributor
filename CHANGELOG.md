HUGE UPDATE... *Distributor will be much more stable from now on...*

- Distributor have been split to satisfy the specific needs of everyone:Split the project:
    - `distributor-core`: Contains the core utilities and the command framework.
    - `distributor-js`: Contains JavaScript utilities and better control over the runtime.

- Added unit tests for the command framework.

- `EventWatcher` now supports both `Events.on` and `Events.run`.

- Implemented java collections views for the arc collections:
    - `ArcList`: This list can take advantage of the performances of the `Seq` while enjoying java features.
    - `ArcSet`: Same for the list

- Added `AbstractPlugin` which provides a better config method and registration methods for the `ArcCommandManager`

- A lot of stuff I might have forgotten...
