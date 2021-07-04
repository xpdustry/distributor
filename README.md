# TemplatePlugin

[![Jitpack latest version](https://jitpack.io/v/fr.xpdustry/TemplatePlugin.svg)](https://jitpack.io/#fr.xpdustry/TemplatePlugin)
[![Build status](https://github.com/Xpdustry/TemplatePlugin/actions/workflows/build.yml/badge.svg?branch=master&event=push)](https://github.com/Xpdustry/TemplatePlugin/actions/workflows/build.yml)

## Description

Template stolen from **Anuken/ExamplePlugin** lol...

This template features some cool stuff such as:
- [Jitpack](https://jitpack.io/) support.
- Gradle tasks for testing:
  - `gradlew moveJar` Move the output jar to your server mod directory.
  - `gradlew runServer` Start the server in a new cmd.
  - `gradlew deployJar` Executes `moveJar` and `runServer`.
- GitHub action for easier release and Jitpack usage:
   - You just have to run the `Release` workflow manually,
     it will automatically take the plugin version in your plugin.json file and upload the jar.

## Tips and nice things to know

- When you use this template, make sure you change the information in `plugin.json`
  and set the `serverDirectoryPath`, `group` and `mindustryVersion` properties is `build.gradle`.
  There is also the `rootProject.name` property in `settings.gradle`.
  
- The plugin compiles to java 8 for compatibility reasons,
  but nothing keeps you to change the compiler target or source to a higher jdk.

- For faster testing, I recommend you to add an exit statement at the end of your server startup script such as:

`run_server.bat` 
```batch
@echo off
java -jar server.jar
exit
```

`run_server.sh`
```shell
#!/usr/bin/env bash
java -jar server.jar
exit
```

Thank you for using this template !
