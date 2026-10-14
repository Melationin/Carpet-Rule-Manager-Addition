package com.carpet.rm.mixin;

import carpet.api.settings.CarpetRule;
import carpet.api.settings.SettingsManager;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

/**
 * Carpet is a mod dependency; its private members require Mixin rather than a vanilla class tweaker.
 */
@Mixin(value = SettingsManager.class,
       remap = false)
public interface SettingsManagerAccessor
{
    @Invoker("suggestMatchingContains")
    static CompletableFuture<Suggestions> crm$suggestRules(Stream<String> rules, SuggestionsBuilder builder)
    {
        throw new AssertionError();
    }

    @Invoker("displayInteractiveSetting")
    Component crm$displayRule(CarpetRule<?> rule);
}
