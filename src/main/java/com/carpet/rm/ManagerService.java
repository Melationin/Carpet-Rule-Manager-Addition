package com.carpet.rm;

import carpet.CarpetSettings;
import carpet.CarpetServer;
import carpet.api.settings.CarpetRule;
import carpet.api.settings.RuleHelper;
import com.carpet.rm.config.ConfigStore;
import com.carpet.rm.config.ManagerConfig;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelResource;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

import static com.carpet.rm.util.CrmTranslations.tr;

import carpet.utils.Messenger;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

public final class ManagerService
{
    private final MinecraftServer server;
    private final ConfigStore store;
    private ManagerConfig config = new ManagerConfig();

    public ManagerService(MinecraftServer server)
    {
        this.server = server;
        store = new ConfigStore(server.getWorldPath(LevelResource.ROOT).resolve("config/carpet-rm-addition.json"));
    }

    public static ManagerConfig.Group group(ManagerConfig config, String name)
    {
        return config.groups.get(name);
    }

    public static Map<CarpetRule<?>, String> resolveRules(Map<String, String> values)
    {
        Map<CarpetRule<?>, String> rules = new LinkedHashMap<>();
        values.forEach((name, value) ->
        {
            CarpetRule<?> rule = CarpetServer.settingsManager.getCarpetRule(name);
            if (rule != null)
                rules.put(rule, value);
        });
        return rules;
    }

    public ManagerConfig config()
    {
        return config;
    }

    public String modifyLevel(String rule)
    {
        return config.modifyPermissions.getOrDefault(rule, CarpetSettings.carpetCommandPermissionLevel);
    }

    public void reload()
    {
        try {
            config = store.load();
            refreshCommands();
        } catch (IOException e) {
            throw new IllegalStateException(tr("crm.error.config_load"), e);
        }
    }

    public void update(Consumer<ManagerConfig> edit)
    {
        edit.accept(config);
        try {
            store.save(config);
        } catch (IOException e) {
            throw new IllegalStateException(tr("crm.error.config_save"), e);
        }
        refreshCommands();
    }

    private void refreshCommands()
    {
        server.getPlayerList().getPlayers().forEach(player -> server.getCommands().sendCommands(player));
    }

    public int apply(CommandSourceStack source, String name, boolean enabled, boolean setDefault)
    {
        var group = group(config, name);
        for (var entry : resolveRules(group.rules).entrySet()) {
            CarpetRule<?> rule = entry.getKey();
            String value = enabled ? entry.getValue() : RuleHelper.toRuleString(rule.defaultValue());
            String command = (setDefault ? "carpet setDefault " : "carpet ") + rule.name() + " " + value;
            try {
                source.getServer().getCommands().getDispatcher().execute(command, source);
            } catch (CommandSyntaxException e) {
                source.sendFailure(
                        Messenger.c("r " + tr("crm.error.rule_set_failed", rule.name(), value, e.getMessage())));
            }
        }
        return 1;
    }
}
