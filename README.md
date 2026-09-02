# acre — Apple Container Resource Explorer

A terminal UI for Apple's `container` CLI on macOS. Watch your containers, read their logs, and start or stop them.

## What it does

- **Containers.** List, inspect, start, stop, restart, kill, delete, prune, logs
- **Images.** List
- **Volumes.** List, inspect
- **Command palette.** Fuzzy search over every command.

## Requirements

- macOS on Apple Silicon
- Apple's `container` CLI, with the service running: `container system start`
- Java 25 or newer (JBang can fetch this for you)

## Install

Use [JBang](https://www.jbang.dev), (fetches Java 25 for you):

```
jbang acre@bogdanpc/acre
```

You can install it as JBang app:

```
jbang app install acre@bogdanpc/acre
acre
```

Or download `acre-<version>.jar` from the [releases page](https://github.com/bogdanpc/acre/releases) and run it:

```
java -jar acre-<version>.jar
```

## Keys

| Key         | Action                            |
|-------------| --------------------------------- |
| `↑` `↓`     | Move up and down                 |
| `1` `2` `3` | Containers / images / volumes     |
| `tab`       | Next tab                          |
| `enter`     | Details for the selected row      |
| `l`         | Container logs                    |
| `s`         | Start                             |
| `x`         | Stop                              |
| `t`         | Restart                           |
| `P`         | Prune                             |
| `r`         | Reload                            |
| `:` or `^p` | Command palette                   |
| `?`         | Help                              |
| `q`         | Quit                              |

## Build from source

```
git clone https://github.com/bogdanpc/acre.git
cd acre
mvn verify
java -jar target/acre-*.jar
```

## How this was built

I wrote acre with help from an AI coding assistant. Bugs are still mine.

## License

Apache-2.0. See [LICENSE](LICENSE).

Built with Java 25 and [TamboUI](https://github.com/tamboui/tamboui).
