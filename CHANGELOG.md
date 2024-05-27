# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/),
and this project adheres to [Semantic Versioning](http://semver.org/).

## v4.0.0-beta.3 - 2024-05-25

Final beta release.
All major feature I wanted for distributor 4 have been implemented.
It is now time to use it in more production plugins to asses the usability of the new APIs.

## v4.0.0-beta.2 - 2024-04-08

Second beta release.
The API is now much more stable is terms of breaking changes.
There are still things to implement and finish like the new Lamp module.

## v4.0.0-beta.1 - 2024-03-28

Initial beta release to finally start migrating xpdustry plugins to distributor 4, and be able to detect potential runtime and design issues.
The documentation is currently a copy of distributor 3.

## v3.3.0 - 2024-01-03

### Features

- Moved plugin annotation parsing to dedicated class (`PluginAnnotationParser`).

### Bugfixes

- Add missing translation for `argument.parse.failure.duration`.
- Fixed annotation parsing failing for package private classes.
- Fixed `ArcLoggerFactory` not taking into account common logger wrappers.

### Deprecated

- Scheduled `EventBus#parse`, `ArcCommandManager#recipe` and `PluginScheduler#recipe` for removal.

## v3.2.1 - 2023-11-13

### Bugfixes

- Added compliance tests for arc container wrappers. They now have the same behavior as the standard java container types.
- Added accent normalization in `Players#findPlayers`.
- Added missing player info parser to `ArcCommandManager`.
- Fix arc container wrappers messing up the internal iterators of their internal collection. Concurrent reads goes BRRR...

## v3.2.0 - 2023-10-11

### Features

- Added cloud Kotlin coroutines extensions to the distributor Kotlin module.
- Added dedicated option for `TRACE` logging (`config trace [true|false]`), only active when debug is.

### Bugfixes

- Fixed inconsistent colors with the `TRACE` log level.

### Chores

- Bumped Mindustry version to `v146` and more.
- Renamed `xpdustry.fr` to `xpdustry.com`.
- Removed Kotlin javadoc.

## v3.1.0 - 2023-07-26

### Features

- Added `List` wrappers for entity groups.
- Added JUL to SLF4J bridge.
- Added plugin name along the class name for logger.
- Added soft dependencies field to `PluginDescriptor`.
- Added `PluginDescriptor#from(ClassLoader)`.
- Added method for super class event posting.
- Added option to automatically grant admin to validated admin player with different usids.
- Added `DistributorProvider#isInitialized` and `DistributorProvider#clear`.
- Allowed plugin listeners to register sub-listeners in `AbstractMindustryPlugin`.
- Implemented logger caller lookup for logger without an explicit class name.

### Changes

- Moved plugin and plugin listener annotation parsing after plugin load.
- Separated details of error logging from stacktrace.

### Bugfixes

- Fixed missing stacktrace log when an exception occurs in a plugin task.
- Fixed `Players#findPlayers` sometimes returning multiple players even with exact name match.
- Fixed not sending the cause of a command execution exception to the sender.
- Much more...

### Chores

- Bumped Mindustry version to `v145`.

## v3.0.0 - 2023-04-04

This is it, fellow plugin enjoyers, the final release of Distributor (its breaking with the version `v3.0.0-rc.3`).

### Features

- Added entity id and uuid lookup in `PlayerArgument`.
- Added `PlayerInfoArgument`.
- Added dedicated methods for async and sync tasks for `PluginScheduler`.
- Added usid player identity validation for the permission system.
- Added support for asynchronous execution in Cloud commands.
- Added `distributor-kotlin` for Kotlin extensions.
- Added command name fallback system in Cloud commands.
- Added permission lookup for group permissions.
- Added Mindustry UUID utilities.
- Added `MindustryTimeUnit` for the plugin scheduler.
- Added annotation based APIs for the plugin scheduler and event bus.
- Much more...

### Changes

- Use root parameter description when available instead of the command description.
- Changed distributor plugin id from `xpdustry-distributor-core` to `distributor-core`.
- Replaced `ExtendedPlugin` with the interface `MindustryPlugin` and its partial implementation `AbstractMindustryPlugin`.
- Reduce Arc collection wrappers visibility to replace with the `ArcCollections`.
- Implemented USID hashing in the player identity validator.
- Much more...

### Chores

- Overhaul of the internals to be more performant.
- Bumped Cloud commands.
- Bumped Mindustry.
- Bumped Toxopid.
- More consistent Javadoc.

## v3.0.0-rc.3 - 2022-11-21

**Final release candidate.**

I made several breaking changes that aims to fix the design issues I encountered during testing in the Xpdustry servers and the feedback of some fellow plugin developers (thanks @Prosta4okua).

### :warning: Breaking changes :warning:

- Replaced the annotation based event API (`EventBus`) with a functional API (`MoreEvents`). Reasoning is that mindustry event bus only support very basic functions, that does not justify the use of a wrapper class.
- Simplified the permission API by removing redundant.
- Removed `fr.xpdustry.distributor.api.manager` package.
- Removed `MUUIDAuthenticator` (Why using a dedicated class for authenticating MUUIDs while you can create an unauthenticated permission group with the permission API).
- `PluginScheduler` has been overhauled to be more intuitive. It's not a plugin owned object with a fluent API, with builders to schedule tasks like `PluginTaskBuilder` and `PluginTaskRecipe`. More in the javadocs.
- Forced the use of `ExtendedPlugin` in the Distributor API.
- `LocalizationSourceRegistry` now requires a default locale.

### Features

- You can now send localized messages to CommandSender without using the global translator (`CommandSender#sendLocalizedMessage`, `CommandSender#sendLocalizedWarning`).
- `ExtendedPlugin` has now listeners with `ExtendedPlugin#addListener`, a much better API compared to using a `ApplicationListener`.
- Added recipes to the scheduler API with `PluginScheduler#recipe`. (Split your plugin tasks between async and sync steps very easily).
- Added localization support for the permission commands.

### Changes

- Improved localization API.
- Some stuff I may have forgotten...

### Chores

- Finished the overall javadoc of Distributor.

### Bugfixes

- Fixed a bug where content wasn't always localized if the language of the JVM wasn't english.

## v3.0.0-rc.2 - 2022-11-01

Second release candidate for ya :)

### Changes

- `EventBus` now requires event listeners marked with the `EventBusListener` interface.
- `ExtendedPlugin#logger` is now available in the plugin constructor.
- Stuff I probably forgot.

### Chores

- Changed the code format of the project to [palantir java format](https://github.com/palantir/palantir-java-format) for
- it's readability.

## v3.0.0-rc.1 - 2022-10-22

Distributor 3 is now feature complete and now enters the testing phase...

### Features

- Plugin task scheduler for running tasks in sync or async very easily.
- Simple permission system with group support and commands.
- Event bus API that can register events using methods.
- Extended plugin class that enforces a correct method call order.£
- SLF4J implementation using arc native logger.
- MUCH MORE FEATURES...

### Changes

- Changed the localization system to use message formats, easier to use with arguments.
- Tighter integration with cloud.
- MUCH MORE CHANGES...

### Deprecated

- Removed the message API, it was awful. It will be soon replaced by an adventure-like library to send messages
- regardless of the platform.
