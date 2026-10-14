package com.carpet.rm.command;

import carpet.CarpetServer;
import carpet.CarpetSettings;
import carpet.api.settings.CarpetRule;
import carpet.api.settings.RuleHelper;
import carpet.utils.Messenger;
import carpet.utils.TranslationKeys;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.carpet.rm.CrmSettings;
import com.carpet.rm.ManagerService;
import com.carpet.rm.config.ManagerConfig;
import com.carpet.rm.mixin.SettingsManagerAccessor;
import com.carpet.rm.util.CommandHelper;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;

import java.util.List;
import java.util.Map;

import static com.carpet.rm.util.CrmTranslations.tr;
import static com.carpet.rm.CarpetRuleManager.service;

/**
 * Adds literal children to the original /carpet command, keeping its rule syntax and menus.
 */
public final class CrmCommand
{
    public static final List<String> OPTIONS = List.of("true", "false", "ops", "0", "1", "2", "3", "4");
    private static final SuggestionProvider<CommandSourceStack> RULES = (context, builder) -> SettingsManagerAccessor.crm$suggestRules(
            CarpetServer.settingsManager.getCarpetRules().stream().map(CarpetRule::name), builder);
    private static final SuggestionProvider<CommandSourceStack> GROUPS = (context, builder) -> SharedSuggestionProvider.suggest(
            service().config().groups.keySet(), builder);

    private CrmCommand()
    {
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher)
    {
        var root = dispatcher.getRoot().getChild("carpet");

        CommandHelper<CommandSourceStack> helper = new CommandHelper<>(root);
        if (root.getChild("group") == null) {
            helper.registerCommand("group{r}").rCarpet(() -> CrmSettings.commandCarpetGroup)
                    .command(CrmCommand::listGroups);

            helper.registerCommand("group list").command(CrmCommand::listGroups);

            helper.registerCommand("group add <name>").arg(StringArgumentType.word()).command(c ->
            {
                String name = text(c, "name");
                service().update(config -> config.groups.put(name, new ManagerConfig.Group()));
                Messenger.m(c.getSource(), "gi " + tr("crm.group.created", name), "!/carpet group " + name);
                return 1;
            });

            helper.registerCommand("group <name>{s}").arg(StringArgumentType.word()).s(GROUPS)
                    .command(CrmCommand::showGroup);

            helper.registerCommand("group <name> add <rule>{s}").reused().arg(StringArgumentType.word()).s(RULES)
                    .command(c -> saveTarget(c, RuleHelper.toRuleString(rule(c).value())));

            helper.registerCommand("group <name> add <rule> <value>{s}").reused().reused()
                    .arg(StringArgumentType.greedyString())
                    .s((context, builder) -> SharedSuggestionProvider.suggest(rule(context).suggestions(), builder))
                    .command(c -> saveTarget(c, text(c, "value")));

            for (boolean enabled : new boolean[]{true, false}) {
                helper.registerCommand("group <name> " + enabled).reused()
                        .command(c -> service().apply(c.getSource(), text(c, "name"), enabled, false));
                helper.registerCommand("group <name> " + enabled + " default").reused()
                        .command(c -> service().apply(c.getSource(), text(c, "name"), enabled, true));
            }
        } else {
            CarpetSettings.LOG.warn(tr("crm.command.already_registered", "/carpet group"));
        }

        if (root.getChild("permission") == null) {
            helper.registerCommand("permission{r}").rCarpet(() -> CrmSettings.commandCarpetPermission)
                    .command(CrmCommand::listPermissions);

            helper.registerCommand("permission list").command(CrmCommand::listPermissions);

            helper.registerCommand("permission <rule>{s} <permission>{s}").arg(StringArgumentType.word()).s(RULES)
                    .arg(StringArgumentType.word())
                    .s((context, builder) -> SharedSuggestionProvider.suggest(OPTIONS, builder)).command(c ->
                    {
                        var rule = rule(c);
                        String level = text(c, "permission");
                        service().update(config -> config.modifyPermissions.put(rule.name(), level));
                        Messenger.m(c.getSource(), "gi " + tr("crm.permission.set", rule.name(), level));
                        return 1;
                    });

            helper.registerCommand("permission <rule> default").reused().command(c ->
            {
                var rule = rule(c);
                service().update(config -> config.modifyPermissions.remove(rule.name()));
                Messenger.m(c.getSource(),
                        "gi " + tr("crm.permission.default", rule.name(), CarpetSettings.carpetCommandPermissionLevel));
                return 1;
            });
        } else {
            CarpetSettings.LOG.warn(tr("crm.command.already_registered", "/carpet permission"));
        }
    }

