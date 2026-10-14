# Rules

**Tip: use `Ctrl+F` to quickly find the rule you need.**

All rules are registered under `/carpet`. Use `/carpet <rule> <value>` to change the current value, or `/carpet setDefault <rule> <value>` to save it to `carpet.conf`.

Permission values follow Carpet: `ops` requires level 2 or higher; `0` and `true` allow everyone; `false` disables access; `1`–`4` specify the minimum permission level.

## carpetCommandViewPermissionLevel

Controls access to native read-only operations such as `/carpet`, `/carpet list` and rule menus. Viewing and modifying rules have separate permissions.

- Type: `String`
- Default: `ops`
- Options: `true`, `false`, `ops`, `0`, `1`, `2`, `3`, `4`
- Categories: `command`

## commandCarpetGroup

Controls all `/carpet group` options, including listing, viewing, creating, editing and toggling groups. Applying a group also passes through each rule's modification permission and native validation.

- Type: `String`
- Default: `ops`
- Options: `true`, `false`, `ops`, `0`, `1`, `2`, `3`, `4`
- Categories: `command`

See [group commands](command.md#commandcarpetgroup).

## commandCarpetPermission

Controls all `/carpet permission` options, including listing, setting and removing rule modification permission overrides.

- Type: `String`
- Default: `ops`
- Options: `true`, `false`, `ops`, `0`, `1`, `2`, `3`, `4`
- Categories: `command`

See [permission commands](command.md#commandcarpetpermission).
