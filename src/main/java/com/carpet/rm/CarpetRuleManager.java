package com.carpet.rm;

import carpet.CarpetExtension;
import carpet.CarpetServer;
import com.mojang.brigadier.CommandDispatcher;
import com.carpet.rm.command.CrmCommand;
import com.carpet.rm.util.CrmTranslations;
import net.fabricmc.api.ModInitializer;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import java.util.Map;

public final class CarpetRuleManager implements ModInitializer, CarpetExtension {
    private static ManagerService active;

    @Override public void onInitialize() { CarpetServer.manageExtension(this); }
    @Override public String version() { return "carpet-rm-addition"; }
    @Override public void onGameStarted() { CarpetServer.settingsManager.parseSettingsClass(CrmSettings.class); }

    @Override public void onServerLoaded(MinecraftServer server) {
        active = new ManagerService(server);
        active.reload();
    }

    @Override public void onServerClosed(MinecraftServer server) { active = null; }

    @Override public void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context) {
        CrmCommand.register(dispatcher);
    }

    public static ManagerService service() {
        return active;
    }

    @Override public Map<String, String> canHasTranslations(String language) {
        return CrmTranslations.load(language);
    }
}
