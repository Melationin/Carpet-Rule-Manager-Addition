package com.carpet.rm.mixin;

import carpet.CarpetSettings;
import carpet.api.settings.CarpetRule;
import carpet.api.settings.SettingsManager;
import carpet.utils.CommandHelper;
import com.mojang.brigadier.CommandDispatcher;
import com.carpet.rm.Access;
import com.carpet.rm.CrmSettings;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;

import java.util.Collection;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = SettingsManager.class,
       remap = false)
public abstract class SettingsManagerMixin
{

    @Inject(method = "registerCommand",
            at = @At("RETURN"))
    @SuppressWarnings("unchecked")
    private void crm$rootPermission(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context, CallbackInfo ci)
    {
        SettingsManager manager = (SettingsManager) (Object) this;
        var node = dispatcher.getRoot().getChild(manager.identifier());
        ((CommandNodeAccessor<CommandSourceStack>) node).crm$setRequirement(
                    source -> !manager.locked() && (CommandHelper.canUseCommand(source,
                            CarpetSettings.carpetCommandPermissionLevel) || CommandHelper.canUseCommand(source,
                            CrmSettings.carpetCommandViewPermissionLevel)));
    }

    @Inject(method = {"setRule", "setDefault"},
            at = @At("HEAD"),
            cancellable = true)
    private void crm$modify(CommandSourceStack source, CarpetRule<?> rule, String value, CallbackInfoReturnable<Integer> cir)
    {
        if (!Access.modify(rule, source)) {
            Access.denied(source);
            cir.setReturnValue(0);
        }
    }

    @Inject(method = "removeDefault",
            at = @At("HEAD"),
            cancellable = true)
    private void crm$removeDefault(CommandSourceStack source, CarpetRule<?> rule, CallbackInfoReturnable<Integer> cir)
    {
        if (!Access.modify(rule, source)) {
            Access.denied(source);
            cir.setReturnValue(0);
        }
    }
}
