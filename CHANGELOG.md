# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/),
and this project adheres to [Semantic Versioning](http://semver.org/).

## v3.0.0-rc.2 - 2022-11-01

Second release candidate for ya :)

### Changes

- `EventBus` now requires event listeners marked with the `EventBusListener` interface.
- `ExtendedPlugin#logger` is now available in the plugin constructor.
- Stuff I probably forgot.

### Chores

- Changed the code format of the project to [palantir java format](https://github.com/palantir/palantir-java-format) for
  it's readability.

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
