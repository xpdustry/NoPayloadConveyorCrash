# NoPayloadConveyorCrashPlugin

[![Build status](https://github.com/Xpdustry/TemplatePlugin/actions/workflows/build.yml/badge.svg?branch=master&event=push)](https://github.com/Xpdustry/TemplatePlugin/actions/workflows/build.yml)
[![Mindustry 7.0](https://img.shields.io/badge/Mindustry-7.0-ffd37f)](https://github.com/Anuken/Mindustry/releases)
[![Downloads](https://img.shields.io/github/downloads/Xpdustry/NoPayloadConveyorCrash/total?color=purple)](https://github.com/Xpdustry/NoPayloadConveyorCrash/releases)

This plugin prevents players from crashing a server by constantly changing the direction of a payload conveyor.
It accomplishes this by :

- Replacing the broken blocks with a patched version using reflection, unlike
  this [mod](https://github.com/Agzam4/Mindustry-bugfixes-plugin) that does this by reloading the entire game.

- Preventing players from binding logic processors to payload conveyors.

## Installation

### Requirements

- Java 17 or above.

- Mindustry v140 or above.

### Deployment

Download the plugin jar in the [releases](https://github.com/Xpdustry/NoPayloadConveyorCrash/releases) and move it in
the `config/mods` directory of your server.

## Building

- `./gradlew shadowJar` to compile the plugin into a usable jar (will be located
  at `builds/libs/NoPayloadConveyorCrashPlugin.jar`).

- `./gradlew jar` for a plain jar that contains only the plugin code.

- `./gradlew runMindustryServer` to run the plugin in a local Mindustry server.
