package com.carpet.rm;

import carpet.api.settings.CarpetRule;
import carpet.utils.CommandHelper;
import net.minecraft.commands.CommandSourceStack;
import carpet.utils.Messenger;
import static com.carpet.rm.util.CrmTranslations.tr;

public final class Access {
    private Access() {}

    public static boolean modify(CarpetRule<?> rule, CommandSourceStack source) {
        return CommandHelper.canUseCommand(source, CarpetRuleManager.service().modifyLevel(rule.name()));
    }

    public static void denied(CommandSourceStack source) {
        source.sendFailure(Messenger.c("r " + tr("crm.error.permission_denied")));
    }
}