    private static int listGroups(CommandContext<CommandSourceStack> context)
    {

        var groups = service().config().groups;
        Messenger.m(context.getSource(), "wb Rule groups:");
        if (groups.isEmpty()) Messenger.m(context.getSource(), "g " + tr("crm.group.empty"));
        groups.forEach((name, group) ->
        {
            var rules = ManagerService.resolveRules(group.rules);
            Messenger.m(context.getSource(), "w - " + name, "!/carpet group " + name, "^y " + tr("crm.group.view"),
                    "g  ", toggle(name, rules, true), "g  ", toggle(name, rules, false));
        });
        return groups.size();
    }

    private static int showGroup(CommandContext<CommandSourceStack> context)
    {

        String name = text(context, "name");
        var group = ManagerService.group(service().config(), name);
        var rules = ManagerService.resolveRules(group.rules);
        Messenger.m(context.getSource(), "");
        Messenger.m(context.getSource(), "wb " + name, "!/carpet group " + name, "^g refresh");
        Messenger.m(context.getSource(), "w Options: ", "y [ ", toggle(name, rules, true), "w  ",
                toggle(name, rules, false), "y  ]");
        for (var entry : rules.entrySet()) {
            var rule = entry.getKey();
            Messenger.m(context.getSource(), ((SettingsManagerAccessor) rule.settingsManager()).crm$displayRule(rule),
                    "g  Target: ", "y " + entry.getValue(),
                    "?/carpet group " + name + " add " + rule.name() + " " + entry.getValue(),
                    "^g " + tr("crm.group.edit_target"));
        }
        return 1;
    }

    private static net.minecraft.network.chat.Component toggle(String name, Map<CarpetRule<?>, String> rules, boolean enabled)
    {
        boolean selected = !rules.isEmpty() && rules.entrySet().stream().allMatch(entry ->
        {
            var rule = entry.getKey();
            String target = enabled ? entry.getValue() : RuleHelper.toRuleString(rule.defaultValue());
            return RuleHelper.toRuleString(rule.value()).equals(target);
        });
        return Messenger.c((selected ? "lbu " : "c ") + "[" + enabled + "]", "!/carpet group " + name + " " + enabled,
                "^g " + tr(TranslationKeys.SWITCH_TO,
                        tr(enabled ? "crm.group.target_configuration" : "crm.group.default_configuration")));
    }

    private static int saveTarget(CommandContext<CommandSourceStack> context, String value)
    {
        String name = text(context, "name");
        var rule = rule(context);
        service().update(config -> ManagerService.group(config, name).rules.put(rule.name(), value));
        Messenger.m(context.getSource(), "gi " + tr("crm.group.saved_target", rule.name(), value),
                "!/carpet group " + name, "^g " + tr("crm.group.view"));
        return 1;
    }

    private static int listPermissions(CommandContext<CommandSourceStack> context)
    {

        var rules = ManagerService.resolveRules(service().config().modifyPermissions);
        Messenger.m(context.getSource(),
                "wb " + tr("crm.permission.header", CarpetSettings.carpetCommandPermissionLevel));
        if (rules.isEmpty()) Messenger.m(context.getSource(), "g " + tr("crm.permission.empty"));
        rules.forEach((rule, level) ->
        {
            String name = rule.name();
            Messenger.m(context.getSource(), "w - " + name, "!/carpet " + name,
                    "^y " + RuleHelper.translatedDescription(rule), "y  [" + level + "]",
                    "?/carpet permission " + name + " ", "^g " + tr("crm.permission.edit"),
                    "c  [default]", "?/carpet permission " + name + " default",
                    "^g " + tr("crm.permission.restore"));
        });
        return rules.size();
    }

    private static String text(CommandContext<CommandSourceStack> context, String name)
    {
        return StringArgumentType.getString(context, name);
    }

    private static CarpetRule<?> rule(CommandContext<CommandSourceStack> context)
    {
        return CarpetServer.settingsManager.getCarpetRule(text(context, "rule"));
    }
}
