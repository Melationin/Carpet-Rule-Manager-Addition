package com.carpet.rm.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class ConfigStore
{
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private final Path file;

    public ConfigStore(Path file)
    {
        this.file = file;
    }

    public ManagerConfig load() throws IOException
    {
        if (Files.notExists(file)) {
            ManagerConfig config = new ManagerConfig();
            save(config);
            return config;
        }
        return GSON.fromJson(Files.readString(file, StandardCharsets.UTF_8), ManagerConfig.class);
    }

    public void save(ManagerConfig config) throws IOException
    {
        Files.createDirectories(file.getParent());
        Files.writeString(file, GSON.toJson(config), StandardCharsets.UTF_8);
    }
}
