# 规则

**提示：可以使用 `Ctrl+F` 快速查找自己想要的规则。**

所有规则注册在 `/carpet` 下。使用 `/carpet <规则> <值>` 修改当前值，使用 `/carpet setDefault <规则> <值>` 保存到 `carpet.conf`。

权限值沿用 Carpet：`ops` 表示等级 2 及以上；`0`、`true` 允许所有来源，`false` 禁止使用；`1`–`4` 表示最低权限等级。

## Carpet 查看命令权限等级 (carpetCommandViewPermissionLevel)

控制 `/carpet`、`/carpet list` 和规则详情等原生查看操作所需的权限等级。查看权限与规则修改权限分开设置。

- 类型：`String`
- 默认值：`ops`
- 参考选项：`true`、`false`、`ops`、`0`、`1`、`2`、`3`、`4`
- 分类：`command`

## Carpet 规则组命令权限 (commandCarpetGroup)

控制 `/carpet group` 全部选项所需的权限等级，包括列出、查看、创建、编辑和切换规则组。批量设置仍经过每条规则的修改权限及原生校验。

- 类型：`String`
- 默认值：`ops`
- 参考选项：`true`、`false`、`ops`、`0`、`1`、`2`、`3`、`4`
- 分类：`command`

用法见[规则组命令](command.md#carpet-规则组命令-commandcarpetgroup)。

## Carpet 规则权限命令权限 (commandCarpetPermission)

控制 `/carpet permission` 全部选项所需的权限等级，包括列出、设置和移除规则的修改权限覆盖。

- 类型：`String`
- 默认值：`ops`
- 参考选项：`true`、`false`、`ops`、`0`、`1`、`2`、`3`、`4`
- 分类：`command`

用法见[规则权限命令](command.md#carpet-规则权限命令-commandcarpetpermission)。
