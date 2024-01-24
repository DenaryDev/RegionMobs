/*
 * Copyright (c) 2024 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.regionmobs;

import me.denarydev.regionmobs.command.CommandManager;
import me.denarydev.regionmobs.listener.ChunkListener;
import me.denarydev.regionmobs.particle.ParticleManager;
import me.denarydev.regionmobs.region.RegionManager;
import me.denarydev.regionmobs.spawn.SpawnManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.spongepowered.configurate.ConfigurateException;

/**
 * @author DenaryDev
 * @since 1:53 12.01.2024
 */
public class RegionMobsPlugin extends JavaPlugin {
    private static RegionMobsPlugin instance;

    public static RegionMobsPlugin instance() {
        return instance;
    }

    private CommandManager commandManager;
    private RegionManager regionManager;
    private ParticleManager particleManager;
    private SpawnManager spawnManager;

    @Override
    public void onEnable() {
        instance = this;

        commandManager = new CommandManager();
        regionManager = new RegionManager(this);
        particleManager = new ParticleManager(this);
        spawnManager = new SpawnManager(this);

        reload();
        commandManager.registerCommands();
        getServer().getPluginManager().registerEvents(new ChunkListener(), this);
    }

    public void reload() {
        onDisable();
        try {
            Config.load(getDataFolder().toPath());
            regionManager.load();
        } catch (ConfigurateException e) {
            logger().error("Failed to load settings", e);
            return;
        }

        particleManager.load();
        spawnManager.load();
    }

    @Override
    public void onDisable() {
        if (particleManager != null)
            particleManager.unload();
        if (spawnManager != null)
            spawnManager.unload();
    }

    public CommandManager commandManager() {
        return commandManager;
    }

    public RegionManager regionManager() {
        return regionManager;
    }

    public ParticleManager particleManager() {
        return particleManager;
    }

    public SpawnManager spawnManager() {
        return spawnManager;
    }

    public @NotNull Logger logger() {
        return super.getSLF4JLogger();
    }
}
