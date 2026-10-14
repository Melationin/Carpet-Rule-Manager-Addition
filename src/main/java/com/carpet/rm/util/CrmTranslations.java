package com.carpet.rm.util;

import carpet.utils.Translations;

import java.util.HashMap;
import java.util.Map;

/**
 * Resources are loaded and selected by Carpet, including its /carpet language rule.
 */
public final class CrmTranslations
{
    private static final String RESOURCE = "assets/carpet-rm-addition/lang/%s.json";
    private static final Map<String, String> ENGLISH = Map.copyOf(
            Translations.getTranslationFromResourcePath(RESOURCE.formatted("en_us")));

    private CrmTranslations()
    {
    }

    public static Map<String, String> load(String language)
    {
        Map<String, String> result = new HashMap<>(ENGLISH);
        result.putAll(Translations.getTranslationFromResourcePath(RESOURCE.formatted(language)));
        return result;
    }

    public static String tr(String key, Object... arguments)
    {
        return Translations.tr(key, ENGLISH.getOrDefault(key, key)).formatted(arguments);
    }
}
