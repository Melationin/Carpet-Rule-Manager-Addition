package com.carpet.rm.config;

import java.util.Map;
import java.util.TreeMap;

/**
 * Each group has one target value per rule. Disabling restores factory defaults.
 */
public final class ManagerConfig
{
    public Map<String, String> modifyPermissions = new TreeMap<>();
    public Map<String, Group> groups = new TreeMap<>();

    public static final class Group
    {
        public Map<String, String> rules = new TreeMap<>();
    }

}
