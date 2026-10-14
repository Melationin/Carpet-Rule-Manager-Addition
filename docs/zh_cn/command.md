# 命令

**提示：可以使用 `Ctrl+F` 快速查找自己想要的命令。**

所有命令直接扩展 `/carpet`，规则参数使用原版规则名，例如 `flippinCactus`。组名、规则名、规则值和权限值支持补全。

## Carpet 规则组命令 (commandCarpetGroup)

定义规则组，每组保存多条规则及其目标值。使用 `commandCarpetGroup` 控制命令权限，默认 `ops`。

- `/carpet group`：列出所有规则组。
- `/carpet group list`：同上。
- `/carpet group <name>`：查看组内规则的当前值和目标值，并显示可点击的开关。
- `/carpet group add <name>`：创建规则组。
- `/carpet group <name> add <rule>`：添加规则，使用该规则的当前值作为目标值。
- `/carpet group <name> add <rule> <value>`：添加规则并指定目标值；规则已在组内时更新目标值。
- `/carpet group <name> true [default]`：将组内规则设为目标值。
- `/carpet group <name> false [default]`：将组内规则恢复为出厂默认值。

`[default]` 是可选的字面参数。带上 `default` 时，逐条通过 `/carpet setDefault` 同时修改当前值并写入 `carpet.conf`；不带时只修改当前会话的规则值。

添加规则只保存组定义，不立即修改规则。切换组以调用者身份逐条执行原生 Carpet 命令，沿用规则权限和校验。

示例：

```mcfunction
/carpet group add survival
/carpet group survival add flippinCactus true
/carpet group survival add stackableShulkerBoxes 16
/carpet group survival
/carpet group survival true
/carpet group survival false
/carpet group survival true default
```

组定义自动保存到存档目录下的 `config/carpet-rm-addition.json`，文件夹不存在时自动创建。组名避开命令关键字 `add`、`list`。扩展移除后，缺失规则暂时跳过，配置继续保留。

## Carpet 规则权限命令 (commandCarpetPermission)

单独设置规则的修改权限。使用 `commandCarpetPermission` 控制命令权限，默认 `ops`。

- `/carpet permission`：列出已加载规则的修改权限覆盖。
- `/carpet permission list`：同上。
- `/carpet permission <rule> <permission>`：设置指定规则的修改权限。
- `/carpet permission <rule> default`：恢复默认权限，使用 Carpet 原有的 `carpetCommandPermissionLevel`。

`<permission>` 支持 `true`、`false`、`ops`、`0`–`4`，含义见[规则文档](rules.md)。权限覆盖作用于原生规则修改、`setDefault`、`removeDefault` 和规则组批量设置。

示例：

```mcfunction
/carpet permission flippinCactus 2
/carpet permission list
/carpet permission flippinCactus default
```

权限覆盖自动保存在存档目录下的 `config/carpet-rm-addition.json`。配置格式见[示例文件](../../examples/carpet-rm-addition.json)。
