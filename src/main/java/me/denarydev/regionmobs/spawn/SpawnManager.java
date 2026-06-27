/*
 * Copyright (c) 2026 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.regionmobs.spawn;

import me.denarydev.regionmobs.Config;
import me.denarydev.regionmobs.RegionMobsPlugin;
import me.denarydev.regionmobs.region.Region;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;

/**
 * @author DenaryDev
 * @since 5:29 17.01.2024
 */
public final class SpawnManager {
    private final RegionMobsPlugin plugin;
    private final List<SpawnRegion> regions = new ArrayList<>();
    private BukkitTask task;

    public SpawnManager(RegionMobsPlugin plugin) {
        this.plugin = plugin;
    }

    public void load() {
        this.regions.clear();
        this.regions.addAll(this.plugin.regionManager().regions().values().stream().filter(Region::enabled).map(SpawnRegion::new).toList());

        this.task = Bukkit.getScheduler().runTaskTimer(this.plugin, () ->
            new ArrayList<>(this.regions).forEach(SpawnRegion::spawnMobs), 20L, Config.settings().mobSpawn.interval);
    }

    public void unload() {
        if (this.task != null) {
            this.task.cancel();
        }

        this.regions.forEach(SpawnRegion::despawnAll);
    }

    public void addRegion(Region region) {
        if (region.enabled()) this.regions.add(new SpawnRegion(region));
    }

    public void removeRegion(String id) {
        this.regions.removeIf(spawnRegion -> spawnRegion.id().equals(id));
    }
}
