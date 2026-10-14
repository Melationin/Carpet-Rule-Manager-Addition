package com.carpet.rm;

import carpet.api.settings.Rule;
import carpet.api.settings.RuleCategory;
import carpet.api.settings.Validators;

public final class CrmSettings {
    private CrmSettings() {}

    @Rule(categories = {RuleCategory.COMMAND}, validators = Validators.CommandLevel.class, strict = false)
    public static String carpetCommandViewPermissionLevel = "ops";

    @Rule(categories = {RuleCategory.COMMAND}, validators = Validators.CommandLevel.class, strict = false)
    public static String commandCarpetGroup = "ops";

    @Rule(categories = {RuleCategory.COMMAND}, validators = Validators.CommandLevel.class, strict = false)
    public static String commandCarpetPermission = "ops";
}
