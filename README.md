# Apple Container Resource Explorer - actui

A terminal UI for Apple's `container` CLI on macOS. Watch your containers, read their logs, and start or stop them.


- **Containers control.** Start, stop, restart, kill, delete, prune.
- **Images and volumes**
- **Command palette.** `^K` (or `:`) opens a fuzzy search over every command


Built with Java 25 and [TamboUI](https://github.com/tamboui/tamboui)


## Requirements

- macOS on Apple Silicon with Apple's `container` CLI
- The container service running: `container system start`
- Java 25 or newer.
