# NoPayloadConveyorCrashPlugin

[![Build status](https://github.com/Xpdustry/TemplatePlugin/actions/workflows/build.yml/badge.svg?branch=master&event=push)](https://github.com/Xpdustry/TemplatePlugin/actions/workflows/build.yml)
[![Mindustry 7.0 ](https://img.shields.io/badge/Mindustry-7.0-ffd37f)](https://github.com/Anuken/Mindustry/releases)

This plugin prevents players from crashing a server by constantly changing the direction of a payload conveyor router.

> Unlike this [mod](https://github.com/Agzam4/Mindustry-bugfixes-plugin), this plugin doesn't reload the entire game but
> uses reflection
> to change the broken code. It also preserves the binary compatibility with other plugins.

## Installation

This plugin requires :

- Java 17 or above.

- Mindustry v140 or above.

## Building

- `./gradlew shadowJar` to compile the plugin into a usable jar (will be located
  at `builds/libs/NoPayloadConveyorCrashPlugin.jar`).

- `./gradlew jar` for a plain jar that contains only the plugin code.

- `./gradlew runMindustryServer` to run the plugin in a local Mindustry server.

- `./gradlew test` to run the unit tests of the plugin.
