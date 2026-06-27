/*
 * Copyright (c) 2026 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.regionmobs;

import io.sapphiremc.lib.configurate.ConfigurateException;
import me.denarydev.regionmobs.listener.ChunkListener;
import me.denarydev.regionmobs.particle.ParticleManager;
import me.denarydev.regionmobs.region.RegionManager;
import me.denarydev.regionmobs.spawn.SpawnManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

/**
 * @author DenaryDev
 * @since 1:53 12.01.2024
 */
public final class RegionMobsPlugin extends JavaPlugin {
    private static RegionMobsPlugin instance;

    public static RegionMobsPlugin instance() {
        return instance;
    }

    private RegionManager regionManager;
    private ParticleManager particleManager;
    private SpawnManager spawnManager;

    @Override
    public void onEnable() {
        instance = this;

        this.regionManager = new RegionManager(this);
        this.particleManager = new ParticleManager(this);
        this.spawnManager = new SpawnManager(this);

        reload();

        getServer().getPluginManager().registerEvents(new ChunkListener(), this);
    }

    public void reload() {
        onDisable();

        try {
            Config.load(getDataFolder().toPath());
            this.regionManager.load();
        } catch (ConfigurateException e) {
            logger().error("Failed to load settings", e);
            return;
        }

        this.particleManager.load();
        this.spawnManager.load();
    }

    @Override
    public void onDisable() {
        if (this.particleManager != null) {
            this.particleManager.unload();
        }

        if (this.spawnManager != null) {
            this.spawnManager.unload();
        }
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
