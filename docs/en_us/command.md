# Commands

**Tip: use `Ctrl+F` to quickly find the command you need.**

All commands extend `/carpet` directly. Rule arguments use native rule names such as `flippinCactus`. Group names, rule names, rule values and permission values support completion.

## commandCarpetGroup

Defines groups containing rules and their target values. Access is controlled by `commandCarpetGroup`, which defaults to `ops`.

- `/carpet group`: List all groups.
- `/carpet group list`: Same as above.
- `/carpet group <name>`: Show current and target rule values, with clickable group switches.
- `/carpet group add <name>`: Create a group.
- `/carpet group <name> add <rule>`: Add a rule using its current value as the target.
- `/carpet group <name> add <rule> <value>`: Add a rule with the specified target value, or update its target if already present.
- `/carpet group <name> true [default]`: Apply the target values.
- `/carpet group <name> false [default]`: Restore the factory defaults of the group's rules.

`[default]` is an optional literal argument. With `default`, each rule is applied through `/carpet setDefault`, changing its current value and saving it to `carpet.conf`. Without it, changes affect only the current session.

Adding rules updates the group definition without changing live rule values. Applying a group executes native Carpet commands as the caller, preserving rule permissions and validation.

Example:

```mcfunction
/carpet group add survival
/carpet group survival add flippinCactus true
/carpet group survival add stackableShulkerBoxes 16
/carpet group survival
/carpet group survival true
/carpet group survival false
/carpet group survival true default
```

Group definitions are saved automatically to `config/carpet-rm-addition.json` inside the world directory. The folder is created automatically when missing. Avoid the command keywords `add` and `list` as group names. Rules from removed extensions are skipped while their saved definitions are retained.

## commandCarpetPermission

Sets modification permissions for individual rules. Access is controlled by `commandCarpetPermission`, which defaults to `ops`.

- `/carpet permission`: List modification permission overrides for loaded rules.
- `/carpet permission list`: Same as above.
- `/carpet permission <rule> <permission>`: Set a rule's modification permission.
- `/carpet permission <rule> default`: Restore the default permission by inheriting Carpet's `carpetCommandPermissionLevel` again.

`<permission>` accepts `true`, `false`, `ops` and `0`–`4`; see [rules](rules.md). Overrides apply to native rule changes, `setDefault`, `removeDefault` and group application.

Example:

```mcfunction
/carpet permission flippinCactus 2
/carpet permission list
/carpet permission flippinCactus default
```

Permission overrides are saved automatically to `config/carpet-rm-addition.json` inside the world directory. See the [example configuration](../../examples/carpet-rm-addition.json).
